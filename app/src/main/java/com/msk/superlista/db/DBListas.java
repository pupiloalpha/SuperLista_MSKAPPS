package com.msk.superlista.db;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;

public class DBListas {

    // Nomes das Colunas do Banco de Dados
    public static final String COLUNA_CODIGO_CESTA = "id_cesta";
    public static final String COLUNA_CODIGO_LISTA = "id_lista";
    public static final String COLUNA_NOME_LISTA = "lista";
    public static final String COLUNA_NOME_ITEM_CESTA = "item_cesta";
    public static final String COLUNA_NOME_ITEM_LISTA = "item_lista";
    public static final String COLUNA_ID_LISTA = "_id";
    public static final String COLUNA_ID_ITEM_CESTA = "_id";
    public static final String COLUNA_ID_ITEM_LISTA = "_id";
    // Novas colunas criadas sobre a descricao dos itens
    public static final String COLUNA_DESCRICAO_ITEM_CESTA = "descr_item_cesta";
    public static final String COLUNA_DESCRICAO_ITEM_LISTA = "descr_item_lista";
    public static final String COLUNA_QUANTIDADE_ITEM_CESTA = "qte_item_cesta";
    public static final String COLUNA_QUANTIDADE_ITEM_LISTA = "qte_item_lista";
    public static final String COLUNA_UNIDADE_ITEM_CESTA = "unid_item_cesta";
    public static final String COLUNA_UNIDADE_ITEM_LISTA = "unid_item_lista";
    public static final String COLUNA_VALOR_ITEM_CESTA = "valor_item_cesta";
    public static final String COLUNA_VALOR_ITEM_LISTA = "valor_item_lista";
    public static final String COLUNA_PRECO_ITEM_CESTA = "preco_item_cesta";
    public static final String COLUNA_PRECO_ITEM_LISTA = "preco_item_lista";
    // Nome das tabelas criadas no banco de dados
    private static final String TABELA_LISTAS = "listas";
    private static final String CRIA_TABELA_LISTA = "CREATE TABLE "
            + TABELA_LISTAS + " (" + COLUNA_ID_LISTA
            + " INTEGER PRIMARY KEY AUTOINCREMENT, " + COLUNA_NOME_LISTA
            + " TEXT NOT NULL, UNIQUE (" + COLUNA_NOME_LISTA
            + ") ON CONFLICT REPLACE)";
    private static final String TABELA_ITENS_LISTA = "itens_da_lista";
    private static final String CRIA_TABELA_ITENS_LISTA = "CREATE TABLE "
            + TABELA_ITENS_LISTA + " ( " + COLUNA_ID_ITEM_LISTA
            + " INTEGER PRIMARY KEY AUTOINCREMENT, " + COLUNA_CODIGO_LISTA
            + " TEXT NOT NULL, " + COLUNA_NOME_ITEM_LISTA
            + " TEXT NOT NULL, " + COLUNA_DESCRICAO_ITEM_LISTA
            + " TEXT, " + COLUNA_QUANTIDADE_ITEM_LISTA
            + " REAL, " + COLUNA_UNIDADE_ITEM_LISTA
            + " TEXT, " + COLUNA_VALOR_ITEM_LISTA
            + " REAL, " + COLUNA_PRECO_ITEM_LISTA + " TXT)";
    private static final String TABELA_ITENS_CESTA = "itens_da_cesta";
    // Comandos SQL para criar as Tabelas do Banco de Dados
    private static final String CRIA_TABELA_ITENS_CESTA = "CREATE TABLE "
            + TABELA_ITENS_CESTA + " ( " + COLUNA_ID_ITEM_CESTA
            + " INTEGER PRIMARY KEY AUTOINCREMENT, " + COLUNA_CODIGO_CESTA
            + " TEXT NOT NULL, " + COLUNA_NOME_ITEM_CESTA
            + " TEXT NOT NULL, " + COLUNA_DESCRICAO_ITEM_CESTA
            + " TEXT, " + COLUNA_QUANTIDADE_ITEM_CESTA
            + " REAL, " + COLUNA_UNIDADE_ITEM_CESTA
            + " TEXT, " + COLUNA_VALOR_ITEM_CESTA
            + " REAL, " + COLUNA_PRECO_ITEM_CESTA + " TXT)";
    // Valores para criar o Banco de Dados
    private static final String BANCO_DE_DADOS = "super_lista";
    private static final int VERSAO_BANCO_DE_DADOS = 3; // NOVA VERSAO FOI CRIADA
    private static final String TAG = "DBListas";
    // Titulos das colunas das Tabelas do Banco de Dados
    private static String[] colunas_listas = {COLUNA_ID_LISTA, COLUNA_NOME_LISTA};
    private static String[] colunas_itens_cesta = {COLUNA_ID_ITEM_CESTA,
            COLUNA_CODIGO_CESTA, COLUNA_NOME_ITEM_CESTA,
            COLUNA_DESCRICAO_ITEM_CESTA, COLUNA_QUANTIDADE_ITEM_CESTA,
            COLUNA_UNIDADE_ITEM_CESTA, COLUNA_VALOR_ITEM_CESTA, COLUNA_PRECO_ITEM_CESTA};
    private static String[] colunas_itens_lista = {COLUNA_ID_ITEM_LISTA,
            COLUNA_CODIGO_LISTA, COLUNA_NOME_ITEM_LISTA,
            COLUNA_DESCRICAO_ITEM_LISTA, COLUNA_QUANTIDADE_ITEM_LISTA,
            COLUNA_UNIDADE_ITEM_LISTA, COLUNA_VALOR_ITEM_LISTA, COLUNA_PRECO_ITEM_LISTA};
    private DatabaseHelper DBHelper;
    private SQLiteDatabase db;

