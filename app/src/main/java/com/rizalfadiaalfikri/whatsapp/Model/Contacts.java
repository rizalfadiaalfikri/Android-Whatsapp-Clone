package com.rizalfadiaalfikri.whatsapp.Model;

public class Contacts {
    String name, status, image, key;

    public Contacts(String name, String status, String image) {
        this.name = name;
        this.status = status;
        this.image = image;
    }

    public Contacts() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
