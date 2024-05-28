package com.medcure.data;

public class Obat {
    private int id;
    private String namaObat;

    // Constructor, getters and setters
    public Obat(int id, String namaObat) {
        this.id = id;
        this.namaObat = namaObat;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNamaObat() {
        return namaObat;
    }

    public void setNamaObat(String namaObat) {
        this.namaObat = namaObat;
    }
}
