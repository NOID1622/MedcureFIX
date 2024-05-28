package com.medcure.app;

import com.medcure.utils.Session;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javafx.stage.Stage;
public class LoginController {

    @FXML
    private Hyperlink signUp;
    @FXML
    private Hyperlink lblForgot;
    @FXML
    private TextField txtUsername;
    @FXML
    private PasswordField txtPassword;
    private Stage loginStage;

    //    private String correctUsername = "admin";
//    private String correctPassword = "admin";
    private Alert alert;


    private boolean checkLogin(String username, String password) {
        return checkLoginFromDatabase(username, password);
    }


    @FXML
    private void initialize() {
        txtUsername.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                attemptLogin();
            }
        });

        txtPassword.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                attemptLogin();
            }
        });
    }

    @FXML
    private void forgotPasswordHyperlinkClicked() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Forget Password");
        alert.setHeaderText(null);
        alert.setContentText("Please contact support to reset your password.\n contact admin : sargio602@gmail.com");

        alert.showAndWait();
    }

    private boolean checkLoginFromDatabase(String username, String password) {
        String url = "jdbc:sqlite:mydatabase.db"; // Ganti dengan path ke file database Anda
        String sql = "SELECT * FROM UserCredentials WHERE (username = ? OR email = ?) AND password = ?";
        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setString(2, username);
            pstmt.setString(3, password);

            ResultSet rs = pstmt.executeQuery();

            return rs.next(); // Return true jika ada hasil (username dan password cocok), false jika tidak
        } catch (SQLException e) {
            showErrorAlert("Error checking login from database: " + e.getMessage());
            return false;
        }
    }

    private void showErrorAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private String getEmailFromDatabase(String username) {
        String email = "";
        String url = "jdbc:sqlite:mydatabase.db"; // Adjust this URL according to your database configuration
        String sql = "SELECT email FROM UserCredentials WHERE username = ?";
        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                email = rs.getString("email");
            }
        } catch (SQLException e) {
            showErrorAlert("Error retrieving email from database: " + e.getMessage());
        }
        return email;
    }


    @FXML
    private void btnLoginClick() {
        attemptLogin();
    }

    private void attemptLogin() {
        String enteredUsername = txtUsername.getText();
        String enteredPassword = txtPassword.getText();

        boolean loginSuccess = checkLoginFromDatabase(enteredUsername, enteredPassword);

        if (loginSuccess) {
            // Fetch the email associated with the logged-in user
            String userEmail = getEmailFromDatabase(enteredUsername);

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("MEDCURE");
            alert.setHeaderText("Information");
            alert.setContentText("Login success!!");
            // Set icon for the information alert
            Stage alertStage = (Stage) alert.getDialogPane().getScene().getWindow();
            alertStage.getIcons().add(new Image(GUI.class.getResourceAsStream("/com/medcure/app/icon.png")));
            Session.getInstance().login(enteredUsername); // Pass the entered username
            Session.getInstance().setUserEmail(userEmail); // Set the user's email
            alert.showAndWait();

            // Close the login stage if it is not null
            if (loginStage != null ) {
                loginStage.hide();
            }

            // Determine which view to show based on the username
            String viewToLoad = enteredUsername.equals("admin") ? "adminview" : "HomePageView";

            // Set the root of the primary stage to the new view
            try {
                GUI.setRoot(viewToLoad, "MEDCURE", false);
            } catch (IOException e) {
                e.printStackTrace();
                showErrorAlert("Error loading " + viewToLoad + ": " + e.getMessage());
            }
        } else {
            showErrorAlert("Invalid username or password. Please try again.");
        }
    }



    @FXML
    private void signUpHyperlinkClicked() throws IOException {
        Stage loginStage = (Stage) signUp.getScene().getWindow();

        loginStage.hide();

        FXMLLoader loader = new FXMLLoader(getClass().getResource("signUp.fxml"));
        Parent signUpParent = loader.load();

        Stage signUpStage = new Stage();
        signUpStage.setTitle("MEDCURE");
        signUpStage.setScene(new Scene(signUpParent));
        signUpStage.setResizable(false);
        signUpStage.getIcons().add(new Image(GUI.class.getResourceAsStream("/com/medcure/app/icon.png")));
        signUpStage.show();
    }

    public LoginController() {
        // Constructor kosong
    }

    public void setLoginStage(Stage loginStage) {
        this.loginStage = loginStage;
    }
}
