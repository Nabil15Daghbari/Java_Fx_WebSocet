package com.example.projetjavafx.root.social;

//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//


public enum Reactions {
    NON(0, "Like", "#606266", "/com/example/projetjavafx/social/img/ic_like_outline.png"),
    LIKE(1, "Like", "#056BE1", "/com/example/projetjavafx/social/img/ic_like.png"),
    LOVE(2, "Love", "#E12C4A", "/com/example/projetjavafx/social/img/ic_love_.png"),
    CARE(3, "Care", "#EAA823", "/com/example/projetjavafx/social/img/ic_care.png"),
    HAHA(4, "Haha", "#EAA823", "/com/example/projetjavafx/social/img/ic_haha.png"),
    WOW(5, "Wow", "#EAA823", "/com/example/projetjavafx/social/img/ic_wow.png"),
    SAD(6, "Sad", "#EAA823", "/com/example/projetjavafx/social/img/ic_sad.png"),
    ANGRY(7, "Angry", "#DD6B0E", "/com/example/projetjavafx/social/img/ic_angry.png");

    private int id;
    private String name;
    private String color;
    private String imgSrc;

    private Reactions(int id, String name, String color, String imgSrc) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.imgSrc = imgSrc;
    }

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getColor() {
        return this.color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getImgSrc() {
        return this.imgSrc;
    }

    public void setImgSrc(String imgSrc) {
        this.imgSrc = imgSrc;
    }
}

