package com.cyclone.ppjdo;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.cyclone.ppjdo.page.cekInOutEntity;
import com.cyclone.ppjdo.page.cekStokEntity;
import com.cyclone.ppjdo.page.orderStokEntity;
import com.cyclone.ppjdo.scan_barcode.BarcodeCaptureActivity;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.vision.barcode.Barcode;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

import static com.cyclone.ppjdo.Config.alert;
import static com.cyclone.ppjdo.Config.urlHandler;

public class MainActivity extends AppCompatActivity {

    public static SharedPreferences detail_user;
    public static LinearLayout main;
    public static FrameLayout content;
    View c1, c2, c3;

    Spinner bulan, tahun, toko;
    ListView ls;

    LayoutInflater layoutInflater;
    BottomNavigationView navigation;

    EditText tanggalBuku, hargaBuku1, hargaBuku2, judulBuku;

    String[] tokoListText, tokoListValue;

    Calendar myCalendar;

    Button btnFilter;

    final int RC_BARCODE_CAPTURE = 9001;
    final int RC_BARCODE_CAPTURE_ORDER_STOK = 9002;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    c1.setVisibility(View.VISIBLE);
                    c2.setVisibility(View.GONE);
                    c3.setVisibility(View.GONE);

                    setTitle(R.string.title_checkinout);

                    cekinout();

                    return true;
                case R.id.navigation_cekstok:
                    c1.setVisibility(View.GONE);
                    c2.setVisibility(View.VISIBLE);
                    c3.setVisibility(View.GONE);

                    setTitle(R.string.title_cekstok);

                    cekstok();

                    return true;
                case R.id.navigation_orderstok:
                    c1.setVisibility(View.GONE);
                    c2.setVisibility(View.GONE);
                    c3.setVisibility(View.VISIBLE);

                    setTitle(R.string.title_orderstok);

                    orderStok();

