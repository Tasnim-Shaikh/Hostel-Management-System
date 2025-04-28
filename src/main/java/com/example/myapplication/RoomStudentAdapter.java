package com.example.myapplication;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class RoomStudentAdapter extends RecyclerView.Adapter<RoomStudentAdapter.ViewHolder> {

    private List<RoomStudent> roomStudentList;

    public RoomStudentAdapter(List<RoomStudent> list) {
        this.roomStudentList = list;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView roomNumber, studentName;

        public ViewHolder(View itemView) {
            super(itemView);
            roomNumber = itemView.findViewById(R.id.roomNumber);
            studentName = itemView.findViewById(R.id.studentName);
        }
    }

    @NonNull
    @Override
    public RoomStudentAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.room_student_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RoomStudentAdapter.ViewHolder holder, int position) {
        RoomStudent roomStudent = roomStudentList.get(position);
        holder.roomNumber.setText(roomStudent.getRoomNumber());
        holder.studentName.setText(roomStudent.getStudentName());
    }

    @Override
    public int getItemCount() {
        return roomStudentList.size();
    }
}
