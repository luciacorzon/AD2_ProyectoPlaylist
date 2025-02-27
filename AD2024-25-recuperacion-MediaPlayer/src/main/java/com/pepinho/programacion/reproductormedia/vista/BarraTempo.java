/*
 * Autor: Pepe Calo
 * Realizado con fines educativos.
 * Puede modificarlo siempre que no lo haga con fines comerciales.
 */
package com.pepinho.programacion.reproductormedia.vista;

import java.awt.Dimension;
//import java.awt.GridBagConstraints;
//import java.awt.GridBagLayout;
//import java.awt.Insets;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * Panel inferior que ejerce de barra de avance del tiempo de reproducción.
 * @author pepecalo
 */
public class BarraTempo extends JPanel implements ChangeListener {

    private JLabel lblTiempo;
    private JLabel lblDuracion;
    private JSlider barra;
    private int duracion;
    private int posicion;

    /**
     * Inicialmente la barra de tiempo está en 0 y tiene longitud de tiempo 0.
     */
    public BarraTempo(){
        this(0);
        barra.setEnabled(false);
    }
    
    /**
     * Crea la barra de tiempo con una duración determinada en la posición 0.
     * Además, pone las etiquetas de la barra con los valores de 00:00:00 y
     * la duración que corresponda.
     * @param duracion de la barra en segundos.
     */
    public BarraTempo(int duracion) {

        // duración y posición de la barra
        this.duracion = duracion;
        this.posicion = 0;
        
        // Etiquetas
        lblTiempo = new JLabel("00:00:00");
        lblDuracion = new JLabel(secondsToMinutes(duracion));
        
        // creación de la barra
        barra = new JSlider(JSlider.HORIZONTAL, 0, duracion, 0);
        barra.setPreferredSize(new Dimension(400, 20));

        add(lblTiempo);
        add(barra);
        add(lblDuracion);

        barra.addChangeListener(this);
    }

    /**
     * Recoge los segundos y devuelve la cadea en formato: hh:mm:ss con dos 
     * dígitos cada parte.
     * @param segundos a convertir.
     * @return cadena en formato hh:mm:ss
     */
    public static final String secondsToMinutes(int segundos) {
        return String.format("%02d:%02d:%02d", segundos / 3600,
                (segundos % 3600) / 60, (segundos % 60));

    }

    /**
     * Cuando cambia la posición cambia el valor de la etiqueta inicial.
     * @param e 
     */
    @Override
    public void stateChanged(ChangeEvent e) {
        //JSlider barra = (JSlider)e.getSource();
        if (!barra.getValueIsAdjusting()) {
            lblTiempo.setText(secondsToMinutes((int) barra.getValue()));
        }
    }
    
    /**
     * Inicializa la barra con una duración determinada. Pone la etiqueta inicial
     * a 00:00:00 y la final a valor que corresponda con la duración.
     * Está habilitada.
     * @param duracion 
     */
    public void iniciarBarra(int duracion){
        this.duracion = duracion;
        this.posicion = 0;
        
        barra.setMaximum(duracion);
        barra.setValue(0);
        
        lblDuracion.setText(secondsToMinutes(duracion));
        lblTiempo.setText("00:00:00");
//        barra.setEnabled(true);
    }

    /**
     * Devuelve la duración en segundos.
     * @return duración en segundos
     */
    public int getDuracion() {
        return duracion;
    }

    /**
     * Asigna una duración a la barra. Además, la habilita si no lo está.
     * @param segundos numero de segundos de la barra.
     */
    public void setDuracion(int segundos) {
        this.duracion = segundos;
        barra.setMaximum(segundos);
        lblDuracion.setText(secondsToMinutes(duracion));
//        barra.setEnabled(true);
    }
    
    /**
     * Sitúa la barra en una posición concreta y actualiza la etiqueta.
     * @param segundos 
     */
    public void situarBarra(int segundos){
        this.posicion = segundos;
        barra.setValue(segundos);
        lblTiempo.setText(secondsToMinutes(segundos));
    }

}