    public DBListas(Context cont) {
        this.DBHelper = new DatabaseHelper(cont);
    }

    public DBListas open() throws SQLException {
        this.db = this.DBHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        this.DBHelper.close();
        this.db.close();
    }

    // ------- CURSORES QUE PERCORREM O BANCO DE DADOS EM BUSCA DE INFORMACOES

    public Cursor buscaItensCesta(String lista) {
        lista = lista.replace("'", "''");
        return this.db.query(TABELA_ITENS_CESTA, colunas_itens_cesta,
                COLUNA_CODIGO_CESTA + " = '" + lista + "' ", null, null, null,
                COLUNA_NOME_ITEM_CESTA + " ASC");
    }

    public Cursor buscaItensLista(String lista) {
        lista = lista.replace("'", "''");
        return this.db.query(TABELA_ITENS_LISTA, colunas_itens_lista,
                COLUNA_CODIGO_LISTA + " = '" + lista + "' ", null, null, null,
                COLUNA_NOME_ITEM_LISTA + " ASC");
    }

    public Cursor buscaListas() {
        return this.db.query(TABELA_LISTAS, colunas_listas, null, null, null,
                null, COLUNA_NOME_LISTA + " ASC");
    }

    // ------METODOS QUE CONTAS VALORES NO BANCO DE DADOS

    public int contaItensCesta(String lista) {
        lista = lista.replace("'", "''");
        Cursor cursor = this.db.query(TABELA_ITENS_CESTA, colunas_itens_cesta,
                COLUNA_CODIGO_CESTA + " = '" + lista + "' ", null, null, null,
                null);
        int i = cursor.getCount();
        cursor.close();
        return i;
    }

    public int contaItensLista(String lista) {
        lista = lista.replace("'", "''");
        Cursor cursor = this.db.query(TABELA_ITENS_LISTA, colunas_itens_lista,
                COLUNA_CODIGO_LISTA + " = '" + lista + "' ", null, null, null,
                null);
        int i = cursor.getCount();
        cursor.close();
        return i;
    }

    public int contaListas() {
        Cursor cursor = this.db.query(true, TABELA_LISTAS, colunas_listas, null, null,
                null, null, null, null);
        int i = cursor.getCount();
        cursor.close();
        return i;
    }

    public int contaNomeListas(String lista) {
        lista = lista.replace("'", "''");
        Cursor cursor = this.db.query(true, TABELA_LISTAS, colunas_listas,
                COLUNA_NOME_LISTA + " = '" + lista + "' ", null, null,
                null, null, null);
        int i = cursor.getCount();
        cursor.close();
        return i;
    }

    public int contaNomeItensListas(String lista, String item) {
        lista = lista.replace("'", "''");
        item = item.replace("'", "''");
        Cursor cursor = this.db.query(
                true,
                TABELA_ITENS_LISTA,
                colunas_itens_lista,
                COLUNA_CODIGO_LISTA + " = '" + lista + "' AND "
                        + COLUNA_NOME_ITEM_LISTA + " = '" + item + "' ",
                null, null, null, null, null);
        int i = cursor.getCount();
        cursor.close();
        return i;
    }

