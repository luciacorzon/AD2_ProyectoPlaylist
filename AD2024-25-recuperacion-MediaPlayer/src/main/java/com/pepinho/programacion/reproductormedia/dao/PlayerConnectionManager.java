/*
 * Autor: Pepe Calo
 * Realizado con fines educativos.
 * Puede modificarlo siempre que no lo haga con fines comerciales.
 */
package com.pepinho.programacion.reproductormedia.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * @author pepecalo
 */
public class PlayerConnectionManager {

    public static final String URL = "jdbc:h2:E:\\98 - Bases de datos\\h2\\playlist"
            + ";DB_CLOSE_ON_EXIT=TRUE;DATABASE_TO_UPPER=FALSE;FILE_LOCK=NO";

    public static final String DRIVER = "org.h2.Driver";

    private static PlayerConnectionManager instance;

    private Connection conexion;


    private PlayerConnectionManager() {

    }

    public static PlayerConnectionManager getInstance() {
        if (instance == null) {
            // esperas ti e eu
            synchronized (PlayerConnectionManager.class) {
                if (instance == null) {
                    instance = new PlayerConnectionManager();
                }
            }
        }
        return instance;
    }

    public Connection getConnection() {
        try {
            if (conexion == null || conexion.isClosed()) {
                synchronized (PlayerConnectionManager.class) {
                    if (conexion == null) {
                        try {
                            Class.forName(DRIVER);
                            conexion = DriverManager.getConnection(URL);
                        } catch (ClassNotFoundException ex) {
                            System.err.println("Drivers non atopados.");
                        } catch (SQLException ex) {
                            System.err.println("Erro ó establecer a conexión: "
                                    + ex.getMessage());
                        }
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ó establecer a conexión: "
                    + e.getMessage());
        }
        return conexion;
    }


}
