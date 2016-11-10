package com.caycek.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by ftoptas on 10/11/16.
 */

public class MessageData {

    @SerializedName("message")
    private String mMessage;

    public MessageData(String message) {
        mMessage = message;
    }
}
