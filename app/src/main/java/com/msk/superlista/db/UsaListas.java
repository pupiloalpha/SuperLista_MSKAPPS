package com.msk.superlista.db;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.LightingColorFilter;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.msk.superlista.R;

@SuppressWarnings("deprecation")
public class UsaListas extends AppCompatActivity {

    // PREFRENCIAS DO USUARIO
    SharedPreferences buscaPreferencias = null;
    Boolean apagarLista = true;
    // ELEMENTOS DA TELA

    private ActionBar actionBar;
    private ImageView menuCestaCheia, menuCestaVazia;
    private LayoutInflater inflaterLista;
    private ListView listaCestaCheia, listaCestaVazia;
    private TextView nomeItem, descricao, semItens, valorLista, preco, unidade;
    // VARIAREIS UTILIZADAS
    private String listaFeita, itemDaLista, quantidadeItem, valorItem, descricaoItem, cesta;
    private Cursor itensCestaCheia = null;
    private Cursor itensCestaVazia = null;
    private SimpleCursorAdapter buscaLista, buscaCesta;
    private View viewLista;
    private Long idItem;
    // CLASSE COM BANCO DE DADOS
    private DBListas dbListaCriada = new DBListas(this);
    OnItemClickListener itemCestaCheia = new OnItemClickListener() {
        // A CESTA CHEIA SIGNIFICA QUE OS ITENS FORAM PEGOS
        public void onItemClick(AdapterView<?> arg0, View arg1, int posicao,
                                long arg3) {
            // METODO DO ITEM DA LISTA CHEIA
            dbListaCriada.open();
            itensCestaCheia = dbListaCriada.buscaItensCesta(listaFeita);
            itensCestaCheia.moveToPosition(posicao);

            // BUSCA DADOS DO ITEM
            idItem = itensCestaCheia.getLong(0);
            itemDaLista = itensCestaCheia.getString(2);
            quantidadeItem = itensCestaCheia.getString(3);
            valorItem = itensCestaCheia.getString(4);
            descricaoItem = itensCestaCheia.getString(5);

            // MUDA O ITEM DE UMA LISTA PARA OUTRA
            dbListaCriada.excluiItemCesta(idItem);
            dbListaCriada.insereItemLista(listaFeita, itemDaLista,
                    descricaoItem, quantidadeItem, valorItem);
            dbListaCriada.close();
            Toast.makeText(
                    getApplicationContext(),
                    String.format(
                            getResources().getString(
                                    R.string.dica_item_fora_cesta), itemDaLista),
                    Toast.LENGTH_SHORT).show();
            CestaCheia();
        }

    };
    // ActionBar actionBar;
    OnItemClickListener itemCestaVazia = new OnItemClickListener() {
        // A CESTA VAZIA SIGNIFICA QUE OS ITENS AINDA NAO FORAM PEGOS
        public void onItemClick(AdapterView<?> arg0, View arg1, int posicao,
                                long arg3) {
            // METODO DO ITEM DA LISTA VAZIA
            dbListaCriada.open();
            itensCestaVazia = dbListaCriada.buscaItensLista(listaFeita);
            itensCestaVazia.moveToPosition(posicao);

            // BUSCA DADOS DO ITEM
            idItem = itensCestaVazia.getLong(0);
            itemDaLista = itensCestaVazia.getString(2);
            quantidadeItem = itensCestaVazia.getString(3);
            valorItem = itensCestaVazia.getString(4);
            descricaoItem = itensCestaVazia.getString(5);

            // MUDA O ITEM DE UMA LISTA PARA OUTRA
            dbListaCriada.excluiItemLista(idItem);
            dbListaCriada.insereItemCesta(listaFeita, itemDaLista,
                    descricaoItem, quantidadeItem, valorItem);
            dbListaCriada.close();
            Toast.makeText(
                    getApplicationContext(),
                    String.format(
                            getResources().getString(
                                    R.string.dica_item_na_cesta), itemDaLista),
                    Toast.LENGTH_SHORT).show();
            CestaVazia();
        }

    };

