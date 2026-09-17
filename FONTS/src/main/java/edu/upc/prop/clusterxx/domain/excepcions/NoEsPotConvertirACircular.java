package edu.upc.prop.clusterxx.domain.excepcions;

/**
 * @file NoEsPotConvertirACircular.java
 * @brief Excepció que es llança quan l'hidato no té Tiles de tipus Hexagon i no es pot convertir a tauler Circular.
 */
public class NoEsPotConvertirACircular extends RuntimeException {
    /**
     * @brief Constructor de l'excepció amb un missatge predeterminat.
     */
    public NoEsPotConvertirACircular() {
        super("L'hidato no té Tiles de tipus Hexagon i no es pot convertir a tauler Circular");
    }

    /**ELs valors introduïts no són vàlids per generar l'hidato
     * @brief Constructor de l'excepció amb un missatge personalitzat.
     * @param missatge El text explicatiu del motiu de l'error.
     */
    public NoEsPotConvertirACircular(String missatge) {
        super(missatge);
    }
}