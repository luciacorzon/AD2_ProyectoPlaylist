/*
 * Autor: Pepe Calo
 * Realizado con fines educativos.
 * Puede modificarlo siempre que no lo haga con fines comerciales.
 */
package com.pepinho.programacion.reproductormedia.control;

import com.pepinho.programacion.reproductormedia.model.IPlayList;
import com.pepinho.programacion.reproductormedia.model.Reproducible;
import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author pepecalo
 */
public class PlayerController implements ControlLista {

    private final IPlayList lista;
    
    public PlayerController(IPlayList lista){
        this.lista = lista;
    }

    @Override
    public boolean addCancion(Path cancion) {
        return lista.addCancion(cancion);
    }

    @Override
    public void play(int i) {
        lista.play();
    }

    @Override
    public void play() {
        lista.play();
    }

    @Override
    public void stop(int i) {
        lista.stop(i);
    }

    @Override
    public void stop(boolean pararActual) {
      lista.stop(pararActual);
    }

    @Override
    public void retroceder() {
        lista.retroceder();
    }

    @Override
    public void avanzar() {
        lista.avanzar();
    }

    @Override
    public int getNumeroCanciones() {
        return lista.getNumeroCanciones();
    }

    @Override
    public List<Reproducible> getCancions() {
        return lista.getCancions();
    }

    @Override
    public void setMute(boolean mute) {
        lista.setMute(mute);
    }

    @Override
    public void setVolume(double v) {
        lista.setVolume(v);
    }

    @Override
    public void pause() {
        lista.pause();
    }
    
}
