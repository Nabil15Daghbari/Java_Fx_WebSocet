//package com.example.projetjavafx.root.messagerie.WebSockets;
//
//import com.example.projetjavafx.root.auth.SessionManager;
//
//public class SessionMg {
//    private static SessionManager instance;
//    private int currentUserId = -1; // ID de l'utilisateur connecté
//    private String currentUserEmail; // Email de l'utilisateur connecté (optionnel)
//    private boolean isUserLoggedIn = false;
//
//    // Constructeur privé pour empêcher l'instanciation directe
//    private SessionMg() {}
//
//    // Méthode pour obtenir l'instance unique de SessionManager
//    public static SessionManager getInstance() {
//        if (instance == null) {
//            instance = new SessionManager();
//        }
//        return instance;
//    }
//
//    // Méthode pour définir l'utilisateur connecté
//    public void setCurrentUser(int userId, String email) {
//        this.currentUserId = userId;
//        this.currentUserEmail = email;
//        this.isUserLoggedIn = true;
//    }
//
//    // Méthode pour obtenir l'ID de l'utilisateur connecté
//    public int getCurrentUserId() {
//        return currentUserId;
//    }
//
//    // Vérifier si un utilisateur est connecté
//    public boolean isUserLoggedIn() {
//        return isUserLoggedIn;
//    }
//
//    // Déconnexion de l'utilisateur
//    public void logout() {
//        this.currentUserId = -1;
//        this.currentUserEmail = null;
//        this.isUserLoggedIn = false;
//    }
//}
