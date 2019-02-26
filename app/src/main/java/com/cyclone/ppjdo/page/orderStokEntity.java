package com.cyclone.ppjdo.page;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.cyclone.ppjdo.R;

/**
 * Created by cyclone on 7/22/17.
 */

public class orderStokEntity extends ArrayAdapter<String> {
    private String[] idax;
    private String[] rating;
    private String[] departemen;
    private String[] judul;
    private String[] harga;
    private String[] orderstok;
    private String[] stokaktual;
    private String[] ito;
    private String[] stokgudang;
    private String[] stokrekomendasi;
    private String[] idtoko;
    private String[] iddep;
    private String[] isbn;
    private String[] stokgudangjkt;
    private String[] stokgudangbdg;
    private String[] stokgudangsby;
    private String[] stokgudangjog;
    //additional
    private String[] stokfisik;
    private Context context;

    public orderStokEntity(Context context, String[] idax, String[] rating, String[] departemen, String[] judul, String[] harga, String[] orderstok, String[] stokaktual, String[] ito, String[] stokgudang, String[] stokrekomendasi, String[] idtoko, String[] iddep,String[] isbn,String[] stokgudangjkt, String[] stokgudangbdg, String[] stokgudangsby, String[] stokgudangjog) {
        super(context, R.layout.baris_orderstok, idax);
        this.context = context;
        this.idax = idax;
        this.rating = rating;
        this.departemen = departemen;
        this.judul = judul;
        this.harga = harga;
        this.orderstok = orderstok;
        this.stokaktual = stokaktual;
        this.ito = ito;
        this.stokgudang = stokgudang;
        this.stokrekomendasi = stokrekomendasi;
        this.idtoko = idtoko;
        this.iddep = iddep;
        this.isbn = isbn;
        this.stokgudangjkt = stokgudangjkt;
        this.stokgudangbdg = stokgudangbdg;
        this.stokgudangsby = stokgudangsby;
        this.stokgudangjog = stokgudangjog;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View listViewItem = inflater.inflate(R.layout.baris_orderstok, null, true);
        TextView txtIdax = (TextView) listViewItem.findViewById(R.id.txtidax);
        TextView txtRating = (TextView) listViewItem.findViewById(R.id.txtRating);
        TextView txtDepartemen = (TextView) listViewItem.findViewById(R.id.txtDepartemen);
        TextView txtJudul = (TextView) listViewItem.findViewById(R.id.txtJudul);
        TextView txtHarga = (TextView) listViewItem.findViewById(R.id.txtHarga);
        TextView txtStok = (TextView) listViewItem.findViewById(R.id.txtStok);
        TextView txtStokAktual = (TextView) listViewItem.findViewById(R.id.txtStokAktual);
        TextView txtIto = (TextView) listViewItem.findViewById(R.id.txtIto);
        TextView txtStokGudang = (TextView) listViewItem.findViewById(R.id.txtStokGudang);
        TextView txtRekomendasiStok = (TextView) listViewItem.findViewById(R.id.txtRekomendasi);
        TextView txtIdtoko = (TextView) listViewItem.findViewById(R.id.idtoko);
        TextView txtIddep = (TextView) listViewItem.findViewById(R.id.iddep);
        TextView txtIsbn = (TextView) listViewItem.findViewById(R.id.txtIsbn);
        TextView txtStokGudangJkt = (TextView) listViewItem.findViewById(R.id.txtStokGudangJkt);
        TextView txtStokGudangBdg = (TextView) listViewItem.findViewById(R.id.txtStokGudangBdg);
        TextView txtStokGudangSby = (TextView) listViewItem.findViewById(R.id.txtStokGudangSby);
        TextView txtStokGudangJog = (TextView) listViewItem.findViewById(R.id.txtStokGudangJog);

        if (idax.length > 0 && idax.length > position && idax[position] != null) {
            txtIdax.setText(idax[position]);
            txtRating.setText("Rating : " + rating[position]);
            txtDepartemen.setText(departemen[position]);
            txtJudul.setText(judul[position]);
            txtHarga.setText(harga[position]);
            txtStok.setText("Order Stok : " + orderstok[position]);
            txtStokAktual.setText("Sisa Stok : " + stokaktual[position]);
            txtIto.setText("ITO : " + ito[position]);
            txtStokGudang.setText("Stok Gudang : " + stokgudang[position]);
            txtRekomendasiStok.setText("Rekomendasi Stok : " + stokrekomendasi[position]);
            txtIdtoko.setText(idtoko[position]);
            txtIddep.setText(iddep[position]);
            txtIsbn.setText(isbn[position]);
            txtStokGudangJkt.setText("Gudang Jakarta : " + stokgudangjkt[position]);
            txtStokGudangBdg.setText("Gudang Bandung : " + stokgudangbdg[position]);
            txtStokGudangSby.setText("Gudang Surabaya : " + stokgudangsby[position]);
            txtStokGudangJog.setText("Gudang Jogjakarta : " + stokgudangjog[position]);
        }

        return listViewItem;
    }
}

