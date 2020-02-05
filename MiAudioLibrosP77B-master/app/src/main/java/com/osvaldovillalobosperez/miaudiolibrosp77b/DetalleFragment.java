package com.osvaldovillalobosperez.miaudiolibrosp77b;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;

import java.io.IOException;

public class DetalleFragment extends Fragment implements View.OnTouchListener,
        MediaPlayer.OnPreparedListener, MediaController.MediaPlayerControl {

    public static String ARG_ID_LIBRO = "id_libro";
    MediaPlayer mediaPlayer;
    MediaController mediaController;
    String guardar = "";

    /**
     * @param id    ID del Libro a utilizar.
     * @param vista Variable que compone el layaout.
     */
    private void ponInfoLibro(int id, View vista) {
        Libro libro = Libro.ejemploLibros().elementAt(id);
        ((TextView) vista.findViewById(R.id.titulo)).setText(libro.titulo);
        ((TextView) vista.findViewById(R.id.autor)).setText(libro.autor);
        ((ImageView) vista.findViewById(R.id.portada)).setImageResource(libro.recursoImagen);
        vista.setOnTouchListener(this);
        if (mediaPlayer != null) {
            mediaPlayer.release();
        }
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setOnPreparedListener(this); // Identifica quien es el escuchador.
        mediaController = new MediaController(getActivity()); // Establece una nueva instancia.
        Uri audio = Uri.parse(libro.urlAudio); // Uri que maneja la localización de archivos.
        guardar = libro.urlAudio;
        try {
            mediaPlayer.setDataSource(getActivity(), audio); // Establece la fuente del audio.
            mediaPlayer.prepareAsync(); //Prepara el archivo de la fuente.
        } catch (IOException e) {
            Log.e("Audiolibros", "ERROR: No se puede reproducir " + audio, e);
        }
    }

    public void ponInfoLibro(int id) {
        ponInfoLibro(id, getView());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        //return super.onCreateView(inflater, container, savedInstanceState);
        View vista = inflater.inflate(R.layout.fragment_detalle,
                container, false);
        Bundle args = getArguments();
        if (args != null) {
            int position = args.getInt(ARG_ID_LIBRO);
            ponInfoLibro(position, vista);
        } else {
            ponInfoLibro(0, vista);
        }
        return vista;
    }


    ServicioLibros servicioLibros;
    boolean mBound = false;

    /**
     * Inicia la reproducción del medio.
     *
     * @param mediaPlayer
     */
    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
        Log.d("AudioLibros", "Entramos en onPrepared de MediaPlayer");
        SharedPreferences preferencias = PreferenceManager.getDefaultSharedPreferences(getActivity());
        if (preferencias.getBoolean("pref_autoreproducir", true)) {
            Intent serviceIntent = new Intent(getActivity().getApplicationContext(), ServicioLibros.class);
            serviceIntent.putExtra("inputExtra", guardar);
            ContextCompat.startForegroundService(getActivity(), serviceIntent);
        }

        mediaController.setMediaPlayer(this);
        mediaController.setAnchorView(getView().findViewById(R.id.fragment_detalle));
        mediaController.setPadding(0, 0, 0, 110);
        mediaController.setEnabled(true);
        mediaController.show();
    }

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            ServicioLibros.MiBinder miBinder = (ServicioLibros.MiBinder) service;
            servicioLibros = miBinder.getService();
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mBound = false;
        }
    };


    void bindService() {
        getActivity().bindService(new Intent(getActivity(), ServicioLibros.class), serviceConnection, Context.BIND_AUTO_CREATE);
    }

    void unBindService() {
        if (mBound == true) {
            getActivity().unbindService(serviceConnection);
            mBound = false;
        }
    }


    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        mediaController.show();
        return false;
    }


    @Override
    public void onStop() {
        mediaController.hide();
        try {
            mediaPlayer.stop();
            mediaPlayer.release();
        } catch (Exception e) {
            Log.d("AudioLibros", "Error en MediaPlayer.stop()");
        }
        super.onStop();
    }

    @Override
    public void start() {
        bindService();
        mediaPlayer.start();
    }

    @Override
    public void pause() {
        servicioLibros.StopAudio();
        unBindService();
        mediaPlayer.pause();
    }

    @Override
    public int getDuration() {
        return mediaPlayer.getDuration();
    }

    @Override
    public int getCurrentPosition() {
        try {
            return mediaPlayer.getCurrentPosition();
        } catch (Exception e) {
            return 0;
        }
    }

    @Override
    public void seekTo(int i) {
        mediaPlayer.seekTo(i);
    }

    @Override
    public boolean isPlaying() {
        return mediaPlayer.isPlaying();
    }

    @Override
    public int getBufferPercentage() {
        return 0;
    }

    @Override
    public boolean canPause() {
        return true;
    }

    @Override
    public boolean canSeekBackward() {
        return true;
    }

    @Override
    public boolean canSeekForward() {
        return true;
    }

    @Override
    public int getAudioSessionId() {
        return 0;
    }

    @Override
    public void onResume() {
        DetalleFragment detalleFragment = (DetalleFragment)
                getFragmentManager().findFragmentById(R.id.detalle_fragment);
        if (detalleFragment == null) {
            ((MainActivity) getActivity()).mostrarElementos(false);
        }
        super.onResume();
    }
}
