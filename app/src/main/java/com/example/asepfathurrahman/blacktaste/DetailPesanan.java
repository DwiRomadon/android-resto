package com.example.asepfathurrahman.blacktaste;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.asepfathurrahman.blacktaste.adapter.AdapterPesanan;
import com.example.asepfathurrahman.blacktaste.data.Pesanan;
import com.example.asepfathurrahman.blacktaste.server.AppController;
import com.example.asepfathurrahman.blacktaste.server.Config_URL;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class DetailPesanan extends AppCompatActivity {


    int socketTimeout = 30000;
    RetryPolicy policy = new DefaultRetryPolicy(socketTimeout,
            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);

    private ProgressDialog pDialog;

    private Toolbar mTopToolbar;



    ArrayList<Pesanan> dataNya = new ArrayList<Pesanan>();

    AdapterPesanan adapter;
    ListView list;

    TextView totalHarga, item;


    String id;
    String idMeja;
    String idMenu;
    String jumbel;
    String catatan;
    double price;
    String namaMenu;

    LinearLayout linearButton;

    String noNeja;
    String idKaryawan;

    Dialog myDialog;

    String stokData;

    int stoks;

    double pricee;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_pesanan);


        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);


        Intent a = getIntent();
        idKaryawan = a.getStringExtra("idKaryawan");
        noNeja = a.getStringExtra("nomeja");

        totalHarga = (TextView) findViewById(R.id.idTotalHarga);
        item       = (TextView) findViewById(R.id.item);
        linearButton = (LinearLayout) findViewById(R.id.linearButton);

        mTopToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(mTopToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Detail Pesanan");
        mTopToolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.colorText));


        list = (ListView) findViewById(R.id.array_list);
        //dataNya.clear();
        list.setItemsCanFocus(false);

        myDialog = new Dialog(this);

        fungsiDialog();

        adapter = new AdapterPesanan(DetailPesanan.this, dataNya);
        list.setAdapter(adapter);


        dataPesanan(noNeja);
        getPrice(noNeja);

        dataPesananget(noNeja);

        linearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inputTransaksi(noNeja, idKaryawan, String.valueOf(pricee));
                Intent a = new Intent(DetailPesanan.this, OrderFix.class);
                a.putExtra("idKaryawan", idKaryawan);
                a.putExtra("nomeja", noNeja);
                startActivity(a);
                finish();
            }
        });
    }


    public void dataPesanan(final String idMejas){
        //Tag used to cancel the request
        String tag_string_req = "req";

        pDialog.setMessage("Please Wait.....");
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST,
                Config_URL.detailPreTransakti, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("Data", "Login Response: " + response.toString());
                hideDialog();
                try {
                    JSONObject jObj = new JSONObject(response);

                    String msg =jObj.getString("msg");

                    if(msg.equals("ok")) {
                        JSONArray data = jObj.getJSONArray("pesanan");
                        for (int i = 0; i < data.length(); i++) {

                            JSONObject obj = data.getJSONObject(i);
                            id = obj.getString("id");
                            idMeja = obj.getString("id_meja");
                            idMenu = obj.getString("id_menu");
                            jumbel = obj.getString("jumlah_beli");
                            catatan = obj.getString("catatan");
                            price = obj.getDouble("price");
                            namaMenu = obj.getString("nama_menu");

                            DecimalFormat kursIndonesia = (DecimalFormat) DecimalFormat.getCurrencyInstance();
                            DecimalFormatSymbols formatRp = new DecimalFormatSymbols();

                            formatRp.setCurrencySymbol("");
                            formatRp.setMonetaryDecimalSeparator(',');
                            formatRp.setGroupingSeparator('.');

                            kursIndonesia.setDecimalFormatSymbols(formatRp);
                            dataNya.add(new Pesanan(id, idMeja, idMenu, jumbel, catatan, kursIndonesia.format(price), namaMenu));
                            //Toast.makeText(getApplicationContext(), "Ini ada data", Toast.LENGTH_LONG).show();
                        }
                    }else {
                        Intent a = new Intent(DetailPesanan.this, DaftarMenu.class);
                        a.putExtra("nomeja", noNeja);
                        a.putExtra("idkaryawan", idKaryawan);
                        startActivity(a);
                        finish();
                    }


                }catch (JSONException e){
                    //JSON error
                    e.printStackTrace();
                }
                adapter.notifyDataSetChanged();
            }
        }, new Response.ErrorListener(){

            @Override
            public void onErrorResponse(VolleyError error){
                Log.e("Data", "Login Error : " + error.getMessage());
                error.printStackTrace();
                hideDialog();
            }
        }){

            @Override
            protected Map<String, String> getParams(){
                Map<String, String> params = new HashMap<String, String>();
                params.put("idMeja", idMejas);
                return params;
            }
        };

        strReq.setRetryPolicy(policy);
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }

    @Override
    public void onBackPressed() {
        Intent a = new Intent(DetailPesanan.this, DaftarMenu.class);
        a.putExtra("nomeja", noNeja);
        a.putExtra("idkaryawan", idKaryawan);
        startActivity(a);
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    public void getPrice(final String idMeja){

        //Tag used to cancel the request
        String tag_string_req = "req_login";

        pDialog.setMessage("Please Wait.....");
        showDialog();
        //loginBtn.startAnimation();

        StringRequest strReq = new StringRequest(Request.Method.POST,
                Config_URL.sumCount, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //Log.d(TAG, "Login Response: " + response.toString());
                //loginBtn.revertAnimation();
                hideDialog();
                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean status = jObj.getBoolean("status");

                    if(status == true){

                        JSONObject user      = jObj.getJSONObject("data");
                        pricee               = user.getDouble("price");
                        String items         = user.getString("count");

                            DecimalFormat kursIndonesia = (DecimalFormat) DecimalFormat.getCurrencyInstance();
                            DecimalFormatSymbols formatRp = new DecimalFormatSymbols();

                            formatRp.setCurrencySymbol("Rp. ");
                            formatRp.setMonetaryDecimalSeparator(',');
                            formatRp.setGroupingSeparator('.');

                            kursIndonesia.setDecimalFormatSymbols(formatRp);

                            totalHarga.setText("Total Harga\t: " + kursIndonesia.format(pricee));
                            item.setText("Item\t: " + items);

                    }else {
                        String error_msg = jObj.getString("msg");
                        Toast.makeText(getApplicationContext(), error_msg, Toast.LENGTH_LONG).show();

                    }

                }catch (JSONException e){
                    //JSON error
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener(){

            @Override
            public void onErrorResponse(VolleyError error){
                //Log.e(TAG, "Login Error : " + error.getMessage());
                error.printStackTrace();
                //loginBtn.revertAnimation();
                hideDialog();
            }
        }){

            @Override
            protected Map<String, String> getParams(){
                Map<String, String> params = new HashMap<String, String>();
                params.put("idMeja", idMeja);
                return params;
            }
        };

        strReq.setRetryPolicy(policy);
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }


    public void dataPesananget(final String menja){
        String tag_string_req = "req";

        pDialog.setMessage("Please Wait.....");
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST,
                Config_URL.detailPreTransakti, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("Data", "Login Response: " + response.toString());
                hideDialog();
                try {
                    JSONObject jObj = new JSONObject(response);
                    final JSONArray data = jObj.getJSONArray("pesanan");

                    for (int j = 0; j < data.length(); j++){
                        try {
                            JSONObject obj      = data.getJSONObject(j);
                            namaMenu     = obj.getString("nama_menu");
                            id           = obj.getString("id");
                            idMeja       = obj.getString("id_meja");
                            idMenu       = obj.getString("id_menu");
                            jumbel       = obj.getString("jumlah_beli");
                            catatan      = obj.getString("catatan");
                            price        = obj.getDouble("price");
                            namaMenu     = obj.getString("nama_menu");
                            //Toast.makeText(getApplicationContext(), idKaryawan +" "+ catatan, Toast.LENGTH_LONG).show();
                            //inputTransaksiDetail(idMenu, jumbel, catatan);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }catch (JSONException e){
                    //JSON error
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener(){

            @Override
            public void onErrorResponse(VolleyError error){
                Log.e("Data", "Login Error : " + error.getMessage());
                error.printStackTrace();
                hideDialog();
            }
        }){

            @Override
            protected Map<String, String> getParams(){
                Map<String, String> params = new HashMap<String, String>();
                params.put("idMeja", menja);
                return params;
            }
        };

        strReq.setRetryPolicy(policy);
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    public void fungsiDialog(){
        list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
                                           final int pos, long id) {

                myDialog.setContentView(R.layout.popup);
                TextView txtclose;
                txtclose =(TextView) myDialog.findViewById(R.id.txtclose);

                txtclose.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        myDialog.dismiss();
                    }
                });

                TextView namaMakanan = myDialog.findViewById(R.id.namaMakanan);
                TextView jumlahBeli  = myDialog.findViewById(R.id.jumlahbeli);
                TextView catatan     = myDialog.findViewById(R.id.catatan);
                Button   btnHapus    = myDialog.findViewById(R.id.btnDelete);

                namaMakanan.setText("Nama \t\t\t\t: " + dataNya.get(pos).getNamaMenu());
                jumlahBeli.setText("Jumlah Beli\t: " + dataNya.get(pos).getJumlahBeli());

                if(dataNya.get(pos).getCatatan().equals("")){
                    catatan.setText("Catatan \t\t\t: Tidak Ada Catatan");
                }else {
                    catatan.setText("Catatan \t\t\t: " + dataNya.get(pos).getCatatan());
                }

                btnHapus.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String tag_string_req = "req_login";

                        StringRequest strReq = new StringRequest(Request.Method.POST,
                                Config_URL.getStok, new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                Log.d("Data", "Login Response: " + response.toString());
                                //loginBtn.revertAnimation();

                                try {
                                    JSONObject jObj = new JSONObject(response);
                                    boolean status = jObj.getBoolean("status");

                                    if(status == true){

                                        String msg          = jObj.getString("msg");
                                        stokData            = jObj.getString("stok");
                                        stoks               = Integer.parseInt(dataNya.get(pos).getJumlahBeli()) + Integer.parseInt(stokData);
                                        deletePesanan(dataNya.get(pos).getId(), dataNya.get(pos).getIdMenu(), String.valueOf(stoks));
                                        Intent a = new Intent(getApplicationContext(), DetailPesanan.class);
                                        a.putExtra("nomeja", noNeja);
                                        a.putExtra("idKaryawan", idKaryawan);
                                        startActivity(a);
                                        finish();
                                    }else {
                                        String error_msg = jObj.getString("msg");
                                        Toast.makeText(getApplicationContext(), error_msg, Toast.LENGTH_LONG).show();
                                    }
                                }catch (JSONException e){
                                    //JSON error
                                    e.printStackTrace();
                                }
                            }
                        }, new Response.ErrorListener(){

                            @Override
                            public void onErrorResponse(VolleyError error){
                                //Log.e(String.valueOf("Data", "Login Error : " + error.getMessage());
                                error.printStackTrace();
                                //loginBtn.revertAnimation();
                            }
                        }){

                            @Override
                            protected Map<String, String> getParams(){
                                Map<String, String> params = new HashMap<String, String>();
                                params.put("idMenu", dataNya.get(pos).getIdMenu());
                                return params;
                            }
                        };

                        strReq.setRetryPolicy(policy);
                        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
                    }
                });

                myDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                myDialog.show();

                return true;
            }
        });
    }

    public void deletePesanan(final String id, final String idMenu, final String stok){

        String tag_string_req = "req_login";

        StringRequest strReq = new StringRequest(Request.Method.POST,
                Config_URL.hapusPesanan, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("Data", "Login Response: " + response.toString());
                //loginBtn.revertAnimation();

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean status = jObj.getBoolean("status");

                    if(status == true){
                        String msg          = jObj.getString("msg");
                        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
                    }else {
                        String error_msg = jObj.getString("msg");
                        Toast.makeText(getApplicationContext(), error_msg, Toast.LENGTH_LONG).show();

                    }

                }catch (JSONException e){
                    //JSON error
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener(){

            @Override
            public void onErrorResponse(VolleyError error){
                //Log.e(String.valueOf("Data", "Login Error : " + error.getMessage());
                error.printStackTrace();
                //loginBtn.revertAnimation();
            }
        }){

            @Override
            protected Map<String, String> getParams(){
                Map<String, String> params = new HashMap<String, String>();
                params.put("id", id);
                params.put("idMenu", idMenu);
                params.put("stok", stok);
                return params;
            }
        };

        strReq.setRetryPolicy(policy);
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }


    //input transaksi
    public void inputTransaksi(final String meja, final String staff, final String total){

        String tag_string_req = "req_login";

        StringRequest strReq = new StringRequest(Request.Method.POST,
                Config_URL.inputTransaksi, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("Data", "Login Response: " + response.toString());
                //loginBtn.revertAnimation();

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean status = jObj.getBoolean("status");

                }catch (JSONException e){
                    //JSON error
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener(){

            @Override
            public void onErrorResponse(VolleyError error){
                //Log.e(String.valueOf("Data", "Login Error : " + error.getMessage());
                error.printStackTrace();
                //loginBtn.revertAnimation();
            }
        }){

            @Override
            protected Map<String, String> getParams(){
                Map<String, String> params = new HashMap<String, String>();
                params.put("meja", meja);
                params.put("staf", staff);
                params.put("total_bayar", total);
                return params;
            }
        };

        strReq.setRetryPolicy(policy);
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }
}
