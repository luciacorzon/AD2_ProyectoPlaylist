/*
 * Autor: Pepe Calo
 * Realizado con fines educativos.
 * Puede modificarlo siempre que no lo haga con fines comerciales.
 */
package com.pepinho.programacion.reproductormedia.vista;

import java.awt.Dimension;
import javax.swing.JSlider;

/**
 *
 * @author pepecalo
 */
public class BarraVolumen extends JSlider {

    private int volume = 50;
    
    public BarraVolumen() {
        this(JSlider.HORIZONTAL, 0, 100, 50);
    }
    
    public BarraVolumen(int orientation, int min, int max, int value) {
        super(orientation, min, max, value);
        setPreferredSize(new Dimension(80, 20));
    }

    public double getVolume() {
        return volume/100.;
    }

    public void setVolume(double volume) {
        this.volume = (int)(100*volume);
    }


    
}
