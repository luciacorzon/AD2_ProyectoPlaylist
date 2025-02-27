package com.pepinho.programacion.reproductormedia.gson;


import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.pepinho.programacion.reproductormedia.model.MediaSong;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Created by Pepe on 19/01/2017.
 * Adaptador para la clase MediaSong.
 */
public class MediaSongTypeAdapter extends TypeAdapter<MediaSong> {

    private static final long serialVersionUID = 1L;
    private Path directorio;

    public MediaSongTypeAdapter(Path pathDirectorio) {
        this.directorio = pathDirectorio;
    }

    /**
     * Método para guardar la canción de un objeto MediaSong en un archivo destino con el
     * título de la canción.
     */
    private void saveAudio(MediaSong mediaSong, String fileName) {
        if (directorio != null && Files.isDirectory(directorio)) {
            try {
                Files.write(directorio.resolve(fileName), mediaSong.getBytes());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void write(JsonWriter writer, MediaSong mediaSong) throws IOException {
        writer.beginObject();
        // Control de nulos:
        if (mediaSong.getIdCancion() == null) {
            writer.name("idCancion").nullValue();
        } else {
            writer.name("idCancion").value(mediaSong.getIdCancion());
        }
        writer.name("título").value(mediaSong.getTitulo());
        writer.name("autor").value(mediaSong.getAutor());
        writer.name("duración").value(mediaSong.getDuracion());
        if(mediaSong.getDataPublicacion() == null){
            writer.name("dataPublicacion").nullValue();
        } else {
            writer.name("dataPublicacion").value(mediaSong.getDataPublicacion().toString());
        }
        if(mediaSong.getAudio() == null){
            writer.name("audioPath").nullValue();
        } else {
            String fileName = mediaSong.getArquivo().getFileName().toString();
            saveAudio(mediaSong, fileName);
            writer.name("audioPath").value(directorio.resolve(fileName).toAbsolutePath().toString());
        }
        writer.endObject();
    }

    @Override
    public MediaSong read(JsonReader jsonReader) throws IOException {
        jsonReader.beginObject();
        MediaSong mediaSong = new MediaSong();
        while (jsonReader.hasNext()) {
            switch (jsonReader.nextName()) {
                case "idCancion" -> {
                    Long idCancion = jsonReader.nextLong();
                    mediaSong.setIdCancion(idCancion);

                }
                case "título" -> mediaSong.setTitulo(jsonReader.nextString());
                case "autor" -> mediaSong.setAutor(jsonReader.nextString());
                case "duración" -> mediaSong.setDuracion(jsonReader.nextInt());
                case "dataPublicacion" -> mediaSong.setDataPublicacion(jsonReader.nextString());
                case "audioPath" -> mediaSong.setAudio(Files.readAllBytes(Path.of(jsonReader.nextString())));
                default -> jsonReader.skipValue();
            }
        }
        return mediaSong;
    }
}
