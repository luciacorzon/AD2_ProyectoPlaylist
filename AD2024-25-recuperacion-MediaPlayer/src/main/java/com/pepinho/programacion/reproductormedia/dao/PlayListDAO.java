package com.pepinho.programacion.reproductormedia.dao;

import com.pepinho.programacion.reproductormedia.model.MediaSong;
import com.pepinho.programacion.reproductormedia.model.PlayList;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * -- PUBLIC.PlayList definition
 * <p>
 * -- Drop table
 * <p>
 * -- DROP TABLE PUBLIC.PlayList;
 * <p>
 * CREATE TABLE PUBLIC.PlayList (
 * idPlayList INTEGER NOT NULL AUTO_INCREMENT,
 * nome CHARACTER VARYING(255) NOT NULL,
 * dataCreacion DATE,
 * CONSTRAINT idPlayList_PK PRIMARY KEY (idPlayList)
 * );
 * CREATE UNIQUE INDEX PRIMARY_KEY_7 ON PUBLIC.PlayList (idPlayList);
 */
public class PlayListDAO implements PlayerDAO<Long, PlayList> {
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

    /**
     * Conexión a la base de datos.
     */
    private Connection con;

    /**
     * Constructor del objeto DAO de Canción. Recoge la conexión a la base de datos.
     *
     * @param con conexión a la base de datos.
     */
    public PlayListDAO(Connection con) {
        this.con = con;
    }


    @Override
    public PlayList get(Long idPlayList) {
        try (PreparedStatement ps = con.prepareStatement("SELECT idPlayList, nome, dataCreacion " +
                "FROM PlayList WHERE idPlayList = ?")) {
            ps.setLong(1, idPlayList);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                MediaSongDAO mediaSongDAO = new MediaSongDAO(con);
                PlayList pl = new PlayList(rs.getLong(1), rs.getString(2),
                        rs.getDate(3).toLocalDate());
                pl.setCancions(mediaSongDAO.getAllFromID(idPlayList));
            }
        } catch (Exception e) {
            System.err.println("Error al obtener la canción de la base de datos: " + e.getMessage());
        }
        return null;
    }

    @Override
    public List<PlayList> getAll() {
        List<PlayList> playLists = new ArrayList<>();
        try (PreparedStatement ps = con.prepareStatement("SELECT idPlayList, nome, dataCreacion " +
                "FROM PlayList")) {
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                long idPlayList = rs.getLong(1);
                String nome = rs.getString(2);
                Date dataCreacion = rs.getDate(3);
                System.out.println("dataCreacion = " + dataCreacion);

                MediaSongDAO mediaSongDAO = new MediaSongDAO(con);
                PlayList pl = new PlayList(idPlayList, nome,
                        (dataCreacion!=null) ? dataCreacion.toLocalDate() : null);
                pl.setCancions(mediaSongDAO.getAllFromID(idPlayList));
                playLists.add(pl);
            }
        } catch (Exception e) {
            System.err.println("Error al obtener las canciones de la base de datos: " + e.getMessage());
        }
        return playLists;
    }

    @Override
    public List<PlayList> getAllFromID(Long idCancion) {
        try (PreparedStatement ps = con.prepareStatement("SELECT P.idPlayList, P.nome, P.dataCreacion " +
                "FROM MediaSongPlayList MP INNER JOIN PlayList P WHERE" +
                " P.idPlayList = MP.idPlayList AND MP.idCancion = ?")) {
            ps.setLong(1, idCancion);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                MediaSongDAO mediaSongDAO = new MediaSongDAO(con);
                PlayList pl = new PlayList(rs.getLong(1), rs.getString(2),
                        (rs.getDate(3) != null) ? rs.getDate(3).toLocalDate() : null);
                pl.setCancions(mediaSongDAO.getAllFromID(idCancion));
            }
        } catch (Exception e) {
            System.err.println("Error al obtener las playList de la base de datos con la canción con " +
                    "idCanción: " + idCancion + System.lineSeparator() + e.getMessage());
        }
        return null;
    }

    @Override
    public Long save(PlayList playList) {
        try {
            con.setAutoCommit(false);

            try (PreparedStatement ps = con.prepareStatement("INSERT INTO PlayList (nome, dataCreacion) " +
                    "VALUES (?, ?)", PreparedStatement.RETURN_GENERATED_KEYS);
                 PreparedStatement ps2 = con.prepareStatement("INSERT INTO MediaSongPlayList (idCancion, idPlayList) " +
                         "VALUES (?, ?)")) {
                ps.setString(1, playList.getNome());
                ps.setDate(2, java.sql.Date.valueOf(playList.getDataCreacion()));
                ps.executeUpdate();
                // Obtengo la clave generada de la playList:
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    playList.setIdPlayList(rs.getLong(1));
                }
                // Inserto las canciones de la playList:
                MediaSongDAO mediaSongDAO = new MediaSongDAO(con);
                for (int i = 0; i < playList.getCancions().size(); i++) {
                    MediaSong mediaSong = (MediaSong) playList.getCancions().get(i);
                    mediaSongDAO.save(mediaSong);
                    ps2.setLong(1, mediaSong.getIdCancion());
                    ps2.setLong(2, playList.getIdPlayList());
                    ps2.executeUpdate();
                }
                con.commit();
                return playList.getIdPlayList();
            } catch (Exception e) {
                System.err.println("Error al insertar la playList en la base de datos: " + e.getMessage());
            }

        } catch (SQLException e) {
            try {
                con.rollback();
            } catch (SQLException ex) {
                System.err.println("Error al hacer rollback: " + ex.getMessage());
            }
            try {
                con.setAutoCommit(true);
            } catch (SQLException ex) {
                System.err.println("Error al hacer setAutoCommit(true): " + ex.getMessage());
            }
        }
        return null;
    }

    @Override
    public void update(PlayList playList) {

    }

    @Override
    public void delete(PlayList playList) {

    }

    @Override
    public boolean deleteById(Long id) {
        return false;
    }

    @Override
    public List<Integer> getAllIds() {
        return null;
    }

    @Override
    public void updateLOB(PlayList book, String f) {

    }

    @Override
    public void updateLOBById(Long id, String f) {

    }

    @Override
    public void deleteAll() {

    }
}
