package edu.upc.prop.clusterxx.domain.excepcions;

/**
 * @file GeneradorNoPotGenerarCami.java
 * @brief Excepció que es llença quan el Generator no pot generar un camí.
 */
public class GeneradorNoPotGenerarCami extends RuntimeException {
    /**
     * @brief Constructor de l'excepció amb un missatge predeterminat.
     */
    public GeneradorNoPotGenerarCami() {
        super("No s'ha pogut generar un camí");
    }

    /**
     * @brief Constructor de l'excepció amb un missatge personalitzat.
     * @param missatge El text explicatiu del motiu pel qual es produeix l'error.
     * */
    public GeneradorNoPotGenerarCami(String missatge) {
        super(missatge);
    }
}