    public int contaNomeItensCestas(String lista, String item) {
        lista = lista.replace("'", "''");
        item = item.replace("'", "''");
        Cursor cursor = this.db.query(
                true,
                TABELA_ITENS_CESTA,
                colunas_itens_cesta,
                COLUNA_CODIGO_CESTA + " = '" + lista + "' AND "
                        + COLUNA_NOME_ITEM_CESTA + " = '" + item + "' ",
                null, null, null, null, null);
        int i = cursor.getCount();
        cursor.close();
        return i;
    }

    // ------ METODOS QUE EXCLUEM VALORES NO BANCO DE DADOS

    public boolean excluiItem(String lista, String item) {
        lista = lista.replace("'", "''");
        item = item.replace("'", "''");
        return this.db.delete(TABELA_ITENS_LISTA, COLUNA_CODIGO_LISTA + " = '"
                + lista + "' AND " + COLUNA_NOME_ITEM_LISTA + "= '" + item
                + "'", null) > 0;
    }

    public boolean excluiItemCesta(long idLong) {
        return this.db.delete(TABELA_ITENS_CESTA, COLUNA_ID_ITEM_CESTA + " = '"
                + idLong + "'", null) > 0;
    }

    public boolean excluiItemLista(long idLong) {
        return this.db.delete(TABELA_ITENS_LISTA, COLUNA_ID_ITEM_LISTA + " = '"
                + idLong + "'", null) > 0;
    }

    public boolean excluiItensCesta(String lista) {
        lista = lista.replace("'", "''");
        return this.db.delete(TABELA_ITENS_CESTA, COLUNA_CODIGO_CESTA + " = '"
                + lista + "' ", null) > 0;
    }

    public boolean excluiItensLista(String lista) {
        lista = lista.replace("'", "''");
        return this.db.delete(TABELA_ITENS_LISTA, COLUNA_CODIGO_LISTA + " = '"
                + lista + "' ", null) > 0;
    }

    public boolean excluiLista(String lista) {
        lista = lista.replace("'", "''");
        return this.db.delete(TABELA_LISTAS, COLUNA_NOME_LISTA + " = '"
                + lista + "' ", null) > 0;
    }

    public void excluiListas() {
        this.db.delete(TABELA_ITENS_CESTA, null, null);
        this.db.delete(TABELA_ITENS_LISTA, null, null);
        this.db.delete(TABELA_LISTAS, null, null);
    }

    // ------ METODOS QUE CRIAM VALORES NO BANCO DE DADOS

    public long insereItemCesta(String cesta, String item, String descricao, double quantidade,
                                String unidade, double valor) {
        ContentValues cv = new ContentValues();
        cv.put(COLUNA_CODIGO_CESTA, cesta);
        cv.put(COLUNA_NOME_ITEM_CESTA, item);
        cv.put(COLUNA_DESCRICAO_ITEM_CESTA, descricao);
        cv.put(COLUNA_QUANTIDADE_ITEM_CESTA, quantidade);
        cv.put(COLUNA_UNIDADE_ITEM_CESTA, unidade);
        cv.put(COLUNA_VALOR_ITEM_CESTA, valor);
        return this.db.insert(TABELA_ITENS_CESTA, null, cv);
    }

    public long insereItemLista(String lista, String item, String descricao, double quantidade,
                                String unidade, double valor) {
        ContentValues cv = new ContentValues();
        cv.put(COLUNA_CODIGO_LISTA, lista);
        cv.put(COLUNA_NOME_ITEM_LISTA, item);
        cv.put(COLUNA_DESCRICAO_ITEM_LISTA, descricao);
        cv.put(COLUNA_QUANTIDADE_ITEM_LISTA, quantidade);
        cv.put(COLUNA_UNIDADE_ITEM_LISTA, unidade);
        cv.put(COLUNA_VALOR_ITEM_LISTA, valor);
        return this.db.insert(TABELA_ITENS_LISTA, null, cv);
    }

    public long criaLista(String lista) {
        ContentValues cv = new ContentValues();
        cv.put(COLUNA_NOME_LISTA, lista);
        return this.db.insert(TABELA_LISTAS, null, cv);
    }

    // ------ METODOS QUE EXIBEM VALORES DO BANCO DE DADOS

