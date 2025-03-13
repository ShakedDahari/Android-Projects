package com.example.projectsemb;

public class EquipmentItem {
    String name;
    boolean isSelected;
    String userName;
    String userId;

    public EquipmentItem() { }

    public EquipmentItem(String name, boolean isSelected, String userName, String userId) {
        this.name = name;
        this.isSelected = isSelected;
        this.userName = userName;
        this.userId = userId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
