package edu.upc.prop.clusterxx.domain.excepcions;

/**
 * @file HidatoInvalidException.java
 * @brief Excepció que es llança quan un tauler d'Hidato no compleix les regles de format o no és vàlid.
 */
public class HidatoInvalidException extends Throwable {
    /**
     * @brief Constructor de l'excepció amb un missatge predeterminat.
     */
    public HidatoInvalidException() {
        super("El tauler de l'Hidato no és vàlid.");
    }

    /**
     * @brief Constructor de l'excepció amb un missatge personalitzat.
     * @param missatge El text explicatiu del motiu pel qual l'Hidato és invàlid.
     */
    public HidatoInvalidException(String missatge) {
        super(missatge);
    }
}
