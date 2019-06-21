package com.example.asepfathurrahman.blacktaste.server;

//This class is for storing all URLs as a model of URLs

public class Config_URL
{
    public static String base_URL           = "http://172.32.1.48/resto";
    public static String dataMenu           = base_URL + "/API/menu";
    public static String loginUrl           = base_URL + "/API/login";
    public static String noMeja             = base_URL + "/API/meja";
    public static String sumCount           = base_URL + "/API/sumCountPreTransaksi";
    public static String addPreTransaction  = base_URL + "/API/addPreeTransaktion";
    public static String detailPreTransakti = base_URL + "/API/detailPreTransakti";
    public static String getStok            = base_URL + "/API/getStok";
    public static String hapusPesanan       = base_URL + "/API/hapusPesanan";
    public static String inputTransaksi     = base_URL + "/API/inputPesanan";
    public static String inputTransaksiDetail= base_URL + "/API/transaksiDetail";
    public static String listPesanan        = base_URL + "/API/listPesanan";
    public static String listDetail         = base_URL + "/API/detailTransaksi";
    public static String cancelPesananDetail = base_URL + "/API/cancelPesananDetail";
    public static String editPesanan        = base_URL + "/API/editPesanan";
    public static String tambahDataPesanan  = base_URL + "/API/tambahPesanan";
    public static String getTotalBayar      = base_URL + "/API/selectDataTransaksi";
    public static String pesananDisajikan   = base_URL + "/API/updateStatus";
    public static String pesananDimasak   = base_URL + "/API/updateStatus2";
    public static String cekNoMeja          = base_URL + "/API/cekDataMeja";
    public static String updateNoMeja       = base_URL + "/API/updateNoMeja";

}