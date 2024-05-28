package com.medcure.app;

import javafx.scene.Node;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import javafx.fxml.FXMLLoader;
import javafx.stage.FileChooser;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import com.medcure.utils.Session;
import javafx.event.ActionEvent;
import java.io.File;
import java.nio.file.Paths;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;


import javafx.scene.control.Alert;

import javafx.stage.Stage;

import static com.medcure.app.homePageController.DB_URL;

public class ProfilController {
    private static final String DB_URL = "jdbc:sqlite:mydatabase.db";
    private homePageController homePageController;
    @FXML
    private ImageView fotoprofil;
    @FXML
    private Button logoutid;
    @FXML
    private Text txtemail;
    @FXML
    private Text txtpass;
    @FXML
    private Text lblusername;
    @FXML
    private Hyperlink editProfileLink;
    private String username;
    private String email;

    public void setHomePageController(homePageController homePageController) {
        this.homePageController = homePageController;
    }

    public void initialize(String username) {
        this.username = username;
        fetchDataFromDatabase();
        lblusername.setText(username);
        setEmailFromSession(username); // Panggil setEmailFromDatabase dengan username
        displayProfilePicture(username); // Display profile picture on initialization
    }

    public void setEmailFromSession(String username) {
        String email = getEmailByUsername(username);
        txtemail.setText(email); // Set email to the 'txtemail' field
    }

    public void setEmailFromHomePage(String email) {
        this.email = email;
        txtemail.setText(email); // Set email to the 'txtemail' field
    }

    public void setLoggedInUsername(String username) {
        lblusername.setText(username);
    }

    public void initData(String username) {
        // This method will be called from the homePageController
        // You can use it to initialize the data for the profile page
        initialize(username);
    }

    public void setEmailFromDatabase(String username) {
        String emailFromDatabase = getEmailByUsername(username);
        txtemail.setText(emailFromDatabase);
    }

