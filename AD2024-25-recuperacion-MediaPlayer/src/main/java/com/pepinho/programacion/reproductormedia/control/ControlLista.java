/*
 * Autor: Pepe Calo
 * Realizado con fines educativos.
 * Puede modificarlo siempre que no lo haga con fines comerciales.
 */
package com.pepinho.programacion.reproductormedia.control;

import com.pepinho.programacion.reproductormedia.model.Reproducible;
import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author pepecalo
 */
public interface ControlLista {
    
    public boolean addCancion(Path cancion); // añade una canción al final y la hace sonar.
    public void play(int i); // reproduce la canción en posición i.
    public void play(); // reproduce la canción actual.
    
    public void pause(); // para la canción actual.
    
    public void stop(int i); // para la canción que está en la posición i
    public void stop(boolean pararActual); // para la canción actual o todas.
    
    public void retroceder();
    public void avanzar(); // Si la actual está sonando, la para y avanza la siguiente reproduciéndola.
    
    public int getNumeroCanciones(); 
    public List<Reproducible> getCancions();
    
    public void setMute(boolean mute); // silencia la canción actual.
    public void setVolume(double v); //Asigna un volumen entre 0 y 1  a la canción actual.
}
