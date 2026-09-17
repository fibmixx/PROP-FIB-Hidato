package edu.upc.prop.clusterxx.domain.excepcions;

/**
 * @file NoEsMapaCorrecte.java
 * @brief Excepció que es llança quan s'intenta assignar una forma de mapa amb una forma de tile que no es correspon.
 */
public class NoEsMapaCorrecte extends RuntimeException {
    /**
     * @brief Constructor de l'excepció amb un missatge predeterminat.
     */
    public NoEsMapaCorrecte() {
        super("Aquest tauler no té cap solució possible.");
    }

    /**
     * @brief Constructor de l'excepció amb un missatge personalitzat.
     * @param missatge El text explicatiu del motiu pel qual no és vàlid el mapa pel tipus de tile
     */
    public NoEsMapaCorrecte(String missatge) {
        super(missatge);
    }
}
