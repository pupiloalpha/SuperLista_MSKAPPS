package com.msk.superlista;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.backup.BackupManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.LightingColorFilter;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.CalendarContract;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatImageButton;
import android.text.InputType;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.msk.superlista.db.DBListas;

import java.text.NumberFormat;
import java.util.Calendar;
import java.util.Locale;

public class SuperLista extends AppCompatActivity {

    private Calendar c = Calendar.getInstance();
    // ActionBar actionBar;
    private Intent correio;
    private SimpleCursorAdapter buscaListas;
    private DBListas dbListasCriadas = new DBListas(this);
    private ImageView ivMenu;
    private LayoutInflater inflaterLista;
    private String listaFeita, descricao, unidade, qtItens, nItem, listaCopia, pastaBackUp;
    private double qteItem, valItem, valor;
    private Cursor listas = null;
    private Cursor itens = null;
    private ListView listasFeitas;
    private TextView nomeLista, qtItensLista, valorLista, listaVazia;
    private View viewLista;

    @Override
    protected void onCreate(Bundle paramBundle) {
        super.onCreate(paramBundle);

        setContentView(R.layout.tela_inicial);

        listasFeitas = ((ListView) findViewById(R.id.lvListas));
        listaVazia = (TextView) findViewById(R.id.tvListaVazia);
        AppCompatImageButton fab = (AppCompatImageButton) findViewById(R.id.ibfab);
        dbListasCriadas.open();
        dbListasCriadas.ModificadaDados();

        FazLista();
        usarActionBar();

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialogo();
            }
        });
    }

    private void Dialogo() {

        final AppCompatEditText localEditText = new AppCompatEditText(SuperLista.this);
        // localEditText.setText(listaFeita);
        localEditText.setInputType(InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
        new AlertDialog.Builder(this)
                .setTitle(R.string.cria_lista)
                .setMessage(R.string.texto_edita_nome)
                .setView(localEditText)
                .setPositiveButton(R.string.ok,
                        new DialogInterface.OnClickListener() {
                            public void onClick(
                                    DialogInterface pDialogInterface,
                                    int pInt) {
                                String str = localEditText.getText()
                                        .toString();

                                if (str.equals(""))
                                    str = "Sem Nome";

                                String nomeLista1 = str;
                                String nomeLista2 = str;
                                dbListasCriadas.open();
                                int a = dbListasCriadas.contaNomeListas(nomeLista1);
                                int b = 1;

                                if (a != 0) {
                                    while (a != 0) {
                                        nomeLista2 = nomeLista1 + b;
                                        a = dbListasCriadas.contaNomeListas(nomeLista2);
                                        b = b + 1;
                                    }
                                    str = nomeLista2;
                                }

                                dbListasCriadas.criaLista(str);
                                dbListasCriadas.close();
                                Bundle localBundle = new Bundle();
                                localBundle.putString("lista", str);
                                Intent localIntent = new Intent("com.msk.superlista.CRIALISTA");
                                localIntent.putExtras(localBundle);
                                startActivity(localIntent);
                            }
                        }).setNegativeButton(R.string.cancelar,
                new DialogInterface.OnClickListener() {
                    public void onClick(
                            DialogInterface pDialogo,
                            int pInt) {
                        pDialogo.dismiss();
                    }
                }).show();
    }

    @SuppressWarnings("deprecation")
    private void FazLista() {
        dbListasCriadas.open();
        listas = dbListasCriadas.buscaListas();
        int i = listas.getCount();
        dbListasCriadas.close();
        if (i >= 0) {
            buscaListas = new SimpleCursorAdapter(this, R.id.lvListas, listas,
                    new String[]{"lista"}, new int[]{R.id.tvcategoria}) {
                public int getCount() {
                    dbListasCriadas.open();
                    int i = dbListasCriadas.contaListas();
                    dbListasCriadas.close();
                    return i;
                }

                public long getItemId(int pInt) {
                    return pInt;
                }

                public View getView(int pInt, View paramAnonymousView,
                                    ViewGroup paramAnonymousViewGroup) {
                    viewLista = paramAnonymousView;
                    inflaterLista = getLayoutInflater();
                    viewLista = inflaterLista.inflate(R.layout.lista_para_uso,
                            null);
                    nomeLista = ((TextView) viewLista
                            .findViewById(R.id.tvNomeLista));
                    qtItensLista = ((TextView) viewLista
                            .findViewById(R.id.tvItensLista));
                    valorLista = ((TextView) viewLista
                            .findViewById(R.id.tvValorLista));
                    ivMenu = ((ImageView) viewLista
                            .findViewById(R.id.ivMenuLista));
                    ivMenu.setFocusable(false);
                    dbListasCriadas.open();
                    listas.moveToPosition(pInt);
                    String listaFeita = listas.getString(1);
                    nomeLista.setText(listaFeita);
                    int qt = dbListasCriadas.contaItensLista(listaFeita);
                    if (qt > 1) {
                        qtItens = qt + "";
                        if (qtItens.length() == 1) {
                            qtItensLista.setText("0" + qtItens + " itens");
                        } else {
                            qtItensLista.setText(qtItens + " itens");
                        }
                    } else if (qt == 1) {
                        qtItensLista.setText(qtItens + " item");
                    } else {
                        qtItensLista.setText(qtItens + " itens");
                    }
                    // ADICIONAR STRINGS PARA QUANTIDADE DE ITENS DA LISTA

                    valor = dbListasCriadas.mostraValor(listaFeita, "cesta") + dbListasCriadas.mostraValor(listaFeita, "lista");
                    Locale current = getResources().getConfiguration().locale;
                    NumberFormat dinheiro = NumberFormat.getCurrencyInstance(current);

                    valorLista.setText(dinheiro.format(valor));
                    if (valor == 0.0D) {
                        valorLista.setVisibility(View.GONE);
                    }

                    viewLista.setTag(listaFeita);
                    ivMenu.setTag(listaFeita);
                    dbListasCriadas.close();

                    ivMenu.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View pView) {
                            ivMenu = ((ImageView) pView);
                            ivMenu.getDrawable().setColorFilter(Color.rgb(51, 181, 229), PorterDuff.Mode.MULTIPLY);
                            openContextMenu(pView);
                        }
                    });

                    registerForContextMenu(ivMenu);

                    viewLista.setOnClickListener(new View.OnClickListener() {

                        public void onClick(View v) {
                            // METODO QUANDO CLICAR NO NOME DA LISTA

                            Bundle envelope = new Bundle();
                            envelope.putString("lista", v.getTag().toString());
                            correio = new Intent("com.msk.superlista.USALISTA");
                            correio.putExtras(envelope);
                            startActivity(correio);

                        }
                    });

                    return viewLista;
                }
            };
            int posicao = listasFeitas.getFirstVisiblePosition();
            listasFeitas.setAdapter(buscaListas);
            listasFeitas.setEmptyView(listaVazia);
            listasFeitas.setSelection(posicao);
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {

        super.onCreateContextMenu(menu, v, menuInfo);

        listaFeita = v.getTag().toString();
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_altera_lista, menu);

    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        dbListasCriadas.open();
        String conteudoLista = dbListasCriadas.pegaItensLista(listaFeita);
        dbListasCriadas.close();

        switch (item.getItemId()) {
            case R.id.botao_edita_lista:
                // Editar Lista
                Bundle envelope = new Bundle();
                envelope.putString("lista", listaFeita);
                envelope.putString("tipo", "edita");
                correio = new Intent("com.msk.superlista.CRIALISTA");
                correio.putExtras(envelope);
                startActivity(correio);

                break;
            case R.id.botao_exclui_lista:
                dbListasCriadas.open();
                dbListasCriadas.excluiLista(listaFeita);
                dbListasCriadas.excluiItensLista(listaFeita);
                dbListasCriadas.close();
                Toast.makeText(
                        getApplicationContext(),
                        String.format(
                                getResources()
                                        .getString(
                                                R.string.dica_lista_removida),
                                listaFeita), Toast.LENGTH_SHORT).show();
                FazLista();
                break;
            case R.id.botao_envia_lista:

                correio = new Intent(Intent.ACTION_SEND);
                correio.putExtra(Intent.EXTRA_TEXT, conteudoLista);
                correio.setType("text/plain");
                startActivity(Intent.createChooser(correio, String
                        .format(getResources().getString(R.string.compartilhar),
                                listaFeita)));

                break;
            case R.id.botao_copia_lista:
                Dialogo(listaFeita);

                break;
            case R.id.botao_lembrete_lista:

                Intent evento = new Intent(Intent.ACTION_EDIT);
                evento.setType("vnd.android.cursor.item/event");
                evento.putExtra(CalendarContract.Events.TITLE, listaFeita);
                evento.putExtra(CalendarContract.Events.DESCRIPTION, conteudoLista);

                evento.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME,
                        c.getTimeInMillis());
                evento.putExtra(CalendarContract.EXTRA_EVENT_END_TIME,
                        c.getTimeInMillis());

                evento.putExtra(CalendarContract.Events.ACCESS_LEVEL, CalendarContract.Events.ACCESS_PRIVATE);
                evento.putExtra(CalendarContract.Events.AVAILABILITY, CalendarContract.Events.AVAILABILITY_BUSY);
                startActivity(evento);

                break;
            case R.id.botao_renomeia_lista:
                final AppCompatEditText localAppCompatEditText = new AppCompatEditText(this);
                localAppCompatEditText.setText(listaFeita);
                new AlertDialog.Builder(this)
                        .setTitle(R.string.titulo_edita_nome)
                        .setMessage(R.string.texto_edita_nome)
                        .setView(localAppCompatEditText)
                        .setPositiveButton(R.string.ok,
                                new DialogInterface.OnClickListener() {
                                    public void onClick(
                                            DialogInterface pDialogInterface,
                                            int pInt) {
                                        String str = localAppCompatEditText.getText()
                                                .toString();

                                        if (!listaFeita.equals(str)) {

                                            String nomeLista1 = str;
                                            String nomeLista2 = str;
                                            dbListasCriadas.open();
                                            int a = dbListasCriadas
                                                    .contaNomeListas(nomeLista1);
                                            int b = 1;

                                            if (a != 0) {
                                                while (a != 0) {
                                                    nomeLista2 = nomeLista1 + b;
                                                    a = dbListasCriadas
                                                            .contaNomeListas(nomeLista2);
                                                    b = b + 1;
                                                }
                                                str = nomeLista2;
                                            }
                                            nomeLista.setText(str);
                                            dbListasCriadas.mudaNomeLista(listaFeita,
                                                    str);
                                            dbListasCriadas.mudaNomeListaItens(
                                                    listaFeita, str);
                                            dbListasCriadas.close();
                                            listaFeita = str;
                                            FazLista();
                                        }
                                    }
                                }).setNegativeButton(R.string.cancelar,
                        new DialogInterface.OnClickListener() {
                            public void onClick(
                                    DialogInterface pDialogo,
                                    int pInt) {
                                pDialogo.dismiss();
                            }
                        }).show();

                break;
        }

        return super.onContextItemSelected(item);
    }


    @Override
    public void onContextMenuClosed(Menu menu) {

        ivMenu.getDrawable().setColorFilter(new LightingColorFilter(Color.DKGRAY, Color.DKGRAY));

        super.onContextMenuClosed(menu);
    }


    private void Dialogo(String lista_copia) {

        listaFeita = lista_copia;
        final AppCompatEditText localET = new AppCompatEditText(this);
        localET.setInputType(InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
        localET.setText(listaFeita);
        new AlertDialog.Builder(this)
                .setTitle(R.string.titulo_edita_nome)
                .setMessage(R.string.texto_edita_nome)
                .setView(localET)
                .setPositiveButton(R.string.ok,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface pDialogo,
                                                int pInt) {
                                dbListasCriadas.open();
                                itens = dbListasCriadas
                                        .buscaItensLista(listaFeita);
                                listaCopia = localET.getText().toString();

                                if (listaFeita.equals(listaCopia)) {

                                    String nomeLista1 = listaCopia;
                                    String nomeLista2 = listaCopia + 1;
                                    int a = dbListasCriadas
                                            .contaNomeListas(nomeLista2);
                                    int b = 1;

                                    listaCopia = nomeLista2;

                                    if (a != 0) {
                                        while (a != 0) {
                                            a = dbListasCriadas
                                                    .contaNomeListas(nomeLista2);
                                            b = b + 1;
                                            nomeLista2 = nomeLista1 + b;
                                            listaCopia = nomeLista2;
                                        }
                                    }

                                }

                                dbListasCriadas.criaLista(listaCopia);

                                for (int j = 0; j < itens.getCount(); j++) {
                                    itens.moveToPosition(j);
                                    nItem = itens.getString(2);
                                    descricao = itens.getString(3);
                                    qteItem = itens.getDouble(4);
                                    unidade = itens.getString(5);
                                    valItem = itens.getDouble(6);

                                    dbListasCriadas.insereItemLista(listaCopia,
                                            nItem, descricao, qteItem, unidade, valItem);
                                }

                                dbListasCriadas.close();
                                buscaListas.notifyDataSetChanged();

                                FazLista();
                            }
                        })
                .setNegativeButton("Cancelar",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface pDialogo,
                                                int pInt) {
                                pDialogo.dismiss();
                                FazLista();
                            }
                        }).show();
    }

    @SuppressLint("NewApi")
    private void usarActionBar() {

        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setHomeButtonEnabled(false);

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
    protected void onResume() {
        super.onResume();
        FazLista();
        usarActionBar();
    }

    @Override
    protected void onDestroy() {

        SharedPreferences buscaPreferencias = PreferenceManager.getDefaultSharedPreferences(this);
        boolean autobkup = buscaPreferencias.getBoolean("autobackup", true);
        SharedPreferences sharedPref = getSharedPreferences("backup", Context.MODE_PRIVATE);
        String pastaBackUp = sharedPref.getString("backup", "");

        dbListasCriadas.open();

        int i = dbListasCriadas.contaListas();

        if (autobkup && i != 0) {

            dbListasCriadas.copiaBD(pastaBackUp);
            BackupManager android = new BackupManager(getApplicationContext());
            android.dataChanged();

        }

        dbListasCriadas.close();
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            FazLista();
        }
    }

}