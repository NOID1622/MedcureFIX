package com.medcure.data;

import javafx.beans.property.*;

import java.sql.Date;

public class Medicine {
    private final IntegerProperty id;
    private final StringProperty namaObat;
    private final StringProperty komposisi;
    private final StringProperty dosis;
    private final StringProperty caraPenggunaan;
    private final StringProperty efekSamping;
    private final StringProperty produsen;
    private final StringProperty harga;
    private final ObjectProperty<Date> tanggalKedaluwarsa;
    private final StringProperty gambarPath;

    public Medicine(int id, String namaObat, String komposisi, String dosis, String caraPenggunaan, String efekSamping, String produsen, String harga, String gambarPath, Date tanggalKedaluwarsa) {
        this.id = new SimpleIntegerProperty(id);
        this.namaObat = new SimpleStringProperty(namaObat);
        this.komposisi = new SimpleStringProperty(komposisi);
        this.dosis = new SimpleStringProperty(dosis);
        this.caraPenggunaan = new SimpleStringProperty(caraPenggunaan);
        this.efekSamping = new SimpleStringProperty(efekSamping);
        this.produsen = new SimpleStringProperty(produsen);
        this.harga = new SimpleStringProperty(harga);
        this.gambarPath = new SimpleStringProperty(gambarPath);
        this.tanggalKedaluwarsa = new SimpleObjectProperty<>(tanggalKedaluwarsa);
    }

    // Getter methods
    public int getId() {
        return id.get();
    }

    public IntegerProperty idProperty() {
        return id;
    }

    public String getNamaObat() {
        return namaObat.get();
    }

    public StringProperty namaObatProperty() {
        return namaObat;
    }

    public String getKomposisi() {
        return komposisi.get();
    }

    public StringProperty komposisiProperty() {
        return komposisi;
    }

    public String getDosis() {
        return dosis.get();
    }

    public StringProperty dosisProperty() {
        return dosis;
    }

    public String getCaraPenggunaan() {
        return caraPenggunaan.get();
    }

    public StringProperty caraPenggunaanProperty() {
        return caraPenggunaan;
    }

    public String getEfekSamping() {
        return efekSamping.get();
    }

    public StringProperty efekSampingProperty() {
        return efekSamping;
    }

    public String getProdusen() {
        return produsen.get();
    }

    public StringProperty produsenProperty() {
        return produsen;
    }

    public String getHarga() {
        return harga.get();
    }

    public StringProperty hargaProperty() {
        return harga;
    }

    public Date getTanggalKedaluwarsa() {
        return tanggalKedaluwarsa.get();
    }

    public ObjectProperty<Date> tanggalKedaluwarsaProperty() {
        return tanggalKedaluwarsa;
    }

    public String getGambarPath() {
        return gambarPath.get();
    }

    public StringProperty gambarPathProperty() {
        return gambarPath;
    }

    // Setter method for gambarPath
    public void setGambarPath(String gambarPath) {
        this.gambarPath.set(gambarPath);
    }
}
