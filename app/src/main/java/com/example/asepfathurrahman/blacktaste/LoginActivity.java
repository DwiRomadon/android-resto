package com.example.asepfathurrahman.blacktaste;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
import com.marozzi.roundbutton.RoundButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = LoginActivity.class.getSimpleName();

    private RoundButton loginBtn;
    private EditText edUsername;
    private  EditText edPassword;

    private ProgressDialog pDialog;
    private SessionManager session;
    SharedPreferences prefs;

    int socketTimeout = 30000;
    RetryPolicy policy = new DefaultRetryPolicy(socketTimeout,
            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);

    String id;
    String namaKar;
    String userName;
    String jabatan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginBtn    = (RoundButton) findViewById(R.id.btnLogin);
        edUsername  = (EditText) findViewById(R.id.edUsername);
        edPassword  = (EditText) findViewById(R.id.edPassword);

        prefs = getSharedPreferences("UserDetails",
                Context.MODE_PRIVATE);

        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        // Session manager
        session     = new SessionManager(getApplicationContext());
        id          = prefs.getString("id","");
        namaKar     = prefs.getString("fName","");
        userName    = prefs.getString("lName", "");
        jabatan     = prefs.getString("email", "");
        if(session.isLoggedIn()){
            Intent a = new Intent(getApplicationContext(), MainActivity.class);
            a.putExtra("id", id);
            a.putExtra("namaKar", namaKar);
            a.putExtra("userName", userName);
            a.putExtra("jabatan", jabatan);
            startActivity(a);
            finish();
        }

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String u = edUsername.getText().toString();
                String p = edPassword.getText().toString();

                if (u.isEmpty()){
                    Toast.makeText(getApplicationContext(), "Username tidak boleh kosong", Toast.LENGTH_SHORT).show();
                }else if(p.isEmpty()){
                    Toast.makeText(getApplicationContext(), "Password tidak boleh kosong", Toast.LENGTH_SHORT).show();
                }else if(p.isEmpty() && u.isEmpty()){
                    Toast.makeText(getApplicationContext(), "Username dan Password tidak boleh kosong", Toast.LENGTH_SHORT).show();
                }else {
                    checkLogin(u, p);
                }
            }
        });
    }

    public void checkLogin(final String username, final String password){

        //Tag used to cancel the request
        String tag_string_req = "req_login";

        //pDialog.setMessage("Login, Please Wait.....");
        //showDialog();
        loginBtn.startAnimation();

        StringRequest strReq = new StringRequest(Request.Method.POST,
                Config_URL.loginUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Login Response: " + response.toString());
                loginBtn.revertAnimation();

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean status = jObj.getBoolean("status");

                    if(status == true){

                        JSONObject user     = jObj.getJSONObject("user");
                        id                  = user.getString("id_karyawan");
                        namaKar             = user.getString("nama_karyawan");
                        userName            = user.getString("username");
                        jabatan             = user.getString("jabatan");
                        String msg          = jObj.getString("msg");

                        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();

                        session.setLogin(true);
                        storeRegIdinSharedPref(getApplicationContext(),id, namaKar, userName, jabatan);
                        Intent a = new Intent(getApplicationContext(), MainActivity.class);
                        a.putExtra("id", id);
                        a.putExtra("namaKar", namaKar);
                        a.putExtra("userName", username);
                        a.putExtra("jabatan", jabatan);
                        startActivity(a);
                        finish();
                        //Create login Session

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
                Log.e(TAG, "Login Error : " + error.getMessage());
                error.printStackTrace();
                loginBtn.revertAnimation();
            }
        }){

            @Override
            protected Map<String, String> getParams(){
                Map<String, String> params = new HashMap<String, String>();
                params.put("username", username);
                params.put("password", password);
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

    private void storeRegIdinSharedPref(Context context,String iduser,String namaKar, String userName, String jabatan) {

        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("id", iduser);
        editor.putString("namaKar", namaKar);
        editor.putString("userName", userName);
        editor.putString("jabatan", jabatan);
        editor.commit();
    }
}
