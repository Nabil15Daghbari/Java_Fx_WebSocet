package com.example.projetjavafx.root.social;

//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//


public enum PostAudience {
    PUBLIC(0, "Public", "/com/example/projetjavafx/social/img/ic_public.png"),
    FRIENDS(1, "Friends", "/com/example/projetjavafx/social/img/ic_friend.png");

    private int id;
    private String name;
    private String imgSrc;

    private PostAudience(int id, String name, String imgSrc) {
        this.id = id;
        this.name = name;
        this.imgSrc = imgSrc;
    }

    public int getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public String getImgSrc() {
        return this.imgSrc;
    }
}

