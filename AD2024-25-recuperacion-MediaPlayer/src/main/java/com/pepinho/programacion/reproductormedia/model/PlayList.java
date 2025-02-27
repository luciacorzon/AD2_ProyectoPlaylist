

package com.pepinho.programacion.reproductormedia.model;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javafx.scene.media.MediaPlayer;

public final class PlayList implements Comparable<PlayList>, IPlayList {

    /**
     * Cadena para la separación del nombre de la PlayList y la fecha de creación.
     * Con respecto a la lista de canciones.
     * @see #toString()
     */
    public static final String BARRA = "**************************";
    /**
     * Título por defecto.
     */
    public static final String DEFAULT_NAME = "Deconocida";
    /**
     * Serial para la serialización.
     */
    @Serial
    private static final long serialVersionUID = 1L;


    /**
     * Identificador de la lista de reproducción.
     */
    private Long idPlayList;

    /**
     * Título de la PlayList.
     */
    private String nome;

    private LocalDate dataCreacion;

    /**
     * Lista de elementos reproducibles, canciones, de la lista de reproducción.
     */
    private final List<Reproducible> cancions = new ArrayList<>();

    /**
     * Número de pista actual seleccionada en la playList.
     * No se guarda en la base de datos.
     */
    private int actual;

    /**
     * Indica si trata de una reproducción aleatoria. No se implanta
     */
    private boolean aleatorio = false;

    /**
     * Lista de vistas que actualizar cuando hay cambios en la reproducción de la playList.
     * Un cambio en el modelo se notifica a los observadores.
     */
    private List<PlayListObserver> viewObservers = new ArrayList<>();

    /**
     * Crea una PlayLit sin canciones y con título "Descoñecida".
     * La fecha de creación es la actual.
     *
     * @see #DEFAULT_NAME
     * @see LocalDate#now()
     */
    public PlayList() {
        this(DEFAULT_NAME);
    }

    /**
     * Crea una PlayList sin canciones y con el título recogido como argumento.
     * La fecha de creación es la actual.
     *
     * @param nome Nombre de la lista de reproducción.
     * @see LocalDate#now()
     */
    public PlayList(String nome) {
        this.nome = nome;
        dataCreacion = LocalDate.now();
    }

    /**
     * Crea una Playlist con el nombre y las canciones recogidas como argumento.
     * La decha es la fecha actual.
     *
     * @param nome     nombre de la PlayList.
     * @param cancions canciones a añadir a la playlist.
     */
    public PlayList(long idPlayList, String nome, LocalDate dataCreacion, List<Reproducible> cancions) {
        this.idPlayList = idPlayList;
        this.nome = nome;
        this.dataCreacion = (dataCreacion!=null) ? dataCreacion : LocalDate.now();
        this.cancions.addAll(cancions);
    }

    public PlayList(long idPlayList, String nome, LocalDate dataCreacion) {
        this.idPlayList = idPlayList;
        this.nome = nome;
        this.dataCreacion = (dataCreacion!=null) ? dataCreacion : LocalDate.now();
    }

    public PlayList(String titulo, int dia, int mes, int anho) {
        this.nome = (titulo != null) ? titulo : DEFAULT_NAME;
        dataCreacion = LocalDate.of(anho, mes, dia);
    }


/* =============================================
Getters y setters.
=============================================== */

    public Long getIdPlayList() {
        return idPlayList;
    }

    public PlayList setIdPlayList(Long idPlayList) {
        this.idPlayList = idPlayList;
        return this;
    }

    /**
     * Devuelve el nombre de la PlayList
     *
     * @return nombre de la PlayList
     */
    public String getNome() {
        return nome;
    }


    /**
     * Pone un nombre a la PlayList.
     *
     * @param nome nombre de la playList.
     */
    public PlayList setNome(String nome) {
        this.nome = nome;
        return this;
    }


    /**
     * Devuelve la fecha de creación de la PlayList.
     *
     * @return
     */
    public LocalDate getDataCreacion() {
        return dataCreacion;
    }

    public PlayList setDataCreacion(LocalDate dataCreacion) {
        this.dataCreacion = dataCreacion;
        return this;
    }


    public PlayList setAleatorio(boolean aleatorio) {
        this.aleatorio = aleatorio;
        return this;
    }

    public boolean isAleatorio() {
        return aleatorio;
    }

    /*======================================================
     * Métodos adicionales
     =====================================================*/