    @Override
    protected void onCreate(Bundle paramBundle) {
        super.onCreate(paramBundle);
        setContentView(R.layout.usa_lista);
        buscaPreferencias = PreferenceManager
                .getDefaultSharedPreferences(getBaseContext());
        apagarLista = buscaPreferencias.getBoolean("cesta", true);
        dbListaCriada.open();
        iniciar();
        listaFeita = getIntent().getExtras().getString("lista");
        usarActionBar();
        CestaCheia();
        CestaVazia();
        listaCestaVazia.setOnItemClickListener(itemCestaVazia);
        listaCestaCheia.setOnItemClickListener(itemCestaCheia);
        listaCestaCheia.setVisibility(View.GONE);

    }

    private void iniciar() {

        listaCestaVazia = ((ListView) findViewById(R.id.lvCestaVazia));
        listaCestaCheia = ((ListView) findViewById(R.id.lvCestaCheia));
        semItens = (TextView) findViewById(R.id.tvCestaSemItem);
        valorLista = (TextView) findViewById(R.id.tvValorLista);

    }

    private void CestaCheia() {
        dbListaCriada.open();
        itensCestaCheia = dbListaCriada.buscaItensCesta(listaFeita);
        int e = itensCestaCheia.getCount();
        dbListaCriada.close();
        if (e >= 0) { // VERIFICA CURSOR NULO
            buscaCesta = new SimpleCursorAdapter(this, R.id.lvCestaCheia,
                    itensCestaCheia, new String[]{"item_cesta",
                    "qte_item_cesta", "preco_item_cesta", "descr_item_cesta"}, new int[]{
                    R.id.tvItemCesta, R.id.tvUnid, R.id.tvPreco, R.id.tvDescricaoItemCesta}) {
                public int getCount() {
                    dbListaCriada.open();
                    int d = dbListaCriada.contaItensCesta(listaFeita);
                    dbListaCriada.close();
                    return d;
                }

                public long getItemId(int pitemCestaCheia) {
                    return pitemCestaCheia;
                }

                public View getView(int pItemCestaCheia, View vCestaCheia,
                                    ViewGroup vGCestaCheia) {

                    CriaItemLista(vCestaCheia, "cheia");
                    BuscaItensDB(pItemCestaCheia, "cheia");

                    // COLOCA ITENS NA TELA
                    nomeItem.setText(itemDaLista);
                    if (descricaoItem != null)
                        descricao.setText(descricaoItem);
                    if (descricao.getText().toString().equals(""))
                        descricao.setText(R.string.descricao_item);
                    unidade.setText(quantidadeItem);
                    preco.setText(valorItem);

                    registerForContextMenu(menuCestaCheia);

                    menuCestaCheia.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View vItem) {
                            cesta = "cheia";
                            menuCestaCheia = (ImageView) vItem.findViewById(R.id.ivMenuItem);
                            menuCestaCheia.getDrawable().setColorFilter(Color.rgb(51, 181, 229), PorterDuff.Mode.MULTIPLY);
                            openContextMenu(vItem);
                        }
                    });

                    return viewLista;
                }
            };

            int posicao = listaCestaCheia.getFirstVisiblePosition();
            listaCestaCheia.setAdapter(buscaCesta);
            listaCestaCheia.setEmptyView(semItens);
            listaCestaCheia.setSelection(posicao);
            mostraValor(listaFeita);
        }
    }

    private void CestaVazia() {
        dbListaCriada.open();
        itensCestaVazia = dbListaCriada.buscaItensLista(listaFeita);
        int u = itensCestaVazia.getCount();
        dbListaCriada.close();

        if (u >= 0) { // VERIFICA CURSOR NULO
            buscaLista = new SimpleCursorAdapter(this, R.id.lvCestaVazia,
                    itensCestaVazia, new String[]{"item_lista",
                    "qte_item_lista", "preco_item_lista", "descr_item_lista"}, new int[]{
                    R.id.tvItemCesta, R.id.tvUnid, R.id.tvPreco, R.id.tvDescricaoItemCesta}) {
                public int getCount() {
                    dbListaCriada.open();
                    int i = dbListaCriada.contaItensLista(listaFeita);
                    dbListaCriada.close();
                    return i;
                }

                public long getItemId(int pItemCestaVazia) {
                    return pItemCestaVazia;
                }

                public View getView(int pItemCestaVazia, View vCestaVazia,
                                    ViewGroup vGCestaVazia) {

                    CriaItemLista(vCestaVazia, "vazia");
                    BuscaItensDB(pItemCestaVazia, "vazia");

                    // COLOCA ITENS NA TELA
                    nomeItem.setText(itemDaLista);
                    if (descricaoItem != null)
                        descricao.setText(descricaoItem);
                    if (descricao.getText().toString().equals(""))
                        descricao.setText(R.string.descricao_item);
                    unidade.setText(quantidadeItem);
                    preco.setText(valorItem);

                    registerForContextMenu(menuCestaVazia);

                    menuCestaVazia
                            .setOnClickListener(new View.OnClickListener() {
                                public void onClick(View vItem) {
                                    cesta = "vazia";
                                    menuCestaVazia = ((ImageView) vItem);
                                    menuCestaVazia.getDrawable().setColorFilter(Color.rgb(51, 181, 229), PorterDuff.Mode.MULTIPLY);
                                    openContextMenu(vItem);
                                }
                            });

                    return viewLista;
                }
            };

            int posicao = listaCestaVazia.getFirstVisiblePosition();
            listaCestaVazia.setAdapter(buscaLista);
            listaCestaVazia.setEmptyView(semItens);
            listaCestaVazia.setSelection(posicao);
            mostraValor(listaFeita);
        }
    }

    private void mostraValor(String lista) {
        dbListaCriada.open();
        String str = dbListaCriada.mostraValorCesta(lista);
        dbListaCriada.close();
        valorLista.setText(getResources().getString(R.string.info_valor_item,
                str));
    }


    private void CriaItemLista(View v, String cesta) {

        // Infla os itens que serao mostrados na lista
        viewLista = v;
        inflaterLista = getLayoutInflater();
        viewLista = inflaterLista.inflate(R.layout.item_na_cesta,
                null);
        nomeItem = ((TextView) viewLista
                .findViewById(R.id.tvItemCesta));
        descricao = ((TextView) viewLista
                .findViewById(R.id.tvDescricaoItemCesta));
        unidade = ((TextView) viewLista.findViewById(R.id.tvUnid));
        preco = ((TextView) viewLista.findViewById(R.id.tvPreco));

        if (cesta.equals("cheia"))
            menuCestaCheia = (ImageView) viewLista
                    .findViewById(R.id.ivMenuItem);
        else
            menuCestaVazia = (ImageView) viewLista
                    .findViewById(R.id.ivMenuItem);
    }

    private void BuscaItensDB(int item, String cesta) {

        // BUSCA ITENS NO DB
        dbListaCriada.open();

        if (cesta.equals("cheia")) {
            itensCestaCheia.moveToPosition(item);
            itemDaLista = itensCestaCheia.getString(2);
            quantidadeItem = itensCestaCheia.getString(3);
            valorItem = itensCestaCheia.getString(4);
            descricaoItem = itensCestaCheia.getString(5);
            menuCestaCheia.setTag(itensCestaCheia.getLong(0));
        } else {
            itensCestaVazia.moveToPosition(item);
            itemDaLista = itensCestaVazia.getString(2);
            quantidadeItem = itensCestaVazia.getString(3);
            valorItem = itensCestaVazia.getString(4);
            descricaoItem = itensCestaVazia.getString(5);
            menuCestaVazia.setTag(itensCestaVazia.getLong(0));
        }
        dbListaCriada.close();
    }

    @SuppressLint("NewApi")
    private void usarActionBar() {

        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setTitle(listaFeita);

        // Define a navegaçao por tabs
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        // Create a tab listener that is called when the user changes tabs.
        ActionBar.TabListener tabListener = new ActionBar.TabListener() {
            public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
                // show the given tab
                switch (tab.getPosition()) {
                    case 0:
                        listaCestaCheia.setVisibility(View.GONE);
                        listaCestaVazia.setVisibility(View.VISIBLE);
                        CestaVazia();
                        break;

                    case 1:
                        listaCestaVazia.setVisibility(View.GONE);
                        listaCestaCheia.setVisibility(View.VISIBLE);
                        CestaCheia();
                        break;
                }
            }

            public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {
                // hide the given tab

            }

            public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {
                // probably ignore this event

            }
        };

        actionBar.addTab(
                actionBar.newTab()
                        .setText(getResources().getString(R.string.item_falta))
                        .setTabListener(tabListener));
        actionBar.addTab(
                actionBar.newTab()
                        .setText(getResources().getString(R.string.item_peguei))
                        .setTabListener(tabListener));

        actionBar.setSelectedNavigationItem(0);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
        getMenuInflater().inflate(R.menu.menu_usa_lista, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case android.R.id.home:
                setResult(RESULT_OK, null);
                dbListaCriada.close();
                finish();
                break;
            case R.id.botao_novo_item:
                Bundle carta = new Bundle();
                carta.putString("lista", listaFeita);
                if (listaCestaVazia.getVisibility() == View.GONE) {
                    carta.putString("cesta", "cheia");
                } else if (listaCestaCheia.getVisibility() == View.GONE) {
                    carta.putString("cesta", "vazia");
                }
                Intent correio = new Intent("com.msk.superlista.NOVOITEM");
                correio.putExtras(carta);
                startActivityForResult(correio, 0);
                break;
            case R.id.botao_refaz_lista:
                dbListaCriada.open();
                itensCestaCheia = dbListaCriada.buscaItensCesta(listaFeita);

                for (int j = 0; j < itensCestaCheia.getCount(); j++) {
                    itensCestaCheia.moveToPosition(j);
                    itemDaLista = itensCestaCheia.getString(2);
                    quantidadeItem = itensCestaCheia.getString(3);
                    valorItem = itensCestaCheia.getString(4);
                    descricaoItem = itensCestaCheia.getString(5);
                    dbListaCriada.insereItemLista(listaFeita, itemDaLista,
                            descricaoItem, quantidadeItem, valorItem);
                }

                dbListaCriada.excluiItensCesta(listaFeita);
                CestaCheia();
                CestaVazia();
                dbListaCriada.close();

                actionBar.setSelectedNavigationItem(0);

                break;
            case R.id.ajustes:
                startActivity(new Intent("com.msk.superlista.AJUSTES"));
                break;
            case R.id.sobre:
                startActivity(new Intent("com.msk.superlista.SOBRE"));
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateContextMenu(ContextMenu cMenu, View v,
                                    ContextMenu.ContextMenuInfo cMenuInfo) {
        super.onCreateContextMenu(cMenu, v, cMenuInfo);

        idItem = ((Long) v.getTag()).longValue();
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_altera_item, cMenu);

    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.botao_edita_item:

                // ENVIA DADOS PARA EDITOR DO ITEM
                Bundle carta = new Bundle();
                carta.putLong("id", idItem);
                carta.putString("cesta", cesta);
                Intent correio = new Intent(
                        "com.msk.superlista.EDITAITEM");
                correio.putExtras(carta);
                startActivityForResult(correio, 0);

                if (cesta.equals("cheia")) {
                    CestaCheia();
                } else {
                    CestaVazia();
                }
                break;
            case R.id.botao_exclui_item:

                // Exclui itens na cesta
                if (cesta.equals("cheia")) {
                    dbListaCriada.open();
                    dbListaCriada.excluiItemCesta(idItem);
                    dbListaCriada.close();
                } else {
                    dbListaCriada.open();
                    dbListaCriada.excluiItemLista(idItem);
                    dbListaCriada.close();
                }

                break;
        }

        return super.onContextItemSelected(item);
    }

    @Override
    public void onContextMenuClosed(Menu menu) {

        ColorFilter filter = new LightingColorFilter(Color.DKGRAY, Color.DKGRAY);

        if (cesta.equals("cheia")) {
            CestaCheia();
            menuCestaCheia.getDrawable().setColorFilter(filter);
        } else {
            CestaVazia();
            menuCestaVazia.getDrawable().setColorFilter(filter);
        }

        super.onContextMenuClosed(menu);
    }

    protected void onDestroy() {
        super.onDestroy();
        dbListaCriada.open();
        itensCestaCheia = dbListaCriada.buscaItensLista(listaFeita);
        if (itensCestaCheia.getCount() == 0 && apagarLista == true) {
            dbListaCriada.excluiItensCesta(listaFeita);
            dbListaCriada.excluiItensLista(listaFeita);
            dbListaCriada.excluiLista(listaFeita);
        }
        dbListaCriada.close();
    }

    @Override
    protected void onResume() {
        CestaCheia();
        CestaVazia();
        super.onResume();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            CestaCheia();
            CestaVazia();
        }
    }

}
