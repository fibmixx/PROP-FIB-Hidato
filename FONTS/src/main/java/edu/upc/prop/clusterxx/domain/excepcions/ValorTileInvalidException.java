package edu.upc.prop.clusterxx.domain.excepcions;

/**
 * @file ValorTileInvalidException.java
 * @brief Excepció que es llança quan els valors introduïts a la Tile no són vàlids.
 */
public class ValorTileInvalidException extends RuntimeException {

    /**
     * @brief Constructor de l'excepció amb un missatge predeterminat.
     */
    public ValorTileInvalidException(){
        super("Els valors introduïts no són vàlids");
    }
    /**ELs valors introduïts no són vàlids per generar l'hidato
     * @brief Constructor de l'excepció amb un missatge personalitzat.
     * @param missatge El text explicatiu del motiu pel qual es genera l'error.
     */
    public ValorTileInvalidException(String missatge){
        super(missatge);
    }
}
