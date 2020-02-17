package com.osvaldovillalobosperez.miclientecpcontactosb;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    ListView lv;
    Button btnCrear, btnBuscar;
    EditText txtFiltro;
    SimpleCursorAdapter adp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        lv = findViewById(R.id.Lv);
        btnCrear = findViewById(R.id.btnCrear);
        btnBuscar = findViewById(R.id.btnBuscar);
        txtFiltro = findViewById(R.id.txtBuscar);

        Cursor c = getContentResolver().query(
                Uri.withAppendedPath(
                        ContractCPContactos.CONTENT_URI,
                        ""
                ),
                ContractCPContactos.PROJECTION,
                null,
                null,
                null
        );

        refrescarLista(c);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Cursor cursor = (Cursor) adp.getItem(position);
                int idC = cursor.getInt(cursor.getColumnIndex("_id"));
                String usuarioC = cursor.getString(cursor.getColumnIndex("usuario"));
                String emailC = cursor.getString(cursor.getColumnIndex("email"));
                String telC = cursor.getString(cursor.getColumnIndex("tel"));
                String fechaNacC = cursor.getString(cursor.getColumnIndex("fecha_nacimiento"));

                Intent intent = new Intent(MainActivity.this, EditarContactoActivity.class);
                intent.putExtra("_id", idC);
                intent.putExtra("usuario", usuarioC);
                intent.putExtra("email", emailC);
                intent.putExtra("tel", telC);
                intent.putExtra("fecha_nacimiento", fechaNacC);
                startActivity(intent);
            }
        });

        lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Cursor cursor = (Cursor) adp.getItem(position);
                String obtenerId = String.valueOf(cursor.getInt(cursor.getColumnIndex("_id")));
                final String selection = ContractCPContactos.FIELD_ID + " = ?";
                final String[] selectionArgs = {obtenerId};

                AlertDialog dialog = new AlertDialog
                        .Builder(MainActivity.this)
                        .setPositiveButton("Sí, eliminar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                getContentResolver().delete(
                                        ContractCPContactos.CONTENT_URI,
                                        selection,
                                        selectionArgs
                                );

                                Intent reconstruir = getIntent();
                                finish();
                                startActivity(reconstruir);
                            }
                        })
                        .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .setTitle("Confirmar")
                        .setMessage("¿Eliminar al contacto " + cursor.getString(cursor.getColumnIndex("usuario")) + "?")
                        .create();
                dialog.show();
                return true;
            }
        });

        // Listener para el botón agregar.
        btnCrear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AgregarContactoActivity.class);
                startActivity(intent);
            }
        });

        btnBuscar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String identificarFiltro = txtFiltro.getText().toString();
                String[] obtenerTexto = {txtFiltro.getText().toString()};
                if (identificarFiltro.matches("")) {
                    Cursor c = getContentResolver().query(
                            ContractCPContactos.CONTENT_URI,
                            ContractCPContactos.PROJECTION,
                            null,
                            null,
                            null
                    );

                    refrescarLista(c);
                } else {
                    Cursor c = getContentResolver().query(
                            ContractCPContactos.CONTENT_URI,
                            ContractCPContactos.PROJECTION,
                            "usuario LIKE ?",
                            obtenerTexto,
                            null
                    );

                    refrescarLista(c);
                }
            }
        });
    }

    public void refrescarLista(Cursor c) {
        adp = new SimpleCursorAdapter(
                this,
                android.R.layout.simple_expandable_list_item_2,
                c,
                new String[]{
                        ContractCPContactos.FIELD_USUARIO,
                        ContractCPContactos.FIELD_EMAIL
                },
                new int[]{
                        android.R.id.text1,
                        android.R.id.text2
                },
                SimpleCursorAdapter.IGNORE_ITEM_VIEW_TYPE
        );
        lv.setAdapter(adp);
    }

    @Override
    protected void onResume() {
        super.onResume();

        Cursor c = getContentResolver().query(
                Uri.withAppendedPath(
                        ContractCPContactos.CONTENT_URI,
                        ""
                ),
                ContractCPContactos.PROJECTION,
                null,
                null,
                null
        );

        refrescarLista(c);
    }
}
