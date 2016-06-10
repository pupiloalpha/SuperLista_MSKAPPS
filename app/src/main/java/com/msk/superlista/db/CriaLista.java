package com.msk.superlista.db;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.provider.CalendarContract.Events;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatAutoCompleteTextView;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.msk.superlista.R;

import java.util.Calendar;

@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
@SuppressLint("InlinedApi")
public class CriaLista extends AppCompatActivity implements View.OnClickListener {

    // VARIAVEIS UTLIZADAS
    final static Calendar c = Calendar.getInstance();
    // CLASSE COM BANCO DE DADOS
    DBListas dbListaCriada = new DBListas(this);
    // ELEMENTSO DA TELA
    private ImageButton ibAdiciona, ibExclui;
    private LayoutInflater inflaterLista;
    private ListView listaCriada;
    private AppCompatAutoCompleteTextView nomeNovoItem;
    private TextView nomeItem, descricaoItem, semItem, preco, unidade;
    private View viewLista;
    // ELEMENTOS PARA MONTAR A LISTA
    private ArrayAdapter<String> adapItem;
    private SimpleCursorAdapter buscaItens;
    private Cursor itensParaLista = null;
    private Cursor itensParaCesta = null;
    private long idItem;
    private int cesta = 0;
    private String conteudoLista, itemDaLista, listaFeita, descricao, outroItemNovo,
            quantidadeItem, valorItem, nItem, valItem, qteItem;

    @Override
    protected void onCreate(Bundle paramBundle) {
        super.onCreate(paramBundle);
        setContentView(R.layout.minha_lista);
        // setTheme(android.R.style.Theme_Black);
        dbListaCriada.open();
        iniciar();
        listaFeita = getIntent().getExtras().getString("lista");
        usarActionBar();
        RecuperaItens();
        FazLista();
        ibAdiciona.setOnClickListener(this);

        listaCriada.setOnItemClickListener(new OnItemClickListener() {

            public void onItemClick(AdapterView<?> arg0, View arg1,
                                    int posicao, long arg3) {

                // METODO CLIQUE NO ITEM DA LISTA
                dbListaCriada.open();
                itensParaLista.moveToPosition(posicao);
                idItem = itensParaLista.getLong(0);
                dbListaCriada.close();
                FazLista();
                Bundle carta = new Bundle();
                carta.putString("cesta", "vazia");
                carta.putLong("id", idItem);
                Intent correio = new Intent("com.msk.superlista.EDITAITEM");
                correio.putExtras(carta);
                startActivity(correio);
            }

        });
    }

