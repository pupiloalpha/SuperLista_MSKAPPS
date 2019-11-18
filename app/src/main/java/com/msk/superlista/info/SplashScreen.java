package com.msk.superlista.info;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.msk.superlista.SuperLista;

/**
 * Created by msk on 08/06/16.
 */
public class SplashScreen extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = new Intent(this, SuperLista.class);
        startActivity(intent);
        finish();
    }
}
