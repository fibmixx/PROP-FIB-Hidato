package edu.upc.prop.clusterxx.domain.excepcions;

/**
 * @file HidatoSenseSolucioException.java
 * @brief Excepció que es llança quan un tauler d'Hidato no té solució.
 */
public class HidatoSenseSolucioException extends Exception {
    /**
     * @brief Constructor de l'excepció amb un missatge predeterminat.
     */
    public HidatoSenseSolucioException() {
        super("Aquest tauler no té cap solució possible.");
    }

    /**
     * @brief Constructor de l'excepció amb un missatge personalitzat.
     * @param missatge El text explicatiu del motiu pel qual l'Hidato és invàlid.
     */
    public HidatoSenseSolucioException(String missatge) {
        super(missatge);
    }
}
