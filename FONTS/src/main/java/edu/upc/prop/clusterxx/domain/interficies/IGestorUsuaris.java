package edu.upc.prop.clusterxx.domain.interficies;

import edu.upc.prop.clusterxx.domain.model.Usuari;

import java.util.Map;

/**
 * @file IGestorUsuaris.java
 *
 * @brief Interficie on es guarda la informació dels usuaris.
 *
 * Serveix per tenir tota la informació dins de l'estructura de dades d'aquest.
 */
public interface IGestorUsuaris {
    /**
     * @brief Funció per afegir un usuari
     *
     * @param username És el nom de l'usuari.
     * @param u És l'usuari que volem afegir.
     *
     * @post Afegeix l'usuari u
     */
    void put(String username, Usuari u);

    /**
     * @brief Funció que retorna un mapa amb tots els usuaris
     *
     * @post Retorna un mapa amb tots els usuaris
     */
    Map<String, Usuari> all();

    /**
     * @brief Funció per comprovar si un usuari existeix
     *
     * @param key És el nom de l'usuari.
     *
     * @post Retorna true si l'usuari existeix, fals si no.
     */
    boolean containsKey(String key);

    /**
     * @brief Funció per eliminar un usuari
     *
     * @param key És el nom de l'usuari.
     *
     * @post Elimina l'usuari amb username key.
     */
    void remove (String key);

    /**
     * @brief Funció per obtenir un usuari
     *
     * @param u És el nom de l'usuari.
     *
     * @post Retorna l'usuari amb username u.
     */
    Usuari get(String u);
}
