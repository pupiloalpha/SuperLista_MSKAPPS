package com.msk.superlista.listas;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

import com.msk.superlista.R;
import com.msk.superlista.db.DBListas;
import com.msk.superlista.db.NovoItem;

import java.util.HashMap;

public class UsaListas extends AppCompatActivity implements ActionBar.TabListener {

    private Paginas mPaginas;
    private DBListas dbListaCriada = new DBListas(this);

    // ELEMENTOS DA TELA
    private ActionBar actionBar;
    private ViewPager mViewPager;

    // VARIAVEIS UTILIZADAS
    private String listaFeita;
    private int nrPagina;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tela_usa_listas);

        listaFeita = getIntent().getExtras().getString("lista");

        mPaginas = new Paginas(getSupportFragmentManager());

        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mPaginas);

        nrPagina = 0;
        UsarActionBar();

        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                actionBar.setSelectedNavigationItem(position);
                nrPagina = position;
                AtualizaFragmento(position);

            }
        });
    }

    private void AtualizaFragmento(int position) {
        Fragment current = mPaginas.getFragment(position);
        if (current != null) {
            current.onResume();
        }
    }

    private void UsarActionBar() {

        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setTitle(listaFeita);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        actionBar.addTab(
                actionBar.newTab()
                        .setText(getResources().getString(R.string.item_falta))
                        .setTabListener(this));
        actionBar.addTab(
                actionBar.newTab()
                        .setText(getResources().getString(R.string.item_peguei))
                        .setTabListener(this));

        actionBar.setSelectedNavigationItem(0);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
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
                nrPagina = mViewPager.getCurrentItem();
                if (nrPagina == 0)
                    carta.putString("tipo", "lista");
                else
                    carta.putString("tipo", "cesta");
                Intent correio = new Intent(this, NovoItem.class);
                correio.putExtras(carta);
                startActivity(correio);
                break;
            case R.id.botao_refaz_lista:
                dbListaCriada.open();
                Cursor itensCestaCheia = dbListaCriada.buscaItensCesta(listaFeita);

                for (int j = 0; j < itensCestaCheia.getCount(); j++) {
                    itensCestaCheia.moveToPosition(j);
                    String itemDaLista = itensCestaCheia.getString(2);
                    String descricaoItem = itensCestaCheia.getString(3);
                    double quantidadeItem = itensCestaCheia.getDouble(4);
                    String unidadeItem = itensCestaCheia.getString(5);
                    double valorItem = itensCestaCheia.getDouble(6);
                    dbListaCriada.insereItemLista(listaFeita, itemDaLista,
                            descricaoItem, quantidadeItem, unidadeItem, valorItem);
                }

                dbListaCriada.excluiItensCesta(listaFeita);
                dbListaCriada.close();
                nrPagina = mViewPager.getCurrentItem();
                AtualizaFragmento(nrPagina);
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
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
        nrPagina = tab.getPosition();
        mViewPager.setCurrentItem(nrPagina);
        AtualizaFragmento(nrPagina);
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        SharedPreferences buscaPreferencias = PreferenceManager
                .getDefaultSharedPreferences(this);
        boolean apagarLista = buscaPreferencias.getBoolean("cesta", true);

        dbListaCriada.open();
        Cursor itensCestaCheia = dbListaCriada.buscaItensLista(listaFeita);
        if (itensCestaCheia.getCount() == 0 && apagarLista) {
            dbListaCriada.excluiItensCesta(listaFeita);
            dbListaCriada.excluiItensLista(listaFeita);
            dbListaCriada.excluiLista(listaFeita);
        }
        dbListaCriada.close();
    }

    protected void onDestroy() {
        super.onDestroy();

        SharedPreferences buscaPreferencias = PreferenceManager
                .getDefaultSharedPreferences(this);
        boolean apagarLista = buscaPreferencias.getBoolean("cesta", true);

        dbListaCriada.open();
        Cursor itensCestaCheia = dbListaCriada.buscaItensLista(listaFeita);
        if (itensCestaCheia.getCount() == 0 && apagarLista) {
            dbListaCriada.excluiItensCesta(listaFeita);
            dbListaCriada.excluiItensLista(listaFeita);
            dbListaCriada.excluiLista(listaFeita);
        }
        dbListaCriada.close();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            nrPagina = mViewPager.getCurrentItem();
            AtualizaFragmento(nrPagina);
        }
    }

    @Override
    protected void onResume() {
        dbListaCriada.open();
        super.onResume();
    }

    /**
     * CLASSE QUE GERENCIA OS FRAGMENTOS
     */
    public class Paginas extends FragmentPagerAdapter {

        HashMap<Integer, String> tags = new HashMap<Integer, String>();

        public Paginas(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int i) {

            nrPagina = i;
            // DEFINE PAGINA NA TELA
            if (i == 0)
                return FragmentoLista.newInstance(listaFeita);
            else
                return FragmentoCesta.newInstance(listaFeita);
        }

        @Override
        public int getCount() {
            // QUANTIDADE DE PAGINAS QUE SERAO MOSTRADAS
            return 2;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            Object obj = super.instantiateItem(container, position);
            if (obj instanceof Fragment) {
                Fragment f = (Fragment) obj;
                String tag = f.getTag();
                tags.put(position, tag);
            }
            return obj;
        }

        public Fragment getFragment(int position) {
            String tag = tags.get(position);
            if (tag == null)
                return null;
            return getSupportFragmentManager().findFragmentByTag(tag);
        }

    }
}
