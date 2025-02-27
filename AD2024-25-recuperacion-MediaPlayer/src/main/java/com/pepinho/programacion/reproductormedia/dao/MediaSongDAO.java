package com.pepinho.programacion.reproductormedia.dao;


import com.pepinho.programacion.reproductormedia.model.MediaSong;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * CREATE TABLE PUBLIC.Cancion (
 * idCancion INTEGER NOT NULL AUTO_INCREMENT,
 * titulo CHARACTER VARYING(255) NOT NULL,
 * autor CHARACTER VARYING(255),
 * duracion INTEGER,
 * dataPublicacion DATE,
 * audio BINARY LARGE OBJECT,
 * CONSTRAINT idCancion_PK PRIMARY KEY (idCancion)
 * );
 * CREATE INDEX Cancion_titulo_IDX ON PUBLIC.Cancion (titulo);
 * CREATE UNIQUE INDEX PRIMARY_KEY_8 ON PUBLIC.Cancion (idCancion);
 */

public class MediaSongDAO implements PlayerDAO<Long, MediaSong> {

    /**
     * Conexión a la base de datos.
     */
    private Connection con;

    /**
     * Constructor del objeto DAO de Canción. Recoge la conexión a la base de datos.
     *
     * @param con conexión a la base de datos.
     */
    public MediaSongDAO(Connection con) {
        this.con = con;
    }

    /**
     * Obtiene la Canción a partir del idCancion.
     *
     * @param idCancion identificador de la canción.
     * @return objeto de tipo Cancion o null si no existe.
     */
    @Override
    public MediaSong get(Long idCancion) {
        try (PreparedStatement ps = con.prepareStatement("SELECT idCancion, titulo, autor, duracion, " +
                "dataPublicacion, audio FROM MediaSong WHERE idCancion = ?")) {
            ps.setLong(1, idCancion);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                long id = rs.getLong("idCancion");
                String titulo = rs.getString("titulo");
                String autor = rs.getString("autor");
                int duracion = rs.getInt("duracion");
                java.time.LocalDate dataPublicacion = (rs.getDate("dataPublicacion") != null) ? rs.getDate("dataPublicacion").toLocalDate() : null;
                byte[] audio = rs.getBytes("audio");
                return new MediaSong(id, titulo, autor, duracion, dataPublicacion, audio);
//                return new MediaSong(rs.getInt("idCancion"), rs.getString("titulo"),
//                        rs.getString("autor"), rs.getInt("duracion"),
//                        rs.getDate("dataPublicacion").toLocalDate(), rs.getBytes("audio"));
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener la canción de la base de datos: " + e.getMessage());
        }
        return null;
    }

