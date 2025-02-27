package com.pepinho.programacion.reproductormedia.model;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;

import javafx.util.Duration;
import javafx.scene.media.Media;
import javafx.scene.media.MediaException;
import javafx.scene.media.MediaException.Type;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaPlayer.Status;

/**
 * @author Pepinho
 */
public class MediaSong extends Cancion implements Reproducible<Cancion> {


    private byte[] audio;

    private transient Path arquivo;
    /**
     * Reproductor de la canción. Cada canción debe tener un MediaPlayer y sólo
     * uno. Tampoco puede cambiarse una vez creado, por eso se pone como
     * atributo de instancia. Se emplea para reproducir audio MP3.
     */
    private transient MediaPlayer mediaPlayer;

    /**
     * Guarda el texto con los mensajes, errores, etc. del objeto.
     */
    private transient final StringBuilder rexistro = new StringBuilder();

    /* ************************************************************
     *                        CONSTRUCTORES
     *************************************************************/

    public MediaSong() {
        super();
    }

    /**
     * Crea la MediaSong con los datos pasados como parámetros.
     * @param idCancion identificador de la canción.
     * @param titulo título de la canción.
     * @param autor     autor de la canción.
     * @param duracion  duración de la canción en segundos.
     * @param dataPublicacion   fecha de publicación de la canción.
     * @param audio    archivo de audio.
     */
    public MediaSong(long idCancion, String titulo, String autor, int duracion, LocalDate dataPublicacion,
                     byte[] audio) {
        super(idCancion, titulo, autor, duracion, dataPublicacion);
        this.audio = audio;
        // Crea el archivo temporal con los bytes de la canción.
        createPathFromBytes();
    }

    /**
     * Constructor que recoge el archivo. Asigna el título igual al nombre del
     * archivo. El archivo debe ser MP3.
     *
     * @param archivo archivo que apunta a la canción Multimedia.
     * @see #setMediaPlayer()
     * @see #Cancion#DEFAULT_TITLE
     */
    public MediaSong(String archivo) {
        // Si el archivo es nulo le pasa el título por defecto.
        super(DEFAULT_TITLE);
        if (archivo != null) {
            this.arquivo = Paths.get(archivo);
            try {
                setBytes(Files.readAllBytes(this.arquivo));
            } catch (IOException e) {
                System.err.println("Error al leer el archivo: " + e.getMessage());
            }
            setMediaPlayer();
        }
        setDataPublicacion(LocalDate.now());
    }

    /**
     * Constructor que recoge el archivo y el título. El archivo debe ser MP3.
     * @param archivo nombre y ruta al archivo que apunta a la canción Multimedia.
     * @param titulo  título de la canción, si es nulo le asigna el valor de DEFAULT_TITLE
     * @see #setMediaPlayer()
     * @see #Cancion#DEFAULT_TITLE
     */
    public MediaSong(String archivo, String titulo) {
        super((titulo != null) ? titulo : DEFAULT_TITLE);
        if (archivo != null) {
            this.arquivo = Paths.get(archivo);
            try {
                // Asignamos los bytes del archivo al atributo audio.
                setBytes(Files.readAllBytes(this.arquivo));
            } catch (IOException e) {
                System.err.println("Error al leer el archivo: " + e.getMessage());
            }
            setMediaPlayer();
        }
        setDataPublicacion(LocalDate.now());
    }

    public MediaSong(String arquivo, String titulo, String autor) {
        super((titulo != null) ? titulo : DEFAULT_TITLE);
        if (arquivo != null) {
            this.arquivo = Paths.get(arquivo);
            setMediaPlayer();
            try {
                // Asignamos los bytes del archivo al atributo audio.
                setBytes(Files.readAllBytes(this.arquivo));
            } catch (IOException e) {
                System.err.println("Error al leer el archivo: " + e.getMessage());
            }
        }
        this.setAutor(autor);
        setDataPublicacion(LocalDate.now());
    }

    /**
     * Crea un archivo temporal a partir de los bytes de la canción.
     * Hay que tener en cuenta que el MediaPlayer sólo puede reproducir archivos, que pueden ser URL. No
     * archivos en memoria.
     * El archivo temporal se borra automáticamente al salir.
     * @return si lo ha podido crear.
     */
    public boolean createPathFromBytes() {
        try {
            arquivo = Files.createTempFile("tmpmedia", ".mp3");
            arquivo.toFile().deleteOnExit();
            try(var fos = new BufferedOutputStream(Files.newOutputStream(arquivo))){
                fos.write(getBytes());
            }
            return true;
        } catch (IOException ex) {
            System.out.println("Erro ó crear o archivo temporal: " + ex.getMessage());
        }
        return false;
    }


