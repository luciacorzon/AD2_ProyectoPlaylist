package com.pepinho.programacion.reproductormedia.gson;

import com.google.gson.*;
import com.pepinho.programacion.reproductormedia.model.PlayList;
import java.lang.reflect.Type;


/**
 * Created by Pepe
 * Adaptador para la clase PlayList.
 */
public class PlayListSerializer implements JsonSerializer<PlayList> {

    @Override
    public JsonElement serialize(PlayList playList, Type type, JsonSerializationContext contexto) {
        JsonObject je = new JsonObject();
        je.addProperty("idPlayList", playList.getIdPlayList());
        je.addProperty("nome", playList.getNome());
        je.addProperty("dataCreacion", playList.getDataCreacion().toString());

        JsonArray ja = new JsonArray();

        //        List<Reproducible> canciones = playList.getCancions();
//        for (Reproducible cancion : canciones) {
//            ja.add(contexto.serialize(cancion));
//        }

        je.add("cancions", contexto.serialize(playList.getCancions()));
        return je;
    }
}
