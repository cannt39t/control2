package com.example.control2.chat.view;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.example.control2.chat.ChatApplication;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class ChatView extends BaseView {

    public static String apiKey = "6faded995f07a67bd6431a5176bb4640";

    static ObjectMapper mapper = new ObjectMapper();

    private TextArea input;

    private TextArea cityWeather;
    private TextArea conversation;
    private AnchorPane pane = null;

    private Button getWeather;
    private final ChatApplication application = BaseView.getChatApplication();
    private final EventHandler onKeyEvent = new EventHandler<KeyEvent>() {

        @Override
        public void handle(KeyEvent event) {
            if (event.getCode() == KeyCode.ENTER) {
                String sender = application.getUserConfig().getName();
                String message = input.getText() + "\n";
                application.getChatClient().sendMessage("\n" + sender + " " + message + "\n");
                conversation.appendText("you: " + message + "\n");

                input.clear();
                event.consume();
            }
        }
    };

    public ChatView() throws Exception {
    }


    @Override
    public Parent getView() {
        if (pane == null) {
            this.createView();
        }

        return pane;
    }

    private void createView() {
        pane = new AnchorPane();

        conversation = new TextArea();
        conversation.setEditable(false);
        conversation.setWrapText(true);


        AnchorPane.setTopAnchor(conversation, 10.0);
        AnchorPane.setLeftAnchor(conversation, 10.0);
        AnchorPane.setRightAnchor(conversation, 10.0);

        input = new TextArea();
        input.setMaxHeight(70);

        AnchorPane.setBottomAnchor(input, 10.0);
        AnchorPane.setLeftAnchor(input, 10.0);
        AnchorPane.setRightAnchor(input, 100.0);

        cityWeather = new TextArea();
        cityWeather.setMaxSize(80, 20);
        cityWeather.setMaxHeight(30);

        getWeather = new Button("get weather");

        EventHandler<ActionEvent> event = new EventHandler<ActionEvent>() {
            public void handle(ActionEvent e)
            {
                conversation.appendText("\nWeather in " + cityWeather.getText() + " is " + getTempurature(cityWeather.getText()) + "°C");
            }
        };

        // when button is pressed
        getWeather.setOnAction(event);

        AnchorPane.setBottomAnchor(getWeather, 10.0);
        AnchorPane.setLeftAnchor(input, 10.0);
        AnchorPane.setRightAnchor(getWeather, 10.0);

        AnchorPane.setBottomAnchor(cityWeather, 40.0);
        AnchorPane.setLeftAnchor(input, 10.0);
        AnchorPane.setRightAnchor(cityWeather, 10.0);

        input.addEventHandler(KeyEvent.KEY_PRESSED, onKeyEvent);
        pane.getChildren().addAll(input, conversation, getWeather, cityWeather);
    }

    public void appendMessageToConversation(String message) {
        if (message != null) conversation.appendText(message);
    }

    private String getTempurature(String city) {
        try {
            URL url = new URL("https://api.openweathermap.org/data/2.5/weather?q=" + city + "&appid=" + apiKey);
            // URL url = new URL("https://api.openweathermap.org/data/2.5/weather?q=Kazan&appid=7f279da3bbdd7b43aacff0d975a78e5d");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            connection.setRequestMethod("GET");
            connection.setRequestProperty("Content-Type", "application/json");

            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);

            try (BufferedReader reader =
                         new BufferedReader(
                                 new InputStreamReader(connection.getInputStream())
                         )) {
                StringBuilder content = new StringBuilder();
                String input;
                while ((input = reader.readLine()) != null) {
                    content.append(input);
                }

                // convert JSON string to Map
                JsonNode node = mapper.readTree(String.valueOf(content));
//                HashMap<String,Object> o = mapper.readValue(node, Map.class);
                String res = String.valueOf(node.get("main").get("temp"));
                return String.valueOf(Double.parseDouble(res) - 273);
            }

        } catch (IOException e) {
            return "Could not find the city";
        }
    }
}
