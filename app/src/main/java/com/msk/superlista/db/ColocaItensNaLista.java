package com.msk.superlista.db;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.msk.superlista.R;

import java.util.ArrayList;
import java.util.List;

public class ColocaItensNaLista extends AppCompatActivity implements
        AdapterView.OnItemSelectedListener {
    private final List<String> selecionados = new ArrayList<String>();
    private String[] ITENSNOVOS = null;
    private ArrayAdapter<String> adapLista;
    private Spinner categorias;
    private CheckBox chk;
    private DBListas dbListaNova = new DBListas(this);
    private LayoutInflater inflaterLista;
    private String itemNome;
    private Cursor itensParaLista = null;
    private String listaCriada;
    private String[] listaItens;
    private ListView listaVazia;
    private TextView nomeItem;
    private View viewLista;

    @Override
    protected void onCreate(Bundle paramBundle) {
        super.onCreate(paramBundle);
        setContentView(R.layout.lista_pronta);
        dbListaNova.open();
        iniciar();
        listaCriada = getIntent().getExtras().getString("lista");
        usarActionBar();
        ITENSNOVOS = getResources().getStringArray(R.array.ListaMantimentos);
        MostraItens();
        categorias.setOnItemSelectedListener(this);
    }

    private void iniciar() {

        listaVazia = ((ListView) findViewById(R.id.lvItensLista));
        categorias = ((Spinner) findViewById(R.id.spCategorias));
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private void MostraItens() {
        listaVazia = ((ListView) findViewById(R.id.lvItensLista));
        adapLista = new ArrayAdapter(this, android.R.layout.simple_list_item_1) {

            public View getView(int posicao, View vLista, ViewGroup vGroup) {

                itemNome = ITENSNOVOS[posicao];

                viewLista = vLista;
                if (viewLista == null) {
                    inflaterLista = getLayoutInflater();
                    viewLista = inflaterLista.inflate(
                            R.layout.item_lista_pronta, null);
                }
                chk = ((CheckBox) viewLista.findViewById(R.id.cbItemNovaLista));
                nomeItem = ((TextView) viewLista
                        .findViewById(R.id.tvItemNovaLista));

                chk.setTag(itemNome);
                nomeItem.setText(itemNome);

                chk.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View paramAnonymous2View) {
                        chk = ((CheckBox) paramAnonymous2View);
                        String str = (String) chk.getTag();
                        if (chk.isChecked()) {
                            Toast.makeText(
                                    getApplicationContext(),
                                    String.format(
                                            getResources().getString(
                                                    R.string.dica_item_novo),
                                            str), Toast.LENGTH_SHORT).show();
                            // dbListaNova.open();
                            dbListaNova.insereItemLista(listaCriada, str, "",
                                    getResources()
                                            .getString(R.string.dica_unid),
                                    getResources()
                                            .getString(R.string.dica_zero));
                            // dbListaNova.close();
                            if (!selecionados.contains(str))
                                selecionados.add(str);
                        } else {
                            Toast.makeText(
                                    getApplicationContext(),
                                    String.format(
                                            getResources()
                                                    .getString(
                                                            R.string.dica_item_removido),
                                            str), Toast.LENGTH_SHORT).show();
                            // dbListaNova.open();
                            dbListaNova.excluiItem(listaCriada, str);
                            // dbListaNova.close();
                            if (!selecionados.contains(str))
                                selecionados.remove(str);
                        }
                    }
                });

                // dbListaNova.open();
                itensParaLista = dbListaNova.buscaItensLista(listaCriada);
                int i;
                if (itensParaLista.getCount() >= 0) {
                    i = 0;
                    while (i < itensParaLista.getCount()) {
                        itensParaLista.moveToPosition(i);
                        selecionados.add(itensParaLista.getString(2));
                        i++;
                    }
                    if (!selecionados.contains(itemNome)) {
                        chk.setChecked(false);
                    } else {
                        chk.setChecked(true);
                    }
                }
                // dbListaNova.close();

                return viewLista;

            }

            @Override
            public int getCount() {
                return ITENSNOVOS.length;
            }

            @Override
            public long getItemId(int posicao) {
                return posicao;
            }

        };
        listaVazia.setAdapter(adapLista);
    }

    public void onItemSelected(AdapterView<?> paramAdapterView, View paramView,
                               int paramInt, long paramLong) {
        switch (paramInt) {
            default:
            case 0:
                listaItens = getResources()
                        .getStringArray(R.array.ListaMantimentos);
                break;
            case 1:
                listaItens = getResources().getStringArray(R.array.ListaCarnes);
                break;
            case 2:
                listaItens = getResources().getStringArray(R.array.ListaFeira);
                break;
            case 3:
                listaItens = getResources().getStringArray(R.array.ListaHigiene);
                break;
            case 4:
                listaItens = getResources().getStringArray(R.array.ListaBebidas);
                break;
            case 5:
                listaItens = getResources().getStringArray(R.array.ListaPapelaria);
                break;
            case 6:
                listaItens = getResources().getStringArray(R.array.ListaPresente);
                break;
            case 7:
                listaItens = getResources().getStringArray(R.array.ListaDiversos);
                break;
        }

        ITENSNOVOS = listaItens;
        MostraItens();

    }

    public void onNothingSelected(AdapterView<?> paramAdapterView) {
    }

    @SuppressLint("NewApi")
    private void usarActionBar() {

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle(listaCriada);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu pMenu) {
        super.onCreateOptionsMenu(pMenu);
        pMenu.clear();
        getMenuInflater().inflate(R.menu.menu_app, pMenu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem pMenuItem) {
        switch (pMenuItem.getItemId()) {

            case android.R.id.home:
                dbListaNova.close();
                setResult(RESULT_OK, null);
                finish();
                break;
            case R.id.ajustes:
                startActivity(new Intent("com.msk.superlista.AJUSTES"));
                break;
            case R.id.sobre:
                startActivity(new Intent("com.msk.superlista.SOBRE"));
                break;
        }
        return false;

    }


    @Override
    protected void onDestroy() {
        dbListaNova.close();
        setResult(RESULT_OK, null);
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        dbListaNova.close();
        super.onPause();
    }

    @Override
    protected void onResume() {
        dbListaNova.open();
        super.onResume();
    }

}
