package com.creation.android.receivenoti;

public class MyNotificationDataModel {
    String message;

    public MyNotificationDataModel(String message) {
        this.message = message;
    }

    public MyNotificationDataModel() {
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
