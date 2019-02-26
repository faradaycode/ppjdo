package com.cyclone.ppjdo.page;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.cyclone.ppjdo.R;

/**
 * Created by cyclone on 7/16/17.
 */

public class cekInOutEntity extends ArrayAdapter<String> {
    private String[] id;
    private String[] tgl;
    private String[] bln;
    private String[] masuk;
    private String[] keluar;
    private String[] jam;
    private String[] toko;
    private String[] idtoko;
    private String[] kordinatLat;
    private String[] kordinatLong;
    private Context context;

    public cekInOutEntity(Context context, String[] id, String[] tgl, String[] bln, String[] jam, String[] idtoko, String[] toko, String[] masuk, String[] keluar, String[] kordinatLat, String[] kordinatLong) {
        super(context, R.layout.baris_cekinout, id);
        this.context = context;
        this.id = id;
        this.tgl = tgl;
        this.bln = bln;
        this.masuk = masuk;
        this.keluar = keluar;
        this.jam = jam;
        this.toko = toko;
        this.idtoko = idtoko;
        this.kordinatLat = kordinatLat;
        this.kordinatLong = kordinatLong;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View listViewItem = inflater.inflate(R.layout.baris_cekinout, null, true);
        TextView txtId = (TextView) listViewItem.findViewById(R.id.id);
        TextView txtTgl = (TextView) listViewItem.findViewById(R.id.tgl);
        TextView txtBulan = (TextView) listViewItem.findViewById(R.id.bln);
        TextView txtBulanFull = (TextView) listViewItem.findViewById(R.id.blnFull);
        TextView txtJam = (TextView) listViewItem.findViewById(R.id.jam);
        TextView txtToko = (TextView) listViewItem.findViewById(R.id.toko);
        TextView txtIdToko = (TextView) listViewItem.findViewById(R.id.idtoko);
        TextView txtCekin = (TextView) listViewItem.findViewById(R.id.labMasuk);
        TextView txtCekout = (TextView) listViewItem.findViewById(R.id.labKeluar);
        TextView txtLat = (TextView) listViewItem.findViewById(R.id.kordinatLat);
        TextView txtLong = (TextView) listViewItem.findViewById(R.id.kordinatLong);

        if (id.length > 0 && id.length > position && id[position] != null) {
            String bulanPendek = bln[position];

            if (bulanPendek != null) {
                bulanPendek = bulanPendek.substring(0, 3);
            }

            txtId.setText(id[position]);
            txtTgl.setText(tgl[position]);
            txtBulan.setText(bulanPendek);
            txtBulanFull.setText(bln[position]);
            txtJam.setText(jam[position]);
            txtToko.setText(toko[position]);
            txtIdToko.setText(idtoko[position]);
            txtCekin.setText("Masuk : " + masuk[position]);
            txtCekout.setText("Keluar : " + keluar[position]);
            txtLat.setText(kordinatLat[position]);
            txtLong.setText(kordinatLong[position]);
        }

        return listViewItem;
    }
}