    public String mostraItemLista(long idLong, String tipo) throws SQLException {

        Cursor cursor;
        if (tipo.equals("lista")) {
            cursor = this.db.query(TABELA_ITENS_LISTA, colunas_itens_lista,
                    COLUNA_ID_ITEM_LISTA + " = '" + idLong + "' ", null, null,
                    null, null);
        } else {
            cursor = this.db.query(TABELA_ITENS_CESTA, colunas_itens_cesta,
                    COLUNA_ID_ITEM_LISTA + " = '" + idLong + "' ", null, null,
                    null, null);
        }

        if (cursor != null && cursor.moveToFirst()) {
            String str = cursor.getString(2);
            cursor.close();
            return str;
        }
        return null;
    }

    public String mostraDescricaoItemLista(long idLong, String tipo) throws SQLException {

        Cursor cursor;
        if (tipo.equals("lista")) {
            cursor = this.db.query(TABELA_ITENS_LISTA, colunas_itens_lista,
                    COLUNA_ID_ITEM_LISTA + " = '" + idLong + "' ", null, null,
                    null, null);
        } else {
            cursor = this.db.query(TABELA_ITENS_CESTA, colunas_itens_cesta,
                    COLUNA_ID_ITEM_LISTA + " = '" + idLong + "' ", null, null,
                    null, null);
        }

        if (cursor != null && cursor.moveToFirst()) {
            String str = cursor.getString(3);
            cursor.close();
            return str;
        }
        return null;
    }

    public String mostraUnidadeItemLista(long idLong, String tipo) throws SQLException {

        Cursor cursor;
        if (tipo.equals("lista")) {
            cursor = this.db.query(TABELA_ITENS_LISTA, colunas_itens_lista,
                    COLUNA_ID_ITEM_LISTA + " = '" + idLong + "' ", null, null,
                    null, null);
        } else {
            cursor = this.db.query(TABELA_ITENS_CESTA, colunas_itens_cesta,
                    COLUNA_ID_ITEM_LISTA + " = '" + idLong + "' ", null, null,
                    null, null);
        }

        if (cursor != null && cursor.moveToFirst()) {
            String str = cursor.getString(5);
            cursor.close();
            return str;
        }
        return null;
    }

    public Double mostraQuantidadeItemLista(long idLong, String tipo) throws SQLException {

        Cursor cursor;
        if (tipo.equals("lista")) {
            cursor = this.db.query(TABELA_ITENS_LISTA, colunas_itens_lista,
                    COLUNA_ID_ITEM_LISTA + " = '" + idLong + "' ", null, null,
                    null, null);
        } else {
            cursor = this.db.query(TABELA_ITENS_CESTA, colunas_itens_cesta,
                    COLUNA_ID_ITEM_CESTA + " = '" + idLong + "' ", null, null,
                    null, null);
        }

        if (cursor != null && cursor.moveToFirst()) {
            Double db = cursor.getDouble(4);
            cursor.close();
            return db;
        }
        return 0.0D;
    }

    public Double mostraValorItemLista(long idLong, String tipo) throws SQLException {
        Cursor cursor;
        if (tipo.equals("lista")) {
            cursor = this.db.query(TABELA_ITENS_LISTA, colunas_itens_lista,
                    COLUNA_ID_ITEM_LISTA + " = '" + idLong + "' ", null, null,
                    null, null);
        } else {
            cursor = this.db.query(TABELA_ITENS_CESTA, colunas_itens_cesta,
                    COLUNA_ID_ITEM_LISTA + " = '" + idLong + "' ", null, null,
                    null, null);
        }
        if (cursor != null && cursor.moveToFirst()) {
            Double db = cursor.getDouble(6);
            cursor.close();
            return db;
        }
        return 0.0D;
    }

    @SuppressLint("DefaultLocale")
    public double mostraValor(String lista, String tipo) throws SQLException {
        lista = lista.replace("'", "''");
        Cursor cursor = null;

        if (tipo.equals("cesta"))
            cursor = this.db.query(TABELA_ITENS_CESTA, colunas_itens_cesta,
                    COLUNA_CODIGO_CESTA + " = '" + lista + "' ", null, null, null,
                    null);
        else
            cursor = this.db.query(TABELA_ITENS_LISTA, colunas_itens_lista,
                    COLUNA_CODIGO_LISTA + " = '" + lista + "' ", null, null, null,
                    null);
        int i = cursor.getCount();
        cursor.moveToLast();
        double preco = 0.0D;
        for (int j = 0; j < i; j++) {
            double qt = cursor.getDouble(4);
            double valor = cursor.getDouble(6);
            preco += qt * valor;
            cursor.moveToPrevious();
        }
        cursor.close();
        return preco;
    }

    // ------------METODOS QUE ALTERAM VALORES NO BANCO DE DADOS

