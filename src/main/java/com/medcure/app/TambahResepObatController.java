package com.medcure.app;

import com.medcure.data.Medicine;
import com.medcure.data.Obat;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class TambahResepObatController {


    @FXML
    private TableView<Medicine> tabelTambahObat; // Menggunakan kelas Obat sebagai model

    @FXML
    private TableColumn<Medicine, Integer> id;

    @FXML
    private TableColumn<Medicine, String> namaobat;

    @FXML
    private TableColumn<Medicine, String> komposisi;

    @FXML
    private TableColumn<Medicine, String> dosis;

    @FXML
    private TableColumn<Medicine, String> caraPenggunaan;

    @FXML
    private TableColumn<Medicine, String> efekSamping;

    @FXML
    private TableColumn<Medicine, String> produsen;

    @FXML
    private TableColumn<Medicine, String> harga;

    @FXML
    private TableColumn<Medicine, String> tglKadaluarsa;
    @FXML
    private Button btnSimpan;


    @FXML
    private TextField searchBox;



    @FXML
    void handleSimpan() {
        // Implementasi logika untuk tombol Simpan
        System.out.println("Menyimpan data obat...");
    }

    @FXML
    private void handleHapus() {
        // Implementasi logika untuk tombol Hapus
        System.out.println("Menghapus data obat...");
    }

    public void initialize() {
        // Membuat list yang akan menampung data obat
        ObservableList<Medicine> medicineList = FXCollections.observableArrayList();

        // Mengambil data obat dari database dan menambahkannya ke dalam list
        fetchDataFromDatabase(medicineList);

        // Menambahkan data list ke dalam tabel
        tabelTambahObat.setItems(medicineList);

        // Menghubungkan setiap TableColumn dengan properti yang sesuai dari kelas Medicine
        id.setCellValueFactory(cellData -> cellData.getValue().idProperty().asObject());
        namaobat.setCellValueFactory(cellData -> cellData.getValue().namaObatProperty());
        komposisi.setCellValueFactory(cellData -> cellData.getValue().komposisiProperty());
        dosis.setCellValueFactory(cellData -> cellData.getValue().dosisProperty());
        caraPenggunaan.setCellValueFactory(cellData -> cellData.getValue().caraPenggunaanProperty());
        efekSamping.setCellValueFactory(cellData -> cellData.getValue().efekSampingProperty());
        produsen.setCellValueFactory(cellData -> cellData.getValue().produsenProperty());
        harga.setCellValueFactory(cellData -> cellData.getValue().hargaProperty());
        tglKadaluarsa.setCellValueFactory(cellData -> {
            ObjectProperty<Date> tanggalKedaluwarsa = cellData.getValue().tanggalKedaluwarsaProperty();
            if (tanggalKedaluwarsa.getValue() != null) {
                return new SimpleStringProperty(tanggalKedaluwarsa.getValue().toString());
            } else {
                return new SimpleStringProperty("");
            }
        });

    }


    private void fetchDataFromDatabase(ObservableList<Medicine> obatList) {
        String url = "jdbc:sqlite:mydatabase.db";
        String sql = "SELECT * FROM katalog_obat";
        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                int id = rs.getInt("id");
                String namaObat = rs.getString("nama_obat");
                String komposisi = rs.getString("komposisi");
                String dosis = rs.getString("dosis");
                String caraPenggunaan = rs.getString("cara_penggunaan");
                String efekSamping = rs.getString("efek_samping");
                String produsen = rs.getString("produsen");
                String harga = rs.getString("harga");
                String gambarPath = rs.getString("gambar_path");
                long millis = rs.getLong("tanggal_kedaluwarsa");

                // Buat objek Date dari nilai milliseconds
                Date tanggalKedaluwarsa = new Date(millis);

                // Buat objek Medicine dari data yang diambil dari database
                Medicine medicine = new Medicine(id, namaObat, komposisi, dosis, caraPenggunaan, efekSamping, produsen, harga, gambarPath, tanggalKedaluwarsa);

                // Tambahkan objek Medicine ke dalam obatList
                obatList.add(medicine);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


}
