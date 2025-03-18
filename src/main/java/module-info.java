module com.example.projetjavafx {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires com.almasb.fxgl.all;
    requires java.sql;
    requires mysql.connector.j;
    requires java.desktop;
    requires jdk.jfr;
    requires okhttp3;
    requires org.json;
    requires flexmark;
    requires java.mail;
    requires tomcat.embed.websocket;
    requires org.java_websocket;
    requires com.google.protobuf;
    requires spring.boot.autoconfigure;
    requires logback.core;
    requires log4j.over.slf4j;
    requires com.google.gson;
    // Root package
    exports com.example.projetjavafx.root;
    opens com.example.projetjavafx.root to javafx.fxml;
    opens com.example.projetjavafx.root.jobFeed to javafx.fxml;



    // Controller package accesses
    opens com.example.projetjavafx.root.auth to javafx.fxml;
    opens com.example.projetjavafx.root.events to javafx.fxml;
    opens com.example.projetjavafx.root.explore to javafx.fxml;
    opens com.example.projetjavafx.root.organizer to javafx.fxml;
    opens com.example.projetjavafx.root.group to javafx.fxml;
    opens com.example.projetjavafx.root.profile to javafx.fxml;
    opens com.example.projetjavafx.root.social to javafx.fxml;
    opens com.example.projetjavafx.root.messagerie to javafx.fxml;

    // mesg


    // Ouvrir les packages nécessaires pour GSON
    opens com.example.projetjavafx.root.mesg.model to com.google.gson;
    opens com.example.projetjavafx.root.mesg.util to com.google.gson;
    opens com.example.projetjavafx.root.mesg.client to javafx.fxml;
    exports com.example.projetjavafx.root.mesg.model;
    exports com.example.projetjavafx.root.mesg.client;
    exports com.example.projetjavafx.root.mesg.server;




    // CSS directories
    opens com.example.projetjavafx.auth.css to javafx.fxml;
//    opens com.example.projetjavafx.events.css to javafx.fxml;
    opens com.example.projetjavafx.explore.css to javafx.fxml;
    opens com.example.projetjavafx.group.css to javafx.fxml;
    opens com.example.projetjavafx.organizer.css to javafx.fxml;
    opens com.example.projetjavafx.profile.css to javafx.fxml;
    opens com.example.projetjavafx.root.css to javafx.fxml;
    opens com.example.projetjavafx.social.css to javafx.fxml;
    opens com.example.projetjavafx.messagerie.userchatroom.css to javafx.fxml;
    opens com.example.projetjavafx.messagerie.discussion.css to javafx.fxml;
    opens com.example.projetjavafx.messagerie.groupchatroom.css to javafx.fxml;
    opens com.example.projetjavafx.root.messagerie.controllers to javafx.fxml;
    opens com.example.projetjavafx.root.messagerie.repository to javafx.fxml;



    // Explicit exports for public controller classes
    exports com.example.projetjavafx.root.auth;
    exports com.example.projetjavafx.root.chatbot;
    exports com.example.projetjavafx.root.events;
    exports com.example.projetjavafx.root.explore;
    exports com.example.projetjavafx.root.organizer;
    exports com.example.projetjavafx.root.group;
    exports com.example.projetjavafx.root.profile;
    exports com.example.projetjavafx.root.social;
    exports com.example.projetjavafx.root.messagerie;
    exports com.example.projetjavafx.root.messagerie.WebSockets;
    exports com.example.projetjavafx.root.messagerie.models;
    exports com.example.projetjavafx.root.messagerie.controllers;
    exports com.example.projetjavafx.root.messagerie.repository;

    // Add to exports section
    exports com.example.projetjavafx.root.jobFeed;
    exports com.example.projetjavafx.root.jobApplications;
    opens com.example.projetjavafx.root.jobApplications to javafx.fxml;
    opens com.example.projetjavafx.root.chatbot to javafx.fxml;




}