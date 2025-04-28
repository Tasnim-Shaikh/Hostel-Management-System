package com.example.myapplication;

import java.util.ArrayList;
import java.util.List;

public class TreeNode {
    String command;
    String response;
    List<TreeNode> children;

    public TreeNode(String command, String response) {
        this.command = command;
        this.response = response;
        this.children = new ArrayList<>();
    }

    public void addChild(TreeNode child) {
        children.add(child);
    }

    public TreeNode findChild(String input) {
        for (TreeNode child : children) {
            if (child.command.equals(input)) {
                return child;
            }
        }
        return null;
    }
}
