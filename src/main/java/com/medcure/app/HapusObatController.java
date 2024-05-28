package com.medcure.app;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

public class HapusObatController {

    @FXML
    private Button btnBalik1;

    @FXML
    private Button btnBersikan1;

    @FXML
    private Button btnHapusObat;

    @FXML
    private TableColumn<?, ?> caraPenggunaan;

    @FXML
    private TextField cariNamaObat;

    @FXML
    private TableColumn<?, ?> dosis;

    @FXML
    private TableColumn<?, ?> efekSamping;

    @FXML
    private TableColumn<?, ?> harga;

    @FXML
    private TableColumn<?, ?> id;

    @FXML
    private TableColumn<?, ?> komposisi;

    @FXML
    private TableColumn<?, ?> namaobat;

    @FXML
    private TableColumn<?, ?> produsen;

    @FXML
    private TableView<?> tableView;

    @FXML
    private TableColumn<?, ?> tglKadaluarsa;

}
