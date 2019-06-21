package com.example.asepfathurrahman.blacktaste;

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
import com.example.asepfathurrahman.blacktaste.server.AppController;
import com.example.asepfathurrahman.blacktaste.server.Config_URL;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.MyViewHolder> {


    Context mContext;
    List<Makanan> mData;
    Dialog mDialog, mDialog2;

    String noMenja,namaPemesan, idKaryawan;

    int socketTimeout = 30000;
    RetryPolicy policy = new DefaultRetryPolicy(socketTimeout,
            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);

    public RecyclerViewAdapter(Context mContext, List<Makanan> mData) {
        this.mContext = mContext;
        this.mData = mData;
        Intent a = ((Activity) mContext).getIntent();
        this.noMenja = a.getStringExtra("nomeja");
        this.namaPemesan = a.getStringExtra("namapemesan");
        this.idKaryawan = a.getStringExtra("idkaryawan");
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, final int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.item_makanan, parent, false);

        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {

        holder.tv_nama.setText(mData.get(position).getNamaMakanan());
        holder.tv_harga.setText("Harga\t: "+ mData.get(position).getHargaMakanan());
        holder.tv_stok.setText("Stok\t\t: "+ mData.get(position).getStokMakanan());
        holder.idMenu.setText(mData.get(position).getIdMenu());
        //holder.img.setImageResource(mData.get(position).getFotoMakanan());
        holder.idMenu.setVisibility(View.GONE);
        Picasso.get()
                .load(Config_URL.base_URL+"/assets/images/produk/"+ mData.get(position).getFotoMakanan())
                .resize(50, 50)
                .centerCrop()
                .into(holder.img);

        mDialog = new Dialog(mContext);

        mDialog.setContentView(R.layout.dialog_makanan);

        //Toast.makeText(mContext, data1 + data2, Toast.LENGTH_SHORT).show();

        holder.button_get.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final TextView dialog_nama_tv = (TextView) mDialog.findViewById(R.id.dialog_nama_makanan);
                final TextView dialog_stok_tv = (TextView) mDialog.findViewById(R.id.dialog_nama_stok);
                Button btnInputMakanan = mDialog.findViewById(R.id.button_input_makanan);
                Button btnBatalMakanan = mDialog.findViewById(R.id.button_batal_makanan);
                final ElegantNumberButton quantitiBtn = mDialog.findViewById(R.id.quantiti_button_makanan);
                final EditText catatan = mDialog.findViewById(R.id.editText);

                dialog_nama_tv.setText(mData.get(position).getNamaMakanan());
                dialog_stok_tv.setText("Stok : " + mData.get(position).getStokMakanan());

                btnInputMakanan.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        String catat = catatan.getText().toString();

                        int stoknya = Integer.parseInt(mData.get(position).getStokMakanan()) - Integer.parseInt(quantitiBtn.getNumber());
                        String convertHarga = mData.get(position).getHargaMakanan().replace(".", "");
                        double harga = Double.parseDouble(convertHarga) * Double.parseDouble(quantitiBtn.getNumber());

                        String quantiti = quantitiBtn.getNumber();

                        if (quantiti.equals("0")){
                            Toast.makeText(mContext, "Isi jumlah beli", Toast.LENGTH_LONG).show();
                        }else if(mData.get(position).getStokMakanan().equals("0")){
                            Toast.makeText(mContext, "Maaf stok habis silahkan restock", Toast.LENGTH_LONG).show();
                        }else {
                            inputDataAndUpdateStock(noMenja, mData.get(position).getIdMenu(),
                                    quantitiBtn.getNumber(), catat, String.valueOf(stoknya), String.valueOf(harga));
                        }

                    }
                });

                btnBatalMakanan.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mDialog.dismiss();
                    }
                });
                mDialog.show();
            }
        });

        mDialog2 = new Dialog(mContext);
        mDialog2.setContentView(R.layout.show_image);

        holder.img.setOnLongClickListener(new View.OnLongClickListener() {

            @Override
            public boolean onLongClick(View v) {
                ImageView img = mDialog2.findViewById(R.id.showImgView);
                Picasso.get()
                        .load(Config_URL.base_URL+"/assets/images/produk/"+ mData.get(position).getFotoMakanan())
                        .into(img);

                mDialog2.show();
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return mData.size();
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

            idMenu = (TextView) itemView.findViewById(R.id.id_menu);
            tv_nama = (TextView) itemView.findViewById(R.id.nama_makanan);
            tv_harga = (TextView) itemView.findViewById(R.id.harga_makanan);
            tv_stok = (TextView) itemView.findViewById(R.id.stok_makanan);
            img = (ImageView) itemView.findViewById(R.id.img_makanan);
            button_get = (Button) itemView.findViewById(R.id.button_makanan);
        }
    }

    public void inputDataAndUpdateStock(final String idMeja, final String idMenu, final String jumlahBeli,
                                        final String catatan, final String stok, final String harga){

        //Tag used to cancel the request
        String tag_string_req = "req_login";

        //pDialog.setMessage("Login, Please Wait.....");
        //showDialog();
        //loginBtn.startAnimation();

        StringRequest strReq = new StringRequest(Request.Method.POST,
                Config_URL.addPreTransaction, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(String.valueOf(RecyclerViewAdapter.this), "Login Response: " + response.toString());
                //loginBtn.revertAnimation();

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean status = jObj.getBoolean("status");

                    if(status == true){

                        String msg          = jObj.getString("msg");

                        Toast.makeText(mContext, msg, Toast.LENGTH_LONG).show();

                        Intent i = new Intent(mContext, DaftarMenu.class);
                        i.putExtra("nomeja", noMenja);
                        i.putExtra("namapemesan", namaPemesan);
                        i.putExtra("idkaryawan", idKaryawan);
                        mContext.startActivity(i);
                        ((Activity)mContext).finish();

                    }else {
                        String error_msg = jObj.getString("msg");
                        Toast.makeText(mContext, error_msg, Toast.LENGTH_LONG).show();

                    }

                }catch (JSONException e){
                    //JSON error
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener(){

            @Override
            public void onErrorResponse(VolleyError error){
                Log.e(String.valueOf(RecyclerViewAdapter.this), "Login Error : " + error.getMessage());
                error.printStackTrace();
                //loginBtn.revertAnimation();
            }
        }){

            @Override
            protected Map<String, String> getParams(){
                Map<String, String> params = new HashMap<String, String>();
                params.put("idMeja", idMeja);
                params.put("idMenu", idMenu);
                params.put("jumbel", jumlahBeli);
                params.put("catatan", catatan);
                params.put("stok", stok);
                params.put("harga", harga);
                return params;
            }
        };

        strReq.setRetryPolicy(policy);
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

}

