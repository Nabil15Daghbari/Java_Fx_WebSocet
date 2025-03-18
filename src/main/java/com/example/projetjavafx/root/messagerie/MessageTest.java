package com.example.projetjavafx.root.messagerie;

import com.example.projetjavafx.root.messagerie.models.Message;
import com.example.projetjavafx.root.messagerie.repository.MessageDB;

import java.sql.SQLException;
import java.util.List;

public class MessageTest {
    public static void main(String[] args) {
        MessageDB messageDB = new MessageDB();

        int senderId = 1;
        int receiverId = 9;
        String messageContent = "ok bonne nuit ";

        boolean success = messageDB.sendMessage(senderId, receiverId, messageContent);
        if (success) {
            System.out.println("✅ Message envoyé de Oubid à Ayaab !");
        } else {
            System.out.println("❌ Échec de l'envoi du message.");
        }

        // 📩 Récupérer les messages entre Oubid et Ayaab
        System.out.println("\n📩 Récupération des messages entre Oubid et Ayaab...");
        try {
            List<Message> messages = messageDB.getMessagesBetweenUsers(senderId, receiverId);
            for (Message msg : messages) {
                System.out.println(msg.getSenderId() + " → " + msg.getReceiverId() + " : " + msg.getContent());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}