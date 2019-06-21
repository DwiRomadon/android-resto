package com.example.asepfathurrahman.blacktaste.tambahpesanan;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import com.example.asepfathurrahman.blacktaste.DetailPesananFragment;
import com.example.asepfathurrahman.blacktaste.ListPesananDetailDisajikan;
import com.example.asepfathurrahman.blacktaste.R;
import com.example.asepfathurrahman.blacktaste.ViewPagerAdapter;

public class DaftarMenuTambah extends AppCompatActivity {


    private TabLayout tabLayout;
    private ViewPager viewPager;
    private ViewPagerAdapter adapter;

    String idTransaksi,idKaryawan,noMeja;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.daftar_menu);

        tabLayout = (TabLayout) findViewById(R.id.tablayout_id);
        viewPager = (ViewPager) findViewById(R.id.viewpager_id);
        adapter = new ViewPagerAdapter(getSupportFragmentManager());

        adapter.AddFragment(new FragmentListMakanan(),"Makanan");
        adapter.AddFragment(new FragmentListMinuman(),"Minuman");

        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);

        Intent a    = getIntent();
        idTransaksi = a.getStringExtra("idtransaksi");
        idKaryawan  = a.getStringExtra("idkaryawan");
        noMeja      = a.getStringExtra("nomeja");
    }

    @Override
    public void onBackPressed() {
        Intent a = new Intent(DaftarMenuTambah.this, DetailPesananFragment.class);
        a.putExtra("idtransaksi", idTransaksi);
        a.putExtra("idkaryawan", idKaryawan);
        a.putExtra("nomeja", noMeja);
        startActivity(a);
        finish();
    }
}
