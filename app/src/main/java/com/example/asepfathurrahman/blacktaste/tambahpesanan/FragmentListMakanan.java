package com.example.asepfathurrahman.blacktaste.tambahpesanan;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.asepfathurrahman.blacktaste.DetailPesanan;
import com.example.asepfathurrahman.blacktaste.Makanan;
import com.example.asepfathurrahman.blacktaste.R;
import com.example.asepfathurrahman.blacktaste.RecyclerViewAdapter;
import com.example.asepfathurrahman.blacktaste.adapter.AdapterListMakanan;
import com.example.asepfathurrahman.blacktaste.server.AppController;
import com.example.asepfathurrahman.blacktaste.server.Config_URL;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FragmentListMakanan extends Fragment {

    View v;
    private RecyclerView myrecyclerview;

    List<Makanan> oneMakanan = new ArrayList<Makanan>();

    AdapterListMakanan recyclerAdapter;

    int socketTimeout = 30000;
    RetryPolicy policy = new DefaultRetryPolicy(socketTimeout,
            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);

    private ProgressDialog pDialog;

    EditText search;
    LinearLayout linearPrice;
    TextView textHarga, textItem;

    public FragmentListMakanan() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.makanan_fragment,container,false);

        Intent a = getActivity().getIntent();
        final String noNeja = a.getStringExtra("nomeja");
        final String idKaryawan = a.getStringExtra("idkaryawan");
        String idTransaksi = a.getStringExtra("idtransaksi");

        myrecyclerview = (RecyclerView) v.findViewById(R.id.makanan_recyclerview);
        //TextView text = (TextView) v.findViewById(R.id.txtTest);
        //text.setText(test + test2);
        search = (EditText) v.findViewById(R.id.pencarian_makanan);
        linearPrice = (LinearLayout) v.findViewById(R.id.linearPrice);
        textHarga   = (TextView) v.findViewById(R.id.txtPrice);
        textItem    = (TextView) v.findViewById(R.id.txtItem);
        recyclerAdapter = new AdapterListMakanan(getContext(),oneMakanan);
        myrecyclerview.setLayoutManager(new LinearLayoutManager(getActivity()));
        myrecyclerview.setHasFixedSize(true);
        oneMakanan.clear();
        myrecyclerview.setAdapter(recyclerAdapter);

        linearPrice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent a = new Intent(getActivity(), DetailPesanan.class);
                a.putExtra("nomeja", noNeja);
                a.putExtra("idKaryawan", idKaryawan);
                startActivity(a);
                getActivity().finish();
            }
        });

        cari();

        return v;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        pDialog = new ProgressDialog(getActivity());
        pDialog.setCancelable(false);

        oneMakanan = new ArrayList<>();
        dataMakanan();

    }

    public void dataMakanan(){
        //Tag used to cancel the request
        String tag_string_req = "req";

        pDialog.setMessage("Please Wait.....");
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.GET,
                Config_URL.dataMenu, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(getTag(), "Login Response: " + response.toString());
                hideDialog();
                try {
                    JSONObject jObj = new JSONObject(response);

                    JSONArray data = jObj.getJSONArray("data");

                    for (int i = 0; i < data.length(); i++) {
                        JSONObject obj = data.getJSONObject(i);
                        String kodeMakanan = obj.getString("id_menu_kategori");
                        if(kodeMakanan.equals("2")){
                            String nama     = obj.getString("nama_menu");
                            double harga    = obj.getDouble("harga_menu");
                            String stok     = obj.getString("stock_menu");
                            String foto     = obj.getString("foto_menu");
                            String idMenu   = obj.getString("id_menu");

                            DecimalFormat kursIndonesia = (DecimalFormat) DecimalFormat.getCurrencyInstance();
                            DecimalFormatSymbols formatRp = new DecimalFormatSymbols();

                            formatRp.setCurrencySymbol("");
                            formatRp.setMonetaryDecimalSeparator(',');
                            formatRp.setGroupingSeparator('.');

                            kursIndonesia.setDecimalFormatSymbols(formatRp);
                            oneMakanan.add(new Makanan(idMenu, nama, kursIndonesia.format(harga), stok, foto));
                        }
                    }

                }catch (JSONException e){
                    //JSON error
                    e.printStackTrace();
                }
                recyclerAdapter.notifyDataSetChanged();
            }
        }, new Response.ErrorListener(){

            @Override
            public void onErrorResponse(VolleyError error){
                Log.e(getTag(), "Login Error : " + error.getMessage());
                error.printStackTrace();
                hideDialog();
            }
        }){

            @Override
            protected Map<String, String> getParams(){
                Map<String, String> params = new HashMap<String, String>();
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

    public void cari(){
        search.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {}

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            public void onTextChanged(CharSequence query, int start, int before, int count) {

                query = query.toString().toLowerCase();

                List<Makanan> filteredList = new ArrayList<Makanan>();

                for (int i = 0; i < oneMakanan.size(); i++) {

                    final String text = oneMakanan.get(i).getNamaMakanan().toLowerCase();
                    if (text.contains(query)) {

                        filteredList.add(oneMakanan.get(i));
                    }
                }

                recyclerAdapter = new AdapterListMakanan(getContext(),filteredList);
                myrecyclerview.setLayoutManager(new LinearLayoutManager(getActivity()));
                myrecyclerview.setAdapter(recyclerAdapter);
                recyclerAdapter.notifyDataSetChanged();
            }
        });
    }



}
