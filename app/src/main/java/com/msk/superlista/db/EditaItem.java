package com.msk.superlista.db;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.provider.CalendarContract.Events;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatAutoCompleteTextView;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.Toolbar;
import android.text.format.DateFormat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.msk.superlista.R;

import java.util.Calendar;
import java.util.Locale;

@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
@SuppressLint("InlinedApi")
public class EditaItem extends AppCompatActivity implements OnItemSelectedListener,
        OnClickListener {

    final static Calendar c = Calendar.getInstance();
    private static int mDia, mMes, mAno, mHora, mMinuto;
    private static AppCompatButton data, horario;
    // CLASSE COM BANCO DE DADOS
    DBListas dbListaCriada = new DBListas(this);
    DialogFragment dialogo;
    // ELEMENTOS DA TELA
    private AppCompatSpinner unidades;
    private AppCompatEditText descricao, valor, quantidade;
    private AppCompatAutoCompleteTextView nome;
    private AppCompatCheckBox calendario;
    private ImageView idata, ihora;
    // VARIAVEIS UTILIZADAS
    private ArrayAdapter<String> adapItem;
    private long idItem;
    private double valorItem, quantidadeItem;
    private String nomeItem, listaItem, descricaoItem, unidadeItem, tipo;

    @Override
    protected void onCreate(Bundle paramBundle) {
        super.onCreate(paramBundle);
        setContentView(R.layout.edita_item);

        Bundle localBundle = getIntent().getExtras();
        tipo = localBundle.getString("tipo");
        idItem = localBundle.getLong("id");
        dbListaCriada.open();
        iniciar();
        usarActionBar();
        mostradados();
        DataDeHoje();
        unidades.setOnItemSelectedListener(this);
        calendario.setOnClickListener(this);
        data.setOnClickListener(this);
        horario.setOnClickListener(this);
    }

    private void iniciar() {
        nome = (AppCompatAutoCompleteTextView) findViewById(R.id.etNomeItem);
        descricao = (AppCompatEditText) findViewById(R.id.etDescricaoItem);
        quantidade = (AppCompatEditText) findViewById(R.id.etQuantidadeItem);
        unidades = (AppCompatSpinner) findViewById(R.id.spUnidadeItem);
        valor = (AppCompatEditText) findViewById(R.id.etValorItem);
        data = (AppCompatButton) findViewById(R.id.tvDataAlarme);
        horario = (AppCompatButton) findViewById(R.id.tvHorarioAlarme);
        calendario = (AppCompatCheckBox) findViewById(R.id.cbCalendario);
        data.setVisibility(View.INVISIBLE);
        horario.setVisibility(View.INVISIBLE);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            idata = (ImageView) findViewById(R.id.ivDataAlarme);
            ihora = (ImageView) findViewById(R.id.ivHorarioAlarme);
            idata.setVisibility(View.INVISIBLE);
            ihora.setVisibility(View.INVISIBLE);
        }

        adapItem = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, getResources()
                .getStringArray(R.array.ListaComTudo));
        nome.setAdapter(adapItem);
    }

    private void mostradados() {

        // BUSCA DADOS
        Cursor buscador = dbListaCriada.buscaItem(idItem, tipo);
        buscador.moveToFirst();
        listaItem = buscador.getString(1);
        nomeItem = buscador.getString(2);
        descricaoItem = buscador.getString(3);
        quantidadeItem = buscador.getDouble(4);
        unidadeItem = buscador.getString(5);
        valorItem = buscador.getDouble(6);

        // COLOCA NOME ITEM NA TELA
        nome.setText(nomeItem);

        // COLOCA DESCRICAO DO ITEM NA TELA
        descricao.setText(descricaoItem);

        // COLOCA QUANTIDADE NA TELA
        if (quantidadeItem != 0.0D)
            quantidade.setText(quantidadeItem + "");
        else
            quantidade.setText("");

        // DEFINE UNIDADE UTILIZADA
        if (unidadeItem.equals("pc")) {
            unidades.setSelection(6);
        } else if (unidadeItem.equals("caixa")) {
            unidades.setSelection(1);
        } else if (unidadeItem.equals("kg")) {
            unidades.setSelection(2);
        } else if (unidadeItem.equals("litro")) {
            unidades.setSelection(3);
        } else if (unidadeItem.equals("g")) {
            unidades.setSelection(4);
        } else if (unidadeItem.equals("ml")) {
            unidades.setSelection(5);
        } else {
            unidades.setSelection(0);
        }

        // COLOCA VALOR NA TELA
        if (valorItem != 0.0D)
            valor.setText(valorItem + "");
        else
            valor.setText("");
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
                String.format(Locale.US, "%d", mAno)));
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
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                        idata.setVisibility(View.VISIBLE);
                        ihora.setVisibility(View.VISIBLE);
                    }
                } else {
                    data.setVisibility(View.INVISIBLE);
                    horario.setVisibility(View.INVISIBLE);
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                        idata.setVisibility(View.INVISIBLE);
                        ihora.setVisibility(View.INVISIBLE);
                    }
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
        Toolbar toolbar = (Toolbar) findViewById(R.id.actionbar_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_cancel_white);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_edita_item, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.menu_edita:
                // QUANTIDADE DO ITEM
                if (quantidade.getText().toString().equals(""))
                    quantidadeItem = 1.0D;
                else
                    quantidadeItem = Double.parseDouble(quantidade.getText().toString());
                // PRECO DO ITEM
                if (valor.getText().toString().equals(""))
                    valorItem = 0.0D;
                else
                    valorItem = Double.parseDouble(valor.getText().toString());
                // MODIFICA DADOS ITEM
                dbListaCriada.mudaItem(idItem, tipo, listaItem, nome.getText().toString(),
                        descricao.getText().toString(), quantidadeItem, unidadeItem, valorItem);

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
                setResult(RESULT_OK, null);
                Toast.makeText(
                        getApplicationContext(),
                        String.format(
                                getResources().getString(
                                        R.string.dica_item_modificado),
                                nomeItem), Toast.LENGTH_SHORT).show();
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
                    m, String.format(Locale.US, "%d", mAno)));
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
