
package com.pepinho.programacion.reproductormedia.model;


import java.io.Serializable;
import java.time.LocalDate;

/**
 *
 * @author pepecalo
 *
 * -- PUBLIC.Cancion definition
 *
 * -- Drop table
 *
 * -- DROP TABLE PUBLIC.Cancion;
 *
 * CREATE TABLE PUBLIC.Cancion (
 * 	idCancion INTEGER NOT NULL AUTO_INCREMENT,
 * 	titulo CHARACTER VARYING(255) NOT NULL,
 * 	autor CHARACTER VARYING(255),
 * 	duracion INTEGER,
 * 	dataPublicacion DATE,
 * 	arquivo BINARY LARGE OBJECT,
 * 	CONSTRAINT idCancion_PK PRIMARY KEY (idCancion)
 * );
 * CREATE INDEX Cancion_titulo_IDX ON PUBLIC.Cancion (titulo);
 * CREATE UNIQUE INDEX PRIMARY_KEY_8 ON PUBLIC.Cancion (idCancion);
 */
public class Cancion
        implements Serializable, Comparable<Cancion> {

    /**
     * Título por defecto.
     */
    public static final String DEFAULT_TITLE = "UnKnown";

    private static final long serialVersionUID = 1L;

    private Long idCancion;
    private String titulo;
    private String autor;
    private int duracion;

    private LocalDate dataPublicacion;



    /**
     * Constructor que recoge el título, la duración y el autor.
     *
     * @param titulo título de la canción.
     * @param duracion duración de la canción en segundos.
     * @param autor autor de la canción.
     */
    public Cancion(String titulo, int duracion, String autor) {
        this.titulo = titulo;
        this.duracion = duracion;
        this.autor = autor;
    }

    /**
     * Constructor por defecto. Asigna el título por defecto.
     */
    public Cancion() {
        this.titulo = DEFAULT_TITLE;
    }

    /**
     * Constructor que recoge el título.
     *
     * @param titulo título de la canción.
     */
    public Cancion(String titulo) {
        this.titulo = titulo;
    }

    public Cancion(long idCancion, String titulo, int duracion) {
        this.idCancion = idCancion;
        this.titulo = titulo;
        this.duracion = duracion;
    }

    /**
     * Constructor que recoge el título y la duración.
     *
     * @param titulo título de la canción.
     * @param duracion duración de la canción en segundos.
     */
    public Cancion(String titulo, int duracion) {
        this.titulo = titulo;
        this.duracion = duracion;
    }


    public Cancion(long idCancion, String titulo, String autor, int duracion) {
        this.idCancion = idCancion;
        this.titulo = titulo;
        this.duracion = duracion;
        this.autor = autor;
    }

    public Cancion(long idCancion, String titulo,  String autor, int duracion, LocalDate dataPublicacion) {
        this.idCancion = idCancion;
        this.titulo = titulo;
        this.duracion = duracion;
        this.autor = autor;
        this.dataPublicacion = (dataPublicacion!=null) ? dataPublicacion : LocalDate.now();
    }

    public Long getIdCancion() {
        return idCancion;
    }

    public Cancion setIdCancion(long idCancion) {
        this.idCancion = idCancion;
        return this;
    }

    public String getAutor() {
        return autor;
    }

    public Cancion setAutor(String autor) {
        if (autor != null) {
            this.autor = autor;
        }
        return this;
    }

    public int getDuracion() {
        return duracion;
    }

    public Cancion setDuracion(int duracion) {
        this.duracion = duracion;
        return this;
    }

    public String getTitulo() {
        return titulo;
    }

    public Cancion setTitulo(String titulo) {
        if (titulo != null) {
            this.titulo = titulo;
        }
        return this;
    }

    public Cancion setDataPublicacion(LocalDate dataPublicacion) {
        this.dataPublicacion = (dataPublicacion!=null) ? dataPublicacion : LocalDate.now();
        return this;
    }

    /**
     * Establece la fecha de publicación de la canción. La fecha debe tener el
     * formato "yyyy-MM-dd".
     * @param dataPublicacion fecha de publicación de la canción como String.
     * @return el objeto Cancion.
     */
    public Cancion setDataPublicacion(String dataPublicacion) {
        this.dataPublicacion = LocalDate.parse(dataPublicacion);
        return this;
    }

    /**
     * Establece la fecha de publicación de la canción.
     * @param ano año de publicación.
     * @param mes mes de publicación.
     * @param dia día de publicación.
     * @return el objeto Cancion.
     */
    public Cancion setDataPublicacion(String ano, String mes, String dia) {
        dataPublicacion = LocalDate.of(Integer.parseInt(ano),
                Integer.parseInt(mes), Integer.parseInt(dia));
        return this;
    }

    public LocalDate getDataPublicacion() {
        return dataPublicacion;
    }

    public String getDuracionAsString() {
        int segundos = getDuracion();
        return String.format("%02d:%02d", (segundos / 60), (segundos  % 60));
    }



    /**
     *
     * @param cancion
     * @return
     */
    @Override
    public int compareTo(Cancion cancion) {
        if (titulo != null && cancion != null) {
            return titulo.compareToIgnoreCase(cancion.getTitulo());
        }
        return -1;
    }

    @Override
    public String toString() {
        return ((titulo != null) ? titulo : "")
                + " [" + getDuracionAsString() + "] - "
                + ((autor != null) ? autor : "");
    }

}
