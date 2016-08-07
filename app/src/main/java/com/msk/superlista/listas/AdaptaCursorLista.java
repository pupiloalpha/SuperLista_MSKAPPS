package com.msk.superlista.listas;

import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.msk.superlista.R;

import java.text.NumberFormat;
import java.util.Locale;

public class AdaptaCursorLista extends CursorAdapter {

    private LayoutInflater inflater;
    private NumberFormat dinheiro, quantidade;
    private Resources res = null;

    @SuppressWarnings("deprecation")
    public AdaptaCursorLista(Context context, Cursor c) {
        super(context, c);
        inflater = LayoutInflater.from(context);
        res = context.getResources();
        Locale current = res.getConfiguration().locale;
        dinheiro = NumberFormat.getCurrencyInstance(current);
        quantidade = NumberFormat.getNumberInstance(current);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        return inflater.inflate(R.layout.item_na_cesta, viewGroup, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        TextView nomeItem = ((TextView) view.findViewById(R.id.tvItemCesta));
        TextView descricao = ((TextView) view.findViewById(R.id.tvDescricaoItemCesta));
        TextView unidade = ((TextView) view.findViewById(R.id.tvUnid));
        TextView preco = ((TextView) view.findViewById(R.id.tvPreco));
        ImageView menuItemLista = (ImageView) view.findViewById(R.id.ivMenuItem);

        long idItem = cursor.getLong(0);
        String itemDaLista = cursor.getString(2);
        String descricaoItem = cursor.getString(3);
        double quantidadeItem = cursor.getDouble(4);
        String unidadeItem = cursor.getString(5);
        double valorItem = cursor.getDouble(6);
        menuItemLista.setTag(idItem);

        // COLOCA ITENS NA TELA
        nomeItem.setText(itemDaLista);
        if (descricaoItem != null)
            descricao.setText(descricaoItem);
        if (descricao.getText().toString().equals(""))
            descricao.setText(R.string.descricao_item);
        unidade.setText(quantidade.format(quantidadeItem) + " " + unidadeItem);
        preco.setText(dinheiro.format(valorItem));
        if (valorItem == 0.0D) {
            preco.setVisibility(View.GONE);
        }
    }
}