    String getEmailByUsername(String username) {
        String email = "";
        String sql = "SELECT email FROM UserCredentials WHERE username = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    email = rs.getString("email");
                }
            }
        } catch (SQLException e) {
            // Handle database error
            System.err.println("Failed to retrieve email for username " + username + ": " + e.getMessage());
        }
        return email;
    }

    @FXML
    private void logout(ActionEvent event) {
        // Panggil metode logout() dari objek Session
        Session.getInstance().logout();

        // Tutup homepage jika ada
        if (homePageController != null) {
            homePageController.closeHomePage();
        }

        // Tutup stage profil
        Stage profileStage = (Stage) logoutid.getScene().getWindow();
        profileStage.close();

        // Tampilkan pesan berhasil logout
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Logout");
        alert.setHeaderText(null);
        alert.setContentText("Anda telah berhasil logout.");
        alert.showAndWait();

        // Arahkan pengguna kembali ke halaman login
        redirectToLogin();
    }

    private void redirectToLogin() {
        try {
            // Load halaman login
            FXMLLoader loader = new FXMLLoader(getClass().getResource("login-view.fxml"));
            Stage stage = (Stage) logoutid.getScene().getWindow();
            stage.setScene(new Scene(loader.load()));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            // Tampilkan pesan kesalahan jika gagal mengarahkan
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Gagal mengarahkan ke halaman login.");
            alert.showAndWait();
        }
    }

    private void fetchDataFromDatabase() {
        // Koneksi ke database
        String url = "jdbc:sqlite:mydatabase.db"; // Sesuaikan dengan path ke file database Anda
        String sql = "SELECT username, email, password FROM UserCredentials WHERE username = ?";
        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            // Set parameter query
            pstmt.setString(1, username);
            // Eksekusi query
            ResultSet rs = pstmt.executeQuery();
            // Jika ada hasil, perbarui tampilan dengan informasi pengguna
            if (rs.next()) {
                String nama = rs.getString("username");
                String email = rs.getString("email");
                String password = rs.getString("password");

                // Set email to the 'email' field
                this.txtemail.setText(email);

                // Set password to the 'txtpass' field
                this.txtpass.setText(password); // Mengatur nilai txtpass dengan password dari database
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
                    fotoprofil.setImage(profileImage);
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
        URL resource = getClass().getResource("/default_img.png");
        if (resource != null) {
            Image defaultImage = new Image(resource.toString());
            fotoprofil.setImage(defaultImage);
        } else {
            // Handle the error, e.g., log a message or set a placeholder image
            System.err.println("Default image not found. Please check the resource path.");
        }
    }


    @FXML
    private void handleEditProfileLink(ActionEvent event) {
        // Memeriksa apakah alamat email sudah diisi dan valid
        if (email.isEmpty() || !isValidEmail(email)) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Invalid Email");
            alert.setHeaderText(null);
            alert.setContentText("Silakan isi alamat email yang valid sebelum mengedit profil.");
            alert.showAndWait();
            return;
        }

        try {
            // Load halaman profiledit.fxml
            FXMLLoader loader = new FXMLLoader(getClass().getResource("profiledit.fxml"));
            Stage newStage = new Stage();
            newStage.setScene(new Scene(loader.load()));
            newStage.setTitle("MEDCURE");
            newStage.setResizable(false);

            // Set the application icon
            newStage.getIcons().add(new Image(GUI.class.getResourceAsStream("/com/medcure/app/icon.png")));
            newStage.show();

            // Pass current user data to the edit profile controller
            ProfileEditController profileEditController = loader.getController();
            profileEditController.initialize(username, email, txtpass.getText());

            // Close the current stage
            Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            currentStage.close();

        } catch (IOException e) {
            e.printStackTrace();
            // Tampilkan pesan kesalahan jika gagal mengarahkan
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Gagal membuka halaman edit profil.");
            alert.showAndWait();
        }
    }

    private boolean isValidEmail(String email) {
        // Pengecekan sederhana apakah string mirip dengan format email
        return email.matches("^(.+)@(.+)$");
    }


    @FXML
    private void deleteAccount() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("MEDCURE");

        Stage alertStage = (Stage) alert.getDialogPane().getScene().getWindow();
        alertStage.getIcons().add(new Image(GUI.class.getResourceAsStream("/com/medcure/app/icon.png")));
        alert.setHeaderText("Konfirmasi Penghapusan Akun");
        alert.setContentText("Apakah Anda yakin ingin menghapus akun Anda? Tindakan ini tidak dapat dibatalkan.");

        // Menambahkan opsi OK dan Batal
        ButtonType buttonTypeOK = new ButtonType("Ya");
        ButtonType buttonTypeCancel = new ButtonType("Batal");
        alert.getButtonTypes().setAll(buttonTypeOK, buttonTypeCancel);

        // Menangani respon dari pengguna
        alert.showAndWait().ifPresent(buttonType -> {
            if (buttonType == buttonTypeOK) {
                // Jika pengguna memilih OK, hapus akun
                performAccountDeletion();
            }
        });
    }
    private void deleteProfilePictureFileFromPath(String imagePath) {
        // Buat objek File yang menunjuk ke gambar profil yang akan dihapus
        File fileToDelete = new File(imagePath);

        // Hapus file gambar profil jika file ada
        if (fileToDelete.exists()) {
            if (fileToDelete.delete()) {
                System.out.println("File gambar profil berhasil dihapus: " + imagePath);
            } else {
                System.err.println("Gagal menghapus file gambar profil: " + imagePath);
            }
        } else {
            System.err.println("File gambar profil tidak ditemukan: " + imagePath);
        }
    }
    private void deleteProfilePicturefile() {
        // Query untuk menghapus entri gambar profil dari tabel ProfilePictures
        String selectSql = "SELECT image_path FROM ProfilePictures WHERE username = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement selectStmt = conn.prepareStatement(selectSql)) {
            selectStmt.setString(1, username);
            try (ResultSet rs = selectStmt.executeQuery()) {
                if (rs.next()) {
                    String imagePath = rs.getString("image_path");
                    // Hapus file gambar profil dari sistem file
                    deleteProfilePictureFileFromPath(imagePath);
                }
            }
        } catch (SQLException e) {
            // Tangani kesalahan SQL
            e.printStackTrace();
            Alert errorAlert = new Alert(Alert.AlertType.ERROR);
            errorAlert.setTitle("MEDCURE");
            errorAlert.setHeaderText("ERROR");
            Stage alertStage = (Stage) errorAlert.getDialogPane().getScene().getWindow();
            alertStage.getIcons().add(new Image(GUI.class.getResourceAsStream("/com/medcure/app/icon.png")));
            errorAlert.setContentText("Gagal menghapus entri gambar profil: " + e.getMessage());
            errorAlert.showAndWait();
        }
    }

    private void deleteProfilePictureEntry() {
        // Query untuk menghapus entri gambar profil dari tabel ProfilePictures
        String deleteSql = "DELETE FROM ProfilePictures WHERE username = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(deleteSql)) {
            pstmt.setString(1, username);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            // Tangani kesalahan SQL
            e.printStackTrace();
            Alert errorAlert = new Alert(Alert.AlertType.ERROR);
            errorAlert.setTitle("MEDCURE");
            errorAlert.setHeaderText("ERROR");
            Stage alertStage = (Stage) errorAlert.getDialogPane().getScene().getWindow();
            alertStage.getIcons().add(new Image(GUI.class.getResourceAsStream("/com/medcure/app/icon.png")));
            errorAlert.setContentText("Gagal menghapus entri gambar profil: " + e.getMessage());
            errorAlert.showAndWait();
        }
    }
    private void performAccountDeletion() {
        // Query untuk menghapus akun dari database
        String deleteSql = "DELETE FROM UserCredentials WHERE username = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(deleteSql)) {
            pstmt.setString(1, username);
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                deleteProfilePicturefile();
                // Jika penghapusan berhasil, hapus juga entri gambar profil
                deleteProfilePictureEntry();

                // Tampilkan pesan sukses
                Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
                successAlert.setTitle("MEDCURE");
                successAlert.setHeaderText("Sukses");
                Stage alertStage = (Stage) successAlert.getDialogPane().getScene().getWindow();
                alertStage.getIcons().add(new Image(GUI.class.getResourceAsStream("/com/medcure/app/icon.png")));
                successAlert.setContentText("Akun Anda berhasil dihapus.");
                successAlert.showAndWait();

                // Logout dan tutup stage
                logout(null);
            } else {
                // Jika tidak ada baris yang terpengaruh, tampilkan pesan kesalahan
                Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                errorAlert.setTitle("MEDCURE");
                errorAlert.setHeaderText("ERROR");
                Stage alertStage = (Stage) errorAlert.getDialogPane().getScene().getWindow();
                alertStage.getIcons().add(new Image(GUI.class.getResourceAsStream("/com/medcure/app/icon.png")));
                errorAlert.setContentText("Gagal menghapus akun.");
                errorAlert.showAndWait();
            }
        } catch (SQLException e) {
            // Tangani kesalahan SQL
            e.printStackTrace();
            Alert errorAlert = new Alert(Alert.AlertType.ERROR);
            errorAlert.setTitle("Error");
            errorAlert.setHeaderText(null);
            errorAlert.setContentText("Gagal menghapus akun: " + e.getMessage());
            errorAlert.showAndWait();
        }
    }
    // Event handler untuk tombol "hapus akun"
    @FXML
    private void handleDeleteAccount(ActionEvent event) {
        deleteAccount();
    }

}