    /**
     * addCancion
     * Devuleve el número de canciones de la PlayList.
     * O si está vacía o es nula.
     *
     * @return número de canciones del PlayList.
     */
    @Override
    public int getNumeroCanciones() {
        return (cancions != null) ? cancions.size() : 0;
    }


    /**
     * Añade una canción a partir de un archivo si la lista no es nula.
     *
     * @param cancion archivo de la canción MP3 a añadir.
     * @return si se ha podido añadir.
     */
    @Override
    public boolean addCancion(Path cancion) {
        MediaSong media = new MediaSong(cancion.toString());
        System.out.println("Añadiendo.. canción: " +
                cancion.toString());
        return this.addCancion(media);
    }


    public PlayList addCancion(Reproducible r, int i) {
        if (i >= 0 && i < getNumeroCanciones()) {
            cancions.add(i, r);
        }
        return this;
    }

    /**
     * Añade una canción si la lista no es nula.
     *
     * @param cancion a añadir.
     * @return si se ha podido añadir.
     */
    public boolean addCancion(Reproducible cancion) {
        if ((cancions != null) && cancions.add(cancion)) {

            pararActual();
            actual = cancions.size() - 1;

            // A medida que cambie la posición del tiempo de reproducción
            // hay que notificar a las vistas.
            if (cancion instanceof MediaSong) {
                MediaPlayer mediaPlayer =
                        ((MediaSong) cancion).getMediaPlayer();

                // Cuando esté preparada actualizo la duración:
                if (mediaPlayer != null)
                    mediaPlayer.setOnReady(() -> {
                        notificarDuracionVistas(
                                (int) (mediaPlayer.getMedia().getDuration().toSeconds())
                        );
                        System.out.println("Duración: " + (int) (mediaPlayer.getMedia().getDuration().toSeconds()));
                    });


                // A medida que avance notifico a las vistas.
                mediaPlayer.currentTimeProperty().
                        addListener(
                                (cancionObservable, duracionAnterior,
                                 duracionNueva) -> {
                                    notificarPosicionVistas(
                                            (int) duracionNueva.toSeconds()
                                    );
                                }
                        );
            }
            return true;
        }
        return false;
    }

    public void setCancions(List<? extends Reproducible> cancions) {
        this.cancions.addAll(cancions);
    }


    /**
     * Elimina la canción de la lista.
     *
     * @param cancion a eliminar.
     * @return si se ha eliminado o no.
     */
    public boolean removeCancion(Reproducible cancion) {
        if (cancion != null && cancion.isPlaying()) {
            cancion.stop();
        }
        if ((cancions != null) && cancions.indexOf(cancion) == actual) // si es la actual, la ponemos la principio.
        {
            actual = 0;
        }
        return (cancions != null) && cancions.remove(cancion);
    }

    /**
     * Eleimina la canción que está en la posición i, si está dentro del rango.
     *
     * @param i índice a eliminar.
     * @return Si se ha podido eliminar o null si no es posible.
     */
    public Reproducible removeCancion(int i) {
        return (cancions != null && i >= 0 && i < cancions.size())
                ? cancions.remove(i) : null;
    }

    /**
     * @return lista de canciones
     */
    @Override
    public List<Reproducible> getCancions() {
        return cancions;
    }

    /**
     * Obtiene el valor de la pista actual.
     *
     * @return canción que está seleccionada
     */
    public Reproducible getPistaActual() {
        return (cancions != null && actual >= 0 && actual < cancions.size())
                ? cancions.get(actual) : null;
    }

    /**
     * Devuleve el número de pista actual.
     *
     * @return número de pista actual.
     */
    public int getActual() {
        return actual;
    }

    /**
     * Cambia la pista actual, siempre que esté dentro del disco. Si no está, la
     * añade al final y la pone como actual.
     *
     * @param actual
     */
    public void addActual(Reproducible actual) {
        if (cancions != null && !cancions.contains(actual)) {
            cancions.add(actual);
        }
        this.actual = cancions.size() - 1;
    }

    /**
     * Cambia la pista actual al número de pista pasada como argumento. Si está
     * sonando una canción, la para. Si no existe el número de pista no hace
     * nada, ni parar la actual.
     *
     * @param actual número de pista a seleccionar.
     */
    public void setActual(int actual) {
        if (cancions != null && actual >= 0 && actual < cancions.size()) {
            pararActual(); // paramos la pista actual.
            this.actual = actual;
        }
    }

