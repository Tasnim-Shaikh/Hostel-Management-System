package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;



import java.util.ArrayList;
import java.util.List;

public class ChatBotActivity extends AppCompatActivity {
    RecyclerView chatRecyclerView;
    EditText userInputEditText;
    Button sendButton;
    ChatAdapter adapter;
    List<String> chatHistory;
    ChatBotTree botTree;
    TreeNode currentNode;

    String currentUserName = ""; // set from previous screen if needed

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatbot);

        chatRecyclerView = findViewById(R.id.chatRecyclerView);
        userInputEditText = findViewById(R.id.userInputEditText);
        sendButton = findViewById(R.id.sendButton);

        chatHistory = new ArrayList<>();
        adapter = new ChatAdapter(chatHistory);
        chatRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        chatRecyclerView.setAdapter(adapter);

        // Get username if passed from login or dashboard
        currentUserName = getIntent().getStringExtra("username");

        // Initialize chatbot
        botTree = new ChatBotTree();
        currentNode = botTree.getRoot();

        // Initial greeting message
        chatHistory.add("Bot: " + currentNode.response);
        adapter.notifyItemInserted(chatHistory.size() - 1);

        sendButton.setOnClickListener(v -> {
            String input = userInputEditText.getText().toString().trim();
            if (!input.isEmpty()) {
                chatHistory.add("You: " + input);
                adapter.notifyItemInserted(chatHistory.size() - 1);

                TreeNode next = currentNode.findChild(input);
                if (next != null) {
                    chatHistory.add("Bot: " + next.response);
                    adapter.notifyItemInserted(chatHistory.size() - 1);

                    navigateIfRequired(input); // use user's raw input

                    // If it's a leaf, reset to root so user can choose again
                    if (next.children.isEmpty()) {
                        currentNode = botTree.getRoot();
                    } else {
                        currentNode = next;
                    }
                } else {
                    chatHistory.add("Bot: Invalid option. Try again.");
                    adapter.notifyItemInserted(chatHistory.size() - 1);
                }

                userInputEditText.setText("");
            }
        });
    }

    private void navigateIfRequired(String input) {
        switch (input) {
            case "1":
                Intent messIntent = new Intent(this, ViewMenuActivity.class);
                messIntent.putExtra("username", currentUserName); // Pass username if needed
                startActivity(messIntent);
                break;
            case "2":
                Intent attendanceIntent = new Intent(this, AttendanceActivity.class);
                attendanceIntent.putExtra("username", currentUserName);
                startActivity(attendanceIntent);
                break;
            case "3":
                startActivity(new Intent(this, ShowAnnoncements.class));
                break;
            case "4":
                startActivity(new Intent(this, About.class));
                break;
            case "5":
                Toast.makeText(this, "Logged out", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(this, LoginActivity.class));
                finish();
                break;
        }
    }
}