    /**
     * Crea el MediaPlayer a partir del archivo. El objeto
     * javafx.scene.media.MediaPlayer viene en la biblioteca de javafx para
     * poder reproducir objetos de tipo Media. Devuelve false si se ha producido
     * alguna excepción durante la creación del objeto MediaPlayer.
     *
     * @return si lo ha podido crear correctamente.
     * @see <a href="https://openjfx.io/javadoc/19/javafx.media/javafx/scene/media/MediaPlayer.html>javafx.scene.media.MediaPlayer</a>
     * @see <a href="https://openjfx.io/javadoc/19/javafx.media/javafx/scene/media/Media.html>javafx.scene.media.Media</a>
     */
    private boolean setMediaPlayer() {
        if (arquivo != null && Files.exists(arquivo)) {
            try {
                Media media = new Media(arquivo.toUri().toString());
                mediaPlayer = new MediaPlayer(media);
                /* Cuando esté preparada (cargado el audio) asignamos
                    la DURACIÓN del objeto MP3. Pues sólo en ese momento es
                    posible conocer la duración del archivo MP3.
                    Lo hace por medio de un hilo de ejecución mientras
                    sigue ejecutando el código.
                    Cuano esté "Ready" ejecuta el código entre llaves.
                    Podríamos consultar más metadatos contenidos en el archivo.
                 */
                mediaPlayer.setOnReady(() -> {
                    setDuracion((int) media.getDuration().toSeconds());
                    if (media.getMetadata().containsKey("title")) {
                        setTitulo(media.getMetadata().get("title").toString());
                    }
                    if (media.getMetadata().containsKey("artist")) {
                        setAutor(media.getMetadata().get("artist").toString());
                    }
/*
                    //                   this.toString();
//                        // Metadatos: para consultar los datos del audio.
//                        media.getMetadata().entrySet().forEach(entry -> {
//                            System.out.println(entry.getKey() + ": " +
//                                    entry.getValue());
//                        });*/
                });
                return true;
            } catch (IllegalArgumentException e) {
                System.err.println("A URL non é axeitada ou ten un esquema nulo: " + e.getMessage());
            } catch (UnsupportedOperationException e) {
                System.err.println("Protocolo NON ADMITIDO: " + e.getMessage());
            } catch (MediaException e) {
                System.err.println("Non é posible conectarse: " +
                        ((e.getType() == MediaException.Type.MEDIA_INACCESSIBLE) ? "INACCESIBLE" : "NON ADMITIDO"));
            }
        }
        return false;
    }

    /**
     * Devuelve la ruta completa al archivo o null si no existe.
     *
     * @return ruta al archivo, pero sólo si existe.
     */
    public String getArquivoAsString() {
        return (Files.exists(arquivo)) ? arquivo.toString() : null;
    }

    public Path getArquivo() {
        return arquivo;
    }

    public Path getPath() {
        return arquivo;
    }

    public void setArquivo(Path archivo) {
        if (archivo != null) {
            this.arquivo = archivo;
            try {
                // Asignamos los bytes del archivo al atributo audio.
                setBytes(Files.readAllBytes(this.arquivo));
            } catch (IOException e) {
                System.err.println("Error al leer el archivo: " + e.getMessage());
            }
            setMediaPlayer();
        }
    }

    /**
     * Recoge el nombre del archivo y crea el objeto de tipo File al archivo
     * pasado como como parámetro. Además, llama al método setMediaPlayer() para
     * que cree el MediaPlayer a ese archivo.
     *
     * @param arquivo archivo de audio, normalmente MP3.
     */
    public void setArquivo(String arquivo) {
        if (arquivo != null) {
            this.arquivo = Paths.get(arquivo);
            try {
                // Asignamos los bytes del archivo al atributo audio.
                setBytes(Files.readAllBytes(this.arquivo));
            } catch (IOException e) {
                System.err.println("Error al leer el archivo: " + e.getMessage());
            }
            setMediaPlayer();
        }
    }

    public byte[] getBytes() {
        return audio;
    }

    public MediaSong setBytes(byte[] audio) {
        this.audio = audio;
        // Crea el archivo temporal con los bytes de la canción.
        createPathFromBytes();
        return this;
    }

    public byte[] getAudio() {
        return audio;
    }

    public MediaSong setAudio(byte[] audio) {
        this.audio = audio;
        // Crea el archivo temporal con los bytes de la canción.
        createPathFromBytes();
        return this;
    }

