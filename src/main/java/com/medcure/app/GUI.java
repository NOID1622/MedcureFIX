package com.medcure.app;

import com.medcure.utils.Session;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

public class GUI extends Application {
    private static Stage primaryStage;

    @Override
    public void start(Stage primaryStage) {
        try {
            // Initialize the JDBC driver
            try {
                Class.forName("org.sqlite.JDBC");
                System.out.println("SQLite JDBC driver loaded");
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
                System.err.println("Failed to load SQLite JDBC driver");
                return;
            }

            // Set the primary stage
            GUI.primaryStage = primaryStage;

            // Load the login view
            FXMLLoader loader = new FXMLLoader(GUI.class.getResource("login-view.fxml"));
            Parent root = loader.load();

            // Set the login stage
            LoginController loginController = loader.getController();
            loginController.setLoginStage(primaryStage);

            primaryStage.setTitle("MEDCURE - Login");
            primaryStage.setScene(new Scene(root));
            primaryStage.getIcons().add(new Image(GUI.class.getResourceAsStream("/com/medcure/app/icon.png")));
            primaryStage.setResizable(false);
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("An error occurred: " + e.getMessage());
        }
    }


    static Parent loadFXML(String fxml) throws IOException {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(GUI.class.getResource(fxml + ".fxml"));
            return fxmlLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Failed to load FXML file: " + fxml);
            throw e;
        }
    }

    public static void setRoot(String fxml, String title, boolean isResizable) throws IOException {
        try {
            primaryStage.getScene().setRoot(loadFXML(fxml));
            primaryStage.sizeToScene();
            primaryStage.setResizable(isResizable);
            if (title != null) {
                primaryStage.setTitle(title);
            }
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Failed to set root: " + fxml);
            throw e;
        }
    }

    public static void openViewWithModal(String fxml, String title, boolean isResizeable)
            throws IOException {
        Stage stage = new Stage();
        stage.setScene(new Scene(loadFXML(fxml)));
        stage.sizeToScene();
        stage.setTitle(title);
        stage.setResizable(isResizeable);
        stage.initOwner(primaryStage);
        stage.initModality(Modality.WINDOW_MODAL);
        stage.showAndWait();
    }
    public static void main(String[] args) {
        launch();
    }
}
