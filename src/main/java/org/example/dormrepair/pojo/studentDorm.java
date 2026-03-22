package org.example.dormrepair.pojo;

public class studentDorm {
    private String studentId;
    private String dormArea;
    private String dormBuilding;
    private String dormRoom;
    private String studentName;

    public studentDorm() {
    }

    public studentDorm(String studentId, String dormArea, String dormBuilding, String dormRoom, String studentName) {
        this.studentId = studentId;
        this.dormArea = dormArea;
        this.dormBuilding = dormBuilding;
        this.dormRoom = dormRoom;
        this.studentName = studentName;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getDormArea() {
        return dormArea;
    }

    public void setDormArea(String dormArea) {
        this.dormArea = dormArea;
    }

    public String getDormBuilding() {
        return dormBuilding;
    }

    public void setDormBuilding(String dormBuilding) {
        this.dormBuilding = dormBuilding;
    }

    public String getDormRoom() {
        return dormRoom;
    }

    public void setDormRoom(String dormRoom) {
        this.dormRoom = dormRoom;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }
}
