package edu.upc.prop.clusterxx.domain.interficies;

import edu.upc.prop.clusterxx.domain.model.Ranking;

import java.util.Map;

/**
 * @file IGestorRankings.java
 *
 * @brief Interfície on s'especifica com guardar la informació dels Rankings.
 *
 * Ens permet guardar a disc els rankings dels jugadors i les seves partides.
 */
public interface IGestorRankings {
    /**@brief Funció per guardar un rànking sencer a disc
     *
     * @param id Nombre que identifica de forma única a un Rànking.
     * @param ranking És el rànking que volem guardar per primera vegada o bé sobreescriure
     *
     */
    void guardaRanking(int id, Ranking ranking);

    /**@brief Funció per obtenir de disc un rànking sencer concret
     * @param id_ranking Identificador que permet saber de forma única quin rànking volem
     * @post Crea una nova instància de Rànking amb totes les dades que hem obtingut del disc
     */
    Ranking agafarRanking(int id_ranking);

    /**@brief Funció que retorna tots els rànquings disponibles i els posa dins d'un Map
     *
     * @return Retorna tots els rànquings existents en una llista de rànkings
     */
    Map<Integer, Ranking> agafarTotsRankings();

    /**@brief Funció que permet esborrar un rànking concret aportant el seu identificador
     * @param id_ranking Identificador únic del rànquing que volem eliminar
     */
    void delRanking(int id_ranking);

    /**@brief Funció que permet saber si un ranking existeix
     *
     * @param id nombre únic que permet identificar a un Ranking de forma única
     *
     * @return Retorna true si existeix el rànking o false si no és així.
     */
    boolean teClau(int id);
}
