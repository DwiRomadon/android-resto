package com.example.asepfathurrahman.blacktaste;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.asepfathurrahman.blacktaste.data.Pesanan;
import com.example.asepfathurrahman.blacktaste.server.AppController;
import com.example.asepfathurrahman.blacktaste.server.Config_URL;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.HashMap;
import java.util.Map;

public class OrderFix extends AppCompatActivity {

    int socketTimeout = 30000;
    RetryPolicy policy = new DefaultRetryPolicy(socketTimeout,
            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);

    private ProgressDialog pDialog;

    String noNeja;
    String idKaryawan;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_fix);

        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);


        Intent a = getIntent();
        idKaryawan = a.getStringExtra("idKaryawan");
        noNeja = a.getStringExtra("nomeja");
        dataPesananget(noNeja);
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
                            String namaMenu     = obj.getString("nama_menu");
                            String id           = obj.getString("id");
                            String idMeja       = obj.getString("id_meja");
                            String idMenu       = obj.getString("id_menu");
                            String jumbel       = obj.getString("jumlah_beli");
                            String catatan      = obj.getString("catatan");
                            double price        = obj.getDouble("price");
                            namaMenu     = obj.getString("nama_menu");
                            //Toast.makeText(getApplicationContext(), idKaryawan +" "+ catatan, Toast.LENGTH_LONG).show();
                            inputTransaksiDetail(idMenu, jumbel, catatan);
                            Intent a = new Intent(OrderFix.this, MainActivity.class);
                            a.putExtra("idkaryawan", idKaryawan);
                            startActivity(a);
                            finish();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    Toast.makeText(getApplicationContext(), "Berhasil memesan", Toast.LENGTH_LONG).show();
                    //if(status == false){

                   // }
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

    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }

    //input transaksi
    public void inputTransaksiDetail(final String idMenu, final String jumbel, final String catatan){

        String tag_string_req = "req_login";

        StringRequest strReq = new StringRequest(Request.Method.POST,
                Config_URL.inputTransaksiDetail, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("Data", "Login Response: " + response.toString());
                //loginBtn.revertAnimation();

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean status = jObj.getBoolean("status");

                    /*if(status == true){
                        String msg          = jObj.getString("msg");
                        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
                    }else {
                        String error_msg = jObj.getString("msg");
                        Toast.makeText(getApplicationContext(), error_msg, Toast.LENGTH_LONG).show();

                    }*/

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
                params.put("idMenu", idMenu);
                params.put("jumbel", jumbel);
                params.put("catatan", catatan);
                return params;
            }
        };

        strReq.setRetryPolicy(policy);
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

}
