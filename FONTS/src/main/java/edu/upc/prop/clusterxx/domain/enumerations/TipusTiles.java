package edu.upc.prop.clusterxx.domain.enumerations;

/**
 * @file TipusTiles.java
 *
 * @brief Definició i implementació de la classe tipus Enum TipusTiles.
 *
 * Serveix per a definir quina forma geomètrica tindran les tiles del Hidato.
 */
public enum TipusTiles {
    /**
     * @brief Indica que les Tiles són triangles, amb 3 costats.
     */
    TRIANGLE(3),
    /**
     * @brief Indica que les Tiles són quadrats, amb 4 costats.
     */
    QUADRAT(4),
    /**
     * @brief Indica que les Tiles són hexagons, amb 6 costats.
     */
    HEXAGON(6);

    /**
     * Quantitat de costats que una forma geomètrica té
     */
    public final int sides;

    /**
     * @brief Creadora de la forma de la Tile.
     *
     * @param sides
     * @post Crea un nou tipus de Tile, amb la quantitat de costats indicada.
     */
    TipusTiles(int sides) {
        /*
        Funció necessària per afegir valors de costats als valors de tiles, per possibles usos futurs.
         */
        this.sides = sides;
    }
}
