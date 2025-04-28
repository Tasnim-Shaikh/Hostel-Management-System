package com.example.myapplication;
public class RoomStudent {
    private String roomNumber;
    private String studentName;

    public RoomStudent() {}

    public RoomStudent(String roomNumber, String studentName) {
        this.roomNumber = roomNumber;
        this.studentName = studentName;
    }

    public String getRoomNumber() {
        return roomNumber;
    }

    public String getStudentName() {
        return studentName;
    }
}