    public boolean mudaNomeItem(long idLong, String item, String tipo) {
        ContentValues cv = new ContentValues();

        if (tipo.equals("lista")) {
            cv.put(COLUNA_NOME_ITEM_LISTA, item);
            return this.db.update(TABELA_ITENS_LISTA, cv, COLUNA_ID_ITEM_LISTA
                    + " = '" + idLong + "'", null) > 0;
        } else {
            cv.put(COLUNA_NOME_ITEM_CESTA, item);
            return this.db.update(TABELA_ITENS_CESTA, cv, COLUNA_ID_ITEM_CESTA
                    + " = '" + idLong + "'", null) > 0;
        }
    }

    public boolean mudaUnidadeItem(long idLong, String unidade, String tipo) {
        ContentValues cv = new ContentValues();

        if (tipo.equals("lista")) {
            cv.put(COLUNA_UNIDADE_ITEM_LISTA, unidade);
            return this.db.update(TABELA_ITENS_LISTA, cv, COLUNA_ID_ITEM_LISTA
                    + " = '" + idLong + "'", null) > 0;
        } else {
            cv.put(COLUNA_UNIDADE_ITEM_CESTA, unidade);
            return this.db.update(TABELA_ITENS_CESTA, cv, COLUNA_ID_ITEM_CESTA
                    + " = '" + idLong + "'", null) > 0;
        }
    }

    public boolean mudaDescricaoItem(long idLong, String descricao, String tipo) {
        ContentValues cv = new ContentValues();

        if (tipo.equals("lista")) {
            cv.put(COLUNA_DESCRICAO_ITEM_LISTA, descricao);
            return this.db.update(TABELA_ITENS_LISTA, cv, COLUNA_ID_ITEM_LISTA
                    + " = '" + idLong + "'", null) > 0;
        } else {
            cv.put(COLUNA_DESCRICAO_ITEM_CESTA, descricao);
            return this.db.update(TABELA_ITENS_CESTA, cv, COLUNA_ID_ITEM_CESTA
                    + " = '" + idLong + "'", null) > 0;
        }
    }

    public boolean mudaNomeLista(String nomeAntes, String nomeDepois) {
        ContentValues cv = new ContentValues();
        nomeAntes = nomeAntes.replace("'", "''");
        cv.put(COLUNA_NOME_LISTA, nomeDepois);
        return this.db.update(TABELA_LISTAS, cv, COLUNA_NOME_LISTA
                + " = '" + nomeAntes + "' ", null) > 0;
    }

    public boolean mudaNomeListaItens(String nomeAntes, String nomeDepois) {
        ContentValues cv = new ContentValues();
        nomeAntes = nomeAntes.replace("'", "''");
        cv.put(COLUNA_CODIGO_LISTA, nomeDepois);
        return this.db.update(TABELA_ITENS_LISTA, cv, COLUNA_CODIGO_LISTA
                + " = '" + nomeAntes + "' ", null) > 0;
    }

    public boolean mudaValorItem(long idLong, double valor, String tipo) {
        ContentValues cv = new ContentValues();
        if (tipo.equals("lista")) {
            cv.put(COLUNA_VALOR_ITEM_LISTA, valor);
            return this.db.update(TABELA_ITENS_LISTA, cv, COLUNA_ID_ITEM_LISTA
                    + " = '" + idLong + "'", null) > 0;
        } else {
            cv.put(COLUNA_VALOR_ITEM_CESTA, valor);
            return this.db.update(TABELA_ITENS_CESTA, cv, COLUNA_ID_ITEM_CESTA
                    + " = '" + idLong + "'", null) > 0;
        }
    }

    public boolean mudaQuantidadeItem(long idLong, double quantidade,
                                      String tipo) {
        ContentValues cv = new ContentValues();
        if (tipo.equals("lista")) {
            cv.put(COLUNA_QUANTIDADE_ITEM_LISTA, quantidade);
            return this.db.update(TABELA_ITENS_LISTA, cv, COLUNA_ID_ITEM_LISTA
                    + " = '" + idLong + "'", null) > 0;
        } else {
            cv.put(COLUNA_QUANTIDADE_ITEM_CESTA, quantidade);
            return this.db.update(TABELA_ITENS_CESTA, cv, COLUNA_ID_ITEM_CESTA
                    + " = '" + idLong + "'", null) > 0;
        }
    }

    // ------------METODOS QUE COPIAM VALORES DO BANCO DE DADOS

