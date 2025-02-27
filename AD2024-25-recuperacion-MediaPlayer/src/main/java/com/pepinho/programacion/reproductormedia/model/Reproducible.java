
package com.pepinho.programacion.reproductormedia.model;

/**
 *
 * @author pepecalo
 * @param <T> Tipor de reproducible a comparar
 */
public  interface Reproducible<T> extends Comparable<T>{

    /**
     * Reproduce el objeto reproducible.
     *
     * @return si ha podido o no ejecutar el objeto
     */
    public boolean play();

    /**
     * Para el objeto reproducible.
     *
     * @return si ha posido o no parar el objeto
     */
    public boolean stop();

    /**
     * Pausa el objeto reproducible.
     *
     * @return si ha posido o no pausar el objeto
     */
    public boolean pause();

    /**
     * Silencia el objeto reproducible.
     *
     */
    public boolean setMute(boolean mute);

    /**
     * Devuelve si el objeto está sonando o no.
     * @return si el objeto suena o no.
     */
    public boolean isPlaying();

}

