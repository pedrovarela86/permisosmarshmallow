package com.blogspot.androidenespannol.permisosmarshmallow;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class MainActivity extends AppCompatActivity {

    private static final int MY_WRITE_EXTERNAL_STORAGE = 0;
    private String comments = null;
    private View mLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mLayout = findViewById(R.id.linearLayoutMain);

        final EditText editText = (EditText) findViewById(R.id.editText);
        Button button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                comments = editText.getText().toString();
                verifyPermission();
            }
        });
    }


//Paso 1. Verificar permiso
private void verifyPermission() {

    //WRITE_EXTERNAL_STORAGE tiene implícito READ_EXTERNAL_STORAGE porque pertenecen al mismo
    //grupo de permisos

    int writePermission = checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);

    if (writePermission != PackageManager.PERMISSION_GRANTED) {
        requestPermission();
    } else {
        saveComments();
    }
}


//Paso 2: Solicitar permiso
private void requestPermission() {
    //shouldShowRequestPermissionRationale es verdadero solamente si ya se había mostrado
    //anteriormente el dialogo de permisos y el usuario lo negó
    if (ActivityCompat.shouldShowRequestPermissionRationale(this,
            Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
        showSnackBar();
    } else {
        //si es la primera vez se solicita el permiso directamente
        requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                MY_WRITE_EXTERNAL_STORAGE);
    }
}

//Paso 3: Procesar respuesta de usuario
@Override
public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults);

    //Si el requestCode corresponde al que usamos para solicitar el permiso y
    //la respuesta del usuario fue positiva
    if (requestCode == MY_WRITE_EXTERNAL_STORAGE) {
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            saveComments();
        } else {
            showSnackBar();
        }
    }
}


    /**
     * Método para mostrar el snackbar de la aplicación.
     * Snackbar es un componente de la librería de diseño 'com.android.support:design:23.1.0'
     * y puede ser personalizado para realizar una acción, como por ejemplo abrir la actividad de
     * configuración de nuestra aplicación.
     */
    private void showSnackBar() {
        Snackbar.make(mLayout, R.string.permission_write_storage,
                Snackbar.LENGTH_LONG)
                .setAction(R.string.settings, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        openSettings();
                    }
                })
                .show();
    }

    /**
     * Abre el intento de detalles de configuración de nuestra aplicación
     */
    public void openSettings() {
        Intent intent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.parse("package:" + getPackageName()));
        startActivity(intent);
    }

    /**
     * Guarda el comentario
     */
    private void saveComments() {

        if (isExternalStorageWritable()) {
            try {
                File file = new File(Environment.getExternalStorageDirectory(), "comments.aee");
                boolean created = file.createNewFile();
                if (file.exists()) {
                    OutputStream fo = new FileOutputStream(file, true);
                    fo.write(comments.getBytes());
                    fo.close();
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /* Checks if external storage is available for read and write */

    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();

        return Environment.MEDIA_MOUNTED.equals(state);
    }
}
