package com.medcure.app;

import com.medcure.data.Medicine;
import com.medcure.utils.Session;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.sql.*;
import java.time.LocalDate;
import java.util.ResourceBundle;
import java.util.function.Predicate;

public class AdminController implements Initializable {
    @FXML
    private TextField fieldNamaObat, fieldDosis, fieldCaraPengunaan, fieldProdusen, fieldHarga, searchBox;
    @FXML
    private TextArea fieldKomposisi, fieldEfekSamping;
    @FXML
    private DatePicker fieldKadaluarsa;
    @FXML
    private ImageView fotoObat;
    @FXML
    private Button logout;
    @FXML
    private TableView<Medicine> tabledataobat;
    @FXML
    private TableColumn<Medicine, Integer> id;
    @FXML
    private TableColumn<Medicine, String> namaobat, komposisi, dosis, caraPenggunaan, efekSamping, produsen, harga, tglKadaluarsa;

    private FilteredList<Medicine> obatFilteredList;
    private Medicine selectedObat;
    private final String DB_URL = "jdbc:sqlite:mydatabase.db";
    private Connection connection;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        obatFilteredList = new FilteredList<>(FXCollections.observableArrayList());
        tabledataobat.setItems(obatFilteredList);
        searchBox.textProperty().addListener(
                (observableValue, oldValue, newValue) -> obatFilteredList.setPredicate(createPredicate(newValue))
        );

        id.setCellValueFactory(new PropertyValueFactory<>("id"));
        namaobat.setCellValueFactory(new PropertyValueFactory<>("namaObat"));
        komposisi.setCellValueFactory(new PropertyValueFactory<>("komposisi"));
        dosis.setCellValueFactory(new PropertyValueFactory<>("dosis"));
        caraPenggunaan.setCellValueFactory(new PropertyValueFactory<>("caraPenggunaan"));
        efekSamping.setCellValueFactory(new PropertyValueFactory<>("efekSamping"));
        produsen.setCellValueFactory(new PropertyValueFactory<>("produsen"));
        harga.setCellValueFactory(new PropertyValueFactory<>("harga"));
        tglKadaluarsa.setCellValueFactory(new PropertyValueFactory<>("tanggalKedaluwarsa"));

