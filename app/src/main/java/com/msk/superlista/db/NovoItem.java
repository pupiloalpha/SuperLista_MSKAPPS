package com.msk.superlista.db;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.msk.superlista.R;

public class NovoItem extends AppCompatActivity implements OnItemSelectedListener,
        OnClickListener {

    // CLASSE COM BANCO DE DADOS
    DBListas dbListaCriada = new DBListas(this);
    // ELEMENTOS DA TELA
    private Button novoItem, cancela;
    private Spinner unidades;
    private AutoCompleteTextView nomeNovoItem;
    private EditText valor, quantidade, descricao;
    private ArrayAdapter<String> adapItem;
    // VARIAVEIS UTILIZADAS
    private double preco;
    private String nomeItem, nomeLista, descricaoItem, quantidadeItem, unidadeItem, valorItem,
            tipoCesta;

    @Override
    protected void onCreate(Bundle paramBundle) {
        super.onCreate(paramBundle);
        setContentView(R.layout.novo_item);
        Bundle localBundle = getIntent().getExtras();
        nomeLista = localBundle.getString("lista");
        tipoCesta = localBundle.getString("cesta");
        dbListaCriada.open();
        iniciar();
        unidades.setOnItemSelectedListener(this);
        novoItem.setOnClickListener(this);
        cancela.setOnClickListener(this);
    }

    private void iniciar() {
        descricao = ((EditText) findViewById(R.id.etDescricaoItem));
        quantidade = ((EditText) findViewById(R.id.etQuantidadeItem));
        nomeNovoItem = ((AutoCompleteTextView) findViewById(R.id.etNomeItem));
        unidades = ((Spinner) findViewById(R.id.spUnidadeItem));
        valor = ((EditText) findViewById(R.id.etValorItem));
        novoItem = ((Button) findViewById(R.id.ivNovoItem));
        cancela = ((Button) findViewById(R.id.ivCancela));
        adapItem = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, getResources()
                .getStringArray(R.array.ListaComTudo));
        nomeNovoItem.setAdapter(adapItem);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
    }

    public void onClick(View botao) {

        switch (botao.getId()) {

            case R.id.ivNovoItem:

                nomeNovoItem.setTag(nomeNovoItem.getText().toString());
                nomeItem = nomeNovoItem.getTag().toString();
                nomeNovoItem.setText("");

                descricaoItem = descricao.getText().toString();
                descricao.setText("");

                quantidadeItem = quantidade.getText().toString();
                // QUANTIDADE DO ITEM
                if (quantidadeItem.equals(""))
                    quantidadeItem = "01";
                if (quantidadeItem.length() == 1)
                    quantidadeItem = "0" + quantidadeItem;

                String str2 = quantidadeItem + " " + unidadeItem;

                valorItem = valor.getText().toString();
                // PRECO DO ITEM
                if (valorItem.equals(""))
                    preco = 0.0D;
                else
                    preco = Double.parseDouble(valorItem);

                String str3 = "R$ " + String.format("%.2f", preco);

                if (!nomeItem.equals("")) {
                    // dbListaCriada.open();

                    int qt = dbListaCriada
                            .contaNomeItensListas(nomeLista, nomeItem);
                    int qt1 = dbListaCriada.contaNomeItensCestas(nomeLista,
                            nomeItem);
                    if (qt == 0 && qt1 == 0) {

                        if (tipoCesta.equals("vazia")) {
                            dbListaCriada.insereItemLista(nomeLista, nomeItem,
                                    descricaoItem, str2, str3);
                        } else {
                            dbListaCriada.insereItemCesta(nomeLista, nomeItem,
                                    descricaoItem, str2, str3);
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
                    // dbListaCriada.close();
                }
                setResult(RESULT_OK, null);
                finish();
                break;
            case R.id.ivCancela:
                dbListaCriada.close();
                setResult(RESULT_OK, null);
                finish();
                break;
        }
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
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case android.R.id.home:
                dbListaCriada.close();
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        dbListaCriada.close();
        setResult(RESULT_OK, null);
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
