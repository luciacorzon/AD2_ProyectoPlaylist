package com.pepinho.programacion.reproductormedia.model;

import javax.swing.*;

public interface PlayListObserver {


    public void actualizarTiempo(int segundos);


    public void actualizarDuracion(int segundos);
}
