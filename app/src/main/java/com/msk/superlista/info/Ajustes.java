package com.msk.superlista.info;

import android.Manifest;
import android.app.AlertDialog;
import android.app.backup.BackupManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceScreen;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.appcompat.widget.AppCompatCheckedTextView;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatRadioButton;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.msk.superlista.R;
import com.msk.superlista.db.DBListas;

public class Ajustes extends PreferenceActivity implements
        OnPreferenceClickListener {

    private static final int ABRE_PASTA = 666;
    private static final int ABRE_ARQUIVO = 777;
    private Toolbar toolbar;
    private DBListas dbMinhasListas = new DBListas(this);
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
            // Seleciona pasta para backup
            if (Build.VERSION.SDK_INT >= 23)
                PermissaoSD(ABRE_PASTA);
            else
                abrePasta(ABRE_PASTA);
        }

        if (chave.equals("restaura")) {
            // Seleciona o arquivo backup
            if (Build.VERSION.SDK_INT >= 23)
                PermissaoSD(ABRE_ARQUIVO);
            else
                abrePasta(ABRE_ARQUIVO);
        }

        if (chave.equals("autobackup")) {
            if (autobkup.isChecked()) {
                autobkup.setSummary(R.string.pref_descricao_auto_bkup_sim);
            } else {
                autobkup.setSummary(R.string.pref_descricao_auto_bkup_nao);
            }
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

        new AlertDialog.Builder(this)
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

    public void abrePasta(int nr) {
        if (nr == ABRE_PASTA) {
            Bundle envelope = new Bundle();
            envelope.putString("tipo", "");
            Intent atividade = new Intent(this, EscolhePasta.class);
            atividade.putExtras(envelope);
            startActivityForResult(atividade, nr);
        }

        if (nr == ABRE_ARQUIVO) {
            Bundle envelope = new Bundle();
            envelope.putString("tipo", "super_lista");
            Intent atividade = new Intent(this, EscolhePasta.class);
            atividade.putExtras(envelope);
            startActivityForResult(atividade, nr);
        }
    }

    private void PermissaoSD(int nr) {
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                // Mensagem para usuario sobre necessidade de permissão
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, nr);
            }
        } else {
            abrePasta(nr);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        // If request is cancelled, the result arrays are empty.
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            // permission was granted, yay! Do the
            abrePasta(requestCode);
        } else {
            // permission denied, boo! Disable the
            Toast.makeText(getApplicationContext(), getString(R.string.sem_itens), Toast.LENGTH_SHORT).show();
        }
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

                            SharedPreferences sharedPref = getSharedPreferences("backup", Context.MODE_PRIVATE);
                            SharedPreferences.Editor edit = sharedPref.edit();
                            edit.putString("backup", path);
                            edit.commit();

                            // Cria um Backup do Banco de Dados
                            dbMinhasListas.open();
                            dbMinhasListas.copiaBD(path);
                            BackupManager android = new BackupManager(getApplicationContext());
                            android.dataChanged();
                            Toast.makeText(getApplicationContext(),
                                    getString(R.string.dica_copia_bd), Toast.LENGTH_SHORT)
                                    .show();
                            dbMinhasListas.close();

                            pastaBackUp = sharedPref.getString("backup", "");

                            if (!pastaBackUp.equals("")) {

                                backup.setSummary(pastaBackUp);
                            }

                        } catch (Exception e) {

                            Log.e("Seleção de arquivos", "Deu erro!!!", e);
                        }
                    }
                }

                break;
            case 222:

                if (resultCode == RESULT_OK) {

                    if (data != null) {

                        Bundle extras = data.getExtras();
                        String path = (String) extras.get(EscolhePasta.CHOSEN_DIRECTORY);

                        try {

                            // Restaura DB
                            dbMinhasListas.open();
                            dbMinhasListas.restauraBD(path);
                            // onResume();
                            Toast.makeText(getApplicationContext(),
                                    getString(R.string.dica_restaura_bd), Toast.LENGTH_SHORT)
                                    .show();
                            dbMinhasListas.close();

                        } catch (Exception e) {

                            Log.e("Selecao de arquivos", "Deu erro!!!", e);
                        }
                    }
                }

                break;
        }


    }
}