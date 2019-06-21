package com.example.asepfathurrahman.blacktaste.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.asepfathurrahman.blacktaste.R;
import com.example.asepfathurrahman.blacktaste.data.DetailTransaksi;
import com.example.asepfathurrahman.blacktaste.server.Config_URL;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class AdapterDetailTransaksi extends ArrayAdapter<DetailTransaksi> {

    Context context;
    DetailTransaksi news;


    public AdapterDetailTransaksi(Context context, ArrayList<DetailTransaksi> a) {
        super(context,0,a);
        this.context = context;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(this.context);
        convertView = inflater.inflate(R.layout.content_detail_transaksi, parent, false);

        news = getItem(position);

        TextView nama         = (TextView) convertView.findViewById(R.id.nama_menu);
        TextView jumlahbeli   = (TextView) convertView.findViewById(R.id.jumlahbeli);
        TextView total        = (TextView) convertView.findViewById(R.id.total);
        ImageView imgMenu     = (ImageView) convertView.findViewById(R.id.img_makanan);

        Picasso.get()
                .load(Config_URL.base_URL+"/assets/images/produk/"+ news.getFotoMenu())
                .resize(50, 50)
                .centerCrop()
                .into(imgMenu);

        nama.setText("Nama Menu \t: " + news.getNamaMenu());
        jumlahbeli.setText("Jumlah Beli \t: " + news.getJumlahBeli());
        total.setText("Total \t\t\t\t\t: " + news.getTotalHarga());

        return convertView;
    }
}
