package com.pepinho.programacion.reproductormedia.gson;

import com.pepinho.programacion.reproductormedia.model.MediaSong;

public class AppPlayListGson {

    public static void main(String[] args) {
        com.sun.javafx.application.PlatformImpl.startup(() -> {
        });

        // A) Uso del método para guardar la canción de un objeto MediaSong en un archivo destin:

        MediaSong mediaSong = new MediaSong("E:\\38 - Audios\\mp3\\03. Piano Trio in E-Flat, Op. 100.mp3",
                "Piano Trio in E-Flat, Op. 100", "Frank  Schubert");
    }
}