        getConnection();
        createTable();
        getAllData();
        tabledataobat.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Medicine>() {
            @Override
            public void changed(ObservableValue<? extends Medicine> observableValue, Medicine oldObat, Medicine newObat) {
                if (newObat != null) {
                    selectedObat = newObat;
                    fieldNamaObat.setText(selectedObat.getNamaObat());
                    fieldKomposisi.setText(selectedObat.getKomposisi());
                    fieldDosis.setText(selectedObat.getDosis());
                    fieldCaraPengunaan.setText(selectedObat.getCaraPenggunaan());
                    fieldEfekSamping.setText(selectedObat.getEfekSamping());
                    fieldProdusen.setText(selectedObat.getProdusen());
                    fieldHarga.setText(selectedObat.getHarga());
                    fieldKadaluarsa.setValue(selectedObat.getTanggalKedaluwarsa().toLocalDate());
                    displayProfilePicture(selectedObat.getGambarPath());
                }
            }
        });

        bersihkan();
    }
    private boolean validateFields() {
        if (fieldNamaObat.getText().isEmpty() || fieldKomposisi.getText().isEmpty() ||
                fieldDosis.getText().isEmpty() || fieldCaraPengunaan.getText().isEmpty() ||
                fieldProdusen.getText().isEmpty() || fieldHarga.getText().isEmpty() ||
                fieldKadaluarsa.getValue() == null) {
            new Alert(Alert.AlertType.ERROR, "Please fill all the fields").showAndWait();
            return false;
        }
        return true;
    }

    public Connection getConnection() {
        if (connection == null) {
            try {
                connection = DriverManager.getConnection(DB_URL);
                System.out.println("Connection established.");
            } catch (SQLException e) {
                e.printStackTrace();
                System.out.println("Connection failed: " + e.getMessage());
            }
        }
        return connection;
    }

    public void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public void createTable() {
        String obatTableSql = "CREATE TABLE IF NOT EXISTS katalog_obat ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "nama_obat TEXT NOT NULL,"
                + "komposisi TEXT NOT NULL,"
                + "dosis TEXT NOT NULL,"
                + "cara_penggunaan TEXT NOT NULL,"
                + "efek_samping TEXT,"
                + "produsen TEXT NOT NULL,"
                + "harga TEXT,"
                + "gambar_path TEXT,"
                + "tanggal_kedaluwarsa DATE"
                + ")";
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(obatTableSql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private ObservableList<Medicine> getObservableList() {
        return (ObservableList<Medicine>) obatFilteredList.getSource();
    }

    private Predicate<Medicine> createPredicate(String searchText) {
        return medicine -> {
            if (searchText == null || searchText.isEmpty()) return true;
            return searchFindsObat(medicine, searchText);
        };
    }

    private boolean searchFindsObat(Medicine obat, String searchText) {
        return (obat.getNamaObat().toLowerCase().contains(searchText.toLowerCase())) ||
                (obat.getKomposisi().toLowerCase().contains(searchText.toLowerCase())) ||
                (obat.getDosis().toLowerCase().contains(searchText.toLowerCase())) ||
                (obat.getCaraPenggunaan().toLowerCase().contains(searchText.toLowerCase())) ||
                (obat.getEfekSamping().toLowerCase().contains(searchText.toLowerCase())) ||
                (obat.getProdusen().toLowerCase().contains(searchText.toLowerCase())) ||
                (obat.getHarga().toLowerCase().contains(searchText.toLowerCase())) ||
                (obat.getTanggalKedaluwarsa().toString().contains(searchText.toLowerCase()));
    }

    private void getAllData() {
        String query = "SELECT * FROM katalog_obat";
        getObservableList().clear();
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String namaObat = resultSet.getString("nama_obat");
                String komposisi = resultSet.getString("komposisi");
                String dosis = resultSet.getString("dosis");
                String caraPenggunaan = resultSet.getString("cara_penggunaan");
                String efekSamping = resultSet.getString("efek_samping");
                String produsen = resultSet.getString("produsen");
                String harga = resultSet.getString("harga");
                String gambarPath = resultSet.getString("gambar_path");
                Date tanggal_kedaluwarsa = resultSet.getDate("tanggal_kedaluwarsa");
                Medicine obat = new Medicine(id, namaObat, komposisi, dosis, caraPenggunaan, efekSamping, produsen, harga, gambarPath, tanggal_kedaluwarsa);
                getObservableList().add(obat);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void bersihkan() {
        fieldNamaObat.clear();
        fieldKomposisi.clear();
        fieldDosis.clear();
        fieldCaraPengunaan.clear();
        fieldEfekSamping.clear();
        fieldProdusen.clear();
        fieldHarga.clear();
        fieldKadaluarsa.setValue(null);
        fotoObat.setImage(new Image(getClass().getResourceAsStream("/com/medcure/app/default_img.png")));

        fieldNamaObat.requestFocus();
        tabledataobat.getSelectionModel().clearSelection();
        selectedObat = null;
    }

    private boolean isObatUpdated() {
        if (selectedObat == null) {
            return false;
        }
        return !selectedObat.getNamaObat().equalsIgnoreCase(fieldNamaObat.getText()) ||
                !selectedObat.getKomposisi().equalsIgnoreCase(fieldKomposisi.getText()) ||
                !selectedObat.getDosis().equalsIgnoreCase(fieldDosis.getText()) ||
                !selectedObat.getCaraPenggunaan().equalsIgnoreCase(fieldCaraPengunaan.getText()) ||
                !selectedObat.getEfekSamping().equalsIgnoreCase(fieldEfekSamping.getText()) ||
                !selectedObat.getProdusen().equalsIgnoreCase(fieldProdusen.getText()) ||
                !selectedObat.getHarga().equalsIgnoreCase(fieldHarga.getText()) ||
                !selectedObat.getTanggalKedaluwarsa().toLocalDate().equals(fieldKadaluarsa.getValue());
    }

    private void onSave() {
        if (selectedObat == null) {
            insertObat();
        } else if (isObatUpdated()) {
            updateObat();
        } else {
            new Alert(Alert.AlertType.INFORMATION, "Tidak ada data yang diubah").showAndWait();
        }
        bersihkan();
    }

    private void onDelete() {
        if (selectedObat != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Konfirmasi");
            alert.setHeaderText("Apakah anda yakin ingin menghapus data?");
            alert.setContentText("Nama Obat: " + selectedObat.getNamaObat());

            alert.showAndWait().ifPresent(buttonType -> {
                if (buttonType == ButtonType.OK) {
                    deleteObat(selectedObat.getId());
                    bersihkan();
                }
            });
        } else {
            new Alert(Alert.AlertType.INFORMATION, "Silakan pilih data yang ingin dihapus").showAndWait();
        }
    }

    private void insertObat() {
        if (!validateFields()) return;

        String insertQuery = "INSERT INTO katalog_obat (nama_obat, komposisi, dosis, cara_penggunaan, efek_samping, produsen, harga, gambar_path, tanggal_kedaluwarsa) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(insertQuery)) {
            preparedStatement.setString(1, fieldNamaObat.getText());
            preparedStatement.setString(2, fieldKomposisi.getText());
            preparedStatement.setString(3, fieldDosis.getText());
            preparedStatement.setString(4, fieldCaraPengunaan.getText());
            preparedStatement.setString(5, fieldEfekSamping.getText());
            preparedStatement.setString(6, fieldProdusen.getText());
            preparedStatement.setString(7, fieldHarga.getText());
            preparedStatement.setString(8, (selectedObat != null ? selectedObat.getGambarPath() : null));
            preparedStatement.setDate(9, Date.valueOf(fieldKadaluarsa.getValue()));

            preparedStatement.executeUpdate();
            getAllData();
        } catch (SQLException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Error inserting data: " + e.getMessage()).showAndWait();
        }
    }


    private void updateObat() {
        String updateQuery = "UPDATE katalog_obat SET nama_obat = ?, komposisi = ?, dosis = ?, cara_penggunaan = ?, efek_samping = ?, produsen = ?, harga = ?, gambar_path = ?, tanggal_kedaluwarsa = ? WHERE id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(updateQuery)) {
            preparedStatement.setString(1, fieldNamaObat.getText());
            preparedStatement.setString(2, fieldKomposisi.getText());
            preparedStatement.setString(3, fieldDosis.getText());
            preparedStatement.setString(4, fieldCaraPengunaan.getText());
            preparedStatement.setString(5, fieldEfekSamping.getText());
            preparedStatement.setString(6, fieldProdusen.getText());
            preparedStatement.setString(7, fieldHarga.getText());
            preparedStatement.setString(8, selectedObat.getGambarPath());
            preparedStatement.setDate(9, Date.valueOf(fieldKadaluarsa.getValue()));
            preparedStatement.setInt(10, selectedObat.getId());
            preparedStatement.executeUpdate();
            getAllData();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void deleteObat(int id) {
        String deleteQuery = "DELETE FROM katalog_obat WHERE id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(deleteQuery)) {
            preparedStatement.setInt(1, id);
            preparedStatement.executeUpdate();
            getAllData();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleFotoObat(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Pilih Gambar");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg")
        );
        File selectedFile = fileChooser.showOpenDialog(new Stage());
        if (selectedFile != null) {
            if (!selectedFile.exists()) {
                new Alert(Alert.AlertType.ERROR, "Selected file does not exist: " + selectedFile.getAbsolutePath()).showAndWait();
                return;
            }

            try {
                // Ensure the destination directory exists
                File destinationDir = new File("foto_obat");
                if (!destinationDir.exists()) {
                    destinationDir.mkdirs();  // Create the directory if it doesn't exist
                }

                File destinationFile = new File(destinationDir, selectedFile.getName());
                Files.copy(selectedFile.toPath(), destinationFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

                if (selectedObat != null) {
                    selectedObat.setGambarPath(destinationFile.getAbsolutePath());
                }
                displayProfilePicture(destinationFile.getAbsolutePath());
            } catch (IOException e) {
                e.printStackTrace();
                new Alert(Alert.AlertType.ERROR, "Error uploading image: " + e.getMessage()).showAndWait();
            }
        }
    }
    @FXML
    public void onTambahObat(ActionEvent actionEvent) {
        bersihkan();

    }




    private void displayProfilePicture(String gambarPath) {
        if (gambarPath != null && !gambarPath.isEmpty()) {
            File file = new File(gambarPath);
            if (file.exists()) {
                Image image = new Image(file.toURI().toString());
                fotoObat.setImage(image);
            } else {
                fotoObat.setImage(new Image(getClass().getResourceAsStream("/com/medcure/app/default_img.png")));
            }
        } else {
            fotoObat.setImage(new Image(getClass().getResourceAsStream("/com/medcure/app/default_img.png")));
        }
    }

    @FXML
    private void handleOnSave(ActionEvent event) {
        onSave();
    }

    @FXML
    private void handleOnDelete(ActionEvent event) {
        onDelete();
    }

    @FXML
    private void handleOnReset(ActionEvent event) {
        searchBox.clear();
    }
    @FXML
    private void logout(ActionEvent event) throws IOException{
        GUI.setRoot("login-view","Login", false);
    }



//    private void redirectToLogin() {
//        try {
//            // Load halaman login
//            FXMLLoader loader = new FXMLLoader(getClass().getResource("login-view.fxml"));
//            Stage stage = (Stage) logout.getScene().getWindow();
//            stage.setScene(new Scene(loader.load()));
//            stage.show();
//        } catch (IOException e) {
//            e.printStackTrace();
//            // Tampilkan pesan kesalahan jika gagal mengarahkan
//            Alert alert = new Alert(Alert.AlertType.ERROR);
//            alert.setTitle("Error");
//            alert.setHeaderText(null);
//            alert.setContentText("Gagal mengarahkan ke halaman login.");
//            alert.showAndWait();
//        }
//    }
}
