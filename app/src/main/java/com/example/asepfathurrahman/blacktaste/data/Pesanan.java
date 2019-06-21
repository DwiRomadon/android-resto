package com.example.asepfathurrahman.blacktaste.data;

public class Pesanan {

    String id;
    String idMeja;
    String idMenu;
    String jumlahBeli;
    String catatan;
    String price;
    String namaMenu;

    public Pesanan(String id, String idMeja, String idMenu, String jumlahBeli, String catatan, String price, String namaMenu){
        this.id         = id;
        this.idMeja     = idMeja;
        this.idMenu     = idMenu;
        this.jumlahBeli = jumlahBeli;
        this.catatan    = catatan;
        this.price      = price;
        this.namaMenu   = namaMenu;
    }

    public Pesanan(){}

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIdMeja() {
        return idMeja;
    }

    public void setIdMeja(String idMeja) {
        this.idMeja = idMeja;
    }

    public String getIdMenu() {
        return idMenu;
    }

    public void setIdMenu(String idMenu) {
        this.idMenu = idMenu;
    }

    public String getJumlahBeli() {
        return jumlahBeli;
    }

    public void setJumlahBeli(String jumlahBeli) {
        this.jumlahBeli = jumlahBeli;
    }

    public String getCatatan() {
        return catatan;
    }

    public void setCatatan(String catatan) {
        this.catatan = catatan;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getNamaMenu() {
        return namaMenu;
    }

    public void setNamaMenu(String namaMenu) {
        this.namaMenu = namaMenu;
    }
}
