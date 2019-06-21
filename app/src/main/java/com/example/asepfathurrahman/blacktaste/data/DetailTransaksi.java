package com.example.asepfathurrahman.blacktaste.data;

public class DetailTransaksi {

    String idTransaksi, idMenu, idTransaksiDetail, namaMenu, fotoMenu, jumlahBeli, harga, totalHarga, catatan, stok, grandTot, status;

    public DetailTransaksi(String idTransaksi, String idMenu, String idTransaksiDetail, String namaMenu,
                           String fotoMenu, String jumlahBeli, String harga, String totalHarga, String catatan,
                           String stok, String grandTot, String status){

        this.idTransaksi        = idTransaksi;
        this.idMenu             = idMenu;
        this.idTransaksiDetail  = idTransaksiDetail;
        this.namaMenu           = namaMenu;
        this.fotoMenu           = fotoMenu;
        this.jumlahBeli         = jumlahBeli;
        this.harga              = harga;
        this.totalHarga         = totalHarga;
        this.catatan            = catatan;
        this.stok               = stok;
        this.grandTot           = grandTot;
        this.status             = status;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getGrandTot() {
        return grandTot;
    }

    public void setGrandTot(String grandTot) {
        this.grandTot = grandTot;
    }

    public String getStok() {
        return stok;
    }

    public void setStok(String stok) {
        this.stok = stok;
    }

    public String getCatatan() {
        return catatan;
    }

    public void setCatatan(String catatan) {
        this.catatan = catatan;
    }

    public String getIdTransaksi() {
        return idTransaksi;
    }

    public void setIdTransaksi(String idTransaksi) {
        this.idTransaksi = idTransaksi;
    }

    public String getIdMenu() {
        return idMenu;
    }

    public void setIdMenu(String idMenu) {
        this.idMenu = idMenu;
    }

    public String getIdTransaksiDetail() {
        return idTransaksiDetail;
    }

    public void setIdTransaksiDetail(String idTransaksiDetail) {
        this.idTransaksiDetail = idTransaksiDetail;
    }

    public String getNamaMenu() {
        return namaMenu;
    }

    public void setNamaMenu(String namaMenu) {
        this.namaMenu = namaMenu;
    }

    public String getFotoMenu() {
        return fotoMenu;
    }

    public void setFotoMenu(String fotoMenu) {
        this.fotoMenu = fotoMenu;
    }

    public String getJumlahBeli() {
        return jumlahBeli;
    }

    public void setJumlahBeli(String jumlahBeli) {
        this.jumlahBeli = jumlahBeli;
    }

    public String getHarga() {
        return harga;
    }

    public void setHarga(String harga) {
        this.harga = harga;
    }

    public String getTotalHarga() {
        return totalHarga;
    }

    public void setTotalHarga(String totalHarga) {
        this.totalHarga = totalHarga;
    }
}
