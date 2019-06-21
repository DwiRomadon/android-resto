package com.example.asepfathurrahman.blacktaste.adapter;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.example.asepfathurrahman.blacktaste.DetailPesananFragment;
import com.example.asepfathurrahman.blacktaste.ListPesananDetailDisajikan;
import com.example.asepfathurrahman.blacktaste.Minuman;
import com.example.asepfathurrahman.blacktaste.R;
import com.example.asepfathurrahman.blacktaste.server.AppController;
import com.example.asepfathurrahman.blacktaste.server.Config_URL;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.example.asepfathurrahman.blacktaste.server.AppController.TAG;

public class AdapterListMinuman extends RecyclerView.Adapter<AdapterListMinuman.MyViewHolder> {

    Context bContext;
    List<Minuman> bData;
    Dialog bDialog;

    String noMenja,namaPemesan, idKaryawan, idTransaksi;
    int socketTimeout = 30000;
    RetryPolicy policy = new DefaultRetryPolicy(socketTimeout,
            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);

    public AdapterListMinuman(Context bContext, List<Minuman> bData) {
        this.bContext = bContext;
        this.bData = bData;
        Intent a = ((Activity) bContext).getIntent();
        this.noMenja = a.getStringExtra("nomeja");
        this.namaPemesan = a.getStringExtra("namapemesan");
        this.idKaryawan = a.getStringExtra("idkaryawan");
        this.idTransaksi = a.getStringExtra("idtransaksi");
    }

