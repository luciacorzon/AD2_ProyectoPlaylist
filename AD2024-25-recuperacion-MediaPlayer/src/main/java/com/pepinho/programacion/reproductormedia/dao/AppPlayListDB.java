package com.pepinho.programacion.reproductormedia.dao;

import com.pepinho.programacion.reproductormedia.files.AppPlayListFiles;
import com.pepinho.programacion.reproductormedia.model.PlayList;

import java.sql.Connection;

import static com.pepinho.programacion.reproductormedia.files.AppPlayListFiles.loadPlayList;

public class AppPlayListDB {


    public static void main(String[] args) {
        com.sun.javafx.application.PlatformImpl.startup(() -> {
        });

        /**
         * 1. Establecer la conexión con la base de datos.
         */
        PlayerConnectionManager pcm = PlayerConnectionManager.getInstance();
        Connection conexion = pcm.getConnection();

        if (conexion != null) {
            System.out.println("Conexión establecida.");
        } else {
            System.out.println("Conexión NON establecida.");
        }

//        /**
//         * 2. Consulta de las canciones de la base de datos.
//         */
//        MediaSongDAO mediaSongDAO = new MediaSongDAO(conexion);
//        mediaSongDAO.getAll().forEach(System.out::println);

        /**
         * 3. Consulta de las PlayList de la base de datos.
         */
        PlayListDAO playListDAO = new PlayListDAO(conexion);
        playListDAO.getAll().forEach(System.out::println);

//   *******************************************************
//   * 1. Cargar la PlayList desde un archivo de texto.
//   *******************************************************
        // Cargamos la PlayList desde el archivo de texto.
        PlayList playList = loadPlayList("playlist.txt");
        System.out.println(playList);

        // Guardo la PlayList en la base de datos.
//        PlayListDAO playListDAO = new PlayListDAO(conexion);
        playListDAO.save(playList);


    }
}
