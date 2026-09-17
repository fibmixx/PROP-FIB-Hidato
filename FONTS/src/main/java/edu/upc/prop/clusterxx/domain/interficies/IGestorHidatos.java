package edu.upc.prop.clusterxx.domain.interficies;

import edu.upc.prop.clusterxx.domain.model.Hidato;

import java.util.Map;

/**
 * @file IGestorHidatos.java
 *
 * @brief Interficie on es guarda la informació dels hidatos.
 *
 * Serveix per tenir tota la informació dins de l'estructura de dades d'aquest.
 */
public interface IGestorHidatos {
    /**
     * @brief Funció per afegir un hidato.
     *
     * @param id És l'identificador de l'Hidato.
     * @param h És l'Hidato que volem afegir.
     * @param idRepo És l'identificador del repositori on es troba l'hidato.
     *
     * @post Afegeix l'Hidato h.
     */
    void put(int id, Hidato h, int idRepo);

    /**
     * @brief Funció per eliminar un hidato.
     *
     * @param id És l'identificador de l'Hidato.
     *
     * @post Elimina el hidato amb identificador id.
     */
    void remove(int id);

    /**
     * @brief Funció per obtenir un hidato
     *
     * @param id És l'identificador de l'Hidato.
     *
     * @post Retorna l'Hidato amb identificador id.
     */
    Hidato get(int id);

    // ---------------------------------------
    /**
     * @brief Obté tots els hidatos associats a un repositori concret.
     *
     * @param idRepo Identificador del repositori.
     *
     * @return Un mapa amb els Hidatos que pertanyen al repositori idRepo.
     */
    Map<Integer, Hidato> carregarPerRepositori(int idRepo);

    /**
     * @brief Funció que retorna un mapa amb tots els hidatos carregats a memòria
     *
     * @post Retorna un mapa amb tots els hidatos, on la clau és el seu id
     */
    Map<Integer, Hidato> all();


}

