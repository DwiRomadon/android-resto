package com.example.asepfathurrahman.blacktaste;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.asepfathurrahman.blacktaste.adapter.AdapterDaftarPesanan;
import com.example.asepfathurrahman.blacktaste.server.AppController;
import com.example.asepfathurrahman.blacktaste.server.Config_URL;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class DaftarPesanan extends AppCompatActivity {

    private Toolbar mTopToolbar;

    int socketTimeout = 30000;
    RetryPolicy policy = new DefaultRetryPolicy(socketTimeout,
            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);

    private ProgressDialog pDialog;

    ArrayList<com.example.asepfathurrahman.blacktaste.data.DaftarPesanan> dataNya = new ArrayList<com.example.asepfathurrahman.blacktaste.data.DaftarPesanan>();

    AdapterDaftarPesanan adapter;
    ListView list;

    Dialog myDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.daftar_pesanan);

        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);
        myDialog = new Dialog(this);

        mTopToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(mTopToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Daftar Pesanan");
        mTopToolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.colorText));


        list = (ListView) findViewById(R.id.array_list);
        //dataNya.clear();
        list.setItemsCanFocus(false);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // TODO Auto-generated method stub
                Intent intent = new Intent(DaftarPesanan.this, DetailPesananFragment.class);
                intent.putExtra("idtransaksi", dataNya.get(position).getIdTransaksi());
                intent.putExtra("idkaryawan", dataNya.get(position).getIdKaryawan());
                intent.putExtra("nomeja", dataNya.get(position).getIdMeja());
                startActivity(intent);
                finish();
            }
        });

        adapter = new AdapterDaftarPesanan(DaftarPesanan.this, dataNya);
        list.setAdapter(adapter);
        list.setEmptyView(findViewById(R.id.textNodata));

        dataPesanan();
        longClick();

    }

    @Override
    public void onBackPressed() {
        Intent a = new Intent(DaftarPesanan.this, MainActivity.class);
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

    public void dataPesanan(){
        //Tag used to cancel the request
        String tag_string_req = "req";

        pDialog.setMessage("Please Wait.....");
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.GET,
                Config_URL.listPesanan, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("Data", "Login Response: " + response.toString());
                hideDialog();
                try {
                    JSONObject jObj = new JSONObject(response);

                    String msg =jObj.getString("msg");

                    if(msg.equals("ok")) {
                        JSONArray data = jObj.getJSONArray("data");
                        for (int i = 0; i < data.length(); i++) {

                            JSONObject obj = data.getJSONObject(i);
                            String idTransaksi = obj.getString("id_transaksi");
                            String idKaryawan = obj.getString("id_karyawan");
                            String idMMeja = obj.getString("id_meja");
                            double totalBayar = obj.getDouble("total_bayar");
                            String statusTransaksi = obj.getString("status_trans");
                            String namaKaryawan = obj.getString("nama_karyawan");

                            DecimalFormat kursIndonesia = (DecimalFormat) DecimalFormat.getCurrencyInstance();
                            DecimalFormatSymbols formatRp = new DecimalFormatSymbols();

                            formatRp.setCurrencySymbol("");
                            formatRp.setMonetaryDecimalSeparator(',');
                            formatRp.setGroupingSeparator('.');

                            kursIndonesia.setDecimalFormatSymbols(formatRp);

                            if(statusTransaksi.equals("wait")){
                                dataNya.add(new com.example.asepfathurrahman.blacktaste.data.DaftarPesanan(idTransaksi, idKaryawan, idMMeja, kursIndonesia.format(totalBayar), statusTransaksi, namaKaryawan));
                            }
                        }
                    }else {
                        Intent a = new Intent(DaftarPesanan.this, MainActivity.class);
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
        });

        strReq.setRetryPolicy(policy);
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    void longClick(){
        list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                myDialog.setContentView(R.layout.popup_update_no_meja);
                TextView txtclose;
                final EditText edtNoMeja;
                txtclose =(TextView) myDialog.findViewById(R.id.txtclose);
                edtNoMeja=(EditText) myDialog.findViewById(R.id.edtNomeje);

                //Toast.makeText(getApplicationContext(), dataNya.get(position).getIdTransaksi(), Toast.LENGTH_LONG).show();

                edtNoMeja.setText(dataNya.get(position).getIdMeja());
                txtclose.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        myDialog.dismiss();
                    }
                });

                Button btnEdit;

                btnEdit = (Button) myDialog.findViewById(R.id.btnEdit);

                btnEdit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        String nomeja = edtNoMeja.getText().toString();

                        updateNoMeja(dataNya.get(position).getIdTransaksi(), nomeja);
                    }
                });

                myDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                myDialog.show();
                return true;
            }
        });
    }

    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }

    public void updateNoMeja(final String idTransaksi, final String idMeja){

        String tag_string_req = "req_login";

        pDialog.setMessage("Mohon Tunggu");
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST,
                Config_URL.updateNoMeja, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("Data", "Login Response: " + response.toString());
                //loginBtn.revertAnimation();
                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean status = jObj.getBoolean("status");

                    if(status == true){
                        String msg          = jObj.getString("msg");
                        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
                        Intent a = new Intent(getApplicationContext(), DaftarPesanan.class);
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
                hideDialog();
            }
        }){

            @Override
            protected Map<String, String> getParams(){
                Map<String, String> params = new HashMap<String, String>();
                params.put("idMeja", idMeja);
                params.put("idtrasaksi", idTransaksi);
                return params;
            }
        };

        strReq.setRetryPolicy(policy);
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }
}