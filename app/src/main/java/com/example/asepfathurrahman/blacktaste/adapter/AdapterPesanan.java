package com.example.asepfathurrahman.blacktaste.adapter;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.asepfathurrahman.blacktaste.DaftarMenu;
import com.example.asepfathurrahman.blacktaste.DetailPesanan;
import com.example.asepfathurrahman.blacktaste.MainActivity;
import com.example.asepfathurrahman.blacktaste.R;
import com.example.asepfathurrahman.blacktaste.RecyclerViewAdapter;
import com.example.asepfathurrahman.blacktaste.data.Pesanan;
import com.example.asepfathurrahman.blacktaste.server.AppController;
import com.example.asepfathurrahman.blacktaste.server.Config_URL;
import com.marozzi.roundbutton.RoundButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdapterPesanan extends ArrayAdapter<Pesanan> {


    private Context context;

    int socketTimeout = 30000;
    RetryPolicy policy = new DefaultRetryPolicy(socketTimeout,
            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);

    String stokData;
    RoundButton btnHapus;
    Pesanan news;
    String idKaryawan, noMeja;

    public AdapterPesanan(Context context, ArrayList<Pesanan> a) {
        super(context,0,a);
        this.context = context;
        Intent intent = ((Activity)context).getIntent();
        idKaryawan = intent.getStringExtra("idKaryawan");
        noMeja = intent.getStringExtra("nomeja");
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(this.context);
        convertView = inflater.inflate(R.layout.content_pesanan, parent, false);

        news = getItem(position);

        TextView id         = (TextView) convertView.findViewById(R.id.id);
        TextView nama       = (TextView) convertView.findViewById(R.id.nama_menu);
        TextView noMeja     = (TextView) convertView.findViewById(R.id.id_meja);
        TextView idMenu     = (TextView) convertView.findViewById(R.id.id_menu);
        TextView jumlahBeli = (TextView) convertView.findViewById(R.id.jumlah_beli);
        TextView catatan    = (TextView) convertView.findViewById(R.id.catatan);
        TextView total      = (TextView) convertView.findViewById(R.id.price);

        id.setText(news.getId());
        id.setVisibility(View.GONE);
        nama.setText("Nama \t\t\t\t: " + news.getNamaMenu());
        noMeja.setText("No Meja \t\t\t: "+ news.getIdMeja());
        idMenu.setText("Id Menu \t\t: " + news.getIdMenu());
        idMenu.setVisibility(View.GONE);
        jumlahBeli.setText("Jumlah Beli\t: "+news.getJumlahBeli());

        if(news.getCatatan().equals("")){
            catatan.setText("Catatan \t\t\t: Tidak Ada Catatan");
            catatan.setTextColor(Color.RED);
        }else {
            catatan.setText("Catatan \t\t\t: " + news.getCatatan());
        }

        total.setText("Total \t\t\t\t\t: "+news.getPrice());
        /*btnHapus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getStock(news.getIdMenu());
            }
        });*/

        /*Intent a = new Intent(context, DetailPesanan.class);
        a.putExtra("data", String.valueOf(news.getNamaMenu()));*/
        return convertView;
    }

}

