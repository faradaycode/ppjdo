package com.cyclone.ppjdo;

import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.cyclone.ppjdo.LocationUtil.LocationHelper;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;

import org.json.JSONObject;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import static com.cyclone.ppjdo.Config.urlHandler;

public class FormCekinout extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, ActivityCompat.OnRequestPermissionsResultCallback {

    String id, jam, bulan, idtoko, toko, tgl, masuk, keluar;
    TextView jamTV, bulanTV, tokoTV, tglTV, masukTV, keluarTV, btnGPS, lokasiDetail;
    RelativeLayout relGetGPS, main;
    View divider;
    Button submit;
    CardView card2;
    double lat1, lon1;

    String event;
    Boolean location = false;

    private Location mLastLocation;

    double latitude;
    double longitude;

    LocationHelper locationHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_cekinout);

        getSupportActionBar().setElevation(0);
        setTitle("Jadwal Cek In / Cek Out");

        main = (RelativeLayout) findViewById(R.id.relMain);

        Bundle bd = getIntent().getExtras();

        id = bd.getString("id");
        jam = bd.getString("jam");
        bulan = bd.getString("bulan");
        idtoko = bd.getString("id");
        toko = bd.getString("toko");
        tgl = bd.getString("tgl");
        masuk = bd.getString("masuk");
        keluar = bd.getString("keluar");
        try {
            lat1 = (double) new Double(bd.getString("lat"));
            lon1 = (double) new Double(bd.getString("long"));
        } catch (Exception e) {
            Config.alert("Check in tidak dapat di lakukan karena kordinat toko tidak di tentukan", 3000, main);
        }

        jamTV = (TextView) findViewById(R.id.jam);
        bulanTV = (TextView) findViewById(R.id.bln);
        tokoTV = (TextView) findViewById(R.id.toko);
        tglTV = (TextView) findViewById(R.id.tgl);
        masukTV = (TextView) findViewById(R.id.masuk);
        keluarTV = (TextView) findViewById(R.id.keluar);
        btnGPS = (TextView) findViewById(R.id.btnGPS);
        lokasiDetail = (TextView) findViewById(R.id.txtLokasi);

        relGetGPS = (RelativeLayout) findViewById(R.id.rlPickLocation);

        submit = (Button) findViewById(R.id.submit);

        card2 = (CardView) findViewById(R.id.card2);

        divider = (View) findViewById(R.id.divider);


        jamTV.setText("Jam : " + jam);
        bulanTV.setText(bulan);
        tokoTV.setText(toko);
        tglTV.setText(tgl);
        masukTV.setText(masuk);
        keluarTV.setText(keluar);

        submit.setVisibility(View.GONE);

        //check in
        if (masuk.equals("Masuk : -")) {
            btnGPS.setText("Cek In Sekarang");
            event = "Cek In";
        } else if (keluar.equals("Keluar : -")) {
            btnGPS.setText("Cek Out Sekarang");
            event = "Cek Out";
        } else {
            relGetGPS.setVisibility(View.GONE);
            divider.setVisibility(View.GONE);
        }


        final AlertDialog.Builder al = new AlertDialog.Builder(this);
        final AlertDialog.Builder alsubmit = new AlertDialog.Builder(this);


        locationHelper = new LocationHelper(this);


        //get current location
        relGetGPS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                al.setTitle("Konfirmasi")
                        .setMessage("Apakah anda sudah pada lokasi toko " + toko + " sekarang?")
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {

                                locationHelper.checkpermission();

                                mLastLocation = locationHelper.getLocation();

                                if (mLastLocation != null) {
                                    latitude = mLastLocation.getLatitude();
                                    longitude = mLastLocation.getLongitude();
                                    getAddress();

                                } else {
                                    showToast("Lokasi GPS tidak di temukan, silahkan dicoba kembali");
                                }

                            }
                        }).setNegativeButton("Tidak", null).show();
            }
        });

        // check availability of play services
        if (locationHelper.checkPlayServices()) {

            // Building the GoogleApi client
            locationHelper.buildGoogleApiClient();
        }


        //save data
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (location) {

                    String jamNow = "";
                    Calendar cal = new GregorianCalendar();
                    jamNow = cal.get(Calendar.HOUR_OF_DAY) + ":" + (cal.get(Calendar.MINUTE) < 10 ? "0" + cal.get(Calendar.MINUTE) : cal.get(Calendar.MINUTE));

                    if (event.equals("Cek In")) {
                        masuk = cal.get(Calendar.YEAR) + "-" + (cal.get(Calendar.MONTH) + 1) + "-" + cal.get(Calendar.DATE) + " " + cal.get(Calendar.HOUR_OF_DAY) + ":" + cal.get(Calendar.MINUTE) + ":" + cal.get(Calendar.SECOND);
                        keluar = cal.get(Calendar.YEAR) + "-" + (cal.get(Calendar.MONTH) + 1) + "-" + cal.get(Calendar.DATE) + " " + keluar;
                    } else {
                        keluar = cal.get(Calendar.YEAR) + "-" + (cal.get(Calendar.MONTH) + 1) + "-" + cal.get(Calendar.DATE) + " " + cal.get(Calendar.HOUR_OF_DAY) + ":" + cal.get(Calendar.MINUTE) + ":" + cal.get(Calendar.SECOND);
                        masuk = cal.get(Calendar.YEAR) + "-" + (cal.get(Calendar.MONTH) + 1) + "-" + cal.get(Calendar.DATE) + " " + masuk;
                    }

                    alsubmit.setTitle("Konfirmasi")
                            .setMessage("Apakah anda akan mengisi status " + event + " pada sekarang atau jam " + jamNow + "?")
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {

                                    //ajax save data cek in out
                                    //running ajax
                                    findViewById(R.id.relProgressBar).setVisibility(View.VISIBLE);

                                    int min = 10000;
                                    int max = 99999;
                                    int randomS = new Random().nextInt((max - min) + 1) + min;
                                    String API = urlHandler + "sales.act?a=true&t=cico_mobile&uniq=" + (String.valueOf(randomS)) + "";

                                    StringRequest stringRequest = new StringRequest(Request.Method.POST, API,
                                            new Response.Listener<String>() {
                                                @Override
                                                public void onResponse(String respon) {

                                                    Log.w("respone", respon);

                                                    findViewById(R.id.relProgressBar).setVisibility(View.GONE);

                                                    try {
                                                        JSONObject json = new JSONObject(respon);

                                                        if (json.getString("sukses").equals("") || !json.getBoolean("sukses")) {
                                                            Config.alert("Data gagal disimpan, silahkan coba beberapa saat lagi", 1000, main);
                                                        } else {
                                                            Config.alert("Data berhasil disimpan", 1000, main);
                                                            finish();
                                                        }


                                                    } catch (Exception e) {
                                                        Log.w("error_volley_json", e.getMessage());

                                                        Config.alert("Data tidak tersedia", 1000, main);
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
                                    }) {
                                        @Override
                                        protected Map<String, String> getParams() {
                                            Map<String, String> params = new HashMap<String, String>();
                                            params.put("id", id);
                                            params.put("jam", jam);
                                            params.put("cekin", masuk.replace("Masuk : ", ""));
                                            params.put("cekout", keluar.replace("Keluar : ", ""));
                                            params.put("pet", MainActivity.detail_user.getString("id", ""));
                                            params.put("idtoko", idtoko);

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
                            })
                            .setNegativeButton("Tidak", null).show();
                } else {
                    Config.alert("Lokasi belum di temukan, silahkan klik cek in / cek out lebih dahulu", 1000, main);
                }
            }
        });

    }


    public void getAddress() {
        Address locationAddress;
        locationAddress = locationHelper.getAddress(latitude, longitude);

        if (locationAddress != null) {
            String address = locationAddress.getAddressLine(0);
            String address1 = locationAddress.getAddressLine(1);
            String city = locationAddress.getLocality();
            String state = locationAddress.getAdminArea();
            String country = locationAddress.getCountryName();
            String postalCode = locationAddress.getPostalCode();

            String currentLocation;

            if (!TextUtils.isEmpty(address)) {
                currentLocation = address;

                if (!TextUtils.isEmpty(address1))
                    currentLocation += "\n" + address1;

                if (!TextUtils.isEmpty(city)) {
                    currentLocation += "\n" + city;

                    if (!TextUtils.isEmpty(postalCode))
                        currentLocation += " - " + postalCode;
                } else {
                    if (!TextUtils.isEmpty(postalCode))
                        currentLocation += "\n" + postalCode;
                }

                if (!TextUtils.isEmpty(state))
                    currentLocation += "\n" + state;

                if (!TextUtils.isEmpty(country))
                    currentLocation += "\n" + country;

                float jarak = cekJarak(lat1, lon1, latitude, longitude);

                if (jarak <= 200) {
                    location = true;

                    relGetGPS.setVisibility(View.GONE);
                    divider.setVisibility(View.GONE);

                    submit.setVisibility(View.VISIBLE);
                } else {
                    Config.alert(event + " tidak dapat di lakukan, Jarak lokasi anda " + String.valueOf(Math.round(jarak)) + " Meter \n\n Batas jarak " + event + " : 200 meter", 3000, main);
                }

                card2.setVisibility(View.VISIBLE);
                lokasiDetail.setText(currentLocation);
            }

        } else {
            showToast("Lokasi GPS tidak di temukan, silahkan dicoba kembali");
        }

    }

    private float cekJarak(double lat1, double lon1, double lat2, double lon2) {
        Location loc1 = new Location("");
        loc1.setLatitude(lat1);
        loc1.setLongitude(lon1);

        Location loc2 = new Location("");
        loc2.setLatitude(lat2);
        loc2.setLongitude(lon2);

        float distanceInMeters = loc1.distanceTo(loc2);

        return distanceInMeters;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        locationHelper.onActivityResult(requestCode, resultCode, data);
    }


    @Override
    protected void onResume() {
        super.onResume();
        locationHelper.checkPlayServices();
    }

    /**
     * Google api callback methods
     */
    @Override
    public void onConnectionFailed(ConnectionResult result) {
        Log.i("Connection failed:", " ConnectionResult.getErrorCode() = "
                + result.getErrorCode());
    }

    @Override
    public void onConnected(Bundle arg0) {

        // Once connected with google api, get the location
        mLastLocation = locationHelper.getLocation();
    }

    @Override
    public void onConnectionSuspended(int arg0) {
        locationHelper.connectApiClient();
    }


    // Permission check functions
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        // redirects to utils
        locationHelper.onRequestPermissionsResult(requestCode, permissions, grantResults);

    }

    public void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

}