    public String pegaItensLista(String lista) throws SQLException {
        lista = lista.replace("'", "''");
        Cursor cursor = this.db.query(TABELA_ITENS_LISTA, colunas_itens_lista,
                COLUNA_CODIGO_LISTA + " = '" + lista + "' ", null, null, null,
                COLUNA_NOME_ITEM_LISTA + " ASC");
        int i = cursor.getColumnIndex(COLUNA_NOME_ITEM_LISTA);
        int j = cursor.getColumnIndex(COLUNA_UNIDADE_ITEM_LISTA);
        int k = cursor.getColumnIndex(COLUNA_DESCRICAO_ITEM_LISTA);
        String str = "Lista de itens de " + lista + ":\n";
        cursor.moveToFirst();
        while (true) {
            if (cursor.isAfterLast()) {
                cursor.close();
                return str;
            }
            str = str + cursor.getString(j) + " de " + cursor.getString(i)
                    + " " + cursor.getString(k) + "\n";
            cursor.moveToNext();
        }
    }

    // CLASSE DO BANCO DE DADOS QUE CRIA TABELAS OU ATUALIZA A VERSAO DO BANCO

    @SuppressWarnings("resource")
    public void copiaBD(String pasta) {

        try {
            File sd = Environment.getExternalStorageDirectory();
            File data = Environment.getDataDirectory();

            if (!pasta.equals(""))
                sd = new File(pasta);

            if (sd.canWrite()) {
                String currentDBPath = "//data//com.msk.superlista//databases//super_lista";
                String backupDBPath = "super_lista";
                File currentDB = new File(data, currentDBPath);
                File backupDB = new File(sd, backupDBPath);

                if (currentDB.exists()) {
                    FileChannel src = new FileInputStream(currentDB)
                            .getChannel();
                    FileChannel dst = new FileOutputStream(backupDB)
                            .getChannel();
                    dst.transferFrom(src, 0, src.size());
                    src.close();
                    dst.close();
                }
            }
        } catch (Exception e) {
        }
    }

    // ----- Procedimentos para fazer BACKUP e RESTARURAR BACKUP

    @SuppressWarnings("resource")
    public void restauraBD(String pasta) {

        try {
            File sd = Environment.getExternalStorageDirectory();
            File data = Environment.getDataDirectory();

            if (!pasta.equals(""))
                sd = new File(pasta);

            if (sd.canWrite()) {
                String currentDBPath = "//data//com.msk.superlista//databases//super_lista";
                String backupDBPath = "super_lista";
                File currentDB = new File(data, currentDBPath);
                //File backupDB = new File(sd, backupDBPath);

                if (currentDB.exists()) {
                    FileChannel src = new FileInputStream(sd)
                            .getChannel();
                    FileChannel dst = new FileOutputStream(currentDB)
                            .getChannel();
                    dst.transferFrom(src, 0, src.size());
                    src.close();
                    dst.close();
                }
            }
        } catch (Exception e) {
        }
    }

