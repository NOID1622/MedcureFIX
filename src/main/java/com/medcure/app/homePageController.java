package com.medcure.app;


import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.stage.Modality;
import javafx.scene.control.cell.PropertyValueFactory;
import java.io.IOException;
import java.sql.*;
import java.util.Objects;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import com.medcure.data.Obat;
import com.medcure.utils.Session;

public class homePageController {
    static final String DB_URL = "jdbc:sqlite:mydatabase.db";
    private GUI gui;
    private Stage primaryStage;
    private SearchHistoryManager searchHistoryManager = new SearchHistoryManager();

    @FXML private MenuButton history;
    @FXML private AnchorPane homepage;
    @FXML private Button idButtonTambahObat;
    @FXML private Button idButtonHapusObat;
    @FXML private TextField textFieldPencarian;
    @FXML private Button idButtonProfile;
    @FXML private Pane paneObat1;
    @FXML private Pane paneObat2;
    @FXML private Pane paneObat3;
    @FXML private Hyperlink cekDetail1;
    @FXML private Hyperlink cekDetail2;
    @FXML private Hyperlink cekDetail3;
    @FXML private TableView<Obat> TabelPencarian;
    @FXML private TableColumn<Obat, Integer> id;
    @FXML private TableColumn<Obat, String> namaObat;

    public homePageController(GUI gui, Stage primaryStage) {
        this.gui = gui;
        this.primaryStage = primaryStage;
    }

    public homePageController() {
        // Default constructor with no parameters
    }

    public homePageController(GUI gui) {
        this.gui = gui;
    }

    @FXML
    private void initialize() {
        initializeColumns();
        textFieldPencarian.setOnAction(event -> handleSearch());
    }

    private void initializeColumns() {
        id.setCellValueFactory(new PropertyValueFactory<>("id"));
        namaObat.setCellValueFactory(new PropertyValueFactory<>("namaObat"));
    }

