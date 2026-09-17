package edu.upc.prop.clusterxx.domain.excepcions;

/**
 * @file DimensionsInvalidesException.java
 * @brief Excepció que es llança quan els valors de les dimensions d'un Hidato no són vàlids.
 */
public class DimensionsInvalidesException extends RuntimeException {
    /**
     * @brief Constructor de l'excepció amb un missatge predeterminat.
     */
    public DimensionsInvalidesException() {
        super("ELs valors introduïts no són vàlids per generar l'hidato");
    }

    /**
     * @brief Constructor de l'excepció amb un missatge personalitzat.
     * @param missatge El text explicatiu del motiu pel qual el valor no és invàlid.
     */
    public DimensionsInvalidesException(String missatge) {
        super(missatge);
    }
}