    /* *************************************************************
     * Métodos para obtener y modificar el estado de reproducción.
     ************************************************************** */

    /**
     * Devuelve la duración en segundos del Media dentro del mediaPlayer. Hace
     * uso del método getDuration() definido con posterioridad. Es necesario
     * convertir el double que devuelve toSeconds() a enterio.
     *
     * @return número de segundos del archivo multimedia (MP3 y admitidos)
     */
    @Override
    public int getDuracion() {
        return (int) getDuration().toSeconds();
    }

    /**
     * Devuelve un objeto con la duración del tipo javafx.util.Duration. Hace
     * uso del método getMedia() de MediaPlayer y getDuration() de Media.
     *
     * @return duración en formato javafx.util.Duration. Duration.ZERO si no
     * existe está creado el mediaPlayer.
     * @see <a href="https://openjfx.io/javadoc/15/javafx.base/javafx/util/Duration.html">javafx.util.Duration</a>
     */
    public Duration getDuration() {
        return (mediaPlayer != null) ? mediaPlayer.getMedia().getDuration() : Duration.ZERO;
    }

    /**
     * Asigna un valor (entre 0 y 1) al volumen de la canción.
     *
     * @param valor valor entre 0 y 1 del volumen.
     * @return si mediaPlayer es nulo o no.
     * @see <a href="https://openjfx.io/javadoc/15/javafx.media/javafx/scene/media/MediaPlayer.html#setVolume(double)">setVolume(double)</a>
     */
    public boolean setVolume(double valor) {
        if (mediaPlayer != null) {
            mediaPlayer.setVolume(valor);
        }
        return (mediaPlayer != null);
    }

    /**
     * Devuelve el volumen con un valor entre 0 y 1. Consulta getVolume() de
     * MediaPlayer. Si el atributo mediaPlayer es null debe devolver 0.
     *
     * @return nivel de volumen entre 0 y 1.
     */
    public double getVolume() {
        return (mediaPlayer != null) ? mediaPlayer.getVolume() : 0;
    }

    /**
     * Devuelve el estado del audio.
     * Descripción del audio que se corresponde con el status de MediaPlayer.
     *
     * @return descripción del audio.
     */
    public Status getStatus() {
        return (mediaPlayer != null) ? mediaPlayer.getStatus() : Status.UNKNOWN;
    }

    /**
     * Devuelve la posición de la ejecución.
     *
     * @return número de Milisegundos.
     */
    public long getPositionInMills() {
        return (mediaPlayer != null) ? (long) mediaPlayer.getCurrentTime().toMillis() : 0;
    }

    /**
     * Devuelve la posición de la ejecución.
     *
     * @return número de segundos.
     */
    public int getPosition() {
        return (mediaPlayer != null) ? (int) mediaPlayer.getCurrentTime().toSeconds() : 0;
    }

    /**
     * Silencia el audio del archivo multimedia o no dependiendo del valor
     * <code>mute</code> pasado como argumento.
     *
     * @param mute silencia el audio (true) o no (false)
     * @return si el mediaPlayer es nulo.
     */
    public boolean setMute(boolean mute) {
        if (mediaPlayer != null) {
            mediaPlayer.setMute(mute);
        }
        return (mediaPlayer != null);
    }

    /**
     * Devuelve si el archivo multimedia está silenciado o no.
     *
     * @return si no es nulo y está silenciado.
     */
    public boolean isMute() {
        return (mediaPlayer != null && mediaPlayer.isMute());
    }

    /**
     * Si el mediaPlayer no es nulo lo hace sonar con el método play(). Puede
     * sonar si está preparada, pausada o parada.
     *
     * @return si no es nula está preparada, pausada o parada.
     */
    @Override
    public boolean play() {
        boolean iCanPlay = false;
        if (mediaPlayer != null && (isReady() || isPaused() || isStopped())) {
            mediaPlayer.play();
            iCanPlay = true;
        }
        return iCanPlay;
    }

    /**
     * Si el mediaPlayer no es nulo y esta pausada, sonando o esperando, ejecuta
     * al método stop() para parar la canción.
     *
     * @return Si mediaPlayer es nulo o no, ejecutándose, parada o esperando.
     */
    @Override
    public boolean stop() {
        boolean iCanStop = false;
        if (mediaPlayer != null && (isPlaying() || isPaused() || isStalled())) {
            mediaPlayer.stop();
            iCanStop = true;
        }
        return iCanStop;
    }

