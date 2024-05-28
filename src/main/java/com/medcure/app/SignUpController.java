package com.medcure.app;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import java.io.IOException;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert;
import java.sql.ResultSet;
import javafx.scene.control.Hyperlink;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SignUpController {
    @FXML
    private TextField createUsername;

    @FXML
    private TextField email;

    @FXML
    private PasswordField createPassword;

    @FXML
    private Button btnSignUp;
    @FXML
    private Hyperlink backToLogin;
    private static final int MAX_USERNAME_LENGTH = 20;
    private static final int MAX_EMAIL_LENGTH = 50;
    private static final int MAX_PASSWORD_LENGTH = 20;
    @FXML
    public void initialize() {
        // Set maximum length for input fields
        createUsername.lengthProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.intValue() > MAX_USERNAME_LENGTH) {
                createUsername.setText(createUsername.getText().substring(0, MAX_USERNAME_LENGTH));
            }
        });

        email.lengthProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.intValue() > MAX_EMAIL_LENGTH) {
                email.setText(email.getText().substring(0, MAX_EMAIL_LENGTH));
            }
        });

        createPassword.lengthProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.intValue() > MAX_PASSWORD_LENGTH) {
                createPassword.setText(createPassword.getText().substring(0, MAX_PASSWORD_LENGTH));
            }
        });
    }
    @FXML
    private void signUp() {
        String username = createUsername.getText();
        String userEmail = email.getText();
        String password = createPassword.getText();

        // Validate input fields
        if (username.isEmpty() || userEmail.isEmpty() || password.isEmpty()) {
            showAlert("Error","Please fill in all fields.");
            return;
        }
        if (!isValidEmail(userEmail)) {
            showAlert("Error", "Please enter a valid email address.");
            return;
        }
        // Create a new user object
        User newUser = new User(username, userEmail, password);
        if (isUserExists(newUser)) {
            showAlert("Error", "User with the same username or email already exists.");
            return;
        }


        // Save user to database
        saveUserToDatabase(newUser);

        // Print user information (for debugging)
        System.out.println("New user signed up:");
        System.out.println("Username: " + newUser.getUsername());
        System.out.println("Email: " + newUser.getEmail());
        System.out.println("Password: " + newUser.getPassword());

        // Clear sign-up form
        clearSignUpForm();
        showAlert("Information", "Sign in success!!");

        goToLogin();
    }
    private boolean isUserExists(User user) {
        String url = "jdbc:sqlite:mydatabase.db";
        String sql = "SELECT * FROM UserCredentials WHERE username = ? OR email = ?";
        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, user.getUsername());
            pstmt.setString(2, user.getEmail());
            ResultSet rs = pstmt.executeQuery();
            return rs.next(); // Mengembalikan true jika pengguna sudah ada, false jika tidak
        } catch (SQLException e) {
            System.out.println("Error checking user existence: " + e.getMessage());
            return false;
        }
    }
    private void showAlert(String header, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }


    private void saveUserToDatabase(User user) {
        String url = "jdbc:sqlite:mydatabase.db"; // Ganti dengan path ke file database Anda
        String sql = "INSERT INTO UserCredentials (username, email, password) VALUES (?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, user.getUsername());
            pstmt.setString(2, user.getEmail());
            pstmt.setString(3, user.getPassword());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error saving user to database: " + e.getMessage());
            System.out.println(e);
        }
    }

    private void goToLogin() {
        try {
            // Load file fxml untuk halaman login
            Parent root = FXMLLoader.load(getClass().getResource("login-view.fxml"));
            Scene scene = new Scene(root);

            // Dapatkan stage dari scene sign-up
            Stage stage = (Stage) btnSignUp.getScene().getWindow();
            // Set scene ke stage dan tampilkan
            stage.setScene(scene);
            stage.setTitle("MEDCURE");
            stage.setResizable(false);

            // Set the application icon
            stage.getIcons().add(new Image(GUI.class.getResourceAsStream("/com/medcure/app/icon.png")));

            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void clearSignUpForm() {
        createUsername.clear();
        email.clear();
        createPassword.clear();
    }
    @FXML
    private void backToLoginClicked() {
        Stage stage = (Stage) backToLogin.getScene().getWindow();
        stage.close(); // Menutup jendela pendaftaran
        // Buka jendela login
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("login-view.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            Stage loginStage = new Stage();
            loginStage.setTitle("MEDCURE");
            loginStage.setScene(scene);
            loginStage.setResizable(false); // Corrected line
            loginStage.getIcons().add(new Image(GUI.class.getResourceAsStream("/com/medcure/app/icon.png"))); // Corrected line

            loginStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean isValidEmail(String email) {
        // Regular expression for valid email address
        String regex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }
}