                    return true;
            }
            return false;
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        main = (LinearLayout) findViewById(R.id.container);
        content = (FrameLayout) findViewById(R.id.content);
        layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        c1 = (View) findViewById(R.id.c1);
        c2 = (View) findViewById(R.id.c2);
        c3 = (View) findViewById(R.id.c3);


        navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        detail_user = getSharedPreferences(Config.keyDb, Context.MODE_PRIVATE);

        bulan = (Spinner) findViewById(R.id.bulanFilter);
        tahun = (Spinner) findViewById(R.id.tahunFilter);
        ls = (ListView) findViewById(R.id.ls);

        //start
        sesi_user();
        cekinout();
    }

    //ferdi's code
    private void sesi_user() {
        String jabatan = detail_user.getString("jabatan", null);

        //check jabatan, if not same with static value inside config, hide the some bottom nav item
        if (!Config.marketings.equals(jabatan)) {
            navigation.getMenu().removeItem(R.id.navigation_cekstok);
            navigation.getMenu().removeItem(R.id.navigation_orderstok);
        }
    }

    private void cekinout() {
        ls = (ListView) findViewById(R.id.ls);

        String[] bulanList = {"-- Filter Bulan --", "Januari", "Februari", "Maret", "April", "Mei", "Juni", "July", "Agustus", "September", "Oktober", "November", "Desember"};
        String[] tahunList = {"-- Filter Tahun --", "2017", "2018", "2019", "2020"};


        ArrayAdapter<String> adapterBulan = new ArrayAdapter<String>(getApplicationContext(), R.layout.baris_spinner, bulanList);
        ArrayAdapter<String> adapterTahun = new ArrayAdapter<String>(getApplicationContext(), R.layout.baris_spinner, tahunList);

        bulan.setAdapter(adapterBulan);
        tahun.setAdapter(adapterTahun);

        Calendar tgl = new GregorianCalendar();

        bulan.setSelection(tgl.get(Calendar.MONTH) + 1);
        tahun.setSelection(tgl.get(Calendar.YEAR) - 2016);

        bulan.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                showDataCekinout();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        tahun.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                showDataCekinout();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        ls.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView idTV = (TextView) view.findViewById(R.id.id);
                TextView jamTV = (TextView) view.findViewById(R.id.jam);
                TextView blnTV = (TextView) view.findViewById(R.id.blnFull);
                TextView idTokoTV = (TextView) view.findViewById(R.id.idtoko);
                TextView tokoTV = (TextView) view.findViewById(R.id.toko);
                TextView tglTV = (TextView) view.findViewById(R.id.tgl);
                TextView masukTV = (TextView) view.findViewById(R.id.labMasuk);
                TextView keluarTV = (TextView) view.findViewById(R.id.labKeluar);
                TextView kordinatLong = (TextView) view.findViewById(R.id.kordinatLong);
                TextView kordinatLat = (TextView) view.findViewById(R.id.kordinatLat);

                //alert("klik id : " + String.valueOf(idTV.getText()) + " : " + String.valueOf(jamTV.getText()) + " : " + String.valueOf(idTokoTV.getText()), 1000, MainActivity.main);

                Intent it = new Intent(getApplicationContext(), FormCekinout.class);

                it.putExtra("id", String.valueOf(idTV.getText()));
                it.putExtra("jam", String.valueOf(jamTV.getText()));
                it.putExtra("bulan", String.valueOf(blnTV.getText()));
                it.putExtra("idtoko", String.valueOf(idTokoTV.getText()));
                it.putExtra("toko", String.valueOf(tokoTV.getText()));
                it.putExtra("tgl", String.valueOf(tglTV.getText()));
                it.putExtra("masuk", String.valueOf(masukTV.getText()));
                it.putExtra("keluar", String.valueOf(keluarTV.getText()));
                it.putExtra("lat", String.valueOf(kordinatLat.getText()));
                it.putExtra("long", String.valueOf(kordinatLong.getText()));

                startActivity(it);
            }
        });

        showDataCekinout();
    }

    private void showDataCekinout() {
        final String bulanVal = String.valueOf(bulan.getSelectedItemPosition());
        final String tahunVal = String.valueOf(tahun.getSelectedItemPosition() + 2016);
        final String petugasVal = MainActivity.detail_user.getString("id", "");

        //running ajax
        findViewById(R.id.relProgressBar).setVisibility(View.VISIBLE);
        ls.setVisibility(View.GONE);
        findViewById(R.id.txtNoData).setVisibility(View.GONE);

        int min = 10000;
        int max = 99999;
        int randomS = new Random().nextInt((max - min) + 1) + min;
        String API = urlHandler + "sales.act?a=true&t=ls_mob&uniq=" + (String.valueOf(randomS)) + "";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, API,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String respon) {

                        findViewById(R.id.relProgressBar).setVisibility(View.GONE);

                        try {
                            JSONObject json = new JSONObject(respon);
                            Integer totalRec = new Integer(json.getString("total"));

                            if (totalRec > 0) {
                                findViewById(R.id.txtNoData).setVisibility(View.GONE);
                                JSONArray list = json.getJSONArray("list");

                                String[] id = new String[totalRec], tgl = new String[totalRec], bln = new String[totalRec], jam = new String[totalRec], idtoko = new String[totalRec], toko = new String[totalRec], masuk = new String[totalRec], keluar = new String[totalRec], kordinatLong = new String[totalRec], kordinatLat = new String[totalRec];

                                for (Integer il = 0; il < list.length(); il++) {
                                    JSONObject rec = list.getJSONObject(il);

                                    id[il] = rec.getString("id");
                                    tgl[il] = rec.getString("tgl");
                                    bln[il] = rec.getString("bln");
                                    jam[il] = rec.getString("jam");
                                    idtoko[il] = rec.getString("idtoko");
                                    toko[il] = rec.getString("toko");
                                    masuk[il] = rec.getString("masuk");
                                    keluar[il] = rec.getString("keluar");
                                    kordinatLat[il] = rec.getString("lat");
                                    kordinatLong[il] = rec.getString("long");
                                }

                                cekInOutEntity customList = new cekInOutEntity(getApplicationContext(), id, tgl, bln, jam, idtoko, toko, masuk, keluar, kordinatLat, kordinatLong);

                                ls.setAdapter(customList);
                                ls.setVisibility(View.VISIBLE);
                            } else {
                                findViewById(R.id.txtNoData).setVisibility(View.VISIBLE);
                            }

                        } catch (Exception e) {
                            Log.w("error_volley_json", e.getMessage());

                            findViewById(R.id.txtNoData).setVisibility(View.VISIBLE);
                            Config.alert("Data tidak tersedia", 1000, MainActivity.main);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();

                Config.alert("Tidak ada koneksi, Silahkan cek koneksi internet anda", 3000, main);

                //Log.w("error_volley_response", error.getMessage());

                findViewById(R.id.relProgressBar).setVisibility(View.GONE);
                findViewById(R.id.txtNoData).setVisibility(View.VISIBLE);
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("pet", petugasVal);
                params.put("bln", bulanVal);
                params.put("thn", tahunVal);

                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.getCache().clear();
        requestQueue.cancelAll(new RequestQueue.RequestFilter() {
            @Override
            public boolean apply(Request<?> request) {
                return true;
            }
        });
        requestQueue.add(stringRequest);
    }

    private void cekstok() {
        //reset listview
        ls = (ListView) findViewById(R.id.lsStok);

        btnFilter = (Button) findViewById(R.id.btnFilter);
        Button btnScan = (Button) findViewById(R.id.btnScan);

        toko = (Spinner) findViewById(R.id.tokoFilter);
        judulBuku = (EditText) findViewById(R.id.judulFilter);

        //running ajax
        findViewById(R.id.relProgressBar).setVisibility(View.VISIBLE);
        ls.setVisibility(View.GONE);

        ls.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView orderStok = (TextView) view.findViewById(R.id.txtStok);
                TextView id_axI = (TextView) view.findViewById(R.id.txtidax);

                String orderStokNumber = String.valueOf(orderStok.getText());
                orderStokNumber = orderStokNumber.replace("Stok : ", "");

                String id_toko = tokoListValue[toko.getSelectedItemPosition()];
                String id_ax = String.valueOf(id_axI.getText());

                openDialogCekStok(orderStokNumber, id_toko, id_ax);
            }
        });

        int min = 10000;
        int max = 99999;
        int randomS = new Random().nextInt((max - min) + 1) + min;
        String API = urlHandler + "sales.act?a=true&t=ltok_mobile&uniq=" + (String.valueOf(randomS)) + "&us=" + MainActivity.detail_user.getString("id", "");

        StringRequest stringRequest = new StringRequest(Request.Method.GET, API,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String respon) {

                        //Log.w("logeee", respon);

                        findViewById(R.id.relProgressBar).setVisibility(View.GONE);

                        try {
                            JSONObject json = new JSONObject(respon);
                            Integer totalRec = new Integer(json.getString("total"));

                            if (totalRec > 0) {
                                JSONArray list = json.getJSONArray("list");

                                tokoListText = new String[totalRec];
                                tokoListValue = new String[totalRec];

                                for (Integer il = 0; il < list.length(); il++) {
                                    JSONObject rec = list.getJSONObject(il);

                                    tokoListText[il] = rec.getString("nama");
                                    tokoListValue[il] = rec.getString("id");
                                }
                            } else {
                                Config.alert("Data toko tidak tersedia", 1000, MainActivity.main);
                            }

                            ArrayAdapter<String> adapterToko = new ArrayAdapter<String>(getApplicationContext(), R.layout.baris_spinner, tokoListText);

                            toko.setAdapter(adapterToko);

                        } catch (Exception e) {
                            Log.w("error_volley_json", e.getMessage());

                            Config.alert("Data toko tidak tersedia", 1000, MainActivity.main);
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();

                Config.alert("Tidak ada koneksi, Silahkan cek koneksi internet anda", 3000, main);

                //Log.w("error_volley_response", error.getMessage());

                findViewById(R.id.relProgressBar).setVisibility(View.GONE);
            }
        });

        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.getCache().clear();
        requestQueue.cancelAll(new RequestQueue.RequestFilter() {
            @Override
            public boolean apply(Request<?> request) {
                return true;
            }
        });
        requestQueue.add(stringRequest);

        btnFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!judulBuku.getText().toString().trim().equals("")) {
                    showDataCekStok();
                } else {
                    alert("Keyword filter kosong, silahkan di isi", 3000, main);
                }
            }
        });

        btnScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Boolean autoFoc = false;

                Intent intent = new Intent(MainActivity.this, BarcodeCaptureActivity.class);
                intent.putExtra(BarcodeCaptureActivity.AutoFocus, true);
                intent.putExtra(BarcodeCaptureActivity.UseFlash, autoFoc);

                startActivityForResult(intent, RC_BARCODE_CAPTURE);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RC_BARCODE_CAPTURE) {
            if (resultCode == CommonStatusCodes.SUCCESS) {
                if (data != null) {
                    Barcode barcode = data.getParcelableExtra(BarcodeCaptureActivity.BarcodeObject);

                    judulBuku.setText(barcode.displayValue.toString().trim());
                    showDataCekStok();
                } else {
                    alert("Barcode tidak di temukan", 3000, main);
                }
            } else {
                alert("Barcode tidak di temukan", 3000, main);
            }
        } else if (requestCode == RC_BARCODE_CAPTURE_ORDER_STOK) {
            if (resultCode == CommonStatusCodes.SUCCESS) {
                if (data != null) {
                    Barcode barcode = data.getParcelableExtra(BarcodeCaptureActivity.BarcodeObject);

                    judulBuku.setText(barcode.displayValue.toString().trim());
                    showDataOrderStok();
                } else {
                    alert("Barcode tidak di temukan", 3000, main);
                }
            } else {
                alert("Barcode tidak di temukan", 3000, main);
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void showDataCekStok() {
        //running ajax
        findViewById(R.id.relProgressBarStok).setVisibility(View.VISIBLE);
        ls.setVisibility(View.GONE);
        findViewById(R.id.txtNoDataStok).setVisibility(View.GONE);

        int min = 10000;
        int max = 99999;
        int randomS = new Random().nextInt((max - min) + 1) + min;
        String API = urlHandler + "sales.act?a=true&t=lsstok_mob&uniq=" + (String.valueOf(randomS)) + "";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, API,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String respon) {

                        findViewById(R.id.relProgressBarStok).setVisibility(View.GONE);

                        //Log.w("loggeee", respon);

                        try {
                            JSONObject json = new JSONObject(respon);
                            Integer totalRec = new Integer(json.getString("total"));

                            if (totalRec > 0) {
                                findViewById(R.id.txtNoDataStok).setVisibility(View.GONE);
                                JSONArray list = json.getJSONArray("list");

                                String[] idax = new String[totalRec], rating = new String[totalRec], departemen = new String[totalRec], judul = new String[totalRec], harga = new String[totalRec], stok = new String[totalRec], stokaktual = new String[totalRec], ito = new String[totalRec], stokgudang = new String[totalRec];
                                String[] isbn = new String[totalRec], gudangJkt = new String[totalRec], gudangBdg = new String[totalRec], gudangSby = new String[totalRec], gudangJog = new String[totalRec];

                                for (Integer il = 0; il < list.length(); il++) {
                                    JSONObject rec = list.getJSONObject(il);

                                    idax[il] = rec.getString("id_ax");
                                    rating[il] = "0";
                                    departemen[il] = rec.getString("departemen");
                                    judul[il] = rec.getString("judul");
                                    harga[il] = rec.getString("harga");
                                    stok[il] = rec.getString("stok");
                                    stokaktual[il] = rec.getString("stok_aktual");
                                    ito[il] = rec.getString("ito");
                                    stokgudang[il] = rec.getString("stok_gudang");
                                    isbn[il] = rec.getString("isbn");
                                    gudangJkt[il] = rec.getString("gudang_jkt");
                                    gudangBdg[il] = rec.getString("gudang_bdg");
                                    gudangSby[il] = rec.getString("gudang_sby");
                                    gudangJog[il] = rec.getString("gudang_jog");
                                }

                                cekStokEntity customList = new cekStokEntity(getApplicationContext(), idax, rating, departemen, judul, harga, stok, stokaktual, ito, stokgudang, isbn, gudangJkt, gudangBdg, gudangSby, gudangJog);

                                ls.setAdapter(customList);
                                ls.setVisibility(View.VISIBLE);

                                Log.w("buka", "harusnya muncul");
                            } else {
                                findViewById(R.id.txtNoDataStok).setVisibility(View.VISIBLE);
                            }

                        } catch (Exception e) {
                            Log.w("error_volley_json", e.getMessage());

                            findViewById(R.id.txtNoDataStok).setVisibility(View.VISIBLE);
                            Config.alert("Data tidak tersedia", 1000, MainActivity.main);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();

                Config.alert("Tidak ada koneksi, Silahkan cek koneksi internet anda", 3000, main);

                //Log.w("error_volley_response", error.getMessage());

                findViewById(R.id.relProgressBarStok).setVisibility(View.GONE);
                findViewById(R.id.txtNoDataStok).setVisibility(View.VISIBLE);
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("toko", tokoListValue[toko.getSelectedItemPosition()]);
                params.put("judul", String.valueOf(judulBuku.getText()));

                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.getCache().clear();
        requestQueue.cancelAll(new RequestQueue.RequestFilter() {
            @Override
            public boolean apply(Request<?> request) {
                return true;
            }
        });
        requestQueue.add(stringRequest);
    }

    private void updateLabelFilterTgl() {
        String myFormat = "dd-MM-yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        tanggalBuku.setText(sdf.format(myCalendar.getTime()));
    }

    private void orderStok() {
        //reset listview
        ls = (ListView) findViewById(R.id.lsStokO);

        btnFilter = (Button) findViewById(R.id.btnFilterO);
        Button btnScan = (Button) findViewById(R.id.btnScanO);

        toko = (Spinner) findViewById(R.id.tokoFilterO);

        tanggalBuku = (EditText) findViewById(R.id.tglFilterO);
        hargaBuku1 = (EditText) findViewById(R.id.hargaFilter1O);
        hargaBuku2 = (EditText) findViewById(R.id.hargaFilter2O);
        judulBuku = (EditText) findViewById(R.id.judulFilterO);

        //set date now
        myCalendar = Calendar.getInstance();
        updateLabelFilterTgl();

        //running ajax
        findViewById(R.id.relProgressBarO).setVisibility(View.VISIBLE);
        ls.setVisibility(View.GONE);

        ls.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView orderStok = (TextView) view.findViewById(R.id.txtStok);
                TextView id_axI = (TextView) view.findViewById(R.id.txtidax);
                TextView id_depI = (TextView) view.findViewById(R.id.iddep);
                TextView id_tokoI = (TextView) view.findViewById(R.id.idtoko);

                String orderStokNumber = String.valueOf(orderStok.getText());
                orderStokNumber = orderStokNumber.replace("Order Stok : ", "");

                String id_toko = String.valueOf(id_tokoI.getText());
                String id_dep = String.valueOf(id_depI.getText());
                String id_ax = String.valueOf(id_axI.getText());

                openDialogOrderStok(orderStokNumber, id_toko, id_dep, id_ax);
            }
        });

        int min = 10000;
        int max = 99999;
        int randomS = new Random().nextInt((max - min) + 1) + min;
        String API = urlHandler + "sales.act?a=true&t=ltok_mobile&uniq=" + (String.valueOf(randomS)) + "&us=" + MainActivity.detail_user.getString("id", "");

        StringRequest stringRequest = new StringRequest(Request.Method.GET, API,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String respon) {

                        //Log.w("logeee", respon);

                        findViewById(R.id.relProgressBarO).setVisibility(View.GONE);

                        try {
                            JSONObject json = new JSONObject(respon);
                            Integer totalRec = new Integer(json.getString("total"));

                            if (totalRec > 0) {
                                JSONArray list = json.getJSONArray("list");

                                tokoListText = new String[totalRec];
                                tokoListValue = new String[totalRec];

                                for (Integer il = 0; il < list.length(); il++) {
                                    JSONObject rec = list.getJSONObject(il);

                                    tokoListText[il] = rec.getString("nama");
                                    tokoListValue[il] = rec.getString("id");
                                }
                            } else {
                                Config.alert("Data toko tidak tersedia", 1000, MainActivity.main);
                            }

                            ArrayAdapter<String> adapterToko = new ArrayAdapter<String>(getApplicationContext(), R.layout.baris_spinner, tokoListText);

                            toko.setAdapter(adapterToko);

                            //start
                            showDataOrderStok();


                        } catch (Exception e) {
                            Log.w("error_volley_json", e.getMessage());

                            Config.alert("Data toko tidak tersedia", 1000, MainActivity.main);
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();

                Config.alert("Tidak ada koneksi, Silahkan cek koneksi internet anda", 3000, main);

                //Log.w("error_volley_response", error.getMessage());

                findViewById(R.id.relProgressBarO).setVisibility(View.GONE);
            }
        });

        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.getCache().clear();
        requestQueue.cancelAll(new RequestQueue.RequestFilter() {
            @Override
            public boolean apply(Request<?> request) {
                return true;
            }
        });
        requestQueue.add(stringRequest);


        myCalendar = Calendar.getInstance();

        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabelFilterTgl();
            }

        };

        tanggalBuku.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                new DatePickerDialog(MainActivity.this, date, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        btnFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDataOrderStok();
            }
        });

        btnScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Boolean autoFoc = false;

                Intent intent = new Intent(MainActivity.this, BarcodeCaptureActivity.class);
                intent.putExtra(BarcodeCaptureActivity.AutoFocus, true);
                intent.putExtra(BarcodeCaptureActivity.UseFlash, autoFoc);

                startActivityForResult(intent, RC_BARCODE_CAPTURE_ORDER_STOK);
            }
        });
    }

    private void showDataOrderStok() {
        //running ajax
        findViewById(R.id.relProgressBarO).setVisibility(View.VISIBLE);
        ls.setVisibility(View.GONE);
        findViewById(R.id.txtNoDataStokO).setVisibility(View.GONE);

        int min = 10000;
        int max = 99999;
        int randomS = new Random().nextInt((max - min) + 1) + min;
        String API = urlHandler + "sales.act?a=true&t=lsanal_mob&uniq=" + (String.valueOf(randomS)) + "";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, API,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String respon) {

                        findViewById(R.id.relProgressBarO).setVisibility(View.GONE);

                        //Log.w("loggeee", respon);

                        try {
                            JSONObject json = new JSONObject(respon);
                            Integer totalRec = new Integer(json.getString("total"));

                            if (totalRec > 0) {
                                findViewById(R.id.txtNoDataStokO).setVisibility(View.GONE);
                                JSONArray list = json.getJSONArray("list");

                                String[] idax = new String[totalRec], rating = new String[totalRec], departemen = new String[totalRec], judul = new String[totalRec], harga = new String[totalRec], orderstok = new String[totalRec], stokaktual = new String[totalRec], ito = new String[totalRec], stokgudang = new String[totalRec], stokrekomendasi = new String[totalRec], idtoko = new String[totalRec], iddep = new String[totalRec];
                                String[] isbn = new String[totalRec], gudangJkt = new String[totalRec], gudangBdg = new String[totalRec], gudangSby = new String[totalRec], gudangJog = new String[totalRec];

                                for (Integer il = 0; il < list.length(); il++) {
                                    JSONObject rec = list.getJSONObject(il);

                                    idax[il] = rec.getString("id_ax");
                                    idtoko[il] = rec.getString("id_toko");
                                    iddep[il] = rec.getString("id_departemen");
                                    rating[il] = "0";
                                    departemen[il] = rec.getString("departemen");
                                    judul[il] = rec.getString("judul");
                                    harga[il] = rec.getString("harga");
                                    orderstok[il] = rec.getString("order_stok");
                                    stokaktual[il] = rec.getString("stok_aktual");
                                    ito[il] = rec.getString("ito");
                                    stokgudang[il] = rec.getString("stok_gudang");
                                    stokrekomendasi[il] = rec.getString("stok_rekomendasi");
                                    isbn[il] = rec.getString("isbn");
                                    gudangJkt[il] = rec.getString("gudang_jkt");
                                    gudangBdg[il] = rec.getString("gudang_bdg");
                                    gudangSby[il] = rec.getString("gudang_sby");
                                    gudangJog[il] = rec.getString("gudang_jog");
                                }

                                orderStokEntity customList = new orderStokEntity(getApplicationContext(), idax, rating, departemen, judul, harga, orderstok, stokaktual, ito, stokgudang, stokrekomendasi, idtoko, iddep, isbn, gudangJkt, gudangBdg, gudangSby, gudangJog);

                                ls.setAdapter(customList);
                                ls.setVisibility(View.VISIBLE);

                                Log.w("buka", "harusnya muncul");
                            } else {
                                findViewById(R.id.txtNoDataStokO).setVisibility(View.VISIBLE);
                            }

                        } catch (Exception e) {
                            Log.w("error_volley_json", e.getMessage());

                            findViewById(R.id.txtNoDataStokO).setVisibility(View.VISIBLE);
                            Config.alert("Data tidak tersedia", 1000, MainActivity.main);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();

                Config.alert("Tidak ada koneksi, Silahkan cek koneksi internet anda", 3000, main);

                //Log.w("error_volley_response", error.getMessage());

                findViewById(R.id.relProgressBarO).setVisibility(View.GONE);
                findViewById(R.id.txtNoDataStokO).setVisibility(View.VISIBLE);
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("toko", tokoListValue[toko.getSelectedItemPosition()]);
                //params.put("tgl", String.valueOf(tanggalBuku.getText()));
                params.put("harga1", String.valueOf(hargaBuku1.getText()));
                params.put("harga2", String.valueOf(hargaBuku2.getText()));
                params.put("judul", String.valueOf(judulBuku.getText()));

                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.getCache().clear();
        requestQueue.cancelAll(new RequestQueue.RequestFilter() {
            @Override
            public boolean apply(Request<?> request) {
                return true;
            }
        });
        requestQueue.add(stringRequest);
    }

    public void openDialogCekStok(String cekStok, final String id_toko, final String id_ax) {

        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_cekstok);
        dialog.setTitle("Cek Stok");

        final EditText txtOrderStok = (EditText) dialog.findViewById(R.id.txtOrderStok);
        txtOrderStok.setText(cekStok);

        dialog.show();

        Button btnCancel = (Button) dialog.findViewById(R.id.dialog_cancel);
        Button btnSubmit = (Button) dialog.findViewById(R.id.dialog_ok);
        final CheckBox cekOrderStok = (CheckBox) dialog.findViewById(R.id.cekOrderStok);

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String clearOrderStok = cekOrderStok.isChecked() ? "Y" : "N";
                //Config.alert("submit data order", 3000, main);

                final String cek_stok = String.valueOf(txtOrderStok.getText());

                int min = 10000;
                int max = 99999;
                int randomS = new Random().nextInt((max - min) + 1) + min;
                String API = urlHandler + "sales.act?a=true&t=saveCS&uniq=" + (String.valueOf(randomS)) + "";

                StringRequest stringRequest = new StringRequest(Request.Method.POST, API,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String respon) {

                                findViewById(R.id.relProgressBarO).setVisibility(View.GONE);

                                //Log.w("loggeee", respon);

                                try {
                                    JSONObject json = new JSONObject(respon);
                                    Integer success = new Integer(json.getString("success"));

                                    dialog.dismiss();

                                    if (success > 0) {
                                        alert("Cek Stok berhasil di simpan", 3000, main);

                                        showDataCekStok();
                                    } else {
                                        alert("Order Stok gagal di simpan, silahkan coba beberapa saat lagi", 3000, main);
                                    }

                                } catch (Exception e) {
                                    Log.w("error_volley_json", e.getMessage());

                                    findViewById(R.id.txtNoDataStokO).setVisibility(View.VISIBLE);
                                    Config.alert("Data tidak tersedia", 1000, MainActivity.main);
                                }
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();

                        Config.alert("Tidak ada koneksi, Silahkan cek koneksi internet anda", 3000, main);

                        //Log.w("error_volley_response", error.getMessage());

                        findViewById(R.id.relProgressBarO).setVisibility(View.GONE);
                        findViewById(R.id.txtNoDataStokO).setVisibility(View.VISIBLE);
                    }
                }) {
                    @Override
                    protected Map<String, String> getParams() {
                        Map<String, String> params = new HashMap<String, String>();
                        params.put("toko", id_toko);
                        params.put("id_ax", id_ax);
                        params.put("stok", cek_stok);
                        params.put("clear", clearOrderStok);
                        params.put("id_petugas", detail_user.getString("id", ""));

                        return params;
                    }
                };

                RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
                requestQueue.getCache().clear();
                requestQueue.cancelAll(new RequestQueue.RequestFilter() {
                    @Override
                    public boolean apply(Request<?> request) {
                        return true;
                    }
                });
                requestQueue.add(stringRequest);
            }
        });
    }

    public void openDialogOrderStok(String orderStok, final String id_toko, final String id_dep, final String id_ax) {

        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_orderstok);
        dialog.setTitle("Order Stok");

        final EditText txtOrderStok = (EditText) dialog.findViewById(R.id.txtOrderStok);
        txtOrderStok.setText(orderStok);

        dialog.show();

        Button btnCancel = (Button) dialog.findViewById(R.id.dialog_cancel);
        Button btnSubmit = (Button) dialog.findViewById(R.id.dialog_ok);

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Config.alert("submit data order", 3000, main);

                final String order_stok = String.valueOf(txtOrderStok.getText());

                int min = 10000;
                int max = 99999;
                int randomS = new Random().nextInt((max - min) + 1) + min;
                String API = urlHandler + "sales.act?a=true&t=saveSO&uniq=" + (String.valueOf(randomS)) + "";

                StringRequest stringRequest = new StringRequest(Request.Method.POST, API,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String respon) {

                                findViewById(R.id.relProgressBarO).setVisibility(View.GONE);

                                //Log.w("loggeee", respon);

                                try {
                                    JSONObject json = new JSONObject(respon);
                                    Integer success = new Integer(json.getString("success"));

                                    dialog.dismiss();

                                    if (success > 0) {
                                        alert("Order Stok berhasil di simpan", 3000, main);

                                        showDataOrderStok();
                                    } else {
                                        alert("Order Stok gagal di simpan, silahkan coba beberapa saat lagi", 3000, main);
                                    }

                                } catch (Exception e) {
                                    Log.w("error_volley_json", e.getMessage());

                                    findViewById(R.id.txtNoDataStokO).setVisibility(View.VISIBLE);
                                    Config.alert("Data tidak tersedia", 1000, MainActivity.main);
                                }
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();

                        Config.alert("Tidak ada koneksi, Silahkan cek koneksi internet anda", 3000, main);

                        //Log.w("error_volley_response", error.getMessage());

                        findViewById(R.id.relProgressBarO).setVisibility(View.GONE);
                        findViewById(R.id.txtNoDataStokO).setVisibility(View.VISIBLE);
                    }
                }) {
                    @Override
                    protected Map<String, String> getParams() {
                        Map<String, String> params = new HashMap<String, String>();
                        params.put("toko", id_toko);
                        params.put("id_departemen", id_dep);
                        params.put("id_ax", id_ax);
                        params.put("order", order_stok);
                        params.put("id_petugas", detail_user.getString("id", ""));

                        return params;
                    }
                };

                RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
                requestQueue.getCache().clear();
                requestQueue.cancelAll(new RequestQueue.RequestFilter() {
                    @Override
                    public boolean apply(Request<?> request) {
                        return true;
                    }
                });
                requestQueue.add(stringRequest);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        //jika cek in / out
        if (navigation.getSelectedItemId() == R.id.navigation_home) {
            showDataCekinout();
        }
    }
}
