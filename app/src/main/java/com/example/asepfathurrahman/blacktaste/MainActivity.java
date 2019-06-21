package com.example.asepfathurrahman.blacktaste;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.asepfathurrahman.blacktaste.server.AppController;
import com.example.asepfathurrahman.blacktaste.server.Config_URL;
import com.example.asepfathurrahman.blacktaste.session.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    CardView PesanCard, DaftarCard, LoginCard;

    private SessionManager session;
    private ProgressDialog pDialog;

    SharedPreferences prefs;
    String id       ;
    String namaKar  ;
    String userNmae ;
    String jabatan  ;


    private Spinner noMejaSpn;
    private ArrayList<String> spnNoMeja;

    //JSON Array
    private JSONArray result;


    int socketTimeout = 30000;
    RetryPolicy policy = new DefaultRetryPolicy(socketTimeout,
            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        spnNoMeja = new ArrayList<String>();

        // Session manager
        session = new SessionManager(getApplicationContext());

        //Session Login
        if(session.isLoggedIn()){
            prefs = getSharedPreferences("UserDetails",
                    Context.MODE_PRIVATE);
             id       = prefs.getString("id","");
             namaKar  = prefs.getString("namaKar","");
             userNmae = prefs.getString("userName", "");
             jabatan  = prefs.getString("jabatan", "");
        }

        PesanCard = (CardView) findViewById(R.id.pesan_card);
        DaftarCard = (CardView) findViewById(R.id.daftar_card);
        LoginCard = (CardView) findViewById(R.id.login_card);

        PesanCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(MainActivity.this);
                dialog.setContentView(R.layout.input_nomer_meja);
                Button btnInput = dialog.findViewById(R.id.button_input_meja);
                Button btnBatal = dialog.findViewById(R.id.button_batal_input);
                final EditText edtNamapemesan = dialog.findViewById(R.id.edNamaPemesan);
                noMejaSpn = (Spinner) dialog.findViewById(R.id.input_meja);
                getData();
                btnInput.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        String strNoMeja = noMejaSpn.getSelectedItem().toString();
                        String strNamaPemesan = edtNamapemesan.getText().toString();

                        //String[] parts = strNoMeja.split(" - ");

                        if (strNoMeja.equals("-- Pilih Nomor Meja --")){
                            Toast.makeText(getApplicationContext(), "Silahkan input nomor meja", Toast.LENGTH_SHORT).show();
                        }else {
                            Intent i = new Intent(MainActivity.this, DaftarMenu.class);
                            i.putExtra("nomeja", strNoMeja);
                            i.putExtra("namapemesan", strNamaPemesan);
                            i.putExtra("idkaryawan", id);
                            startActivity(i);
                            finish();
                        }
                    }
                });
                btnBatal.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(MainActivity.this, MainActivity.class));
                        finish();
                    }
                });
                dialog.show();
            }
        });

        DaftarCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, DaftarPesanan.class));
            }
        });


        LoginCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                session.setLogin(false);
                session.setSkip(false);
                session.setSessid(0);

                // Launching the login activity
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
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

    private void getData(){

        String tag_string_req = "req_";
        pDialog.setMessage("Loading.....");
        showDialog();
        //Creating a string request
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Config_URL.noMeja,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        hideDialog();
                        JSONObject j = null;
                        try {
                            //Parsing the fetched Json String to JSON Object
                            j = new JSONObject(response);

                            //Storing the Array of JSON String to our JSON Array
                            result = j.getJSONArray("data");

                            //Calling method getStudents to get the students from the JSON Array
                            getDataNya(result);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(String.valueOf(getApplicationContext()), "Login Error : " + error.getMessage());
                        error.printStackTrace();
                        Toast.makeText(getApplicationContext(),
                                error.getMessage(), Toast.LENGTH_LONG).show();
                        Toast.makeText(getApplicationContext(), "Please Check Your Network Connection", Toast.LENGTH_LONG).show();
                        hideDialog();
                    }
                });

        stringRequest.setRetryPolicy(policy);
        AppController.getInstance().addToRequestQueue(stringRequest, tag_string_req);
    }

    private void getDataNya(JSONArray j){
        //Traversing through all the items in the json array
        spnNoMeja.add("-- Pilih Nomor Meja --");
        for(int i=0;i<j.length();i++){
            try {
                //Getting json object
                JSONObject json = j.getJSONObject(i);

                //Adding the name of the student to array list
                spnNoMeja.add(json.getString("no_meja"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        //Setting adapter to show the items in the spinner
        noMejaSpn.setAdapter(new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_spinner_dropdown_item, spnNoMeja));
        ArrayAdapter<String> mAdapter;
        mAdapter = new ArrayAdapter<String>(this, R.layout.spinner_item, spnNoMeja);
        noMejaSpn.setAdapter(mAdapter);
    }
}
