package com.example.asepfathurrahman.blacktaste;


public class Makanan {

    private String idMenu;
    private String NamaMakanan;
    private String HargaMakanan;
    private String StokMakanan;
    private String FotoMakanan;

    public Makanan(String idMenu, String nama, String harga, String stok, String foto) {
        this.NamaMakanan = nama;
        this.HargaMakanan = harga;
        this.StokMakanan = stok;
        this.FotoMakanan = foto;
        this.idMenu      = idMenu;
    }

    public String getIdMenu() {
        return idMenu;
    }

    public void setIdMenu(String idMenu) {
        this.idMenu = idMenu;
    }

    //Getter
    public String getNamaMakanan() {
        return NamaMakanan;
    }

    public String getHargaMakanan() {
        return HargaMakanan;
    }

    public String getStokMakanan() {
        return StokMakanan;
    }

    public String getFotoMakanan() {
        return FotoMakanan;
    }

    //Setter
    public void setNamaMakanan(String nama) {
        NamaMakanan = nama;
    }

    public void setHargaMakanan(String harga) {
        HargaMakanan = harga;
    }

    public void setStokMakanan(String stok) {
        StokMakanan = stok;
    }

    public void setFotoMakanan(String foto) {
        FotoMakanan = foto;
    }
}
