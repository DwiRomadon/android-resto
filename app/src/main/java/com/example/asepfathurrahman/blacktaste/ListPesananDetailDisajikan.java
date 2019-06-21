package com.example.asepfathurrahman.blacktaste;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.asepfathurrahman.blacktaste.adapter.AdapterDetailTransaksi;
import com.example.asepfathurrahman.blacktaste.data.DetailTransaksi;
import com.example.asepfathurrahman.blacktaste.server.AppController;
import com.example.asepfathurrahman.blacktaste.server.Config_URL;
import com.example.asepfathurrahman.blacktaste.tambahpesanan.DaftarMenuTambah;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ListPesananDetailDisajikan extends Fragment {

    int socketTimeout = 30000;
    RetryPolicy policy = new DefaultRetryPolicy(socketTimeout,
            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);

    private ProgressDialog pDialog;

    private Toolbar mTopToolbar;

    View v;


    ArrayList<DetailTransaksi> dataNya = new ArrayList<DetailTransaksi>();

    AdapterDetailTransaksi adapter;
    ListView list;

    String idTransaksi, idKaryawan, noMeja;

    Dialog myDialog;

    ImageView imgTambahMenu;
    TextView txtView;

    public ListPesananDetailDisajikan(){

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.activity_list_pesanan_detail,container,false);

        pDialog = new ProgressDialog(getActivity());
        pDialog.setCancelable(false);


        Intent a    = getActivity().getIntent();
        idTransaksi = a.getStringExtra("idtransaksi");
        idKaryawan  = a.getStringExtra("idkaryawan");
        noMeja      = a.getStringExtra("nomeja");

        list = (ListView) v.findViewById(R.id.array_list);
        txtView = (TextView) v.findViewById(R.id.textNodata) ;
        dataNya.clear();
        list.setEmptyView(txtView);
//        list.setItemsCanFocus(false);
        adapter = new AdapterDetailTransaksi(getActivity(), dataNya);
        list.setAdapter(adapter);

        myDialog = new Dialog(getActivity());

        imgTambahMenu = (ImageView) v.findViewById(R.id.viewTambahPesanan);

        imgTambahMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), DaftarMenuTambah.class);
                i.putExtra("nomeja", noMeja);
                i.putExtra("idtransaksi", idTransaksi);
                i.putExtra("idkaryawan", idKaryawan);
                startActivity(i);
                getActivity().finish();

            }
        });

        data(idTransaksi);
        fungsiDialog();

        return v;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }


    public void data(final String idtransaksi){
        //Tag used to cancel the request
        String tag_string_req = "req";

        pDialog.setMessage("Please Wait.....");
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST,
                Config_URL.listDetail, new Response.Listener<String>() {
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

                            JSONObject obj          = data.getJSONObject(i);
                            String idtransaksii     = obj.getString("id_transaksi");
                            String idMenu           = obj.getString("id_menu");
                            String idtransDetail    = obj.getString("id_transaksi_detail");
                            String nama_menu        = obj.getString("nama_menu");
                            String foto             = obj.getString("foto_menu");
                            String jumlahBeli       = obj.getString("jumlah_beli");
                            String total                   = obj.getString("total");
                            String harga            = obj.getString("harga_menu");
                            String catatan          = obj.getString("catatan_detail");
                            String stok             = obj.getString("stock_menu");
                            String grandtot         = obj.getString("total_bayar");
                            String status           = obj.getString("status");

                            DecimalFormat kursIndonesia = (DecimalFormat) DecimalFormat.getCurrencyInstance();
                            DecimalFormatSymbols formatRp = new DecimalFormatSymbols();

                            formatRp.setCurrencySymbol("");
                            formatRp.setMonetaryDecimalSeparator(',');
                            formatRp.setGroupingSeparator('.');


                            kursIndonesia.setDecimalFormatSymbols(formatRp);

                            if(status.equals("disajikan")){
                                dataNya.add(new
                                        DetailTransaksi(idtransaksii, idMenu, idtransDetail,nama_menu, foto, jumlahBeli, harga, total, catatan, stok, grandtot, status));
                                txtView.setVisibility(View.VISIBLE);
                            }
                            //Toast.makeText(getApplicationContext(), "Ini ada data", Toast.LENGTH_LONG).show();
                            //adapter.remove(adapter.getItem(position));
                        }
                    }else {
                        Intent a = new Intent(getActivity(), DaftarPesanan.class);
                        startActivity(a);
                        getActivity().finish();
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
                params.put("idtransaksi", idtransaksi);
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


    public void fungsiDialog(){
        list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                myDialog.setContentView(R.layout.edit_hapus_pesanan);
                TextView txtclose;
                txtclose =(TextView) myDialog.findViewById(R.id.txtclose);


                EditText namaMakanan = myDialog.findViewById(R.id.namaMakanan);
                final EditText jumbel      = myDialog.findViewById(R.id.jumlahBeli);
                final EditText catatan     = myDialog.findViewById(R.id.catatan);
                final EditText harga       = myDialog.findViewById(R.id.harga);
                final EditText totHarga    = myDialog.findViewById(R.id.totalHarga);

                namaMakanan.setText(dataNya.get(position).getNamaMenu());
                namaMakanan.setEnabled(false);
                jumbel.setText(dataNya.get(position).getJumlahBeli());
                catatan.setText(dataNya.get(position).getCatatan());

                harga.setText(dataNya.get(position).getHarga());
                harga.setEnabled(false);
                totHarga.setText(dataNya.get(position).getTotalHarga());
                totHarga.setEnabled(false);

                final Button btnEdit = (Button) myDialog.findViewById(R.id.btnEdit);
                final Button btnEdit2 = (Button) myDialog.findViewById(R.id.btnEdit2);
               //x Toast.makeText(getApplicationContext(), dataNya.get(position).getIdTransaksiDetail(), Toast.LENGTH_LONG).show();


                final String[] text = new String[1];
                final String[] intJumbel = new String[1];
                final String[] intHarga = new String[1];
                final int[] intTotHarga = new int[1];

                jumbel.addTextChangedListener(new TextWatcher() {

                    public void afterTextChanged(Editable s) {}

                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                    public void onTextChanged(CharSequence query, int start, int before, int count) {

                        text[0] = jumbel.getText().toString();
                        if(text[0].equals("")){
                            totHarga.setText("0");
                            totHarga.setEnabled(false);
                        }else {
                            intJumbel[0] = jumbel.getText().toString();
                            intHarga[0] = harga.getText().toString();
                            intTotHarga[0] = Integer.parseInt(intJumbel[0]) * Integer.parseInt(intHarga[0]);
                            totHarga.setText(String.valueOf(intTotHarga[0]));
                            totHarga.setEnabled(false);

                            int hitStok = Integer.parseInt(jumbel.getText().toString());

                            int intConvertIntText = Integer.parseInt(text[0]);
                            int intConvertJumbel  = Integer.parseInt(dataNya.get(position).getJumlahBeli());

                            final int hitGrandTotal;

                            if(intConvertIntText > intConvertJumbel){
                                int totalJumbel = Integer.parseInt(totHarga.getText().toString());
                                final int hitTotAwal = Integer.parseInt(dataNya.get(position).getGrandTot()) - Integer.parseInt(dataNya.get(position).getTotalHarga());
                                hitGrandTotal =  hitTotAwal + totalJumbel;
                                //Toast.makeText(getApplicationContext(), String.valueOf(hitGrandTotal), Toast.LENGTH_LONG).show();
                                final int stoksUntukEdit = Integer.parseInt(dataNya.get(position).getStok()) - hitStok;
                                btnEdit2.setVisibility(View.GONE);
                                btnEdit.setVisibility(View.VISIBLE);

                                btnEdit.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {

                                        String catat = catatan.getText().toString();
                                        String jumbels = jumbel.getText().toString();

                                        editPesanan(dataNya.get(position).getIdTransaksiDetail(),dataNya.get(position).getIdMenu()
                                                ,String.valueOf(stoksUntukEdit),String.valueOf(hitGrandTotal),dataNya.get(position).getIdTransaksi(),
                                                catat,jumbels);
                                    }
                                });
                            }else if(intConvertIntText == intConvertJumbel){
                                hitGrandTotal = Integer.parseInt(dataNya.get(position).getGrandTot());
                            }else {
                                int totalJumbel = Integer.parseInt(totHarga.getText().toString());
                                final int hitTotAwal = Integer.parseInt(dataNya.get(position).getGrandTot()) - Integer.parseInt(dataNya.get(position).getTotalHarga());
                                hitGrandTotal =  hitTotAwal + totalJumbel;
                                //Toast.makeText(getApplicationContext(), String.valueOf(hitGrandTotal), Toast.LENGTH_LONG).show();
                                final int stoksUntukEdit = Integer.parseInt(dataNya.get(position).getStok()) + hitStok;

                                btnEdit2.setVisibility(View.GONE);
                                btnEdit.setVisibility(View.VISIBLE);

                                btnEdit.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {

                                        String catat = catatan.getText().toString();
                                        String jumbels = jumbel.getText().toString();

                                        editPesanan(dataNya.get(position).getIdTransaksiDetail(),dataNya.get(position).getIdMenu()
                                                ,String.valueOf(stoksUntukEdit),String.valueOf(hitGrandTotal),dataNya.get(position).getIdTransaksi(),
                                                catat,jumbels);
                                    }
                                });
                            }
                        }
                    }
                });

                txtclose.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        myDialog.dismiss();
                    }
                });

                final int stoksUntukHapus = Integer.parseInt(dataNya.get(position).getStok()) + Integer.parseInt(dataNya.get(position).getJumlahBeli());
                final int totalHarga   = Integer.parseInt(dataNya.get(position).getGrandTot()) - Integer.parseInt(dataNya.get(position).getTotalHarga());

                Button btnHapus = (Button) myDialog.findViewById(R.id.btnDelete);
                btnHapus.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        String cekJumbel = jumbel.getText().toString();

                        if(!cekJumbel.isEmpty()){

                            deletePesanan(dataNya.get(position).getIdTransaksiDetail(), dataNya.get(position).getIdMenu(),
                                    String.valueOf(stoksUntukHapus), String.valueOf(totalHarga), dataNya.get(position).getIdTransaksi());

                            //Toast.makeText(getApplication()," Stok"+String.valueOf(stoksUntukHapus) +"Grand Total "+String.valueOf(totalHarga), Toast.LENGTH_LONG).show();
                        }else {
                            Toast.makeText(getActivity(), "Jumlah beli salah", Toast.LENGTH_LONG).show();
                        }
                    }
                });


                //edit tanpa jumlah beli
                btnEdit2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        String catat = catatan.getText().toString();
                        String jumbels = jumbel.getText().toString();

                        editPesanan(dataNya.get(position).getIdTransaksiDetail(),dataNya.get(position).getIdMenu()
                                ,dataNya.get(position).getStok(),dataNya.get(position).getGrandTot(),dataNya.get(position).getIdTransaksi(),
                                catat,jumbels);
                        //Toast.makeText(getApplicationContext(), "Ini Button untuk catatan", Toast.LENGTH_LONG).show();

                    }
                });


                myDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                myDialog.show();

                return true;
            }
        });
    }


    public void editPesanan(final String idTransaksiDetail, final String idMenu, final String stok, final String grandTotal,
                            final String idtransaksi, final String catatan, final String jumlahBeli){

        String tag_string_req = "req_login";

        pDialog.setMessage("Mohon Tunggu");
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST,
                Config_URL.editPesanan, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("Data", "Login Response: " + response.toString());
                //loginBtn.revertAnimation();
                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean status = jObj.getBoolean("status");

                    if(status == true){
                        //String msg          = jObj.getString("msg");
                        Toast.makeText(getActivity(), "Berhasil dirubah", Toast.LENGTH_LONG).show();
                        Intent a = new Intent(getActivity(), DetailPesananFragment.class);
                        a.putExtra("idtransaksi", idTransaksi);
                        startActivity(a);
                        getActivity().finish();
                    }else {
                        String error_msg = jObj.getString("msg");
                        Toast.makeText(getActivity(), error_msg, Toast.LENGTH_LONG).show();

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
                params.put("idTransaksiDetail", idTransaksiDetail);
                params.put("idMenu", idMenu);
                params.put("stok", stok);
                params.put("grandTotal", grandTotal);
                params.put("idtransaksi", idtransaksi);
                params.put("catatan", catatan);
                params.put("jumlahBeli", jumlahBeli);
                return params;
            }
        };

        strReq.setRetryPolicy(policy);
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    public void deletePesanan(final String id, final String idMenu, final String stok, final String totBayar, final String idtransaksi){

        String tag_string_req = "req_login";

        pDialog.setMessage("Mohon Tunggu");
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST,
                Config_URL.cancelPesananDetail, new Response.Listener<String>() {
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
                        Toast.makeText(getActivity(), msg, Toast.LENGTH_LONG).show();
                        Intent a = new Intent(getActivity(), DetailPesananFragment.class);
                        a.putExtra("idtransaksi", idTransaksi);
                        startActivity(a);
                        getActivity().finish();
                    }else {
                        String error_msg = jObj.getString("msg");
                        Toast.makeText(getActivity(), error_msg, Toast.LENGTH_LONG).show();

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
                params.put("id", id);
                params.put("idMenu", idMenu);
                params.put("stok", stok);
                params.put("totbayar", totBayar);
                params.put("idtransaksi", idtransaksi);
                return params;
            }
        };

        strReq.setRetryPolicy(policy);
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }


}
