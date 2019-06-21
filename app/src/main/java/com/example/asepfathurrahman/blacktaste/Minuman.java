package com.example.asepfathurrahman.blacktaste;


public class Minuman {

    private String NamaMinuman;
    private String HargaMinuman;
    private String StokMinuman;
    private String FotoMinuman;
    private String idMenu;

    public Minuman() {
    }


    public Minuman(String idMenu, String nama, String harga, String stok, String foto) {
        this.NamaMinuman = nama;
        this.HargaMinuman = harga;
        this.StokMinuman = stok;
        this.FotoMinuman = foto;
        this.idMenu      = idMenu;

    }

    //Getter
    public String getIdMenu() {
        return idMenu;
    }

    public String getNamaMinuman() {
        return NamaMinuman;
    }

    public String getHargaMinuman() {
        return HargaMinuman;
    }

    public String getStokMinuman() {
        return StokMinuman;
    }

    public String getFotoMinuman() {
        return FotoMinuman;
    }

    //Setter


    public void setNamaMinuman(String nama) {
        NamaMinuman = nama;
    }

    public void setHargaMinuman(String harga) {
        HargaMinuman = harga;
    }

    public void setStokMinuman(String stok) {
        StokMinuman = stok;
    }

    public void setFotoMinuman(String foto) {
        FotoMinuman = foto;
    }
    public void setIdMenu(String idMenu) {
        this.idMenu = idMenu;
    }

}
