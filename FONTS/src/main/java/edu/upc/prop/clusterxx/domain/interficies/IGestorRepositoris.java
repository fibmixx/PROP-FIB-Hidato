package edu.upc.prop.clusterxx.domain.interficies;

import edu.upc.prop.clusterxx.domain.model.Repositori;
import java.util.Map;

/**
 * @file IGestorRepositoris.java
 *
 * @brief Interficie on es guarda la informació dels repositoris.
 *
 * Serveix per tenir tota la informació dins de l'estructura de dades d'aquest.
 */
public interface IGestorRepositoris {
    // username de l'owner del repo
    /**
     * @brief Funció per afegir un repositori.
     *
     * @param id És l'identificador del repositori.
     * @param r És el Repositori que volem afegir.
     * @param owner És l'usuari a qui li pertany el repositori r.
     *
     * @post Afegeix el Repositori r.
     */
    void put(int id, Repositori r, String owner);

    /**
     * @brief Funció que busca i carrega del mapa owners els repositoris d'un usuari en concret.
     *
     * @param owner  És l'usuari a qui li pertany el repositori r.
     *
     * @return Retorna un HashMap amb tots els repositoris de l'usuari.
     */
    Map<Integer, Repositori> carregarPerUsuari(String owner);

    /**
     * @brief Funció per eliminar un repositori.
     *
     * @param id És l'identificador del Repositori.
     *
     * @post Elimina el repositori amb identificador id.
     */
    void remove(int id);

    /**
     * @brief Funció per obtenir un repositori del mapa
     *
     * @param id És l'identificador del Repositori.
     *
     * @post Retorna el Repositori amb identificador id.
     */
    Repositori get(int id);
}