    /**
     * Para la pista actualmente seleccionada del disco. Devuelve
     * <code>true</code> si estaba sonando y la para. <code>false</code> en caso
     * contrario.
     *
     * @return si está sonando o no.
     */
    private boolean pararActual() {
        // Paramos la pista que sonaba, si lo hacía:
        Reproducible pistaActual = cancions.get(this.actual);
        if (pistaActual.isPlaying()) {
            pistaActual.stop();
            return true;
        }
        return false;
    }

    /**
     * Avanza a la siguiente canción. Si es la última vuelve a la primera. Si la
     * actual está sonando, la para y avanza la siguiente reproduciéndola.
     */
    @Override
    public void avanzar() {
        Reproducible r = getPistaActual();
        actual = (cancions != null && actual < cancions.size() - 1) ? actual + 1 : 0;
        if (r.isPlaying()) {
            r.stop(); // paro la anterior.
            getPistaActual().play(); // toco la actual.
        }
    }

    /**
     * Retrocede a la anterior canción. Si es la primera vuelve a la última. Si
     * la actual está sonando, la para y avanza la siguiente reproduciéndola.
     */
    @Override
    public void retroceder() {
        Reproducible r = getPistaActual();
        actual = (cancions != null && actual > 0) ? actual - 1 : cancions.size() - 1;
        if (r.isPlaying()) {
            r.stop(); // paro la anterior.
            getPistaActual().play(); // toco la actual.
        }
    }

    /**
     * Reproduce la siguiente canción a la que se está reproduciendo. Si no hay
     * ninguna reproduciéndose no hace nada.
     */
    public void siguiente() {
        boolean atopada = false;
        for (int i = 0; i < cancions.size() && !atopada; i++) {
            Reproducible cancion = cancions.get(i);
            if (cancion.isPlaying()) {
                cancion.stop();
                i = (i == cancions.size() - 1) ? 0 : i + 1;
                cancions.get(i).play();
                atopada = true;
            }
        }
    }

    /**
     * Reproduce la anterior canción a la que se está reproduciendo. Si no hay
     * ninguna reproduciéndose no hace nada.
     */
    public void anterior() {
        boolean atopada = false;
        for (int i = 0; i < cancions.size() && !atopada; i++) {
            Reproducible cancion = cancions.get(i);
            if (cancion.isPlaying()) {
                cancion.stop();
                i = (i == 0) ? cancions.size() - 1 : i - 1;
                cancions.get(i).play();
                atopada = true;
            }
        }
    }


    /**
     * Devuelve la canción que está en la posición i. El índice i va desde 0
     * hasta el número de canciones del disco (no incluido). Si se sale del
     * rango devuelve "null"
     *
     * @param i índice entre 0 y gerNumeroCanciones()
     * @return Canción que está en la posición i.
     */
    public Reproducible getCancion(int i) {
        return (i >= 0 && i < cancions.size()) ? cancions.get(i) : null;
    }

    /**
     * Reproduce la canción que está en la posición i, si existe la canción.
     *
     * @param i índice de la canción a tocar.
     */
    @Override
    public void stop(int i) {
        if (i >= 0 && i < cancions.size() && cancions.get(i) != null) {
            cancions.get(i).stop();
        }
    }

    /**
     * Para todas las canciones del disco que están sonandoo la actual si el
     * valor pasado es true Recorre todo el disco y para las canciones no nulas
     * que están sonando.
     *
     * @param pararActual verdadero si queremos parar la actual, false para
     *                    todas.
     */
    @Override
    public void stop(boolean pararActual) {
        if (pararActual) {
            cancions.get(actual).stop();
        } else {
            cancions.stream().filter(cancion -> (cancion != null
                    && cancion.isPlaying())).forEachOrdered(cancion -> {
                cancion.stop();
            });
        }
    }

    /**
     * Reproduce la canción que está en la posición i, siempre que esté dentro
     * del rango válido y no sea nula. Previamente para la/s canción/es que
     * está/n sonando.
     *
     * @param i índice de la canción a reproducir.
     */
    @Override
    public void play(int i) {
        if (i >= 0 && i < cancions.size() && cancions.get(i) != null) {
            // Se precisa parar la canción que esté sonando.
            stop(true); // cancions.get(actual).stop();
            actual = i;
            cancions.get(i).play();
        }
    }

    /**
     * Reproduce la canción actual, siempre que esté dentro del rango válido y
     * no sea nula..
     */
    @Override
    public void play() {
        cancions.get(actual).play();
    }

