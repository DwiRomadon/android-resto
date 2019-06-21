package com.example.asepfathurrahman.blacktaste.data;

public class DaftarPesanan {

    String idTransaksi;
    String idKaryawan;
    String idMeja;
    String totalBayar;
    String status;
    String namaKaryawan;


    public DaftarPesanan(String idTransaksi,String idKaryawan,String idMeja,String totalBayar,String status,String namaKaryawan){
        this.idKaryawan  = idKaryawan;
        this.idTransaksi = idTransaksi;
        this.idMeja      = idMeja;
        this.totalBayar  = totalBayar;
        this.status      = status;
        this.namaKaryawan= namaKaryawan;
    }

    public String getIdTransaksi() {
        return idTransaksi;
    }

    public void setIdTransaksi(String idTransaksi) {
        this.idTransaksi = idTransaksi;
    }

    public String getIdKaryawan() {
        return idKaryawan;
    }

    public void setIdKaryawan(String idKaryawan) {
        this.idKaryawan = idKaryawan;
    }

    public String getIdMeja() {
        return idMeja;
    }

    public void setIdMeja(String idMeja) {
        this.idMeja = idMeja;
    }

    public String getTotalBayar() {
        return totalBayar;
    }

    public void setTotalBayar(String totalBayar) {
        this.totalBayar = totalBayar;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getNamaKaryawan() {
        return namaKaryawan;
    }

    public void setNamaKaryawan(String namaKaryawan) {
        this.namaKaryawan = namaKaryawan;
    }
}