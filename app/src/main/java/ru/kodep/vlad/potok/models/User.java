package ru.kodep.vlad.potok.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by vlad on 22.02.18
 */

public class User {


    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("phones")
    @Expose
    private List<String> phones;
    @SerializedName("avatar")
    @Expose
    private String avatar;
    @SerializedName("applicant_url")
    @Expose
    private String applicantUrl;
    @SerializedName("created_at")
    @Expose
    private String createdAt;
    @SerializedName("updated_at")
    @Expose
    private String updatedAt;

    public User(Integer id, String name, String title, List<String> phones, String avatar, String applicantUrl, String createdAt, String updatedAt){
        this.id = id;
        this.name = name;
        this.title = title;
        this.phones = phones;
        this.avatar = avatar;
        this.applicantUrl = applicantUrl;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<String> getPhones() {
        return phones;
    }

    public void setPhones(List<String> phones) {
        this.phones = phones;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getApplicantUrl() {
        return applicantUrl;
    }

    public void setApplicantUrl(String applicantUrl) {
        this.applicantUrl = applicantUrl;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

}