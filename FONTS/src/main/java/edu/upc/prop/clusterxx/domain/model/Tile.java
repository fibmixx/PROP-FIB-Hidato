package edu.upc.prop.clusterxx.domain.model;

import edu.upc.prop.clusterxx.domain.excepcions.ValorTileInvalidException;

/**
 * @file Tile.java
 *
 * @brief Definició i implementació de la classe Tile.
 *
 * Serveix per a proporcionar una Tile, que és la unitat bàsica del Hidato, i que pot tenir un valor i un estat d'agafada o no.
 */
public class Tile {
    /**
     * @brief Valor que indica quin serà el pròxim id de Tile
     */
    private static int num_Tiles = 0;
    /**
     * @brief Int que identifica a cada Tile.
     */
    private int id;
    /**
     * @brief Booleà que declara si la Tile està agafada o no.
     */
    private boolean agafada;

    /**
     * @brief Hidato al que està lligada la Tile.
     */

    private Hidato hidato;

    /**
     * @brief String amb el valor de la Tile, que pot ser un número, un forat ("*") o una casella buida ("#").
     */

    private String valor;
    /**
     * @brief Boolean que indica si la Tile comença resolta (descoberta) o no.
     */
    private boolean startsSolved;

    /**
     * @brief Boolean que permet indicar si una Tile en específic representa un forat o no.
     */

    private boolean esForat;

    /**
     * @breif Funció que ens permet saber si un valor String passat és un valor vàlid per passar a ser part de l'atribut valor.
     * @param valor String que s'ha d'avaluar.
     * @return Retorna true si el valor és un valor vàlid per ocupar "valor".
     */
    private boolean valorCorrecte(String valor){
        if (valor.length() == 1 && valor.charAt(0) == '#') {
            return true;
        }
        else if (valor.length() == 1 && valor.charAt(0) == '?') {
            return true;
        }
        else if (valor.length() == 1 && valor.charAt(0) == '*') {
            return true;
        }
        else if (valor.matches("\\d+") && valor.charAt(0) != '0') {
            return true;
        }else{
            return false;
        }
    }

    /**
     * @brief Creadora de la classe Tile, que inicialitza el valor a "#" (forat) i l'estat d'agafada a false.
     * @param hidato Hidato al que assciem la Tile que volem crear.
     * @post S'ha creat una Tile amb valor d'agafada false i valor "#".
     */

    public Tile(Hidato hidato) {
        if(hidato != null){
            this.id = num_Tiles++;
            this.hidato = hidato;
            this.agafada = false;
            this.valor = "#"; // COMPTE!!!
            this.startsSolved = false; //COMPTE!!!
            this.esForat = false;
        }else{
            throw new IllegalArgumentException("Hidato al que pertany no és vàlid");
        }
    }

    /**
     * @param hidato Hidato al qual pertany la Tile creada.
     * @brief Creadora de la classe Tile amb només l'hidato del que forma part.
     * @post La Tile s'ha creat amb els valors "agafada" i "esForat" en false s'ha assignat un identificador a la tile i s'ha marcat com a què no començarà Solucionada.
     */

    public Tile(Hidato hidato, Tile tile) { //Per fer una còpia de tots els paràmteres de la Tile
        if(hidato != null && tile != null) {
            this.id = num_Tiles++;
            this.agafada = tile.agafada;
            this.hidato = hidato;
            this.valor = tile.valor;
            this.startsSolved = tile.startsSolved;
            this.esForat = tile.esForat;
        }else{
            throw new IllegalArgumentException("Hidato or Tile null");
        }

    }

    /**
     * @brief Creadora de la classe Tile amb un valor i un estat d'agafada específics.
     * @param val   Valor donat de la Tile
     * @param chose Estat d'agafada donat de la Tile
     * @post La Tile s'ha creat amb el valor i l'estat d'agafada donats.
     */


    public Tile(Hidato hidato, String val, boolean chose) {
        if(hidato != null) {
            if(valorCorrecte(val)) {
                this.id = num_Tiles++;
                this.agafada = chose;
                this.valor = val;
                this.esForat = false;
                this.hidato = hidato;
            }else{
                throw new ValorTileInvalidException("Valor Tile no vàlid");
            }
        }else{
            throw new IllegalArgumentException("Hidato or Tile null");
        }

    }