    /**
     * Sitúa (Seeks) la reproducción en un nueva posición. La invocación de este
     * método no tiene efecto si el estado del mediaPlayer es
     * MediaPlayer.Status.STOPPED o la duración es Duration.INDEFINITE.
     *
     * @param segundos Duración en segundos en la que poner la reproducción.
     * @return si el mediaPlayer no es nulo
     */
    public boolean seek(int segundos) {
        if (mediaPlayer != null) {
            // millis recoge milisegundos.
            mediaPlayer.seek(Duration.millis(segundos * 1000));
        }
        return (mediaPlayer != null);
    }

    /**
     * Si el mediaPlayer no es nulo y esta sonando, parada o esperando, ejecuta
     * al método pause() para parar la canción.
     *
     * @return Si mediaPlayer no es nulo, está sonando, parada o esperando.
     */
    @Override
    public boolean pause() {
        boolean iCanPause = false;
        if (mediaPlayer != null && (isPlaying() || isStopped() || isStalled())) {
            mediaPlayer.pause();
            iCanPause = true;
        }
        return iCanPause;
    }

    /**
     * @return Registro de mensajes
     */
    public String getRexistro() {
        return rexistro.toString();
    }

    /**
     * Devuelve el MediaPlayer
     *
     * @return MediaPlayer de la canción.
     */
    public MediaPlayer getMediaPlayer() {
        return mediaPlayer;
    }

    /* *********************************************************************
    
    Los siguientes métodos permiten conocer el ESTADO del archivo multimedia
    
    ********************************************************************* */

    /**
     * Si se está precargando el audio. No puede hacerse nada hasta que lo
     * cargue.
     *
     * @return si se está precargando.
     * @see <a href="https://openjfx.io/javadoc/15/javafx.media/javafx/scene/media/MediaPlayer.Status.html">javafx.scene.media.MediaPlayer.Status</a>
     */
    public boolean isUnknown() {
        return mediaPlayer != null && mediaPlayer.getStatus().equals(Status.UNKNOWN);
    }

    /**
     * Si se está preparada. Puede llamarse a play() (o autoplay)
     *
     * @return si se está preparada.
     */
    public boolean isReady() {
        return mediaPlayer != null && mediaPlayer.getStatus().equals(Status.READY);
    }

    /**
     * Si se esta sonando. Puede llamarse a "pause()" o "stop()".
     *
     * @return si se está ejecutando.
     */
    public boolean isPlaying() {
        return mediaPlayer != null && mediaPlayer.getStatus().equals(Status.PLAYING);
    }

    /**
     * Si se está pausada. Puede llamarse a play() o stop().
     *
     * @return si se está pausada.
     */
    public boolean isPaused() {
        return mediaPlayer != null && mediaPlayer.getStatus().equals(Status.PAUSED);
    }

    /**
     * Si se está parada. Puede llamarse a pause() o play().
     *
     * @return si se está parada.
     */
    public boolean isStopped() {
        return mediaPlayer != null && mediaPlayer.getStatus().equals(Status.STOPPED);
    }

    /**
     * Si se está estancada (cargando datos al buffer). Puede llamarse a pause()
     * o stop().
     *
     * @return si se está estancada.
     */
    public boolean isStalled() {
        return mediaPlayer != null && mediaPlayer.getStatus().equals(Status.STALLED);
    }

    /**
     * Si se ha liberado el recurso Media por medio de la invocación del método
     * dispose(). NO SE PUEDE USAR. Los recursos Media y MediaView pueden volver
     * a ser empleados.
     *
     * @return si ha sido liberados los recursos.
     */
    public boolean isDisposed() {
        return mediaPlayer != null && mediaPlayer.getStatus().equals(Status.DISPOSED);
    }

    /**
     * Debe devolver el si está sonando la canción. Para saberlo hay que obtener
     * el estado del mediaPlayer con getStatus() y compararlo (equals)
     * Status.PLAYING. Es un alias de isPlaying
     *
     * @return si está sonando si el Status es "Status.PLAYING".
     * @see #isPlaying()
     */
    public boolean isSoando() {
        return isPlaying();
    }

    /**
     * Llama al toString de Canción. Además, si el archivo existe debe aparecer
     * el nombre del archivo entre corchetes. Si no existe el archivo debe poner
     * un [*] Opcional: Si está sonando debe poner un [*] al principio
     *
     * @return String que representa la canción y el archivo.
     * @see Cancion#toString()
     */
    @Override
    public String toString() {
        return ((isPlaying()) ? "[*]" : "") + super.toString() + ((arquivo != null && Files.exists(arquivo)) ? " [" + arquivo.getFileName().toString() + "]" : " [*]");
    }



}
