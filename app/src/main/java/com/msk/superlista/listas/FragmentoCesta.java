package com.msk.superlista.listas;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.LightingColorFilter;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.msk.superlista.R;
import com.msk.superlista.db.DBListas;
import com.msk.superlista.db.EditaItem;

import java.text.NumberFormat;
import java.util.Locale;

public class FragmentoCesta extends Fragment {

    private static final String ARG_CESTA = "lista";

    // CLASSE COM BANCO DE DADOS
    private DBListas dbListaCriada;

    // ELEMENTOS DA TELA
    private ImageView menuItemLista;
    private LayoutInflater inflaterLista;
    private LinearLayout info;
    private ListView listaParaUsar;
    private TextView nomeItem, descricao, semItens, valorLista, preco, unidade;

    // VARIAREIS UTILIZADAS
    private String lista, itemDaLista, descricaoItem, unidadeItem;
    private double quantidadeItem, valorItem;
    private Cursor itensLista = null;
    private NumberFormat dinheiro, quantidade;
    private View viewItem;
    private Long idItem;

    public FragmentoCesta() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static FragmentoCesta newInstance(String str) {
        FragmentoCesta fragment = new FragmentoCesta();
        Bundle args = new Bundle();
        args.putString(ARG_CESTA, str);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            lista = getArguments().getString(ARG_CESTA);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View vCesta = inflater.inflate(R.layout.conteudo_usa_listas, container, false);

        listaParaUsar = (ListView) vCesta.findViewById(R.id.listaDeItens);
        semItens = (TextView) vCesta.findViewById(R.id.tvCestaSemItem);
        valorLista = (TextView) vCesta.findViewById(R.id.tvValorLista);

        ColocaItensLista();

        listaParaUsar.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                // METODO DO ITEM DA LISTA VAZIA
                dbListaCriada.open();
                itensLista = dbListaCriada.buscaItensCesta(lista);
                itensLista.moveToPosition(i);

                // BUSCA DADOS DO ITEM
                idItem = itensLista.getLong(0);
                itemDaLista = itensLista.getString(2);
                descricaoItem = itensLista.getString(3);
                quantidadeItem = itensLista.getDouble(4);
                unidadeItem = itensLista.getString(5);
                valorItem = itensLista.getDouble(6);


                // MUDA O ITEM DE UMA LISTA PARA OUTRA
                dbListaCriada.excluiItemCesta(idItem);
                dbListaCriada.insereItemLista(lista, itemDaLista,
                        descricaoItem, quantidadeItem, unidadeItem, valorItem);
                dbListaCriada.close();
                Toast.makeText(
                        getActivity(),
                        String.format(
                                getResources().getString(
                                        R.string.dica_item_fora_cesta), itemDaLista),
                        Toast.LENGTH_SHORT).show();
                ColocaItensLista();
            }
        });

        return vCesta;
    }

    private void ColocaItensLista() {

        Locale current = getActivity().getResources().getConfiguration().locale;
        dinheiro = NumberFormat.getCurrencyInstance(current);
        quantidade = NumberFormat.getNumberInstance(current);

        dbListaCriada.open();
        itensLista = dbListaCriada.buscaItensCesta(lista);
        int u = itensLista.getCount();
        dbListaCriada.close();

        if (u >= 0) { // VERIFICA CURSOR NULO
            SimpleCursorAdapter buscaLista = new SimpleCursorAdapter(getActivity(), R.id.listaDeItens,
                    itensLista, new String[]{"item_cesta",
                    "qte_item_cesta", "preco_item_cesta", "descr_item_cesta"}, new int[]{
                    R.id.tvItemCesta, R.id.tvUnid, R.id.tvPreco, R.id.tvDescricaoItemCesta}) {
                public int getCount() {
                    int i = itensLista.getCount();
                    return i;
                }

                public long getItemId(int pItemCesta) {
                    return pItemCesta;
                }

                public View getView(int pItemCesta, View vCesta,
                                    ViewGroup vGCesta) {
                    BuscaItensDB(pItemCesta);
                    CriaItemLista(vCesta);
                    return viewItem;
                }
            };

            int posicao = listaParaUsar.getFirstVisiblePosition();
            listaParaUsar.setAdapter(buscaLista);
            listaParaUsar.setEmptyView(semItens);
            listaParaUsar.setSelection(posicao);
            mostraValor(lista);
        }
    }

    private void mostraValor(String lista) {
        dbListaCriada.open();
        double db = dbListaCriada.mostraValor(lista, "cesta");
        double dbt = db + dbListaCriada.mostraValor(lista, "lista");
        dbListaCriada.close();
        valorLista.setText(getResources().getString(R.string.info_valor_cesta,
                dinheiro.format(db)));
        if (dbt == 0.0D) {
            valorLista.setVisibility(View.GONE);
        }
    }

    private void BuscaItensDB(int item) {

        // BUSCA ITENS NO DB
        dbListaCriada.open();
        itensLista.moveToPosition(item);
        idItem = itensLista.getLong(0);
        itemDaLista = itensLista.getString(2);
        descricaoItem = itensLista.getString(3);
        quantidadeItem = itensLista.getDouble(4);
        unidadeItem = itensLista.getString(5);
        valorItem = itensLista.getDouble(6);
        dbListaCriada.close();
    }

    private void CriaItemLista(View v) {

        // Infla os itens que serao mostrados na lista
        viewItem = v;
        viewItem = inflaterLista.inflate(R.layout.item_na_cesta, null);
        info = (LinearLayout) viewItem.findViewById(R.id.infoItem);
        nomeItem = ((TextView) viewItem.findViewById(R.id.tvItemCesta));
        descricao = ((TextView) viewItem.findViewById(R.id.tvDescricaoItemCesta));
        unidade = ((TextView) viewItem.findViewById(R.id.tvUnid));
        preco = ((TextView) viewItem.findViewById(R.id.tvPreco));
        menuItemLista = (ImageView) viewItem.findViewById(R.id.ivMenuItem);

        // COLOCA ITENS NA TELA
        nomeItem.setText(itemDaLista);
        if (descricaoItem != null)
            descricao.setText(descricaoItem);
        if (descricao.getText().toString().equals(""))
            descricao.setText(R.string.descricao_item);
        unidade.setText(quantidade.format(quantidadeItem) + " " + unidadeItem);
        preco.setText(dinheiro.format(valorItem));
        if (quantidadeItem == 1.0D && valorItem == 0.0D) {
            info.setVisibility(View.GONE);
        } else {
            if (valorItem == 0.0D) {
                preco.setVisibility(View.GONE);
            }
        }

        menuItemLista.setTag(idItem);
        menuItemLista
                .setOnClickListener(new View.OnClickListener() {
                    public void onClick(View vItem) {
                        menuItemLista = ((ImageView) vItem);
                        menuItemLista.getDrawable().setColorFilter(
                                Color.rgb(51, 181, 229), PorterDuff.Mode.MULTIPLY);
                        idItem = ((Long) vItem.getTag()).longValue();
                        menuContexto();
                    }
                });
    }

    private void menuContexto() {
        AlertDialog.Builder dialogoBuilder = new AlertDialog.Builder(getActivity());

        String[] meuMenu = new String[]{getActivity().getResources().getString(R.string.dica_edita_item),
                getActivity().getResources().getString(R.string.dica_exclui_item)};

        dialogoBuilder.setItems(meuMenu,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        switch (id) {
                            case 0:
                                // ENVIA DADOS PARA EDITOR DO ITEM
                                Bundle carta = new Bundle();
                                carta.putLong("id", idItem);
                                carta.putString("tipo", "cesta");
                                Intent correio = new Intent(FragmentoCesta.this.getActivity(), EditaItem.class);
                                correio.putExtras(carta);
                                FragmentoCesta.this.startActivity(correio);
                                dialog.dismiss();
                                break;
                            case 1:
                                // Exclui itens na lista
                                dbListaCriada.open();
                                dbListaCriada.excluiItemCesta(idItem);
                                dbListaCriada.close();
                                ColocaItensLista();
                                dialog.dismiss();
                                break;
                        }
                    }
                });

        // create alert dialog
        AlertDialog alertDialog = dialogoBuilder.create();
        alertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {

                ColorFilter filter = new LightingColorFilter(Color.DKGRAY, Color.DKGRAY);
                menuItemLista.getDrawable().setColorFilter(filter);
            }
        });
        // show it
        alertDialog.show();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    @Override
    public void onResume() {
        ColocaItensLista();
        super.onResume();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        dbListaCriada = new DBListas(context);
        inflaterLista = getActivity().getLayoutInflater();
        dbListaCriada.open();

    }

    @Override
    public void onDetach() {
        super.onDetach();
        dbListaCriada.close();
    }

}
