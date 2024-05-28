package com.medcure.app;

import com.medcure.data.Obat;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

public class PencarianController {

    @FXML
    private TableView<Obat> TabelPencarian;

    @FXML
    private TableColumn<Obat, Integer> id;

    @FXML
    private TableColumn<Obat, String> namaObat;

    @FXML
    private void initialize() {
        id.setCellValueFactory(new PropertyValueFactory<>("id"));
        namaObat.setCellValueFactory(new PropertyValueFactory<>("namaObat"));
    }

    public void setSearchResults(ObservableList<Obat> hasilPencarian) {
        TabelPencarian.setItems(hasilPencarian);
    }
}