    private ObservableList<Obat> cariObat(String query) {
        ObservableList<Obat> hasilPencarian = FXCollections.observableArrayList();
        String sql = "SELECT ko.id, ko.nama_obat FROM user_katalog_obat uko " +
                "JOIN katalog_obat ko ON uko.obat_id = ko.id " +
                "JOIN UserCredentials uc ON uko.user_id = uc.id " +
                "WHERE ko.nama_obat LIKE ?";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, "%" + query + "%");
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                hasilPencarian.add(new Obat(rs.getInt("id"), rs.getString("nama_obat")));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return hasilPencarian;
    }

    @FXML
    private void handleSearch() {
        String query = textFieldPencarian.getText();
        if (query != null && !query.isEmpty()) {
            searchHistoryManager.addSearchQuery(query);
            updateHistoryMenu();
            ObservableList<Obat> hasilPencarian = cariObat(query);

            if (hasilPencarian.isEmpty()) {
                showEmptySearchResultsAlert();
            } else {
                showSearchResults(hasilPencarian);
            }
        }
    }

    private void showEmptySearchResultsAlert() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Hasil Pencarian");
        alert.setHeaderText("Pencarian Tidak Ditemukan");
        alert.setContentText("Maaf, hasil pencarian tidak ditemukan. Silakan coba lagi dengan kata kunci yang berbeda.");
        alert.showAndWait();
    }

    private void showSearchResults(ObservableList<Obat> hasilPencarian) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/medcure/app/pencarian.fxml"));
            Parent root = loader.load();

            PencarianController pencarianController = loader.getController();
            pencarianController.setSearchResults(hasilPencarian);

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Hasil Pencarian");
            stage.setResizable(false);
            stage.getIcons().add(new Image(GUI.class.getResourceAsStream("/com/medcure/app/icon.png")));
            stage.initModality(Modality.WINDOW_MODAL);
            stage.initOwner(primaryStage);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void updateHistoryMenu() {
        history.getItems().clear();
        for (String recentSearch : searchHistoryManager.getRecentSearches()) {
            MenuItem menuItem = new MenuItem(recentSearch);
            menuItem.setOnAction(event -> textFieldPencarian.setText(recentSearch));
            history.getItems().add(menuItem);
        }
    }

    public void initData(String username) {
        textFieldPencarian.setPromptText("Pencarian");
        textFieldPencarian.setOnAction(e -> searchObat(textFieldPencarian.getText()));

        idButtonTambahObat.setOnAction(e -> tambahObat());
        idButtonProfile.setOnAction(e -> goToProfile());
        idButtonHapusObat.setOnAction(e -> showDeletePage());

        if (username != null && !username.isEmpty()) {
            textFieldPencarian.setText("Selamat datang, " + username + "!");

            String email = getEmailByUsername(username);
            System.out.println("Email: " + email);
        }
    }

    private String getEmailByUsername(String username) {
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
            System.err.println("Failed to retrieve email for username " + username + ": " + e.getMessage());
        }
        return email;
    }

    private void searchObat(String query) {
        // Implementasi logika pencarian obat
        System.out.println("Mencari obat: " + query);
    }
    @FXML
    private void shortByAction(ActionEvent event) {
        MenuItem menuItem = (MenuItem) event.getSource();
        String sortBy = menuItem.getText();

        // Menentukan kolom yang akan diurutkan berdasarkan pilihan pengguna
        TableColumn<Obat, ?> columnToSort;
        switch (sortBy) {
            case "Nama Ascending":
                columnToSort = namaObat;
                break;
            case "Nama Descending":
                columnToSort = namaObat;
                break;
            case "Date Ascending":
                // Tambahkan kolom tanggal jika belum ada
                TableColumn<Obat, ?> dateColumn = new TableColumn<>("Tanggal");
                dateColumn.setCellValueFactory(new PropertyValueFactory<>("tanggal")); // Ganti "tanggal" dengan properti tanggal yang sesuai
                if (!TabelPencarian.getColumns().contains(dateColumn)) {
                    TabelPencarian.getColumns().add(dateColumn);
                }
                columnToSort = dateColumn;
                break;
            case "Date Descending":
                // Tambahkan kolom tanggal jika belum ada
                TableColumn<Obat, ?> dateColumnDesc = new TableColumn<>("Tanggal");
                dateColumnDesc.setCellValueFactory(new PropertyValueFactory<>("tanggal")); // Ganti "tanggal" dengan properti tanggal yang sesuai
                if (!TabelPencarian.getColumns().contains(dateColumnDesc)) {
                    TabelPencarian.getColumns().add(dateColumnDesc);
                }
                columnToSort = dateColumnDesc;
                break;
            default:
                // Defaultnya adalah mengurutkan berdasarkan nama secara naik
                columnToSort = namaObat;
                break;
        }

        // Lakukan pengurutan sesuai kolom yang ditentukan
        TabelPencarian.getSortOrder().clear(); // Bersihkan pengurutan sebelumnya
        columnToSort.setSortType(sortBy.contains("Ascending") ? TableColumn.SortType.ASCENDING : TableColumn.SortType.DESCENDING);
        TabelPencarian.getSortOrder().add(columnToSort);
        TabelPencarian.sort();
    }

    @FXML
    private void tambahObat() {
        // Implementasi logika untuk menambah obat
        try {
            // Load FXML halaman TambahResepObat.fxml
            FXMLLoader loader = new FXMLLoader(getClass().getResource("TambahResepObat.fxml"));
            Parent root = loader.load();

            // Dapatkan controller dari halaman TambahResepObatController
            TambahResepObatController controller = loader.getController();

            // Panggil method di TambahResepObatController untuk melakukan sesuatu setelah menambah obat
            controller.handleSimpan();

            // Tampilkan halaman TambahResepObatController dalam sebuah stage baru
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("MEDCURE");
            stage.getIcons().add(new Image(GUI.class.getResourceAsStream("/com/medcure/app/icon.png")));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

//    private void hapusObat() {
//        // Implementasi logika untuk menghapus obat
//        try {
//            // Load FXML halaman HapusObat.fxml
//            FXMLLoader loader = new FXMLLoader(getClass().getResource("HapusObat.fxml"));
//            Parent root = loader.load();
//
//            // Dapatkan controller dari halaman HapusObatController
//            HapusObatController controller = loader.getController();
//
//            // Panggil method di HapusObatController untuk melakukan sesuatu setelah menghapus obat
//            controller.hapusObatTerpilih();
//
//            // Tampilkan halaman HapusObatController dalam sebuah stage baru
//            Stage stage = new Stage();
//            stage.setScene(new Scene(root));
//            stage.setTitle("Hapus Obat");
//            stage.getIcons().add(new Image(GUI.class.getResourceAsStream("/com/medcure/app/icon.png")));
//            stage.show();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }

    @FXML
    private void goToProfile() {
        String loggedInUsername = Session.getInstance().getLoggedInUsername();
        if (loggedInUsername != null && !loggedInUsername.isEmpty()) {
            try {
                // Tutup stage homepage
                Stage homepageStage = (Stage) homepage.getScene().getWindow();
                // homepageStage.close(); // Mungkin ini perlu dihilangkan karena Anda telah menutup jendela di metode closeHomePage()

                // Load FXML halaman profil
                FXMLLoader loader = new FXMLLoader(getClass().getResource("profil.fxml"));
                Parent root = loader.load();

                // Dapatkan controller dari halaman profil
                ProfilController profilController = loader.getController();
                profilController.setHomePageController(this); // Set homePageController di ProfilController

                // Set nama pengguna ke dalam profilController
                profilController.initData(loggedInUsername);

                // Ambil email dari database dan set ke profilController
                String email = getEmailByUsername(loggedInUsername);
                profilController.setEmailFromHomePage(email);

                // Tampilkan halaman profil dalam sebuah stage baru
                Stage stage = new Stage();
                stage.setScene(new Scene(root));
                stage.setTitle("MEDCURE");
                stage.setResizable(false);
                stage.getIcons().add(new Image(GUI.class.getResourceAsStream("/com/medcure/app/icon.png")));
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            // Jika pengguna belum login, berikan alert dan arahkan mereka ke halaman login
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("MEDCURE");
            alert.setHeaderText("WARNING");
            Stage alertStage = (Stage) alert.getDialogPane().getScene().getWindow();
            alertStage.getIcons().add(new Image(GUI.class.getResourceAsStream("/com/medcure/app/icon.png")));
            alert.setContentText("You are not logged in. Please log in to view your profile.");
            alert.showAndWait();

            // Close the current homepage stage
            Stage homepageStage = (Stage) homepage.getScene().getWindow();
            homepageStage.close();

            // Load the login page
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("login-view.fxml"));
                Parent root = loader.load();
                Stage stage = new Stage();
                stage.setScene(new Scene(root));
                stage.setTitle("MEDCURE");
                stage.setResizable(false);
                stage.getIcons().add(new Image(Objects.requireNonNull(GUI
                        .class.getResourceAsStream("/com/medcure/app/icon.png"))));

                stage.show();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    @FXML
    private void showDeletePage() {
        try {
            GUI.setRoot("HapusObat-view", "Hapus Obat", false);
            Stage currentStage = (Stage) homepage.getScene().getWindow();
            currentStage.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void cekDetailObat1() {
        // Implementasi logika untuk menampilkan detail obat 1
        System.out.println("Menampilkan detail obat 1...");
    }

    @FXML
    private void cekDetailObat2() {
        // Implementasi logika untuk menampilkan detail obat 2
        System.out.println("Menampilkan detail obat 2...");
    }

    @FXML
    private void cekDetailObat3() {
        // Implementasi logika untuk menampilkan detail obat 3
        System.out.println("Menampilkan detail obat 3...");
    }

    public void closeHomePage() {
        Stage stage = (Stage) homepage.getScene().getWindow();
        stage.close();
    }
}
