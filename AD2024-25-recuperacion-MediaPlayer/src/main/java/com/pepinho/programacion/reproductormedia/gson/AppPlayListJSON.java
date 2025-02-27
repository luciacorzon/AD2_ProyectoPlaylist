package com.pepinho.programacion.reproductormedia.gson;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.pepinho.programacion.reproductormedia.model.MediaSong;
import com.pepinho.programacion.reproductormedia.model.PlayList;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static com.pepinho.programacion.reproductormedia.files.AppPlayListFiles.loadPlayList;

public class AppPlayListJSON {


    public static final String DIRECTORIO = "E:\\98 - Bases de datos\\json\\";

    public static void main(String[] args) {
        com.sun.javafx.application.PlatformImpl.startup(() -> {
        });

        // Prueba de escritura de un objeto MediaSong en un archivo JSON:
        MediaSong mediaSong = new MediaSong("E:\\38 - Audios\\mp3\\03. Piano Trio in E-Flat, Op. 100.mp3",
                "Piano Trio in E-Flat, Op. 100", "Frank  Schubert");

        Gson gson = new GsonBuilder().serializeNulls()
                .registerTypeAdapter(PlayList.class, new PlayListSerializer())
                .registerTypeAdapter(MediaSong.class, new MediaSongTypeAdapter(Path.of(DIRECTORIO)))
                .setPrettyPrinting().create();

        // Serialización de un objeto MediaSong a JSON:
        try (var file = Files.newBufferedWriter(Path.of(DIRECTORIO, mediaSong.getTitulo() + ".json"))) {
            gson.toJson(mediaSong, file);
        } catch (IOException e) {
            System.err.println("Error al escribir el archivo JSON: " + e.getMessage());
        }

        // Prueba de escritura de un objeto PlayList en un archivo JSON:
        PlayList playList = loadPlayList("playlist.txt");

        // Serialización de un objeto PlayList a JSON:
        try (var file = Files.newBufferedWriter(Path.of(DIRECTORIO, playList.getNome() + ".json"))) {
            gson.toJson(playList, file);
        } catch (IOException e) {
            System.err.println("Error al escribir el archivo JSON: " + e.getMessage());
        }
    }
}