    @Override
    public List<MediaSong> getAll() {
        List<MediaSong> canciones = new ArrayList<>();
        try (PreparedStatement ps = con.prepareStatement("SELECT idCancion, titulo, autor, duracion, dataPublicacion," +
                " audio FROM MediaSong")) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
//                int idCancion = rs.getInt("idCancion");
//                String titulo = rs.getString("titulo");
//                String autor = rs.getString("autor");
//                int duracion = rs.getInt("duracion");
//                java.time.LocalDate dataPublicacion = (rs.getDate("dataPublicacion")!=null) ? rs.getDate("dataPublicacion").toLocalDate() : null;
//                byte[] audio = rs.getBytes("audio");
//                canciones.add(new MediaSong(idCancion, titulo, autor, duracion, dataPublicacion, audio));

                canciones.add(new MediaSong(rs.getInt("idCancion"), rs.getString("titulo"),
                        rs.getString("autor"), rs.getInt("duracion"),
                        (rs.getDate("dataPublicacion") != null) ? rs.getDate("dataPublicacion").toLocalDate() : null,
                        rs.getBytes("audio")));

            }
        } catch (SQLException e) {
            System.err.println("Error al obtener todas las canciones: " + e.getMessage());
        }

        return canciones;
    }

    @Override
    public List<MediaSong> getAllFromID(Long idPlayList) {
        List<MediaSong> canciones = new ArrayList<>();
        try (PreparedStatement ps = con.prepareStatement("SELECT C.idCancion, C.titulo, C.autor, C.duracion, " +
                "C.dataPublicacion, C.audio FROM MediaSongPlayList CP INNER JOIN MediaSong C WHERE" +
                " C.idCancion = CP.idCancion AND CP.idPlayList = ?")) {
            ps.setLong(1, idPlayList);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                canciones.add(new MediaSong(rs.getInt(1), rs.getString(2),
                        rs.getString(3), rs.getInt(4),
                        (rs.getDate(5) != null) ? rs.getDate(5).toLocalDate() : null, rs.getBytes(6)));
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener todas las canciones del idPlayList: " + idPlayList
                    + System.lineSeparator() + e.getMessage());
        }

        return canciones;
    }

    @Override
    public Long save(MediaSong cancion) {
        try (PreparedStatement ps = con.prepareStatement("INSERT INTO MediaSong (titulo, autor, duracion, " +
                "dataPublicacion, audio) VALUES (?, ?, ?, ?, ?)", PreparedStatement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, cancion.getTitulo());
            ps.setString(2, cancion.getAutor());
            ps.setInt(3, cancion.getDuracion());
            if (cancion.getDataPublicacion() != null) {
                ps.setDate(4, java.sql.Date.valueOf(cancion.getDataPublicacion()));
            } else {
                ps.setNull(4, java.sql.Types.DATE);
            }
            if (cancion.getBytes() != null) {
                ps.setBytes(5, cancion.getBytes());
            } else {
                ps.setNull(5, java.sql.Types.BLOB);
            }
            int insertadas = ps.executeUpdate();
            if (insertadas != 0) {
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    cancion.setIdCancion(rs.getLong(1));
                }
                System.out.println("Canción correctamente insertada");
                return cancion.getIdCancion();
            }
        } catch (SQLException e) {
            System.err.println("Error al insertar la canción: " + e.getMessage());
        }
        return null;
    }

    @Override
    public void update(MediaSong cancion) {
        try (PreparedStatement ps = con.prepareStatement("UPDATE MediaSong SET titulo = ?, autor = ?, duracion = ?, " +
                "dataPublicacion = ?, audio = ? WHERE idCancion = ?")) {
            ps.setString(1, cancion.getTitulo());
            ps.setString(2, cancion.getAutor());
            ps.setInt(3, cancion.getDuracion());
            ps.setDate(4, java.sql.Date.valueOf(cancion.getDataPublicacion()));
            ps.setBytes(5, cancion.getBytes());
            ps.setLong(6, cancion.getIdCancion());
            int actualizadas = ps.executeUpdate();
            if (actualizadas != 0) {
                System.out.println("Canción correctamente actualizada");
            } else {
                System.err.println("Canción no actualizada");
            }
        } catch (SQLException e) {
            System.err.println("Error al actualizar la canción: " + e.getMessage());
        }
    }

    @Override
    public void delete(MediaSong cancion) {
        try (PreparedStatement ps = con.prepareStatement("DELETE FROM MediaSong WHERE idCancion = ?")) {
            ps.setLong(1, cancion.getIdCancion());
            int borradas = ps.executeUpdate();
            if (borradas != 0) {
                System.out.println("Canción correctamente borrada");
            } else {
                System.err.println("Canción no borrada");
            }
        } catch (SQLException e) {
            System.err.println("Error al borrar la canción: " + e.getMessage());
        }
    }

    @Override
    public boolean deleteById(Long idCancion) {
        try (PreparedStatement ps = con.prepareStatement("DELETE FROM MediaSong WHERE idCancion = ?")) {
            ps.setLong(1, idCancion);
            int borradas = ps.executeUpdate();
            if (borradas != 0) {
                System.out.println("Canción correctamente borrada");
                return true;
            } else {
                System.err.println("Canción no borrada");
            }
        } catch (SQLException e) {
            System.err.println("Error al borrar la canción: " + e.getMessage());
        }
        return false;
    }

    @Override
    public List<Integer> getAllIds() {
        List<Integer> ids = new ArrayList<>();
        try (PreparedStatement ps = con.prepareStatement("SELECT idCancion FROM MediaSong")) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                ids.add(rs.getInt(0));
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener todos los ids de las canciones: " + e.getMessage());
        }
        return ids;
    }

    @Override
    public void updateLOB(MediaSong cancion, String file) {
        if (cancion != null) {
            updateLOBById(cancion.getIdCancion(), file);
        }

    }

    @Override
    public void updateLOBById(Long idCancion, String file) {
        try (PreparedStatement ps = con.prepareStatement("UPDATE MediaSong SET audio = ? WHERE idCancion = ?")) {
            ps.setBytes(1, Files.readAllBytes(Paths.get(file)));
            ps.setLong(2, idCancion);
            int actualizadas = ps.executeUpdate();
            if (actualizadas != 0) {
                System.err.println("Canción correctamente actualizada");
            } else {
                System.err.println("Canción no actualizada");
            }
        } catch (SQLException e) {
            System.err.println("Error al actualizar la canción: " + e.getMessage());
        } catch (IOException e) {
            System.err.println("Error al leer el archivo: " + e.getMessage());
        }
    }

    @Override
    public void deleteAll() {
        try (PreparedStatement ps = con.prepareStatement("DELETE FROM MediaSong")) {
            int borradas = ps.executeUpdate();
            if (borradas != 0) {
                System.err.println("Canciones correctamente borradas");
            } else {
                System.err.println("Canciones no borradas");
            }
        } catch (SQLException e) {
            System.err.println("Error al borrar las canciones: " + e.getMessage());
        }
    }
}
