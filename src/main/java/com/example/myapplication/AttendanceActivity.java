package com.example.myapplication;
import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.*;
import org.opencv.features2d.DescriptorMatcher;
import org.opencv.features2d.ORB;
import org.opencv.imgproc.Imgproc;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
public class AttendanceActivity extends AppCompatActivity {
    private static final int REQUEST_CAMERA = 101;
    private static final int CAMERA_PERMISSION_CODE = 102;
    private EditText editTextUsername;
    private ImageView imageViewCaptured;
    private Button btnCapture, btnMarkAttendance;
    private DatabaseHelper databaseHelper;
    private Bitmap capturedBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.attendance_layout);

        editTextUsername = findViewById(R.id.editTextName);
        imageViewCaptured = findViewById(R.id.imageViewCaptured);
        btnCapture = findViewById(R.id.btnCapture);
        btnMarkAttendance = findViewById(R.id.btnMarkAttendance);
        databaseHelper = new DatabaseHelper(this);

        if (!OpenCVLoader.initDebug()) {
            Log.e("OpenCV", "OpenCV initialization failed.");
        }

        btnCapture.setOnClickListener(view -> requestCameraPermission());
        btnMarkAttendance.setOnClickListener(view -> checkAttendance());
    }

    private void requestCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_CODE);
        } else {
            openCamera();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openCamera();
            } else {
                Toast.makeText(this, "Camera permission is required!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void openCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, REQUEST_CAMERA);
        } else {
            Toast.makeText(this, "No Camera App Found!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CAMERA && resultCode == RESULT_OK && data != null) {
            capturedBitmap = (Bitmap) data.getExtras().get("data");
            imageViewCaptured.setImageBitmap(capturedBitmap);
        }
    }
    private void checkAttendance() {
//        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
//        String currentTime = sdf.format(new Date());
//
//        // Define allowed time range
//        String startTime = "20:30"; // 8:30 PM
//        String endTime = "21:30";   // 9:30 PM
//
//        // Check if current time is within range
//        if (currentTime.compareTo(startTime) < 0 || currentTime.compareTo(endTime) > 0) {
//            Toast.makeText(this, "Not a correct timing for attendance!", Toast.LENGTH_SHORT).show();
//            return;
//        }
        String username = editTextUsername.getText().toString().trim();
        if (username.isEmpty()) {
            Toast.makeText(this, "Enter username!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!databaseHelper.isUserExists(username)) {
            Toast.makeText(this, "User not found!", Toast.LENGTH_SHORT).show();
            return;
        }
        Bitmap storedBitmap = databaseHelper.getUserPhoto(username);
        if (storedBitmap == null) {
            Toast.makeText(this, "No image found for this user!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (capturedBitmap == null) {
            Toast.makeText(this, "Capture an image first!", Toast.LENGTH_SHORT).show();
            return;
        }
        boolean isMatch = compareImagesORB(storedBitmap, capturedBitmap);
        String status = isMatch ? "P" : "A";
        String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        databaseHelper.markAttendance(username, currentDate, status);
        Toast.makeText(this, "Attendance Marked: " + status, Toast.LENGTH_SHORT).show();
    }
    private boolean compareImagesORB(Bitmap img1, Bitmap img2) {
        Mat mat1 = new Mat();
        Mat mat2 = new Mat();

        // Convert Bitmap to Mat
        org.opencv.android.Utils.bitmapToMat(img1, mat1);
        org.opencv.android.Utils.bitmapToMat(img2, mat2);

        // Convert to grayscale
        Imgproc.cvtColor(mat1, mat1, Imgproc.COLOR_RGBA2GRAY);
        Imgproc.cvtColor(mat2, mat2, Imgproc.COLOR_RGBA2GRAY);

        // Resize for consistency
        Size newSize = new Size(300, 300);
        Imgproc.resize(mat1, mat1, newSize);
        Imgproc.resize(mat2, mat2, newSize);

        // 🔹 Step 1: ORB Feature Matching
        ORB orb = ORB.create(1000);
        MatOfKeyPoint keypoints1 = new MatOfKeyPoint();
        MatOfKeyPoint keypoints2 = new MatOfKeyPoint();
        Mat descriptors1 = new Mat();
        Mat descriptors2 = new Mat();

        orb.detectAndCompute(mat1, new Mat(), keypoints1, descriptors1);
        orb.detectAndCompute(mat2, new Mat(), keypoints2, descriptors2);

        // If descriptors are empty, return false
        if (descriptors1.empty() || descriptors2.empty()) {
            Log.e("FaceMatch", "Descriptors are empty! Image may not have enough keypoints.");
            return false;
        }

        DescriptorMatcher matcher = DescriptorMatcher.create(DescriptorMatcher.BRUTEFORCE_HAMMING);
        List<MatOfDMatch> knnMatches = new ArrayList<>();
        matcher.knnMatch(descriptors1, descriptors2, knnMatches, 2);

        // Lowe's Ratio Test to filter good matches
        float ratioThreshold = 0.8f; // Looser threshold for real-world conditions
        int goodMatches = 0;
        for (MatOfDMatch matOfDMatch : knnMatches) {
            DMatch[] matches = matOfDMatch.toArray();
            if (matches.length >= 2 && matches[0].distance < ratioThreshold * matches[1].distance) {
                goodMatches++;
            }
        }

        Log.d("FaceMatch", "Good Matches: " + goodMatches);

        // 🔹 Step 2: Histogram Comparison for Backup Matching
        double similarityScore = compareHistograms(mat1, mat2);
        Log.d("FaceMatch", "Histogram Similarity Score: " + similarityScore);

        // ✅ Improved Condition: ORB Matching + Histogram Similarity
        return goodMatches > 25 || similarityScore > 0.75;
    }
    private double compareHistograms(Mat img1, Mat img2) {
        Mat hist1 = new Mat();
        Mat hist2 = new Mat();
        // Compute histograms
        Imgproc.calcHist(Collections.singletonList(img1), new MatOfInt(0), new Mat(), hist1, new MatOfInt(256), new MatOfFloat(0, 256));
        Imgproc.calcHist(Collections.singletonList(img2), new MatOfInt(0), new Mat(), hist2, new MatOfInt(256), new MatOfFloat(0, 256));
        // Normalize histograms
        Core.normalize(hist1, hist1, 0, 1, Core.NORM_MINMAX);
        Core.normalize(hist2, hist2, 0, 1, Core.NORM_MINMAX);
        return Imgproc.compareHist(hist1, hist2, Imgproc.HISTCMP_CORREL);
    }
}


//
//package com.example.myapplication;
//
//import android.Manifest;
//import android.content.Context;
//import android.content.Intent;
//import android.content.pm.PackageManager;
//import android.graphics.Bitmap;
//import android.os.Bundle;
//import android.provider.MediaStore;
//import android.util.Log;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.ImageView;
//import android.widget.Toast;
//import androidx.annotation.NonNull;
//import androidx.annotation.Nullable;
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.core.app.ActivityCompat;
//import androidx.core.content.ContextCompat;
//import com.google.firebase.database.DatabaseReference;
//import com.google.firebase.database.FirebaseDatabase;
//import com.google.firebase.database.ValueEventListener;
//import org.opencv.android.OpenCVLoader;
//import org.opencv.core.*;
//import org.opencv.features2d.DescriptorMatcher;
//import org.opencv.features2d.ORB;
//import org.opencv.imgproc.Imgproc;
//import java.text.SimpleDateFormat;
//import java.util.ArrayList;
//import java.util.Collections;
//import java.util.Date;
//import java.util.List;
//import java.util.Locale;
//
//public class AttendanceActivity extends AppCompatActivity {
//    private static final int REQUEST_CAMERA = 101;
//    private static final int CAMERA_PERMISSION_CODE = 102;
//    private EditText editTextUsername;
//    private ImageView imageViewCaptured;
//    private Button btnCapture, btnMarkAttendance;
//    private FirebaseDatabase firebaseDatabase;
//    private DatabaseReference usersRef;
//    private Bitmap capturedBitmap;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.attendance_layout);
//
//        editTextUsername = findViewById(R.id.editTextName);
//        imageViewCaptured = findViewById(R.id.imageViewCaptured);
//        btnCapture = findViewById(R.id.btnCapture);
//        btnMarkAttendance = findViewById(R.id.btnMarkAttendance);
//
//        // Initialize Firebase
//        firebaseDatabase = FirebaseDatabase.getInstance();
//        usersRef = firebaseDatabase.getReference("users");
//
//        if (!OpenCVLoader.initDebug()) {
//            Log.e("OpenCV", "OpenCV initialization failed.");
//        }
//
//        btnCapture.setOnClickListener(view -> requestCameraPermission());
//        btnMarkAttendance.setOnClickListener(view -> checkAttendance());
//    }
//
//    private void requestCameraPermission() {
//        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_CODE);
//        } else {
//            openCamera();
//        }
//    }
//
//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        if (requestCode == CAMERA_PERMISSION_CODE) {
//            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                openCamera();
//            } else {
//                Toast.makeText(this, "Camera permission is required!", Toast.LENGTH_SHORT).show();
//            }
//        }
//    }
//
//    private void openCamera() {
//        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//        if (intent.resolveActivity(getPackageManager()) != null) {
//            startActivityForResult(intent, REQUEST_CAMERA);
//        } else {
//            Toast.makeText(this, "No Camera App Found!", Toast.LENGTH_SHORT).show();
//        }
//    }
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == REQUEST_CAMERA && resultCode == RESULT_OK && data != null) {
//            capturedBitmap = (Bitmap) data.getExtras().get("data");
//            imageViewCaptured.setImageBitmap(capturedBitmap);
//        }
//    }
//
//    private void checkAttendance() {
//        String username = editTextUsername.getText().toString().trim();
//        if (username.isEmpty()) {
//            Toast.makeText(this, "Enter username!", Toast.LENGTH_SHORT).show();
//            return;
//        }
//
//        // Check if user exists in Firebase
//        usersRef.child(username).get().addOnCompleteListener(task -> {
//            if (!task.isSuccessful()) {
//                Toast.makeText(AttendanceActivity.this, "Error checking user", Toast.LENGTH_SHORT).show();
//                return;
//            }
//
//            if (!task.getResult().exists()) {
//                Toast.makeText(AttendanceActivity.this, "User not found!", Toast.LENGTH_SHORT).show();
//                return;
//            }
//
//            // Get the stored user photo
//            String photoPath = task.getResult().child("photo").getValue(String.class); // Assuming stored as a base64 string or URL
//            if (photoPath == null) {
//                Toast.makeText(AttendanceActivity.this, "No image found for this user!", Toast.LENGTH_SHORT).show();
//                return;
//            }
//
//            // Convert photoPath (base64 or URL) to Bitmap if needed
//            Bitmap storedBitmap = getBitmapFromStorage(photoPath);
//            if (storedBitmap == null) {
//                Toast.makeText(AttendanceActivity.this, "Error retrieving user photo!", Toast.LENGTH_SHORT).show();
//                return;
//            }
//
//            if (capturedBitmap == null) {
//                Toast.makeText(this, "Capture an image first!", Toast.LENGTH_SHORT).show();
//                return;
//            }
//
//            boolean isMatch = compareImagesORB(storedBitmap, capturedBitmap);
//            String status = isMatch ? "P" : "A";
//            String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
//
//            // Mark attendance in Firebase
//            usersRef.child(username).child("attendance").child(currentDate).setValue(status).addOnSuccessListener(aVoid -> {
//                Toast.makeText(AttendanceActivity.this, "Attendance Marked: " + status, Toast.LENGTH_SHORT).show();
//            }).addOnFailureListener(e -> {
//                Toast.makeText(AttendanceActivity.this, "Failed to mark attendance", Toast.LENGTH_SHORT).show();
//            });
//        });
//    }
//
//    private boolean compareImagesORB(Bitmap img1, Bitmap img2) {
//        Mat mat1 = new Mat();
//        Mat mat2 = new Mat();
//
//        // Convert Bitmap to Mat
//        org.opencv.android.Utils.bitmapToMat(img1, mat1);
//        org.opencv.android.Utils.bitmapToMat(img2, mat2);
//
//        // Convert to grayscale
//        Imgproc.cvtColor(mat1, mat1, Imgproc.COLOR_RGBA2GRAY);
//        Imgproc.cvtColor(mat2, mat2, Imgproc.COLOR_RGBA2GRAY);
//
//        // Resize for consistency
//        Size newSize = new Size(300, 300);
//        Imgproc.resize(mat1, mat1, newSize);
//        Imgproc.resize(mat2, mat2, newSize);
//
//        // 🔹 Step 1: ORB Feature Matching
//        ORB orb = ORB.create(1000);
//        MatOfKeyPoint keypoints1 = new MatOfKeyPoint();
//        MatOfKeyPoint keypoints2 = new MatOfKeyPoint();
//        Mat descriptors1 = new Mat();
//        Mat descriptors2 = new Mat();
//
//        orb.detectAndCompute(mat1, new Mat(), keypoints1, descriptors1);
//        orb.detectAndCompute(mat2, new Mat(), keypoints2, descriptors2);
//
//        // If descriptors are empty, return false
//        if (descriptors1.empty() || descriptors2.empty()) {
//            Log.e("FaceMatch", "Descriptors are empty! Image may not have enough keypoints.");
//            return false;
//        }
//
//        DescriptorMatcher matcher = DescriptorMatcher.create(DescriptorMatcher.BRUTEFORCE_HAMMING);
//        List<MatOfDMatch> knnMatches = new ArrayList<>();
//        matcher.knnMatch(descriptors1, descriptors2, knnMatches, 2);
//
//        // Lowe's Ratio Test to filter good matches
//        float ratioThreshold = 0.8f; // Looser threshold for real-world conditions
//        int goodMatches = 0;
//        for (MatOfDMatch matOfDMatch : knnMatches) {
//            DMatch[] matches = matOfDMatch.toArray();
//            if (matches.length >= 2 && matches[0].distance < ratioThreshold * matches[1].distance) {
//                goodMatches++;
//            }
//        }
//
//        Log.d("FaceMatch", "Good Matches: " + goodMatches);
//
//        // 🔹 Step 2: Histogram Comparison for Backup Matching
//        double similarityScore = compareHistograms(mat1, mat2);
//        Log.d("FaceMatch", "Histogram Similarity Score: " + similarityScore);
//
//        // ✅ Improved Condition: ORB Matching + Histogram Similarity
//        return goodMatches > 25 || similarityScore > 0.75;
//    }
//
//    private double compareHistograms(Mat img1, Mat img2) {
//        Mat hist1 = new Mat();
//        Mat hist2 = new Mat();
//        // Compute histograms
//        Imgproc.calcHist(Collections.singletonList(img1), new MatOfInt(0), new Mat(), hist1, new MatOfInt(256), new MatOfFloat(0, 256));
//        Imgproc.calcHist(Collections.singletonList(img2), new MatOfInt(0), new Mat(), hist2, new MatOfInt(256), new MatOfFloat(0, 256));
//        // Normalize histograms
//        Core.normalize(hist1, hist1, 0, 1, Core.NORM_MINMAX);
//        Core.normalize(hist2, hist2, 0, 1, Core.NORM_MINMAX);
//        return Imgproc.compareHist(hist1, hist2, Imgproc.HISTCMP_CORREL);
//    }
//
//    private Bitmap getBitmapFromStorage(String path) {
//        // Here, implement logic to convert base64 string or URL to Bitmap
//        // For now, returning null (you will need to implement this based on how photos are stored)
//        return null;
//    }
//}
//
