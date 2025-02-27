package com.pepinho.programacion.reproductormedia.files;

import com.pepinho.programacion.reproductormedia.model.MediaSong;
import com.pepinho.programacion.reproductormedia.model.PlayList;
import com.pepinho.programacion.reproductormedia.model.Reproducible;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class AppPlayListFiles {

    /**
     * Cadena separadora de los diferentes campos del archivo de texto cuando se
     * carga el disco desde un archivo de texto en formato UTF-8.
     */
    public static final String SEPARADOR = "\\|";

    /**
     * Carácter de inicio de comentario del archivo de texto para cargar el
     * disco.
     */
    public static final char COMENTARIO = '#';

    public static void main(String[] args) {
        com.sun.javafx.application.PlatformImpl.startup(() -> {
        });

        // A) Uso del método para guardar la canción de un objeto MediaSong en un archivo destin:

        MediaSong mediaSong = new MediaSong("E:\\38 - Audios\\mp3\\03. Piano Trio in E-Flat, Op. 100.mp3",
                "Piano Trio in E-Flat, Op. 100", "Frank  Schubert");

//        MediaSong mediaSong = new MediaSong("C:\\Users\\dam2ad\\Desktop\\mp3\\03. Piano Trio in E-Flat, Op. 100.mp3", "Piano Trio in E-Flat, Op. 100", "Schubert");


        // Si queremos esperar a que cargue, podemos usar el método setOnReady
        // para que se ejecute un código cuando esté cargada la canción.
        // O simplemente podemos invocar al método saveMediaFile(mediaSong, "E:\\proba.mp3");
        if (mediaSong.getMediaPlayer() != null) {
            mediaSong.getMediaPlayer().setOnReady(
                    () -> {
                        System.out.println(mediaSong);
                        saveMediaFile(mediaSong, "E:\\proba.mp3");
//                        saveMediaFile(mediaSong, "C:\\Users\\dam2ad\\Desktop\\mp3\\copia.mp3");
                    }
            );
        } else {
            System.out.println("No se pudo cargar la canción: " + mediaSong.getArquivo());
        }

//        // O esperamos en este hilo a que cargue la canción.
//        while (mediaSong.isUnknown()) {
//            System.out.print("");
//        }
//        saveMediaFile(mediaSong, "E:\\proba.mp3");

        // B) Uso del método para guardar la canción de un objeto MediaSong en un archivo destin:
//        PlayList playList = loadPlayList("E:\\00 - Acceso a Datos\\Curso 2023-24 Acceso a datos\\examenes\\1eval\\02 bases de datos\\playlist.txt");
        PlayList playList = loadPlayList("playlist.txt");
        System.out.println(playList);

    }

    /**
     * Recoge una línea del archivo con dos o tres campos, título, nombre
     * archivo y, opcional, nombre del autor, separados por la cadena SEPARADOR,
     * y devuelve el array de dos o tres elementos. Si la línea no tiene 2 o 3
     * elementos devuelve <code>null</code>.
     *
     * @param linha línea de dos o tres campos separados por el SEPARADOR
     * @return array de dos o tres elementos: título, archivo, autor.
     * @see #SEPARADOR
     */
    private static String[] getMediaSong(String linha) {
        String[] campos = linha.split(SEPARADOR);
        return ((campos.length != 2) && (campos.length != 3)) ? null : campos;
    }


    /**
     * Recoge el nombre del archivo de texto, sólo el nombre, que debe estar en
     * el directorio de la aplicación, y lee línea a línea los datos del
     * archivo. OJO, el archivo está en formato UTF-8. Los comentarios empiezan
     * por '#' y la primera línea que no es un comentario es el título de
     * la PlayList. Siempre la primera línea. El resto son líneas con campos separados
     * por | (SEPARADOR) con el siguiente formato:
     * <code>titulo canción|nombre archivo|autor</autor>
     * El autor es opcional. DEBE COMPROBAR QUE EL ARCHIVO EXISTE. AYUDA: la
     * ruta al directorio de ejecución se puede conocer por medio de la
     * propiedad del sistema "user.dir". Por ejemplo:
     * <code>System.getProperty("user.dir")</code>.
     *
     * @param nomeArquivo, nombre del archivo, sin la ruta, en la que lee el
     *                     disco.
     */
    public static PlayList loadPlayList(String nomeArquivo) {
        PlayList playlist = new PlayList();
        String nome;
        Path p = Paths.get(System.getProperty("user.dir"), nomeArquivo);
//        Path p = Paths.get(nomeArquivo);
        if (Files.exists(p)) {
            try (var br = new BufferedReader(new FileReader(p.toFile()));) {
                String linha;
                boolean primeraLinha = true;
                while ((linha = br.readLine()) != null) {
                    if (linha.trim().charAt(0) != COMENTARIO) {
                        if (primeraLinha) {
                            nome = linha.trim();
                            playlist.setNome(nome);
                            primeraLinha = false;
                        } else {
                            String[] campos = getMediaSong(linha);
                            if (campos != null && campos.length > 1) {
                                // ael autor es opcional en el archivo de texto.
                                // (String titulo, String arquivo, String autor)
                                MediaSong mediaSong = new MediaSong(campos[0], campos[1],
                                        (campos.length > 2) ? campos[2] : null);
                                // espero a que cargue...
                                while (mediaSong.isUnknown()) {
                                    System.out.print("");
                                }
                                try {
                                    playlist.addCancion(mediaSong);
                                } catch (Exception ex) {
                                    System.err.println("Error al añadir la canción: " + ex.getMessage());
                                }
                            }
                        }
                    }
                }
            } catch (FileNotFoundException ex) {
                System.err.println("Non existe o arquivo: " + ex.getMessage());

            } catch (IOException ex) {
                System.err.println("Erro de I/O: " + ex.getMessage());
            }
        }
        return playlist;
    }

    /**
     * Guarda el audio de la canción i, si existe, en el directorio de la
     * aplicación con el nombre de archivo pasado por parámetro. Hace uso del
     * método correspondiente de MediaSong. Debe comprobar que el valor de i
     * está dentro del rango posible.
     *
     * @param i       índice de la canción
     * @param destino ruta destino y nombre del archivo en el que guardar la
     *                canción.
     */
    public static void saveCancion(PlayList playList, int i, String destino) {

        var cancions = playList.getCancions();
        if (i >= 0 && cancions != null && i < cancions.size()) {
            Reproducible cancion = cancions.get(i);
            if (cancion instanceof MediaSong) {
                saveMediaFile((MediaSong) cancion, destino);
            }
        }

    }

    /**
     * Recoge el nombre y la ruta a un archivo destino y hace una copia del
     * archivo multimedia (si existe) en dicha localización. La escritura debe
     * ser con Buffer (BufferedOutputStream), pues se trata de un archivo
     * binario.
     *
     * @param destino nombre y ruta al archivo destino.
     * @return si no se ha producido ningún error y la copia ha sido correcta.
     */
    public static boolean saveMediaFile(MediaSong cancion, String destino) {
        Path arquivo = cancion.getArquivo();
        if (arquivo == null || !Files.exists(arquivo)) {
            return false;
        }
        try (var bOrixe = new BufferedInputStream(Files.newInputStream(arquivo));
             var bDestino = new BufferedOutputStream(Files.newOutputStream(Paths.get(destino)))) {
            byte[] buffer = new byte[1024];
            int bytesLen;
            while ((bytesLen = bOrixe.read(buffer)) != -1) {
                bDestino.write(buffer, 0, bytesLen);
            }
        } catch (FileNotFoundException ex) {
            System.err.println("Archivo no encontrado: " + ex.getMessage());
            return false;
        } catch (IOException ex) {
            System.err.println("Error de E/S; " + ex.getMessage());
            return false;
        }
        return true;
    }

    public boolean saveToFile(String file) {
        File f = new File(file);
        if (f.exists()) {
            try (ObjectOutputStream oos = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(file)));) {
                oos.writeObject(this);
            } catch (IOException ex) {
                System.out.println("Erro ó escribir no arquivo: " + ex.getMessage());
            }
        }
        return false;
    }

}
