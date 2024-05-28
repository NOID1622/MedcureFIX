package com.medcure.app;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.sql.*;

public class ProfileEditController {
    private static final String DB_URL = "jdbc:sqlite:mydatabase.db";
    @FXML
    private ImageView fotoprofiledit;


    @FXML
    private TextField usernamefield;
    @FXML
    private TextField emailfield;
    @FXML
    private TextField passwordfield;

    private String currentUsername;

    public void initialize(String username, String email, String password) {
        currentUsername = username;
        usernamefield.setText(username);
        emailfield.setText(email);
        passwordfield.setText(password);

        // Load and display the current profile picture
        displayProfilePicture(username);
    }

    @FXML
    private void chooseProfilePicture() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Pilih Foto Profil");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg")
        );
        Stage stage = (Stage) fotoprofiledit.getScene().getWindow();
        File selectedFile = fileChooser.showOpenDialog(stage);
        if (selectedFile != null) {
            // Save and display the new profile picture
            saveProfilePicture(selectedFile);
            displayProfilePicture(currentUsername);
        }
    }

    private void saveProfilePicture(File file) {
        try {
            // Directory to save profile pictures
            String profilePicturesFolder = "profile_pictures/";
            String profilePicturesPath = Path.of(profilePicturesFolder).toAbsolutePath().toString();

            // Create directory if it doesn't exist
            File directory = new File(profilePicturesPath);
            if (!directory.exists()) {
                directory.mkdirs();
            }

            // Check for existing profile picture and delete if it exists
            deleteExistingProfilePicture();

            // Copy the selected file to the profile pictures directory
            Path sourcePath = file.toPath();
            String fileName = file.getName();
            Path destinationPath = Path.of(profilePicturesPath, fileName);
            Files.copy(sourcePath, destinationPath, StandardCopyOption.REPLACE_EXISTING);

            // Save the image path to the database
            saveProfilePictureToDatabase(currentUsername, profilePicturesFolder + fileName);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void deleteExistingProfilePicture() {
        String sql = "SELECT image_path FROM ProfilePictures WHERE username = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, currentUsername);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                String existingImagePath = rs.getString("image_path");
                if (existingImagePath != null && !existingImagePath.isEmpty()) {
                    File existingImageFile = new File(existingImagePath);
                    if (existingImageFile.exists()) {
                        existingImageFile.delete();
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void saveProfilePictureToDatabase(String username, String imagePath) {
        // Periksa apakah sudah ada entri untuk pengguna yang sama dalam database
        try (Connection conn = DriverManager.getConnection(DB_URL)) {
            String selectSql = "SELECT * FROM ProfilePictures WHERE username = ?";
            try (PreparedStatement selectStmt = conn.prepareStatement(selectSql)) {
                selectStmt.setString(1, username);
                try (ResultSet rs = selectStmt.executeQuery()) {
                    if (rs.next()) {
                        // Jika sudah ada entri, perbarui path gambar
                        String updateSql = "UPDATE ProfilePictures SET image_path = ? WHERE username = ?";
                        try (PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
                            updateStmt.setString(1, imagePath);
                            updateStmt.setString(2, username);
                            updateStmt.executeUpdate();
                        }
                    } else {
                        // Jika belum ada entri, tambahkan entri baru
                        String insertSql = "INSERT INTO ProfilePictures (username, image_path) VALUES (?, ?)";
                        try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
                            insertStmt.setString(1, username);
                            insertStmt.setString(2, imagePath);
                            insertStmt.executeUpdate();
                        }
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void displayProfilePicture(String username) {
        String sql = "SELECT image_path FROM ProfilePictures WHERE username = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                String imagePath = rs.getString("image_path");
                if (imagePath != null && !imagePath.isEmpty()) {
                    // Load the image using its file path
                    File imageFile = new File(imagePath);
                    Image profileImage = new Image(imageFile.toURI().toString());
                    fotoprofiledit.setImage(profileImage);
                } else {
                    // No image path found, load the default image
                    loadDefaultImage();
                }
            } else {
                // No record found for the username, load the default image
                loadDefaultImage();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // In case of an exception, also load the default image
            loadDefaultImage();
        }
    }

    private void loadDefaultImage() {
        // Assuming default_img.png is in the resources package
        String defaultImagePath = getClass().getResource("/resources/default_img.png").toString();
        Image defaultImage = new Image(defaultImagePath);
        fotoprofiledit.setImage(defaultImage);
    }

    @FXML
    private void save() {
        // Mendapatkan nilai yang baru dari field
        String newUsername = usernamefield.getText();
        String newEmail = emailfield.getText();
        String newPassword = passwordfield.getText();

        // Memeriksa apakah ada perubahan yang dibuat
        if (newUsername.equals(currentUsername) && newEmail.equals(emailfield.getPromptText()) && newPassword.isEmpty()) {
            // Jika tidak ada perubahan, berikan pemberitahuan dan tutup jendela
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("MEDCURE");
            alert.setHeaderText("No Changes");
            Stage alertStage = (Stage) alert.getDialogPane().getScene().getWindow();
            alertStage.getIcons().add(new Image(GUI.class.getResourceAsStream("/com/medcure/app/icon.png")));
            alert.setContentText("Tidak ada perubahan yang dibuat.");
            alert.showAndWait();

            // Close the edit profile stage
            Stage stage = (Stage) fotoprofiledit.getScene().getWindow();
            stage.close();
            return;
        }

        // Memeriksa apakah ada kolom yang kosong
        if (newUsername.isEmpty() || newEmail.isEmpty() || newPassword.isEmpty()) {
            // Menampilkan peringatan untuk mengisi semua field
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("MEDCURE");
            alert.setHeaderText("Field kosong");
            Stage alertStage = (Stage) alert.getDialogPane().getScene().getWindow();
            alertStage.getIcons().add(new Image(GUI.class.getResourceAsStream("/com/medcure/app/icon.png")));
            alert.setContentText("Harap isi semua field.");
            alert.showAndWait();
            return;
        }

        // Memeriksa apakah email sesuai format
        if (!isValidEmail(newEmail)) {
            // Menampilkan peringatan jika email tidak valid
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("MEDCURE");
            alert.setHeaderText("Email Tidak Valid");
            Stage alertStage = (Stage) alert.getDialogPane().getScene().getWindow();
            alertStage.getIcons().add(new Image(GUI.class.getResourceAsStream("/com/medcure/app/icon.png")));
            alert.setContentText("Harap masukkan alamat email yang valid.");
            alert.showAndWait();
            return;
        }

        // Implement the method to update user information in the database
        boolean isUpdated = updateUserInfoInDatabase(currentUsername, newUsername, newEmail, newPassword);

        // Notify the user that the data was saved successfully
        if (isUpdated) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("MEDCURE");
            alert.setHeaderText("Success");
            Stage alertStage = (Stage) alert.getDialogPane().getScene().getWindow();
            alertStage.getIcons().add(new Image(GUI.class.getResourceAsStream("/com/medcure/app/icon.png")));
            alert.setContentText("Berhasil mengedit Profil.");
            alert.showAndWait();
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("MEDCURE");
            alert.setHeaderText("Error");
            Stage alertStage = (Stage) alert.getDialogPane().getScene().getWindow();
            alertStage.getIcons().add(new Image(GUI.class.getResourceAsStream("/com/medcure/app/icon.png")));
            alert.setContentText("Gagal mengedit profil.");
            alert.showAndWait();
        }

        // Close the edit profile stage
        Stage stage = (Stage) fotoprofiledit.getScene().getWindow();
        stage.close();
    }

    private boolean isValidEmail(String email) {
        // Pengecekan sederhana apakah string mirip dengan format email
        return email.matches("^(.+)@(.+)$");
    }
    private boolean updateUserInfoInDatabase(String currentUsername, String newUsername, String newEmail, String newPassword) {
        String sql = "UPDATE UserCredentials SET username = ?, email = ?, password = ? WHERE username = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, newUsername);
            pstmt.setString(2, newEmail);
            pstmt.setString(3, newPassword);
            pstmt.setString(4, currentUsername);
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    @FXML
    private void cancel() {
        // Close the edit profile stage without saving any changes
        Stage stage = (Stage) fotoprofiledit.getScene().getWindow();
        stage.close();
    }
}
