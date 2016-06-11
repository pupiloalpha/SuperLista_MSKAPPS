package com.msk.superlista.info;

import android.app.backup.BackupManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceScreen;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.AppCompatCheckedTextView;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatRadioButton;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.msk.superlista.R;
import com.msk.superlista.db.DBListas;

public class Ajustes extends PreferenceActivity implements
        OnPreferenceClickListener {

    Toolbar toolbar;
    DBListas dbMinhasListas = new DBListas(this);
    private Preference backup, restaura, apagatudo, versao;
    private CheckBoxPreference cesta, autobkup;
    private PreferenceScreen prefs;
    private String chave, nrVersao, pastaBackUp;
    private PackageInfo info;

    @SuppressWarnings("deprecation")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.preferencias);

        prefs = getPreferenceScreen();

        backup = (Preference) prefs.findPreference("backup");
        restaura = (Preference) prefs.findPreference("restaura");
        apagatudo = (Preference) prefs.findPreference("apagatudo");
        versao = (Preference) prefs.findPreference("versao");
        cesta = (CheckBoxPreference) prefs.findPreference("cesta");
        autobkup = (CheckBoxPreference) prefs.findPreference("autobackup");

        try {
            info = getPackageManager().getPackageInfo(getPackageName(), 0);
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        nrVersao = info.versionName;

        versao.setSummary(getResources().getString(
                R.string.pref_descricao_versao, nrVersao));

        if (cesta.isChecked()) {
            cesta.setSummary(R.string.pref_descricao_cesta);
        } else {
            cesta.setSummary(R.string.pref_descricao_lista);
        }

        if (autobkup.isChecked()) {
            autobkup.setSummary(R.string.pref_descricao_auto_bkup_sim);
        } else {
            autobkup.setSummary(R.string.pref_descricao_auto_bkup_nao);
        }

        SharedPreferences sharedPref = getSharedPreferences("backup", Context.MODE_PRIVATE);
        pastaBackUp = sharedPref.getString("backup", "");

        if (!pastaBackUp.equals("")) {

            backup.setSummary(pastaBackUp);
        }

        toolbar.setTitle(getTitle());

        backup.setOnPreferenceClickListener(this);
        restaura.setOnPreferenceClickListener(this);
        apagatudo.setOnPreferenceClickListener(this);
        cesta.setOnPreferenceClickListener(this);
        autobkup.setOnPreferenceClickListener(this);
    }

    public boolean onPreferenceClick(Preference itemPref) {

        chave = itemPref.getKey();

        if (chave.equals("backup")) {
            // Cria um Backup do Banco de Dados
            abrePasta();

        }
        if (chave.equals("autobackup")) {

            if (autobkup.isChecked()) {
                autobkup.setSummary(R.string.pref_descricao_auto_bkup_sim);
            } else {
                autobkup.setSummary(R.string.pref_descricao_auto_bkup_nao);
            }
        }

        if (chave.equals("restaura")) {
            // Restaura DB
            dbMinhasListas.open();
            dbMinhasListas.restauraBD(pastaBackUp);
            // onResume();
            Toast.makeText(getApplicationContext(),
                    getString(R.string.dica_restaura_bd), Toast.LENGTH_SHORT)
                    .show();
            dbMinhasListas.close();
        }
        if (chave.equals("apagatudo")) {

            Dialogo();
        }
        if (chave.equals("cesta")) {
            if (cesta.isChecked()) {
                cesta.setSummary(R.string.pref_descricao_cesta);
            } else {
                cesta.setSummary(R.string.pref_descricao_lista);
            }

        }
        return false;
    }

    private void Dialogo() {

        new AlertDialog.Builder(new ContextThemeWrapper(this,
                R.style.TemaDialogo))
                .setTitle(R.string.titulo_apaga_tudo)
                .setMessage(R.string.texto_apaga_tudo)
                .setPositiveButton(R.string.ok,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface pDialogo,
                                                int pInt) {

                                // Apaga banco de dados
                                dbMinhasListas.open();
                                dbMinhasListas.excluiListas();
                                // onResume();
                                Toast.makeText(getApplicationContext(),
                                        getString(R.string.dica_exclusao_bd),
                                        Toast.LENGTH_SHORT).show();
                                dbMinhasListas.close();

                            }
                        })
                .setNegativeButton(R.string.cancelar,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface pDialogo,
                                                int pInt) {
                                pDialogo.dismiss();
                            }
                        }).show();

    }

    @Override
    public void setContentView(int layoutResID) {
        ViewGroup contentView = (ViewGroup) LayoutInflater.from(this).inflate(
                R.layout.ajustes, new LinearLayout(this), false);

        toolbar = (Toolbar) contentView.findViewById(R.id.actionbar_toolbar);
        toolbar.setBackgroundColor(Color.parseColor("#FF0099CC"));
        toolbar.setTitleTextColor(Color.parseColor("#FFFFFFFF"));
        toolbar.setNavigationIcon(R.drawable.ic_back_white);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                finish();
            }
        });

        ViewGroup contentWrapper = (ViewGroup) contentView
                .findViewById(R.id.conteudo_ajustes);
        LayoutInflater.from(this).inflate(layoutResID, contentWrapper, true);

        getWindow().setContentView(contentView);
    }

    @Override
    public View onCreateView(String name, Context context, AttributeSet attrs) {
        // Allow super to try and create a view first
        final View result = super.onCreateView(name, context, attrs);
        if (result != null) {
            return result;
        }

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            switch (name) {
                case "EditText":
                    return new AppCompatEditText(this, attrs);
                case "Spinner":
                    return new AppCompatSpinner(this, attrs);
                case "CheckBox":
                    return new AppCompatCheckBox(this, attrs);
                case "RadioButton":
                    return new AppCompatRadioButton(this, attrs);
                case "CheckedTextView":
                    return new AppCompatCheckedTextView(this, attrs);
            }
        }

        return null;
    }

    public void abrePasta() {
        startActivityForResult(new Intent(this, EscolhePasta.class), 111);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 111:

                if (resultCode == RESULT_OK) {

                    if (data != null) {

                        Bundle extras = data.getExtras();
                        String path = (String) extras.get(EscolhePasta.CHOSEN_DIRECTORY);

                        try {

                            // Cria um Backup do Banco de Dados
                            dbMinhasListas.open();
                            dbMinhasListas.copiaBD(path);
                            BackupManager android = new BackupManager(getApplicationContext());
                            android.dataChanged();
                            Toast.makeText(getApplicationContext(),
                                    getString(R.string.dica_copia_bd), Toast.LENGTH_SHORT)
                                    .show();
                            dbMinhasListas.close();

                        } catch (Exception e) {

                            Log.e("Seleção de arquivos", "Deu erro!!!", e);
                        }
                    }
                }

                break;
        }


    }
}