package edu.upc.prop.clusterxx.domain.enumerations;

/**
 * @file TipusAdjacència.java
 *
 * @brief Definició i implementació de la classe tipus Enum TipusAdjacència.
 *
 * Serveix per a definir quins tipus d'ajacència poden tenir les Tiles del Hidato.
 */

public enum TipusAdjacencia {
    /**
     * @brief Indica que les Tiles són adjacents només si comparteixen un costat (vertical o horitzontal).
     */
    VERTEX,
    /**
     * @brief Indica que les Tiles són adjacents només si comparteixen un vèrtex (diagonal).
     */
    ARESTA,
    /**
     * @brief Indica que les Tiles són adjacents si comparteixen un costat o un vèrtex (vertical, horitzontal o diagonal).
     */
    VERTEXIARESTA
    //Fill out
}