    private void iniciar() {
        nomeNovoItem = ((AppCompatAutoCompleteTextView) findViewById(R.id.etItem));
        nomeItem = ((TextView) findViewById(R.id.tvItemEscolhido));
        semItem = ((TextView) findViewById(R.id.tvSemItem));
        unidade = ((TextView) findViewById(R.id.tvUnidade));
        preco = ((TextView) findViewById(R.id.tvValor));
        listaCriada = ((ListView) findViewById(R.id.lvListaCriada));
        ibExclui = ((ImageButton) findViewById(R.id.ibExcluiItemEscolhido));
        ibAdiciona = ((ImageButton) findViewById(R.id.ibAdicionaItens));
        adapItem = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, getResources()
                .getStringArray(R.array.ListaComTudo));
        nomeNovoItem.setAdapter(adapItem);

    }

    private void RecuperaItens() {
        // VERIFICA SE USUARIO DESEJA RECUPERAR ITENS DA CESTA
        dbListaCriada.open();
        cesta = dbListaCriada.contaItensCesta(listaFeita);
        dbListaCriada.close();

        if (cesta > 0) {
            // O QUE FAZER SE EXISTEM ITENS NA CESTA MAS NAO EXISTE NA LISTA
            new AlertDialog.Builder(new ContextThemeWrapper(this,
                    R.style.TemaDialogo))
                    .setTitle(R.string.titulo_recupera_lista)
                    .setMessage(R.string.texto_recupera_lista)
                    .setPositiveButton(R.string.ok,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface pDialogo,
                                                    int pInt) {
                                    dbListaCriada.open();
                                    itensParaCesta = dbListaCriada
                                            .buscaItensCesta(listaFeita);
                                    for (int j = 0; j < itensParaCesta
                                            .getCount(); j++) {
                                        itensParaCesta.moveToPosition(j);
                                        nItem = itensParaCesta.getString(2);
                                        qteItem = itensParaCesta.getString(3);
                                        valItem = itensParaCesta.getString(4);
                                        descricao = itensParaCesta.getString(5);
                                        dbListaCriada.insereItemLista(
                                                listaFeita, nItem, descricao,
                                                qteItem, valItem);
                                    }

                                    dbListaCriada.excluiItensCesta(listaFeita);
                                    buscaItens.notifyDataSetChanged();
                                    FazLista();
                                }
                            })
                    .setNegativeButton(getResources().getString(R.string.cancelar),
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface pDialogo,
                                                    int pInt) {
                                    pDialogo.dismiss();
                                }
                            }).show();

        }

    }

    @SuppressWarnings("deprecation")
    private void FazLista() {
        dbListaCriada.open();
        itensParaLista = dbListaCriada.buscaItensLista(listaFeita);
        int i = itensParaLista.getCount();
        dbListaCriada.close();
        if (i >= 0) {
            buscaItens = new SimpleCursorAdapter(this, R.id.lvListaCriada,
                    itensParaLista, new String[]{"item_lista",
                    "qte_item_lista", "preco_item_lista", "descr_item_lista"},
                    new int[]{R.id.tvItemEscolhido, R.id.tvUnidade,
                            R.id.tvValor, R.id.tvDescricaoItemEscolhido}) {

                // Metodo que monta a lista
                public View getView(int pItemLista, View vLista,
                                    ViewGroup vGLista) {

                    // MONTA A LINHA DA LISTA
                    viewLista = vLista;
                    inflaterLista = getLayoutInflater();
                    viewLista = inflaterLista.inflate(R.layout.item_da_lista,
                            null);
                    nomeItem = ((TextView) viewLista
                            .findViewById(R.id.tvItemEscolhido));
                    descricaoItem = ((TextView) viewLista
                            .findViewById(R.id.tvDescricaoItemEscolhido));
                    unidade = ((TextView) viewLista
                            .findViewById(R.id.tvUnidade));
                    preco = ((TextView) viewLista.findViewById(R.id.tvValor));
                    ibExclui = ((ImageButton) viewLista
                            .findViewById(R.id.ibExcluiItemEscolhido));
                    ibExclui.setFocusable(false);
                    // BUSCA VALORES PARA COLOCAR NA LINHA
                    dbListaCriada.open();
                    itensParaLista.moveToPosition(pItemLista);
                    itemDaLista = itensParaLista.getString(2);
                    quantidadeItem = itensParaLista.getString(3);
                    valorItem = itensParaLista.getString(4);
                    descricao = itensParaLista.getString(5);

                    // COLOCA OS VALORES NA LINHA
                    nomeItem.setText(itemDaLista);
                    if (descricao != null)
                        descricaoItem.setText(descricao);
                    if (descricaoItem.getText().toString().equals(""))
                        descricaoItem.setText(R.string.descricao_item);
                    ibExclui.setTag(Long.valueOf(itensParaLista.getLong(0)));
                    unidade.setText(quantidadeItem);
                    preco.setText(valorItem);
                    dbListaCriada.close();

                    // METODO AO CLICAR NO ICONE DE EXCLUIR ITEM
                    ibExclui.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View vItem) {
                            ibExclui = ((ImageButton) vItem);
                            idItem = ((Long) ibExclui.getTag()).longValue();
                            dbListaCriada.open();
                            String str = dbListaCriada.mostraItemLista(idItem, "vazia");

                            if (dbListaCriada.excluiItemLista(idItem) == true)
                                Toast.makeText(
                                        getApplicationContext(),
                                        String.format(
                                                getResources()
                                                        .getString(
                                                                R.string.dica_item_removido),
                                                str), Toast.LENGTH_SHORT)
                                        .show();

                            dbListaCriada.close();
                            buscaItens.notifyDataSetChanged();
                            FazLista();
                            usarActionBar();
                        }
                    });

                    return viewLista;
                }

                // Metodo que conta a quantidade de itens na lista
                public int getCount() {
                    dbListaCriada.open();
                    int i = dbListaCriada.contaItensLista(listaFeita);
                    dbListaCriada.close();
                    return i;
                }

                // Metodo que entrega um numero para cada item da lista
                public long getItemId(int pItemLista) {
                    return pItemLista;
                }

            };

            int posicao = listaCriada.getFirstVisiblePosition();
            listaCriada.setAdapter(buscaItens);
            listaCriada.setEmptyView(semItem);
            listaCriada.setSelection(posicao);
        }
    }

    public void onClick(View paramView) {
        switch (paramView.getId()) {
            case R.id.ibAdicionaItens:
                nomeNovoItem.setTag(nomeNovoItem.getText().toString());
                outroItemNovo = nomeNovoItem.getTag().toString();
                nomeNovoItem.setText("");
                if (!outroItemNovo.equals("")) {
                    dbListaCriada.open();
                    int qt = dbListaCriada.contaNomeItensListas(listaFeita,
                            outroItemNovo);
                    int qt0 = dbListaCriada.contaNomeItensCestas(listaFeita,
                            outroItemNovo);
                    if (qt == 0 && qt0 == 0) {
                        if (dbListaCriada.insereItemLista(listaFeita,
                                outroItemNovo, null,
                                getResources().getString(R.string.dica_unid),
                                getResources().getString(R.string.dica_zero)) >= 0)
                            Toast.makeText(
                                    getApplicationContext(),
                                    String.format(
                                            getResources().getString(
                                                    R.string.dica_item_novo),
                                            outroItemNovo), Toast.LENGTH_SHORT)
                                    .show();
                    } else {
                        Toast.makeText(
                                getApplicationContext(),
                                String.format(
                                        getResources().getString(
                                                R.string.dica_item_existente),
                                        outroItemNovo), Toast.LENGTH_SHORT).show();
                    }
                    dbListaCriada.close();
                }
                usarActionBar();
                break;

        }
        buscaItens.notifyDataSetChanged();
        FazLista();
    }

    @SuppressLint("NewApi")
    private void usarActionBar() {

        ActionBar toolbar = getSupportActionBar();

        toolbar.setTitle(listaFeita);
        dbListaCriada.open();
        int i = dbListaCriada.contaItensLista(listaFeita);
        dbListaCriada.close();
        String qtItens = i + "";
        if (i > 1) {
            if (qtItens.length() == 1) {
                qtItens = "0" + qtItens + " itens";
            } else {
                qtItens = qtItens + " itens";
            }
        } else if (i == 1) {
            qtItens = qtItens + " item";
        } else {
            qtItens = qtItens + " itens";
        }
        toolbar.setSubtitle(qtItens);
        toolbar.setDisplayHomeAsUpEnabled(true);
        toolbar.setHomeButtonEnabled(true);
        //nomeLista.setText(listaFeita);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu pMenu) {
        super.onCreateOptionsMenu(pMenu);
        pMenu.clear();
        getMenuInflater().inflate(R.menu.menu_cria_lista, pMenu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem pMenuItem) {
        switch (pMenuItem.getItemId()) {

            case android.R.id.home:
                dbListaCriada.close();
                finish();
                break;
            case R.id.ajustes:
                startActivity(new Intent("com.msk.superlista.AJUSTES"));
                break;
            case R.id.sobre:
                startActivity(new Intent("com.msk.superlista.SOBRE"));
                break;
            case R.id.botao_busca_item:
                String str = listaFeita;
                Bundle carta = new Bundle();
                carta.putString("lista", str);
                Intent envelope = new Intent("com.msk.superlista.COLOCAITENS");
                envelope.putExtras(carta);
                startActivity(envelope);
                break;
            case R.id.botao_envia_lista:
                dbListaCriada.open();
                conteudoLista = dbListaCriada.pegaItensLista(listaFeita);
                dbListaCriada.close();
                Intent correio = new Intent(Intent.ACTION_SEND);
                correio.putExtra(Intent.EXTRA_TEXT, conteudoLista);
                correio.setType("text/plain");
                startActivity(Intent.createChooser(correio, String
                        .format(getResources().getString(R.string.compartilhar),
                                listaFeita)));
                finish();
                break;
            case R.id.botao_lembrete_lista:
                dbListaCriada.open();
                conteudoLista = dbListaCriada.pegaItensLista(listaFeita);
                dbListaCriada.close();
                Intent evento = new Intent(Intent.ACTION_EDIT);
                evento.setType("vnd.android.cursor.item/event");
                evento.putExtra(Events.TITLE, listaFeita);
                evento.putExtra(Events.DESCRIPTION, conteudoLista);

                evento.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME,
                        c.getTimeInMillis());
                evento.putExtra(CalendarContract.EXTRA_EVENT_END_TIME,
                        c.getTimeInMillis());

                evento.putExtra(Events.ACCESS_LEVEL, Events.ACCESS_PRIVATE);
                evento.putExtra(Events.AVAILABILITY, Events.AVAILABILITY_BUSY);
                startActivity(evento);
                break;
        }
        return false;

    }

    @Override
    protected void onDestroy() {

        dbListaCriada.open();
        int lista = dbListaCriada.contaItensLista(listaFeita);
        int cesta = dbListaCriada.contaItensCesta(listaFeita);

        if (lista <= 0 && cesta <= 0) {
            dbListaCriada.excluiLista(listaFeita);
        }

        dbListaCriada.close();
        setResult(RESULT_OK, null);
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        dbListaCriada.open();
        FazLista();
        usarActionBar();
        super.onResume();

    }

    @Override
    protected void onPause() {
        dbListaCriada.close();
        super.onPause();
    }

}
