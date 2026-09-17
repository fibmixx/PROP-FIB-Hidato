package edu.upc.prop.clusterxx.domain.model;

/**
 * @file RankingGlobal.java
 * 
 * @brief Definició i implementació de la subclasse de tipus Singleton RànkingGlobal.
 * 
 * Serveix com a rànking al qual tots els usuaris poden accedir-ne.
 */
public class RankingGlobal extends Ranking {

    /**
     * @brief Instància única del rànking global
     */
    private static RankingGlobal rankingInstancia;
    /**
     * @brief Creadora del rànking global.
     *
     * @post Es crea una instància de RànkingGlobal.
     */
    private RankingGlobal() {
        // LI PASSEM L'IDENTIFICADOR 0 !!!!!!!
        super(0);
    }

    /**
     * @brief Funció per obtenir la instància única del rànking global.
     *
     * Si la instància no existeix, es crea.
     *
     * @return Retorna la instància del rànking global.
     */
    public static RankingGlobal getInstance() {
        if (rankingInstancia == null) {
            rankingInstancia = new RankingGlobal();
        }
        return rankingInstancia;
    }
}
