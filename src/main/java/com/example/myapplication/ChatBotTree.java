package com.example.myapplication;
public class ChatBotTree {
    TreeNode root;

    public ChatBotTree() {
        root = new TreeNode("hi", "Hello student! How can I assist you today?\nWelcome to our hostel 😊\n\nChoose:\n1. Mess Menu\n2. Attendance\n3. Announcement\n4. About\n5. Logout");

        root.addChild(new TreeNode("1", "Navigating to Mess Menu..."));
        root.addChild(new TreeNode("2", "Navigating to Attendance..."));
        root.addChild(new TreeNode("3", "Navigating to Announcements..."));
        root.addChild(new TreeNode("4", "Navigating to About..."));
        root.addChild(new TreeNode("5", "Logging you out..."));
    }

    public TreeNode getRoot() {
        return root;
    }
}
