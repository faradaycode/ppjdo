package com.cyclone.ppjdo.page;

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

public class cekStokEntity extends ArrayAdapter<String> {
    private String[] idax;
    private String[] rating;
    private String[] departemen;
    private String[] judul;
    private String[] harga;
    private String[] stok;
    private String[] stokaktual;
    private String[] ito;
    private String[] stokgudang;
    private String[] isbn;
    private String[] stokgudangjkt;
    private String[] stokgudangbdg;
    private String[] stokgudangsby;
    private String[] stokgudangjog;
    private Context context;

    public cekStokEntity(Context context, String[] idax, String[] rating, String[] departemen, String[] judul, String[] harga, String[] stok, String[] stokaktual, String[] ito, String[] stokgudang, String[] isbn, String[] stokgudangjkt, String[] stokgudangbdg, String[] stokgudangsby, String[] stokgudangjog) {
        super(context, R.layout.baris_cekstok, idax);
        this.context = context;
        this.idax = idax;
        this.rating = rating;
        this.departemen = departemen;
        this.judul = judul;
        this.harga = harga;
        this.stok = stok;
        this.stokaktual = stokaktual;
        this.ito = ito;
        this.stokgudang = stokgudang;
        this.isbn = isbn;
        this.stokgudangjkt = stokgudangjkt;
        this.stokgudangbdg = stokgudangbdg;
        this.stokgudangsby = stokgudangsby;
        this.stokgudangjog = stokgudangjog;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View listViewItem = inflater.inflate(R.layout.baris_cekstok, null, true);
        TextView txtIdax = (TextView) listViewItem.findViewById(R.id.txtidax);
        TextView txtIsbn = (TextView) listViewItem.findViewById(R.id.txtIsbn);
        TextView txtStokGudangJkt = (TextView) listViewItem.findViewById(R.id.txtStokGudangJkt);
        TextView txtStokGudangBdg = (TextView) listViewItem.findViewById(R.id.txtStokGudangBdg);
        TextView txtStokGudangSby = (TextView) listViewItem.findViewById(R.id.txtStokGudangSby);
        TextView txtStokGudangJog = (TextView) listViewItem.findViewById(R.id.txtStokGudangJog);
        TextView txtRating = (TextView) listViewItem.findViewById(R.id.txtRating);
        TextView txtDepartemen = (TextView) listViewItem.findViewById(R.id.txtDepartemen);
        TextView txtJudul = (TextView) listViewItem.findViewById(R.id.txtJudul);
        TextView txtHarga = (TextView) listViewItem.findViewById(R.id.txtHarga);
        TextView txtStok = (TextView) listViewItem.findViewById(R.id.txtStok);
        TextView txtStokAktual = (TextView) listViewItem.findViewById(R.id.txtStokAktual);
        TextView txtIto = (TextView) listViewItem.findViewById(R.id.txtIto);
        TextView txtStokGudang = (TextView) listViewItem.findViewById(R.id.txtStokGudang);

        if (idax.length > 0 && idax.length > position && idax[position] != null) {
            txtIdax.setText(idax[position]);
            txtRating.setText("Rating : " + rating[position]);
            txtDepartemen.setText(departemen[position]);
            txtJudul.setText(judul[position]);
            txtHarga.setText(harga[position]);
            txtStok.setText("Stok : " + stok[position]);
            txtStokAktual.setText("Sisa Stok : " + stokaktual[position]);
            txtIto.setText("ITO : " + ito[position]);
            txtStokGudang.setText("Stok Gudang Total : " + stokgudang[position]);
            txtIsbn.setText(isbn[position]);
            txtStokGudangJkt.setText("Gudang Jakarta : " + stokgudangjkt[position]);
            txtStokGudangBdg.setText("Gudang Bandung : " + stokgudangbdg[position]);
            txtStokGudangSby.setText("Gudang Surabaya : " + stokgudangsby[position]);
            txtStokGudangJog.setText("Gudang Jogjakarta : " + stokgudangjog[position]);
        }

        return listViewItem;
    }
}
