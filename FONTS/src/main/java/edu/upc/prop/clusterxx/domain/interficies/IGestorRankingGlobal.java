package edu.upc.prop.clusterxx.domain.interficies;

import edu.upc.prop.clusterxx.domain.model.RankingGlobal;

/**
 * @file IGestorRankingGlobal.java
 *
 * @brief Interfície on s'especifica com guardar la informació del RànkingGlobal.
 *
 * Ens permet guardar a disc els rankings dels jugadors i les seves partides.
 */
public interface IGestorRankingGlobal {
    /**
     * @breif Funció per guardar la instància actual de ranking global a disc
     * @param rankingGlobal Ranking global que volem guardar a disc
     * @post S'ha guardat el rànking global a disc.
     */
    void guardaRankingGlobal(RankingGlobal rankingGlobal);

    /**
     * @breif Funció que permet recuperar les dades del rànking global guardant-los a la instància actual
     * @param global instància del rànking global que s'està executant.
     * @return Retorna un objecte RankingGlobal amb les dades que estaven guardades a disc.
     */
    void agafarRankingGlobal(RankingGlobal global);

}
