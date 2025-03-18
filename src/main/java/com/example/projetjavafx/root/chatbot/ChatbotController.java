package com.example.projetjavafx.root.chatbot;

import com.example.projetjavafx.root.auth.SessionManager;
import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import okhttp3.*;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URLDecoder;

public class ChatbotController {
    @FXML
    private VBox rootContainer;
    @FXML
    private TextField inputField;
    @FXML
    private Button sendButton;
    // Remplacement de TextArea par WebView pour afficher du HTML
    @FXML
    private WebView responseWebView;

    private final String ollamaApiUrl = "http://127.0.0.1:11434/api/generate";
    private boolean forceStop = false;

    @FXML
    public void initialize() {
        sendButton.setOnAction(event -> sendRequestToOllama());
    }

    // Called when the stop button is clicked in the UI.
    @FXML
    public void stopChatbot() {
        forceStop = true;
    }

    private void sendRequestToOllama() {
        // Reset the stop flag for a new request.
        forceStop = false;

        String userInput = inputField.getText();
        if (userInput.isEmpty()) {
            javafx.application.Platform.runLater(() ->
                    responseWebView.getEngine().loadContent("<p>Veuillez entrer une requête.</p>")
            );
            return;
        }
        // Affichage initial dans le WebView
        javafx.application.Platform.runLater(() ->
                responseWebView.getEngine().loadContent("<p>En cours...</p>")
        );

        String guidance = "Tu es le chatbot officiel de ConnectSphere, une application dédiée à l'organisation et la gestion d'événements. Ta mission est d'aider les utilisateurs en leur présentant clairement les fonctionnalités de l'application et en répondant à leurs questions concernant l'organisation d'événements. Tu dois :" +
                "- Introduire les principales fonctionnalités de ConnectSphere (consultation des évènements, inscription aux événements, recherche d'événements, création d'événements, gestion de profil, etc.)." +
                "- Fournir des réponses claires, précises et amicales." +
                "- Guider l'utilisateur pour trouver les informations dont il a besoin sur l'organisation des événements (localisation, horaires, modalités d'inscription, etc.)." +
                "- Encourager l'utilisateur à explorer l'application pour découvrir tous les outils disponibles pour faciliter la participation aux événements." +
                "Reste toujours courtois et utile, et adapte tes réponses selon les questions spécifiques posées par les utilisateurs, et n'oublie pas de donner des reponses très bref et précises";

        OkHttpClient client = new OkHttpClient();
        RequestBody body = RequestBody.create(MediaType.parse("application/json"),
                "{\"model\": \"llama3.2\", \"prompt\": \"" + userInput + guidance + "\"}");
        Request request = new Request.Builder()
                .url(ollamaApiUrl)
                .addHeader("Content-Type", "application/json")
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                javafx.application.Platform.runLater(() ->
                        responseWebView.getEngine().loadContent("<p>Erreur de connexion à l'API Ollama.</p>")
                );
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.body() != null) {
                    StringBuilder fullResponse = new StringBuilder();
                    String line;

                    try (BufferedReader reader = new BufferedReader(new InputStreamReader(response.body().byteStream()))) {
                        while ((line = reader.readLine()) != null) {
                            // Check if the user has requested to stop the chatbot.
                            if (forceStop) {
////                                javafx.application.Platform.runLater(() ->
////                                        responseWebView.getEngine().loadContent("<p>Chatbot stopped.</p>")
//                                );
                                break;
                            }

                            System.out.println("Réponse brute de l'API : " + line);

                            try {
                                JSONObject jsonResponse = new JSONObject(line);
                                if (jsonResponse.has("response")) {
                                    String partialResponse = jsonResponse.getString("response");

                                    // Filtrer les segments de raisonnement
                                    if (partialResponse.contains("\u003cthink\u003e") || partialResponse.contains("\u003c/think\u003e")) {
                                        continue;
                                    }

                                    // Décoder les caractères Unicode
                                    partialResponse = URLDecoder.decode(partialResponse, "UTF-8");

                                    // Accumuler la réponse
                                    fullResponse.append(partialResponse);

                                    // Conversion du Markdown en HTML avec Flexmark
                                    String finalResponse = fullResponse.toString().trim();
                                    Parser parser = Parser.builder().build();
                                    HtmlRenderer renderer = HtmlRenderer.builder().build();
                                    String htmlContent = renderer.render(parser.parse(finalResponse));

                                    // Injection d'un style CSS pour personnaliser l'affichage
                                    String css = "<style>"
                                            + "body { font-family: 'Arial'; padding: 10px; }"
                                            + "strong { font-weight: bold; }"
                                            + "ol, ul { margin-left: 20px; }"
                                            + "</style>";
                                    String finalHtml = "<html><head>" + css + "</head><body>" + htmlContent + "</body></html>";

                                    // Mise à jour en temps réel du WebView
                                    javafx.application.Platform.runLater(() ->
                                            responseWebView.getEngine().loadContent(finalHtml)
                                    );
                                }
                                // Arrêter la lecture si la réponse est complète
                                if (jsonResponse.optBoolean("done", false)) {
                                    break;
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                                javafx.application.Platform.runLater(() ->
                                        responseWebView.getEngine().loadContent("<p>Format de réponse JSON invalide.</p>")
                                );
                            }
                        }
                    }
                } else {
                    javafx.application.Platform.runLater(() ->
                            responseWebView.getEngine().loadContent("<p>Réponse invalide de l'API.</p>")
                    );
                }
            }
        });
    }

    private void loadView(String fxmlPath, ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();
            Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    protected void onOrganizerButtonClick(ActionEvent event) {
        if (SessionManager.getInstance().getCurrentUserId() == -1) {
            loadView("/com/example/projetjavafx/auth/login-view.fxml", event);
        } else {
            loadView("/com/example/projetjavafx/organizer/organizer-view.fxml", event);
        }
    }

    @FXML
    protected void onEventsClick(ActionEvent event) {
        loadView("/com/example/projetjavafx/events/events-view.fxml", event);
    }

    @FXML
    protected void onProfileClick(ActionEvent event) {
        if (SessionManager.getInstance().getCurrentUserId() == -1) {
            loadView("/com/example/projetjavafx/auth/login-view.fxml", event);
        } else {
            loadView("/com/example/projetjavafx/profile/profile-view.fxml", event);
        }
    }

    @FXML
    protected void onLoginClick(ActionEvent event) {
        loadView("/com/example/projetjavafx/auth/login-view.fxml", event);
    }

    @FXML
    protected void onGroupButtonClick(ActionEvent event) {
        if (SessionManager.getInstance().getCurrentUserId() == -1) {
            loadView("/com/example/projetjavafx/auth/login-view.fxml", event);
        } else {
            loadView("/com/example/projetjavafx/group/group-profile-view.fxml", event);
        }
    }

    @FXML
    protected void onRegisterClick(ActionEvent event) {
        loadView("/com/example/projetjavafx/auth/register-view.fxml", event);
    }

    @FXML
    protected void onCreateJobClick(ActionEvent event) {
        if (SessionManager.getInstance().getCurrentUserId() == -1) {
            loadView("/com/example/projetjavafx/auth/login-view.fxml", event);
        } else {
            loadView("/com/example/projetjavafx/organizer/create-job-offer-view.fxml", event);
        }
    }

    public void onDashboardClick(ActionEvent event) {
        loadView("/com/example/projetjavafx/organizer/organizer-view.fxml", event);
    }

    @FXML
    protected void onAnalyticsClick(ActionEvent event) {
        if (SessionManager.getInstance().getCurrentUserId() == -1) {
            loadView("/com/example/projetjavafx/auth/login-view.fxml", event);
        } else {
            loadView("/com/example/projetjavafx/organizer/analytics-view.fxml", event);
        }
    }

    @FXML
    protected void onJobFeedClick(ActionEvent event) {
        if (SessionManager.getInstance().getCurrentUserId() == -1) {
            loadView("/com/example/projetjavafx/auth/login-view.fxml", event);
        } else {
            loadView("/com/example/projetjavafx/jobfeed/job-feed-view.fxml", event);
        }
    }

    @FXML
    protected void onSocialButtonClick(ActionEvent event) {
        loadView("/com/example/projetjavafx/social/Feed.fxml", event);
    }

    public void onChatbotClick(ActionEvent event) {
        loadView("/com/example/projetjavafx/chatbot/chatbot.fxml", event);
    }

    public void handleHomeButton(ActionEvent event) {
        loadView("/com/example/projetjavafx/root/root-view.fxml" ,event);
    }
}