    /**
     * @brief Funció per a obtenir el valor de la Tile.
     * @return Retorna el valor de la Tile.
     */

    public String getValor() {
        return this.valor;
    }

    /**
     * @return Retorna l'estat d'agafada de la Tile.
     * @brief Funció per a obtenir l'estat d'agafada de la Tile.
     */


    public boolean getAgafada() {
        return this.agafada;
    }

    /**
     * @param agafat Valor que adquirirà el camp agafadade la Tile.
     * @brief Funció per establir un nou estat d'agafada per a la Tile.
     * @post L'estat de la Tile s'ha actualitzat al nou valor donat.
     */


    public void setAgafada(boolean agafat) {
        this.agafada = agafat;
    }

    /**
     * @return Retorna el valor de StartSolved de la Tile.
     * @brief Funció per obtenir l'estat del paràmetre StartSolved de la Tile.
     */

    public boolean getStartSolved() {
        return startsSolved;
    }

    /**
     * @param startSolved boolean que conté el valor a introduir dins de StartSolved de la Tile.
     * @brief Funció que permet actualitzar el valor del paràmetre StartSolved de la Tile.
     * @post S'estableix el paràmetre startSolved de la Tile al valor startSolved proprocionat.
     */

    public void setStartSolved(boolean startSolved) {
        this.startsSolved = startSolved;
    }

    /**
     * @return Es retorna l'Hidato al qual pertany la Tile.
     * @brief Funció que permet obtenir l'Hidato al qual pertany una Tile.
     */
    public Hidato getHidato() {
        return hidato;
    }

    /**
     * @param hidato Hidato al qual pertany la Tile
     * @brief Funció que permet establir l'Hidato al qual pertany una Tile en concret.
     * @post La funció ha posat l'Hidato proporcionat com l'Hidato al qual pertany la Tile.
     */
    public void setHidato(Hidato hidato) {
        if (hidato != null) {
            this.hidato = hidato;
        }else{
            throw new NullPointerException("L'Hidato proporcionat és NULL");
        }
    }

    /**
     * @return Retorna si la Tile és un forat (true) o no (false).
     * @brief Funció que permet consultar si una Tile és designada com a forat.
     */

    public boolean getEsForat() {
        return esForat;
    }

    /**
     * @param esForat Valor que adoptarà el paràmetre esForat de la Tile.
     * @brief Funció que permet especificar si una Tile és un forat o no dins del hidato.
     * @post el paràmetre esForat de la Tile adquireix el valor esForat proporcionat.
     */

    public void setEsForat(boolean esForat) {
        if(esForat && !this.valor.equals("#")){ //Si té valor "#" vol dir que la considerem una part "estructural" i no un forat.
            this.esForat = esForat;
            this.valor = "*";
        }
    }

    /**
     * @return Retorna l'identificador únic dins del Hidato de la Tile.
     * @brief Funció que permet obtenir el valor de l'identificador de la Tile.
     */
    public int getId() {
        return this.id;
    }

    /**
     * @param nouValor El nou valor que es vol establir per a la Tile.
     * @brief Funció per establir un nou valor per a la Tile, que ha de ser un número, un forat ("*") o una paret ("#").buida
     * @post El valor de la Tile passa a ser nouValor.
     */
    public void setValor(String nouValor) throws ValorTileInvalidException {
        if (!esForat || nouValor.equals("#")) { //Si no és un forat podem modificar el valor de la Tile (a no ser que estiguem reiniciant el mapa)
            if (valorCorrecte(nouValor)) {
                this.valor = nouValor; // Afegim el valor demanat a valor
                // Si el valor és un número, comença resolt
                if (nouValor.matches("\\d+")) {
                    ///Caution
                    this.agafada = true;
                } else if (nouValor.equals("?")) { // Si és ?, comença sense resoldre
                    this.startsSolved = false;
                    System.out.println("Algu ha posat ? a la Tile...");
                }else if (nouValor.matches("#")){
                    this.agafada = false;
                    this.startsSolved = true; //Ha de mostrar el contingut (#)
                    if(esForat){
                        this.esForat = false;
                    }
                }else if (nouValor.equals("*")) {
                    this.startsSolved = true;
                    this.esForat = true;
                    ////CAUTIONNN
                    this.agafada = true;
                }
            } else {
                throw new ValorTileInvalidException("No s'ha introduït un valor correcte per guardar a la Tile");
            }
        }
    }
}
