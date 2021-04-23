package com.example.myapplication;

import com.google.firebase.firestore.Exclude;

import java.lang.reflect.Array;
import java.util.ArrayList;

/*Model class*/
public class Model {
    @Exclude
    String docId;
    String fullName;
    String email;
    String title;
    String description;
    Integer signature;
    String date;
    ArrayList<String> signs = new ArrayList<String>();

    private Model() {
    }

    public Model(String fullName, String email, String petitionTitle, String petitionDescription, String date, Integer signature) {
        this.fullName = fullName;
        this.email = email;
        this.date = date;
        this.description = petitionDescription;
        this.signature = signature;
        this.title = petitionTitle;
        this.signs.add(email);
    }

    @Exclude
    public String getDocId() {
        return docId;
    }

    public void setDocId(String docId) {
        this.docId = docId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Integer getSignature() {
        return signature;
    }

    public void setSignature(Integer signature) {
        this.signature = signature;
    }

    public ArrayList<String> getSigns() {
        return signs;
    }
}