    public void ModificadaDados() throws SQLException {
        // CORRIGE VALOR, QUANTIDADE E UNIDADE
        String valor = "";
        String qte = "";
        String un = "";

        Cursor cursor = db.query(TABELA_ITENS_LISTA, null,
                null, null, null, null, null);
        int i = cursor.getCount();
        cursor.moveToLast();
        long idLong;
        double preco = 0.0D;
        double qt = 0.0D;

        try {
            if (!cursor.getString(7).equals("")) {
                for (int j = 0; j < i; j++) {
                    idLong = cursor.getLong(0);
                    valor = cursor.getString(7);
                    qte = cursor.getString(5);
                    qte = qte.replace("", "");
                    qte = qte.replace(" unid", "");
                    qte = qte.replace(" caixa", "");
                    qte = qte.replace(" kg", "");
                    qte = qte.replace(" litro", "");
                    qte = qte.replace(" g", "");
                    qte = qte.replace(" ml", "");
                    qte = qte.replace(" pc", "");
                    qte = qte.replace(",", ".");
                    qt = Double.parseDouble(qte);
                    un = cursor.getString(5);
                    int k = un.length();
                    String unid = un.substring(k);
                    if (unid.equals("c")) {
                        un = "pc";
                    } else if (unid.equals("a")) {
                        un = "caixa";
                    } else if (unid.equals("g")) {
                        un = "kg";
                    } else if (unid.equals("o")) {
                        un = "litro";
                    } else if (unid.equals("l")) {
                        un = "ml";
                    } else {
                        un = "unid";
                    }
                    valor = valor.replace("R$ ", "");
                    valor = valor.replace("$ ", "");
                    valor = valor.replace(",", ".");
                    preco = Double.parseDouble(valor);
                    ContentValues cv = new ContentValues();
                    cv.put(COLUNA_QUANTIDADE_ITEM_LISTA, qt);
                    cv.put(COLUNA_VALOR_ITEM_LISTA, preco);
                    cv.put(COLUNA_UNIDADE_ITEM_LISTA, un);
                    cv.put(COLUNA_PRECO_ITEM_LISTA, "");
                    db.update(TABELA_ITENS_LISTA, cv, COLUNA_ID_ITEM_LISTA
                            + " = '" + idLong + "'", null);
                    cursor.moveToPrevious();
                }
            }

            cursor.close();

            // CORRIGE VALOR, QUANTIDADE E UNIDADE
            valor = "";
            qte = "";
            un = "";
            cursor = db.query(TABELA_ITENS_CESTA, null,
                    null, null, null, null, null);
            i = cursor.getCount();
            cursor.moveToLast();
            preco = 0.0D;
            qt = 0.0D;
            if (!cursor.getString(7).equals("")) {
                for (int j = 0; j < i; j++) {
                    idLong = cursor.getLong(0);
                    valor = cursor.getString(7);
                    qte = cursor.getString(5);
                    qte = qte.replace("", "");
                    qte = qte.replace(" unid", "");
                    qte = qte.replace(" caixa", "");
                    qte = qte.replace(" kg", "");
                    qte = qte.replace(" litro", "");
                    qte = qte.replace(" g", "");
                    qte = qte.replace(" ml", "");
                    qte = qte.replace(" pc", "");
                    qte = qte.replace(",", ".");
                    qt = Double.parseDouble(qte);
                    un = cursor.getString(5);
                    int k = un.length();
                    String unid = un.substring(k);
                    if (unid.equals("c")) {
                        un = "pc";
                    } else if (unid.equals("a")) {
                        un = "caixa";
                    } else if (unid.equals("g")) {
                        un = "kg";
                    } else if (unid.equals("o")) {
                        un = "litro";
                    } else if (unid.equals("l")) {
                        un = "ml";
                    } else {
                        un = "unid";
                    }
                    valor = valor.replace("R$ ", "");
                    valor = valor.replace("$ ", "");
                    valor = valor.replace(",", ".");
                    preco = Double.parseDouble(valor);
                    ContentValues cv = new ContentValues();
                    cv.put(COLUNA_QUANTIDADE_ITEM_CESTA, qt);
                    cv.put(COLUNA_VALOR_ITEM_CESTA, preco);
                    cv.put(COLUNA_UNIDADE_ITEM_CESTA, un);
                    cv.put(COLUNA_PRECO_ITEM_CESTA, "");
                    db.update(TABELA_ITENS_CESTA, cv, COLUNA_ID_ITEM_CESTA
                            + " = '" + idLong + "'", null);
                    cursor.moveToPrevious();
                }
            }
            cursor.close();
        } catch (Exception e) {
            Log.w(TAG, "Erro ao modificar dados");
        }


    }

    private static class DatabaseHelper extends SQLiteOpenHelper {
        DatabaseHelper(Context pContext) {
            super(pContext, BANCO_DE_DADOS, null, VERSAO_BANCO_DE_DADOS);
        }

        public void onCreate(SQLiteDatabase pSQLiteDB) {

            // CRIA AS TABELAS DO BANCO DE DADOS
            pSQLiteDB.execSQL(CRIA_TABELA_LISTA);
            pSQLiteDB.execSQL(CRIA_TABELA_ITENS_LISTA);
            pSQLiteDB.execSQL(CRIA_TABELA_ITENS_CESTA);
            Log.w(TAG, "DB criado com sucesso!");
        }

