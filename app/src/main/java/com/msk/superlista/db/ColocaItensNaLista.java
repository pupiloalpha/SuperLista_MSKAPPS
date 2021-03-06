package com.msk.superlista.db;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
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

import androidx.appcompat.app.AppCompatActivity;

import com.msk.superlista.R;

import java.util.ArrayList;
import java.util.List;

public class ColocaItensNaLista extends AppCompatActivity implements
        AdapterView.OnItemSelectedListener {
    private final List<String> selecionados = new ArrayList<String>();
    private String[] ITENSNOVOS = null;
    private Spinner categorias;
    private CheckBox chk;
    private DBListas dbListaNova = new DBListas(this);
    private LayoutInflater inflaterLista;
    private String itemNome;
    private Cursor itensParaLista = null;
    private String listaCriada;
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

        listaVazia = findViewById(R.id.lvItensLista);
        categorias = findViewById(R.id.spCategorias);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private void MostraItens() {
        listaVazia = findViewById(R.id.lvItensLista);
        ArrayAdapter<String> adapLista = new ArrayAdapter(this, android.R.layout.simple_list_item_1) {

            public View getView(int posicao, View vLista, ViewGroup vGroup) {

                itemNome = ITENSNOVOS[posicao];

                viewLista = vLista;
                if (viewLista == null) {
                    inflaterLista = getLayoutInflater();
                    viewLista = inflaterLista.inflate(
                            R.layout.item_lista_pronta, null);
                }
                chk = viewLista.findViewById(R.id.cbItemNovaLista);
                nomeItem = viewLista
                        .findViewById(R.id.tvItemNovaLista);

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
                                    0.0D, "unid", 0.0D);
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
                        setResult(RESULT_OK, null);
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
                ITENSNOVOS = getResources()
                        .getStringArray(R.array.ListaMantimentos);
                break;
            case 1:
                ITENSNOVOS = getResources().getStringArray(R.array.ListaCarnes);
                break;
            case 2:
                ITENSNOVOS = getResources().getStringArray(R.array.ListaFeira);
                break;
            case 3:
                ITENSNOVOS = getResources().getStringArray(R.array.ListaHigiene);
                break;
            case 4:
                ITENSNOVOS = getResources().getStringArray(R.array.ListaBebidas);
                break;
            case 5:
                ITENSNOVOS = getResources().getStringArray(R.array.ListaPapelaria);
                break;
            case 6:
                ITENSNOVOS = getResources().getStringArray(R.array.ListaPresente);
                break;
            case 7:
                ITENSNOVOS = getResources().getStringArray(R.array.ListaDiversos);
                break;
        }

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
