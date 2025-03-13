package com.example.projectsemb;

import android.media.Image;

import java.util.List;

public class Event {
    String id;
    String name;
    String location;
    String date;
    String time;
    String description;
    List<String> participantIds;
    List<EquipmentItem> equipmentList;

    public Event() {

    }

    public Event(String id, String name, String location, String date, String time, String description, List<String> participantIds, List<EquipmentItem> equipmentList) {
        this.id = id;
        this.name = name;
        this.location = location;
        this.date = date;
        this.time = time;
        this.description = description;
        this.participantIds = participantIds;
        this.equipmentList = equipmentList;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<String> getParticipantIds() {
        return participantIds;
    }

    public void setParticipantIds(List<String> participantIds) {
        this.participantIds = participantIds;
    }

    public List<EquipmentItem> getEquipmentList() {
        return equipmentList;
    }

    public void setEquipmentList(List<EquipmentItem> equipmentList) {
        this.equipmentList = equipmentList;
    }
}
