package com.example.projetjavafx.root.messagerie.controllers;

import com.example.projetjavafx.root.messagerie.repository.NotificationsDB;
import com.example.projetjavafx.root.auth.SessionManager;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;

import java.util.List;

public class NotificationsController {
    @FXML
    private ListView<String> notificationsList;

    @FXML
    private Button markAllAsRead;

    private final NotificationsDB notificationsDB = new NotificationsDB();
    private int currentUserId;

    @FXML
    public void initialize() {
        // Vérifie si un utilisateur est connecté avant de charger les notifications
        if (SessionManager.getInstance().isUserLoggedIn()) {
            this.currentUserId = SessionManager.getInstance().getCurrentUserId();
            loadNotifications();
        } else {
            System.out.println("⚠ Aucun utilisateur connecté. Impossible de récupérer les notifications.");
        }
    }

    private void loadNotifications() {
        if (currentUserId != -1) {
            List<String> notifications = notificationsDB.getUserNotifications(currentUserId);
            notificationsList.getItems().setAll(notifications);
        } else {
            System.out.println("⚠ Impossible de charger les notifications : utilisateur non connecté.");
        }
    }

    @FXML
    private void markAllAsRead() {
        if (currentUserId != -1) {
            notificationsDB.markNotificationsAsRead(currentUserId, -1); // -1 pour tous les messages reçus
            loadNotifications(); // Rafraîchir la liste après la mise à jour
            System.out.println("✅ Toutes les notifications ont été marquées comme lues !");
        } else {
            System.out.println("⚠ Impossible de marquer comme lu : utilisateur non connecté.");
        }
    }
}
