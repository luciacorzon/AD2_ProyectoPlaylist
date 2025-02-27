/*
 * Autor: Pepe Calo
 * Realizado con fines educativos.
 * Puede modificarlo siempre que no lo haga con fines comerciales.
 */
package com.pepinho.programacion.reproductormedia.vista;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.RoundRectangle2D;
import javax.swing.JComponent;
import javax.swing.JSlider;
import javax.swing.plaf.basic.BasicSliderUI;

/**
 * Barra de color de volumen. Un Slider personalizado. No se usa.
 * 
 * @author pepecalo
 */
public class BarraColor extends BasicSliderUI {

    private static final int ALTURA_BARRA = 8;
    private static final int ANCHURA_BARRA = 8;
    private static final int ARCO_BARRA = 5;
    private static final Dimension TAMANHO = new Dimension(20, 20);
    private final RoundRectangle2D.Float barraShape = 
            new RoundRectangle2D.Float();

    public BarraColor(final JSlider b) {
        super(b);
    }

    @Override
    protected void calculateTrackRect() {
        super.calculateTrackRect();
        if (isHorizontal()) {
            trackRect.y = trackRect.y + (trackRect.height - ALTURA_BARRA) / 2;
            trackRect.height = ALTURA_BARRA;
        } else {
            trackRect.x = trackRect.x + (trackRect.width - ANCHURA_BARRA) / 2;
            trackRect.width = ANCHURA_BARRA;
        }
        barraShape.setRoundRect(trackRect.x, trackRect.y, trackRect.width, 
                trackRect.height, ARCO_BARRA, ARCO_BARRA);
    }

    @Override
    protected void calculateThumbLocation() {
        super.calculateThumbLocation();
        if (isHorizontal()) {
            thumbRect.y = trackRect.y + (trackRect.height - thumbRect.height) / 2;
        } else {
            thumbRect.x = trackRect.x + (trackRect.width - thumbRect.width) / 2;
        }
    }

    @Override
    protected Dimension getThumbSize() {
        return TAMANHO;
    }

    private boolean isHorizontal() {
        return slider.getOrientation() == JSlider.HORIZONTAL;
    }

    @Override
    public void paint(final Graphics g, final JComponent c) {
        ((Graphics2D) g).setRenderingHint(RenderingHints.KEY_ANTIALIASING, 
                RenderingHints.VALUE_ANTIALIAS_ON);
        super.paint(g, c);
    }

    @Override
    public void paintTrack(final Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        Shape clip = g2.getClip();

        boolean horizontal = isHorizontal();
        boolean invertido = slider.getInverted();

        // Pinta la sombra.
        g2.setColor(new Color(170, 170, 170));
        g2.fill(barraShape);

        // Pinta el fondo de la pista.
        g2.setColor(new Color(200, 200, 200));
        g2.setClip(barraShape);
        barraShape.y += 1;
        g2.fill(barraShape);
        barraShape.y = trackRect.y;

        g2.setClip(clip);

        // Pinta la pista seleccionada.
        if (horizontal) {
            boolean ltr = slider.getComponentOrientation().isLeftToRight();
            if (ltr) {
                invertido = !invertido;
            }
            int thumbPos = thumbRect.x + thumbRect.width / 2;
            if (invertido) {
                g2.clipRect(0, 0, thumbPos, slider.getHeight());
            } else {
                g2.clipRect(thumbPos, 0, slider.getWidth() - thumbPos, slider.getHeight());
            }

        } else {
            int thumbPos = thumbRect.y + thumbRect.height / 2;
            if (invertido) {
                g2.clipRect(0, 0, slider.getHeight(), thumbPos);
            } else {
                g2.clipRect(0, thumbPos, slider.getWidth(), slider.getHeight() - thumbPos);
            }
        }
        g2.setColor(Color.ORANGE);
        g2.fill(barraShape);
        g2.setClip(clip);
    }

    @Override
    public void paintThumb(final Graphics g) {
        g.setColor(new Color(246, 146, 36));
        g.fillOval(thumbRect.x, thumbRect.y, thumbRect.width, thumbRect.height);
    }

    @Override
    public void paintFocus(final Graphics g) {
    }
}
