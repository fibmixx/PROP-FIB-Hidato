package edu.upc.prop.clusterxx.domain.excepcions;

/**
 * @file CoordenadesInvalidesException.java
 * @brief Excepció que es llença quan el valor de les coordenades d'una Tile no son vàlids.
 */
public class CoordenadesInvalidesException extends RuntimeException{
    /**
     * @brief Constructor de l'excepció amb un missatge predeterminat.
     */
    public CoordenadesInvalidesException() {
        super("No s'ha pogut obtenir la Tile que s'ha demanat (Coordenades Invàlides");
    }

    /**ELs valors introduïts no són vàlids per generar l'hidato
     * @brief Constructor de l'excepció amb un missatge personalitzat.
     * @param missatge El text explicatiu del motiu pel qual es genera l'error.
     */
    public CoordenadesInvalidesException(String missatge) {
        super(missatge);
    }
}
