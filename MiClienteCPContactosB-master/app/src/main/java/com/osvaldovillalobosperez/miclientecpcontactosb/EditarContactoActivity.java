package com.osvaldovillalobosperez.miclientecpcontactosb;

import android.content.ContentValues;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

public class EditarContactoActivity extends AppCompatActivity {

    EditText etUsuarioE, etMailE, etTelefonoE, etFechaNacE;
    Button btnActualizarE, btnCancelarE;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        etUsuarioE = findViewById(R.id.txtUsuarioEditar);
        etMailE = findViewById(R.id.txtEmailEditar);
        etTelefonoE = findViewById(R.id.txtTelefonoEditar);
        etFechaNacE = findViewById(R.id.txtFechaNacimientoEditar);
        btnActualizarE = findViewById(R.id.btnActualizarEditar);
        btnCancelarE = findViewById(R.id.btnCancelarEditar);

        Bundle extras = getIntent().getExtras();
        if (extras == null) {
            finish();
            return;
        }

        final int idContacto = extras.getInt("_id");
        String usuarioContacto = extras.getString("usuario");
        String emailContacto = extras.getString("email");
        String telefonoContacto = extras.getString("tel");
        String fechaNacContacto = extras.getString("fecha_nacimiento");

        // Llenamos los campos Text con los datos del contacto.
        etUsuarioE.setText(String.valueOf(usuarioContacto));
        etMailE.setText(String.valueOf(emailContacto));
        etTelefonoE.setText(String.valueOf(telefonoContacto));
        etFechaNacE.setText(String.valueOf(fechaNacContacto));

        // Listener para el botón de cancelar, para cerrar la actividad.
        btnCancelarE.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // Listener del botón actualizar para guardar los cambios.
        btnActualizarE.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etUsuarioE.setError(null);
                etMailE.setError(null);
                etTelefonoE.setError(null);
                etFechaNacE.setError(null);

                //Crea el contacto con los nuevos cambios pero con el ID anterior.
                String nuevoUsuario = etUsuarioE.getText().toString();
                String nuevoMail = etMailE.getText().toString();
                String nuevoTelefono = etTelefonoE.getText().toString();
                String nuevaFechaNac = etFechaNacE.getText().toString();

                if (nuevoUsuario.isEmpty()) {
                    etUsuarioE.setError("Escribe el nuevo usuario");
                    etUsuarioE.requestFocus();
                    return;
                }
                if (nuevoMail.isEmpty()) {
                    etMailE.setError("Escribe el nuevo email");
                    etMailE.requestFocus();
                    return;
                }
                if (nuevoTelefono.isEmpty()) {
                    etTelefonoE.setError("Escribe el nuevo teléfono");
                    etTelefonoE.requestFocus();
                    return;
                }
                if (nuevaFechaNac.isEmpty()) {
                    etFechaNacE.setError("Escribe la nueva fecha de nacimiento");
                    etFechaNacE.requestFocus();
                    return;
                }

                String dondeId = String.valueOf(idContacto);
                String selection = ContractCPContactos.FIELD_ID + " = ?";
                String[] selectionArgs = {dondeId};

                ContentValues contentValues = new ContentValues();
                contentValues.put(ContractCPContactos.FIELD_USUARIO, nuevoUsuario);
                contentValues.put(ContractCPContactos.FIELD_EMAIL, nuevoMail);
                contentValues.put(ContractCPContactos.FIELD_TEL, nuevoTelefono);
                contentValues.put(ContractCPContactos.FIELD_FECHANACIMIENTO, nuevaFechaNac);

                int actualizarContacto = getContentResolver().update(ContractCPContactos.CONTENT_URI, contentValues, selection, selectionArgs);
                Log.i("ACTUALIZAR", "Numero de actualizaciones: " + actualizarContacto);

                finish();

                /*if (uri != 1) {
                    Toast.makeText(EditarContactoActivity.this, "Error al actualizar el contacto", Toast.LENGTH_SHORT).show();
                } else {
                    finish();
                }*/
            }
        });
    }

}
