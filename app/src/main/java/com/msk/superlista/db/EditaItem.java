package com.msk.superlista.db;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.provider.CalendarContract.Events;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatAutoCompleteTextView;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatSpinner;
import android.text.format.DateFormat;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TimePicker;
import android.widget.Toast;

import com.msk.superlista.R;

import java.util.Calendar;

@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
@SuppressLint("InlinedApi")
public class EditaItem extends AppCompatActivity implements OnItemSelectedListener,
        OnClickListener {

    final static Calendar c = Calendar.getInstance();
    private static Button data, horario;
    private static int mDia, mMes, mAno, mHora, mMinuto;
    // CLASSE COM BANCO DE DADOS
    DBListas dbListaCriada = new DBListas(this);
    DialogFragment dialogo;
    // ELEMENTOS DA TELA
    private Button mudaItem, cancela;
    private AppCompatSpinner unidades;
    private AppCompatEditText descricao, valor, quantidade;
    private AppCompatAutoCompleteTextView nome;
    private AppCompatCheckBox calendario;
    // VARIAVEIS UTILIZADAS
    private ArrayAdapter<String> adapItem;
    private long idItem;
    private double preco;
    private String nomeItem, descricaoItem, quantidadeItem, unidadeItem, valorItem, cesta;

    @Override
    protected void onCreate(Bundle paramBundle) {
        super.onCreate(paramBundle);
        setContentView(R.layout.edita_item);

        Bundle localBundle = getIntent().getExtras();
        cesta = localBundle.getString("cesta");
        idItem = localBundle.getLong("id");
        dbListaCriada.open();
        iniciar();
        usarActionBar();
        mostradados();
        DataDeHoje();
        unidades.setOnItemSelectedListener(this);
        mudaItem.setOnClickListener(this);
        cancela.setOnClickListener(this);
        calendario.setOnClickListener(this);
        data.setOnClickListener(this);
        horario.setOnClickListener(this);
    }

    private void iniciar() {
        nome = ((AppCompatAutoCompleteTextView) findViewById(R.id.etNomeItem));
        descricao = ((AppCompatEditText) findViewById(R.id.etDescricaoItem));
        quantidade = ((AppCompatEditText) findViewById(R.id.etQuantidadeItem));
        unidades = ((AppCompatSpinner) findViewById(R.id.spUnidadeItem));
        valor = ((AppCompatEditText) findViewById(R.id.etValorItem));
        mudaItem = ((Button) findViewById(R.id.btModificaItemLista));
        cancela = ((Button) findViewById(R.id.ivCancela));
        data = (Button) findViewById(R.id.tvDataAlarme);
        horario = (Button) findViewById(R.id.tvHorarioAlarme);
        calendario = (AppCompatCheckBox) findViewById(R.id.cbCalendario);
        data.setVisibility(View.INVISIBLE);
        horario.setVisibility(View.INVISIBLE);
        adapItem = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, getResources()
                .getStringArray(R.array.ListaComTudo));
        nome.setAdapter(adapItem);
    }

    private void mostradados() {

        // BUSCA DADOS
        // dbListaCriada.open();
        quantidadeItem = dbListaCriada.mostraQuantidadeItemLista(idItem, cesta);
        valorItem = dbListaCriada.mostraValorItemLista(idItem, cesta);
        descricaoItem = dbListaCriada.mostraDescricaoItemLista(idItem, cesta);
        nomeItem = dbListaCriada.mostraItemLista(idItem, cesta);

        // dbListaCriada.close();

        // COLOCA NOME ITEM NA TELA
        nome.setText(nomeItem);

        // CONVERTE VALOR EM DECIMAL
        valorItem = valorItem.replace("R$ ", "");
        valorItem = valorItem.replace("$ ", "");
        valorItem = valorItem.replace(",", ".");

        // COLOCA VALOR NA TELA
        if (!valorItem.equals("0.00"))
            valor.setText(valorItem);
        else
            valor.setText("");

        // DEFINE UNIDADE UTILIZADA
        int i = quantidadeItem.length();
        String unid = quantidadeItem.substring(i);

        if (unid.equals("c")) {
            unidades.setSelection(6);
        } else if (unid.equals("a")) {
            unidades.setSelection(1);
        } else if (unid.equals("g")) {
            unidades.setSelection(2);
        } else if (unid.equals("o")) {
            unidades.setSelection(3);
        } else if (unid.equals("g")) {
            unidades.setSelection(4);
        } else if (unid.equals("l")) {
            unidades.setSelection(5);
        } else {
            unidades.setSelection(0);
        }

        // CONVERTE QUANTIDADE EM NUMERO
        quantidadeItem = quantidadeItem.replace(" unid", "");
        quantidadeItem = quantidadeItem.replace(" caixa", "");
        quantidadeItem = quantidadeItem.replace(" kg", "");
        quantidadeItem = quantidadeItem.replace(" litro", "");
        quantidadeItem = quantidadeItem.replace(" g", "");
        quantidadeItem = quantidadeItem.replace(" ml", "");
        quantidadeItem = quantidadeItem.replace(" pc", "");

        // COLOCA QUANTIDADE NA TELA
        if (!quantidadeItem.equals("01"))
            quantidade.setText(quantidadeItem);
        else
            quantidade.setText("");

        // COLOCA DESCRICAO DO ITEM NA TELA
        descricao.setText(descricaoItem);
    }

    private void DataDeHoje() {
        mAno = c.get(Calendar.YEAR);
        mMes = c.get(Calendar.MONTH);
        mDia = c.get(Calendar.DAY_OF_MONTH);
        String d = mDia + "";
        int mm = (mMes + 1);
        String me = mm + "";
        if (d.length() == 1)
            d = "0" + d;
        if (me.length() == 1)
            me = "0" + me;
        data.setText(getResources().getString(R.string.dica_dia_alarme, d, me,
                mAno));
        mHora = c.get(Calendar.HOUR_OF_DAY);
        mMinuto = c.get(Calendar.MINUTE);
        String h = mHora + "";
        String m = mMinuto + "";
        if (h.length() == 1)
            h = "0" + h;
        if (m.length() == 1)
            m = "0" + m;
        horario.setText(getResources().getString(R.string.dica_horario_alarme,
                h, m));
    }

    public void onClick(View botao) {

        switch (botao.getId()) {

            case R.id.cbCalendario:

                if (calendario.isChecked()) {
                    data.setVisibility(View.VISIBLE);
                    horario.setVisibility(View.VISIBLE);
                } else {
                    data.setVisibility(View.INVISIBLE);
                    horario.setVisibility(View.INVISIBLE);
                }

                break;
            case R.id.tvDataAlarme:

                dialogo = new AlteraData();
                dialogo.show(getFragmentManager(), "alteraData");

                break;
            case R.id.tvHorarioAlarme:

                dialogo = new AlteraHorario();
                dialogo.show(getFragmentManager(), "alteraHorario");

                break;
            case R.id.btModificaItemLista:

                //NOME DO ITEM

                if (!nome.getText().toString().equals(nomeItem))
                    dbListaCriada.mudaNomeItem(idItem, nome.getText()
                                    .toString(),
                            cesta);

                dbListaCriada.mudaDescricaoItem(idItem, descricao.getText()
                                .toString(),
                        cesta);

                quantidadeItem = quantidade.getText().toString();
                // QUANTIDADE DO ITEM
                if (quantidadeItem.equals(""))
                    quantidadeItem = "01";
                if (quantidadeItem.length() == 1)
                    quantidadeItem = "0" + quantidadeItem;

                String str2 = quantidadeItem + " " + unidadeItem;
                // dbListaCriada.open();
                dbListaCriada.mudaQuantidadeItem(idItem, str2, cesta);
                // dbListaCriada.close();

                valorItem = valor.getText().toString();
                // PRECO DO ITEM
                if (valorItem.equals(""))
                    preco = 0.0D;
                else
                    preco = Double.parseDouble(valorItem);

                String str3 = "R$ " + String.format("%.2f", preco);

                // dbListaCriada.open();
                if (dbListaCriada.mudaPrecoItem(idItem, str3, cesta) == true) {
                    Toast.makeText(
                            getApplicationContext(),
                            String.format(
                                    getResources().getString(
                                            R.string.dica_item_modificado),
                                    nomeItem), Toast.LENGTH_SHORT).show();
                }
                // dbListaCriada.close();

                if (calendario.isChecked()) {
                    c.set(mAno, mMes, mDia, mHora, mMinuto);

                    Intent intent = new Intent(Intent.ACTION_EDIT);
                    intent.setType("vnd.android.cursor.item/event");
                    intent.putExtra(Events.TITLE, nomeItem);
                    intent.putExtra(Events.DESCRIPTION,
                            "Evento criado no SuperLista");

                    intent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME,
                            c.getTimeInMillis());
                    intent.putExtra(CalendarContract.EXTRA_EVENT_END_TIME,
                            c.getTimeInMillis());

                    intent.putExtra(Events.ACCESS_LEVEL, Events.ACCESS_PRIVATE);
                    intent.putExtra(Events.AVAILABILITY, Events.AVAILABILITY_BUSY);
                    startActivity(intent);
                }

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

    @SuppressLint("NewApi")
    private void usarActionBar() {

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

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

    public static class AlteraData extends DialogFragment implements
            DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {

            return new DatePickerDialog(getActivity(), this, mAno, mMes, mDia);
        }

        public void onDateSet(DatePicker view, int ano, int mes, int dia) {
            mAno = ano;
            mMes = mes;
            mDia = dia;

            String d = mDia + "";
            int mm = (mMes + 1);
            String m = mm + "";
            if (d.length() == 1)
                d = "0" + d;
            if (m.length() == 1)
                m = "0" + m;
            data.setText(getResources().getString(R.string.dica_dia_alarme, d,
                    m, mAno));
        }
    }

    public static class AlteraHorario extends DialogFragment implements
            TimePickerDialog.OnTimeSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {

            return new TimePickerDialog(getActivity(), this, mHora, mMinuto,
                    DateFormat.is24HourFormat(getActivity()));
        }

        public void onTimeSet(TimePicker view, int hora, int minuto) {

            mHora = hora;
            mMinuto = minuto;

            String h = mHora + "";
            String m = mMinuto + "";
            if (h.length() == 1)
                h = "0" + h;
            if (m.length() == 1)
                m = "0" + m;
            horario.setText(getResources().getString(
                    R.string.dica_horario_alarme, h, m));
        }
    }

}