    @Override
    public AdapterListMinuman.MyViewHolder onCreateViewHolder(ViewGroup parent, final int viewType) {
        View v = LayoutInflater.from(bContext).inflate(R.layout.item_minuman, parent, false);

        return new AdapterListMinuman.MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(AdapterListMinuman.MyViewHolder holder, final int position) {

        holder.tv_nama.setText(bData.get(position).getNamaMinuman());
        holder.tv_harga.setText("Harga\t: "+bData.get(position).getHargaMinuman());
        holder.tv_stok.setText("Stok\t\t: "+bData.get(position).getStokMinuman());
        holder.idMenu.setText(bData.get(position).getIdMenu());
        holder.idMenu.setVisibility(View.GONE);

        Picasso.get()
                .load(Config_URL.base_URL+"/assets/images/produk/"+ bData.get(position).getFotoMinuman())
                .resize(50, 50)
                .centerCrop()
                .into(holder.img);

        bDialog = new Dialog(bContext);
        bDialog.setContentView(R.layout.dialog_minuman);


        holder.button_get.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final TextView dialog_nama_tv = (TextView) bDialog.findViewById(R.id.dialog_nama_minuman);
                TextView dialog_stok_tv = (TextView) bDialog.findViewById(R.id.dialog_nama_stok);
                Button btnInputMinuman = bDialog.findViewById(R.id.button_input_minuman);
                Button btnBatalMinuman = bDialog.findViewById(R.id.button_batal_minuman);
                final ElegantNumberButton quantitiBtn = bDialog.findViewById(R.id.quantiti_button_minuman);
                dialog_nama_tv.setText(bData.get(position).getNamaMinuman());
                dialog_stok_tv.setText("Stok : "+ bData.get(position).getStokMinuman());
                final EditText catatan = bDialog.findViewById(R.id.editText);

                btnInputMinuman.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final String catat = catatan.getText().toString();

                        final int stoknya = Integer.parseInt(bData.get(position).getStokMinuman()) - Integer.parseInt(quantitiBtn.getNumber());
                        String convertHarga = bData.get(position).getHargaMinuman().replace(".", "");
                        final double harga = Double.parseDouble(convertHarga) * Double.parseDouble(quantitiBtn.getNumber());

                        String tag_string_req = "req_login";

                        StringRequest strReq = new StringRequest(Request.Method.POST,
                                Config_URL.getTotalBayar, new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                Log.d(TAG, "Login Response: " + response.toString());
                                ///loginBtn.revertAnimation();

                                try {
                                    JSONObject jObj = new JSONObject(response);
                                    boolean status = jObj.getBoolean("status");

                                    if(status == true){

                                        String totbayar     = jObj.getString("totbayar");

                                        double totbyr       = Double.parseDouble(totbayar) + harga;

                                        String quantiti = quantitiBtn.getNumber();

                                        if (quantiti.equals("0")){
                                            Toast.makeText(bContext, "Isi jumlah beli", Toast.LENGTH_LONG).show();
                                        }else {
                                            inputDataAndUpdateStock(noMenja, String.valueOf(totbyr), idTransaksi,idKaryawan, quantitiBtn.getNumber(), catat, bData.get(position).getIdMenu(), String.valueOf(stoknya));
                                            Toast.makeText(bContext, "Berhasil menambah pesanan", Toast.LENGTH_LONG).show();
                                        }

                                    }else {
                                        String error_msg = jObj.getString("msg");
                                        Toast.makeText(bContext, error_msg, Toast.LENGTH_LONG).show();

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
                            }
                        }){

                            @Override
                            protected Map<String, String> getParams(){
                                Map<String, String> params = new HashMap<String, String>();
                                params.put("idtransaksi", idTransaksi);
                                return params;
                            }
                        };

                        strReq.setRetryPolicy(policy);
                        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
                    }
                });

                btnBatalMinuman.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        bDialog.dismiss();
                    }
                });
                bDialog.show();
            }
        });

        //getTotal(idTransaksi);
    }

    @Override
    public int getItemCount() {
        return bData.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView idMenu;
        TextView tv_nama;
        TextView tv_harga;
        TextView tv_stok;
        ImageView img;
        Button button_get;

        public MyViewHolder(View itemView) {
            super(itemView);

            idMenu  = (TextView) itemView.findViewById(R.id.id_menu);
            tv_nama = (TextView) itemView.findViewById(R.id.nama_minuman);
            tv_harga = (TextView) itemView.findViewById(R.id.harga_minuman);
            tv_stok = (TextView) itemView.findViewById(R.id.stok_minuman);
            img = (ImageView) itemView.findViewById(R.id.img_minuman);
            button_get = (Button) itemView.findViewById(R.id.button_minuman);

        }
    }

    public void inputDataAndUpdateStock(final String idMeja, final String totbayar, final String idTransaksi,
                                        final String idkaryawan, final String jumlahbeli, final String catatan
            , final String idmenu, final String stok){

        //Tag used to cancel the request
        String tag_string_req = "req_login";

        //pDialog.setMessage("Login, Please Wait.....");
        //showDialog();
        //loginBtn.startAnimation();

        StringRequest strReq = new StringRequest(Request.Method.POST,
                Config_URL.tambahDataPesanan, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(String.valueOf(AdapterListMinuman.this), "Login Response: " + response.toString());
                //loginBtn.revertAnimation();

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean status = jObj.getBoolean("status");

                    if(status == true){

                        String msg          = jObj.getString("msg");

                        Toast.makeText(bContext, msg, Toast.LENGTH_LONG).show();

                        Intent i = new Intent(bContext, DetailPesananFragment.class);
                        i.putExtra("nomeja", noMenja);
                        i.putExtra("idtransaksi", idTransaksi);
                        i.putExtra("idkaryawan", idKaryawan);
                        bContext.startActivity(i);
                        ((Activity)bContext).finish();

                    }else {
                        String error_msg = jObj.getString("msg");
                        Toast.makeText(bContext, error_msg, Toast.LENGTH_LONG).show();

                    }

                }catch (JSONException e){
                    //JSON error
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener(){

            @Override
            public void onErrorResponse(VolleyError error){
                Log.e(String.valueOf(AdapterListMinuman.this), "Login Error : " + error.getMessage());
                error.printStackTrace();
                //loginBtn.revertAnimation();
            }
        }){

            @Override
            protected Map<String, String> getParams(){
                Map<String, String> params = new HashMap<String, String>();
                params.put("idmeja", idMeja);
                params.put("total_bayar", totbayar);
                params.put("idtransaksi", idTransaksi);
                params.put("idkaryawan", idkaryawan);
                params.put("jumlahbeli", jumlahbeli);
                params.put("catatan", catatan);
                params.put("idmenu", idmenu);
                params.put("stok", stok);
                return params;
            }
        };

        strReq.setRetryPolicy(policy);
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }
}
