package com.caycek.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by ftoptas on 10/11/16.
 */

public class PushRequest {

    @SerializedName("to")
    private String mTo;

    @SerializedName("data")
    private MessageData mMessageData;

    public PushRequest(String to, MessageData messageData) {
        mTo = to;
        mMessageData = messageData;
    }
}
