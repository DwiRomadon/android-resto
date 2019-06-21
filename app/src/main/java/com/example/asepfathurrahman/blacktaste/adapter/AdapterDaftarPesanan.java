package com.example.asepfathurrahman.blacktaste.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.asepfathurrahman.blacktaste.R;
import com.example.asepfathurrahman.blacktaste.data.DaftarPesanan;

import java.util.ArrayList;

public class AdapterDaftarPesanan extends ArrayAdapter<DaftarPesanan> {

    Context context;
    DaftarPesanan news;


    public AdapterDaftarPesanan(Context context, ArrayList<DaftarPesanan> a) {
        super(context,0,a);
        this.context = context;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(this.context);
        convertView = inflater.inflate(R.layout.content_daftar_pesanan, parent, false);

        news = getItem(position);

        TextView idtransaksi         = (TextView) convertView.findViewById(R.id.idTransaksi);
        TextView idKaryawan       = (TextView) convertView.findViewById(R.id.idKaryawan);
        TextView idMeja     = (TextView) convertView.findViewById(R.id.idMeja);
        TextView totalBayar     = (TextView) convertView.findViewById(R.id.totBayar);
        TextView status = (TextView) convertView.findViewById(R.id.status);
        TextView namaKaryawan    = (TextView) convertView.findViewById(R.id.namaKaryawan);

        idtransaksi.setText(news.getIdTransaksi());
        idtransaksi.setVisibility(View.GONE);
        idKaryawan.setText(news.getIdKaryawan());
        idKaryawan.setVisibility(View.GONE);
        idMeja.setText("No Meja \t\t\t\t: " + news.getIdMeja());
        totalBayar.setText("Total Bayar \t\t: "+ news.getTotalBayar());
        namaKaryawan.setText("Pelayan\t\t\t\t\t: "+news.getNamaKaryawan());

        if(news.getStatus().equals("wait")){
            status.setText("Status \t\t\t\t\t: " + news.getStatus());
            status.setTextColor(Color.RED);
        }
        return convertView;
    }

}
