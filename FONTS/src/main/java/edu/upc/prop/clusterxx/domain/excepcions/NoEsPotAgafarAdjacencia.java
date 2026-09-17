package edu.upc.prop.clusterxx.domain.excepcions;

/**
 * @file NoEsPotAgafarAdjacencia.java
 * @brief Excepció que es llança quan la definició de l'adjacència del Hidato no és correcta.
 */
public class NoEsPotAgafarAdjacencia extends RuntimeException {
    /**
     * @brief Constructor de l'excepció amb un missatge predeterminat.
     */
    public NoEsPotAgafarAdjacencia() {
        super("Hi ha problemes en la definició de l'adjacència del Hidato");
    }

    /**ELs valors introduïts no són vàlids per generar l'hidato
     * @brief Constructor de l'excepció amb un missatge personalitzat.
     * @param missatge El text explicatiu del motiu de l'error.
     */
    public NoEsPotAgafarAdjacencia(String missatge) {
        super(missatge);
    }
}
