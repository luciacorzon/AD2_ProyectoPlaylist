/*
 * Autor: Pepe Calo
 * Realizado con fines educativos.
 * Puede modificarlo siempre que no lo haga con fines comerciales.
 */
package com.pepinho.programacion.reproductormedia.dao;

import java.util.List;

/**
 *<
 * @author pepecalo
 * @param <T> Tipo de dato del objeto
 */
public interface PlayerDAO<K, T> {

    T get(K id);

    List<T> getAll();

    List<T> getAllFromID(K id);

    K save(T t);

    void update(T t);
   
    void delete(T t);

    public boolean deleteById(K id);

    public List<Integer> getAllIds();

    public void updateLOB(T book, String f);

    public void updateLOBById(K id, String f);
    
    void deleteAll();
}