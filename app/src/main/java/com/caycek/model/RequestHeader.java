package com.caycek.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by ftoptas on 10/11/16.
 */

public class RequestHeader {

    @SerializedName("Content-Type")
    private String mContentType;

    @SerializedName("Authorization")
    private String mAuth;

    public RequestHeader(String contentType, String auth) {
        mContentType = contentType;
        mAuth = auth;
    }
}
