package ru.kodep.vlad.testingreadnumberphone.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by vlad on 22.02.18
 */

public class AuthorizationModel {
    @SerializedName("token")
    @Expose
    private String token;
    @SerializedName("valid_to")
    @Expose
    private String validTo;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getValidTo() {
        return validTo;
    }

    public void setValidTo(String validTo) {
        this.validTo = validTo;
    }
}