        public void onUpgrade(SQLiteDatabase pSQLiteDB, int ver1, int ver2) {

            // METODO PARA MODIFICAR BANCO DE DADOS SEM PERDER INFORMACOES
            Log.w(TAG, "Atualizando o banco de dados da versao " + ver1
                    + " para " + ver2 + ", os dados serao apagados!");

            // TRANSFERINDO DADOS DE UMA TABELA PARA OUTRA E DEPOIS ACRESCENTANDO COLUNA

            // TABELA DE ITENS DENTRO DAS LISTAS
            pSQLiteDB.execSQL("CREATE TABLE "
                    + "tabela_temporaria" + " ( " + COLUNA_ID_ITEM_LISTA
                    + " INTEGER PRIMARY KEY AUTOINCREMENT, " + COLUNA_CODIGO_LISTA
                    + " TEXT, " + COLUNA_NOME_ITEM_LISTA
                    + " TEXT, " + COLUNA_DESCRICAO_ITEM_LISTA
                    + " TEXT, " + COLUNA_QUANTIDADE_ITEM_LISTA
                    + " REAL, " + COLUNA_UNIDADE_ITEM_LISTA
                    + " TXT, " + COLUNA_VALOR_ITEM_LISTA
                    + " REAL, preco_item_lista TXT )");

            pSQLiteDB.execSQL("INSERT INTO tabela_temporaria"
                    + " SELECT " + COLUNA_ID_ITEM_LISTA
                    + ", " + COLUNA_CODIGO_LISTA
                    + ", " + COLUNA_NOME_ITEM_LISTA
                    + ", " + COLUNA_DESCRICAO_ITEM_LISTA
                    + ", " + null
                    + ", " + COLUNA_QUANTIDADE_ITEM_LISTA
                    + ", " + null
                    + ", preco_item_lista"
                    + " FROM " + TABELA_ITENS_LISTA);

            pSQLiteDB.execSQL("DROP TABLE " + TABELA_ITENS_LISTA);

            pSQLiteDB.execSQL(CRIA_TABELA_ITENS_LISTA);

            pSQLiteDB.execSQL("INSERT INTO "
                    + TABELA_ITENS_LISTA + " SELECT " + COLUNA_ID_ITEM_LISTA
                    + ", " + COLUNA_CODIGO_LISTA
                    + ", " + COLUNA_NOME_ITEM_LISTA
                    + ", " + COLUNA_DESCRICAO_ITEM_LISTA
                    + ", " + COLUNA_QUANTIDADE_ITEM_LISTA
                    + ", " + COLUNA_UNIDADE_ITEM_LISTA
                    + ", " + COLUNA_VALOR_ITEM_LISTA
                    + ", preco_item_lista FROM tabela_temporaria");

            pSQLiteDB.execSQL("DROP TABLE tabela_temporaria");

            // TABELA DE ITENS DENTRO DAS CESTAS
            pSQLiteDB.execSQL("CREATE TABLE "
                    + "tabela_temporaria" + " ( " + COLUNA_ID_ITEM_CESTA
                    + " INTEGER PRIMARY KEY AUTOINCREMENT, " + COLUNA_CODIGO_CESTA
                    + " TEXT, " + COLUNA_NOME_ITEM_CESTA
                    + " TEXT, " + COLUNA_DESCRICAO_ITEM_CESTA
                    + " TEXT, " + COLUNA_QUANTIDADE_ITEM_CESTA
                    + " REAL, " + COLUNA_UNIDADE_ITEM_CESTA
                    + " TXT, " + COLUNA_VALOR_ITEM_CESTA
                    + " REAL, preco_item_cesta TXT )");

            pSQLiteDB.execSQL("INSERT INTO tabela_temporaria"
                    + " SELECT " + COLUNA_ID_ITEM_CESTA
                    + ", " + COLUNA_CODIGO_CESTA
                    + ", " + COLUNA_NOME_ITEM_CESTA
                    + ", " + COLUNA_DESCRICAO_ITEM_CESTA
                    + ", " + null
                    + ", " + COLUNA_QUANTIDADE_ITEM_CESTA
                    + ", " + null
                    + ", preco_item_cesta"
                    + " FROM " + TABELA_ITENS_CESTA);

            pSQLiteDB.execSQL("DROP TABLE " + TABELA_ITENS_CESTA);

            pSQLiteDB.execSQL(CRIA_TABELA_ITENS_CESTA);

            pSQLiteDB.execSQL("INSERT INTO "
                    + TABELA_ITENS_CESTA + " SELECT " + COLUNA_ID_ITEM_CESTA
                    + ", " + COLUNA_CODIGO_CESTA
                    + ", " + COLUNA_NOME_ITEM_CESTA
                    + ", " + COLUNA_DESCRICAO_ITEM_CESTA
                    + ", " + COLUNA_QUANTIDADE_ITEM_CESTA
                    + ", " + COLUNA_UNIDADE_ITEM_CESTA
                    + ", " + COLUNA_VALOR_ITEM_CESTA
                    + ", preco_item_cesta FROM tabela_temporaria");

            pSQLiteDB.execSQL("DROP TABLE tabela_temporaria");
        }
    }

}
