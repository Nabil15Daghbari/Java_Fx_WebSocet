//package com.example.projetjavafx.root.messagerie.WebSockets;
//
//import com.example.projetjavafx.root.messagerie.controllers.GroupChatroomController;
//import com.example.projetjavafx.root.messagerie.models.Message;
//import javafx.scene.control.ListCell;
//import javafx.scene.layout.HBox;
//import javafx.scene.layout.VBox;
//import javafx.scene.paint.Color;
//import javafx.scene.text.Text;
//import javafx.scene.control.Label;
//import java.text.SimpleDateFormat;
//import java.util.Date;
//
//public class MessageCell extends ListCell<Message> {
//    private HBox hbox = new HBox();
//    private Label messageLabel = new Label();
//    private Label timeLabel = new Label();
//    private VBox container = new VBox();
//
//    private SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm"); // Format HH:mm
//
//    public MessageCell(int currentUserId) {
//        super();
//        messageLabel.setWrapText(true);
//        messageLabel.setMaxWidth(300); // Limite la largeur du message
//
//        timeLabel.setStyle("-fx-font-size: 10px; -fx-text-fill: #aaaaaa;"); // Style de l'heure
//
//        container.getChildren().addAll(messageLabel, timeLabel);
//        hbox.getChildren().add(container);
//
//        setPrefWidth(0); // Ajustement auto
//
//        setStyle("-fx-padding: 5px; -fx-background-radius: 10px;");
//    }
//
//    @Override
//    protected void updateItem(Message message, boolean empty) {
//        super.updateItem(message, empty);
//
//        if (empty || message == null) {
//            setGraphic(null);
//        } else {
//            messageLabel.setText(message.getSenderUsername() + ": " + message.getContent());
//
//            // ✅ Convertir le timestamp en HH:mm
//            String formattedTime = timeFormat.format(new Date(message.getTimestamp()));
//            timeLabel.setText(formattedTime);
//
//            if (message.getSenderId() == getCurrentUserId()) {
//                // ✅ Message envoyé → Fond bleu
//                container.setStyle("-fx-background-color: #4A90E2; -fx-text-fill: white; -fx-padding: 10px; -fx-background-radius: 10px;");
//                hbox.setStyle("-fx-alignment: center-right;");
//            } else {
//                // ✅ Message reçu → Fond vert
//                container.setStyle("-fx-background-color: #7ED321; -fx-text-fill: white; -fx-padding: 10px; -fx-background-radius: 10px;");
//                hbox.setStyle("-fx-alignment: center-left;");
//            }
//
//            setGraphic(hbox);
//        }
//    }
//
//    private int getCurrentUserId() {
//        return ((GroupChatroomController) getListView().getScene().getUserData()).getUserId();
//    }
//}
