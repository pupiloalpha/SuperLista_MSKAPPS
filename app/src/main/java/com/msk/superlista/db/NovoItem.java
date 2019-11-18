package com.msk.superlista.db;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.msk.superlista.R;

public class NovoItem extends AppCompatActivity implements OnItemSelectedListener {

    // CLASSE COM BANCO DE DADOS
    private DBListas dbListaCriada = new DBListas(this);
    // ELEMENTOS DA TELA
    private Spinner unidades;
    private AutoCompleteTextView nomeNovoItem;
    private EditText valor, quantidade, descricao;
    private ArrayAdapter<String> adapItem;
    // VARIAVEIS UTILIZADAS
    private double preco, quantidadeItem;
    private String nomeLista, unidadeItem, tipoCesta;

    @Override
    protected void onCreate(Bundle paramBundle) {
        super.onCreate(paramBundle);
        setContentView(R.layout.novo_item);
        Bundle localBundle = getIntent().getExtras();
        nomeLista = localBundle.getString("lista");
        tipoCesta = localBundle.getString("tipo");
        dbListaCriada.open();
        iniciar();

        Toolbar toolbar = (Toolbar) findViewById(R.id.actionbar_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_cancel_white);

        unidades.setOnItemSelectedListener(this);
    }

    private void iniciar() {
        descricao = ((EditText) findViewById(R.id.etDescricaoItem));
        quantidade = ((EditText) findViewById(R.id.etQuantidadeItem));
        nomeNovoItem = ((AutoCompleteTextView) findViewById(R.id.etNomeItem));
        unidades = ((Spinner) findViewById(R.id.spUnidadeItem));
        valor = ((EditText) findViewById(R.id.etValorItem));
        adapItem = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, getResources()
                .getStringArray(R.array.ListaComTudo));
        nomeNovoItem.setAdapter(adapItem);
    }

    public void onItemSelected(AdapterView<?> adapter, View pView,
                               int paramInt, long paramLong) {

        switch (paramInt) {
            case 0:
                unidadeItem = "unid";
                break;
            case 1:
                unidadeItem = "caixa";
                break;
            case 2:
                unidadeItem = "kg";
                break;
            case 3:
                unidadeItem = "litro";
                break;
            case 4:
                unidadeItem = "g";
                break;
            case 5:
                unidadeItem = "ml";
                break;
            case 6:
                unidadeItem = "pc";
                break;
        }

    }

    public void onNothingSelected(AdapterView<?> paramAdapterView) {
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_cria_item, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.menu_cria:
                String nomeItem = nomeNovoItem.getText().toString();
                String descricaoItem = descricao.getText().toString();

                // QUANTIDADE DO ITEM
                if (quantidade.getText().toString().equals(""))
                    quantidadeItem = 1.0D;
                else
                    quantidadeItem = Double.parseDouble(quantidade.getText().toString());

                // VALOR DO ITEM
                if (valor.getText().toString().equals(""))
                    preco = 0.0D;
                else
                    preco = Double.parseDouble(valor.getText().toString());

                if (!nomeItem.equals("")) {

                    int qt = dbListaCriada
                            .contaNomeItensListas(nomeLista, nomeItem);
                    int qt1 = dbListaCriada.contaNomeItensCestas(nomeLista,
                            nomeItem);
                    if (qt == 0 && qt1 == 0) {

                        if (tipoCesta.equals("lista")) {
                            dbListaCriada.insereItemLista(nomeLista, nomeItem,
                                    descricaoItem, quantidadeItem, unidadeItem, preco);
                        } else {
                            dbListaCriada.insereItemCesta(nomeLista, nomeItem,
                                    descricaoItem, quantidadeItem, unidadeItem, preco);
                        }
                        Toast.makeText(
                                getApplicationContext(),
                                String.format(
                                        getResources().getString(
                                                R.string.dica_item_novo), nomeItem),
                                Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(
                                getApplicationContext(),
                                String.format(
                                        getResources().getString(
                                                R.string.dica_item_existente),
                                        nomeItem), Toast.LENGTH_SHORT).show();
                    }

                }
                setResult(RESULT_OK, null);
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        dbListaCriada.close();
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        dbListaCriada.close();
        super.onPause();
    }

    @Override
    protected void onResume() {
        dbListaCriada.open();
        super.onResume();
    }
}