    /**
     * Para la canción que está en la posición i, siempre que esté dentro del
     * rango y no sea nula.
     *
     * @param i índice de la canción a parar.
     */
    public void pause(int i) {
        if (i >= 0 && i < cancions.size() && cancions.get(i) != null) {
            cancions.get(i).pause();
        }
    }

    /**
     * Sitúa la reproducción de la canción i en la posición indicada.
     *
     * @param i        indice de la canción.
     * @param segundos segundos en los que poner la reproducción.
     */
    public void seek(int i, int segundos) {
        if (i >= 0 && i < cancions.size() && cancions.get(i) != null) {
            if (cancions.get(i) instanceof MediaSong) {
                ((MediaSong) cancions.get(i)).seek(segundos);
            }
        }
    }

    /**
     * Sitúa la reproducción de la canción actual en la posición indicada.
     *
     * @param segundos segundos en los que poner la reproducción.
     */
    public void situar(int segundos) {
        seek(actual, segundos);
    }

    /**
     * Devuelve la cadena con la lista de canciones, numeradas desde 0 hasta el
     * tamaño del disco.
     *
     * @return Cadea que representa la lista de discos.
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(BARRA + "\n" + nome + "\n"
                + BARRA + "\n");
        for (int i = 0; i < cancions.size(); i++) {
            sb.append("(").append(i).append(") ").append(cancions.get(i)).append("\n");
        }

        return sb.toString();
    }

    @Override
    public int compareTo(PlayList d) {
        return this.nome.compareTo(d.getNome());
    }

    /**
     * Silencia la canción actual.
     *
     * @param mute Pone a mute o no.
     */
    @Override
    public void setMute(boolean mute) {
        if (cancions != null) {
            Reproducible cancion = getPistaActual();
            if (cancion instanceof MediaSong) {
                ((MediaSong) cancion).setMute(mute);
            }
        }
    }

    /**
     * Asigna un volumen a la canción actual.
     *
     * @param v valor entre 0 y 1 del volumen
     */
    @Override
    public void setVolume(double v) {
        if (cancions != null) {
            Reproducible cancion = getPistaActual();
            if (cancion instanceof MediaSong) {
                ((MediaSong) cancion).setVolume(v);
            }
        }
    }

    /**
     * Añade la vista a la lista de observadores a informar
     *
     * @param v vista a añadir
     */
    @Override
    public void addViewObserver(PlayListObserver v) {
        if (viewObservers != null && !viewObservers.contains(v)) {
            viewObservers.add(v);
        }
    }

    /**
     * Elimina, si existe, de las lista de observadores (vistas) la que recoge
     * como argumento.
     *
     * @param v
     */
    @Override
    public void removeViewObserver(PlayListObserver v) {
        if (viewObservers != null) {
            viewObservers.remove(v);
        }
    }

    /**
     * Notifica a las vistas del cambio de posición o pista.
     *
     * @param segundos nueva posición del media/vídeo
     */
    public void notificarPosicionVistas(int segundos) {
        viewObservers.forEach(vista -> {
            vista.actualizarTiempo(segundos);
        });

    }

    /**
     * Notifica a las vistas del cambio de duración de la pieza.
     *
     * @param segundos nueva duración del media/vídeo
     */
    public void notificarDuracionVistas(int segundos) {
        viewObservers.forEach(vista -> {
            vista.actualizarDuracion(segundos);
            System.out.println("Actualizando duración vista = " + segundos);
        });
    }

    /**
     * Devuelve la posición actual en segundos.
     *
     * @return
     */
    @Override
    public int getPosition() {
        Reproducible pista = getPistaActual();
        return (pista instanceof MediaSong)
                ? ((MediaSong) pista).getPosition() : 0;
    }

//    /**
//     * Devuelve la posición de la pista en Milisegundos como double.
//     *
//     * @return número de milisegundos.
//     */
//    @Override
//    public double getPositionInMills() {
//        Reproducible pista = getPistaActual();
//        return (pista instanceof MediaSong)
//                ? ((MediaSong) pista).getPositionInMills() : 0;
//    }

    /**
     * Pausa la canción actual
     */
    @Override
    public void pause() {
        Reproducible pista = getPistaActual();
        if (pista != null) {
            pista.pause();
        }
    }

    @Override
    public int getDuracion() {
        Reproducible pista = getPistaActual();
        return (pista instanceof MediaSong)
                ? ((MediaSong) pista).getDuracion() : 0;
    }

    @Override
    public void seek(int segundos) {
        Reproducible pista = getPistaActual();
        if (pista != null) {
            ((MediaSong) pista).seek(segundos);
        }
    }
}
