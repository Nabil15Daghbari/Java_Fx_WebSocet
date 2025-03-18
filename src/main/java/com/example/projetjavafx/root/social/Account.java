package com.example.projetjavafx.root.social;

public class Account {
    private String name;
    private String profileImg;
    private boolean isVerified;

    // ✅ Ajout du constructeur avec paramètres
    public Account(String name, String profileImg, boolean isVerified) {
        this.name = name;
        this.profileImg = profileImg;
        this.isVerified = isVerified;
    }

    // ✅ Constructeur par défaut (inutile si on n'en a pas besoin)
    public Account() {}

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProfileImg() {
        return this.profileImg;
    }

    public void setProfileImg(String profileImg) {
        this.profileImg = profileImg;
    }

    public boolean isVerified() {
        return this.isVerified;
    }

    public void setVerified(boolean verified) {
        this.isVerified = verified;
    }
}
