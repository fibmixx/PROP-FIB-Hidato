package edu.upc.prop.clusterxx.domain.enumerations;

import java.util.Random;

/**
 * @file TipusDificultat.java
 *
 * @brief Definició i implementació de la classe TipusDificultat.java
 *
 * Serveix per a establir diferents categories de dificultat, cada una amb un índex, que ens serviran per a calcular les puntuacions de cada partida.
 */
public enum TipusDificultat {
    /**
     * @brief Indica que la dificulat és Fàcil, S'assigna unes mides de tauler de 6x6 una probabilitat d'amagar les Tiles resultants del 0.75 i
     * el nombre maxim que podem possara al hidato oscila entre [28-33].
     */
    FACIL(4,4, new int[]{10,11,12,13,14,15} , 0.5 ),
    /**
     * @brief Indica que la dificultat és Mitjana. S'assigna unes mides de tauler de 8x8, una probabilitat del 0.80 d'amagar les Tiles
     * resultant. El nombre màxim oscil·la entre [51-56].
     */
    MITJA(8,8,new int[]{51,52,53,54,55,56}, 0.80),
    /**
     * @brief Indica que la dificultat és Difícil. S'assigna unes mides de tauler de 9x9, una probabilitat del 0.90 d'amagar les Tiles
     * resultat. El nombre màxim oscil·larà entre [65-71].
     */
    DIFICIL (9,9,new int[]{65,67,68,69,70,71}, 0.90);

    private final int files;
    private final int columnes;
    private final int[] conjunt_numMax;
    private final double num_amagar;

    /**
     * @brief Creadora d'un TipusDificultat, que assigna un índex a cada dificultat.
     * @param
     *
     * @post Crea un nou tipus de dificultat, amb un índex propi.
     */
    TipusDificultat(int files, int columnes, int [] num_max, double num_amagar) {
        /*
        Funció necessària per a poder introduir valors a les dificultats,
        i així poder fer servir aquests valors a l'hora de calcular les puntuacions.
         */
        this.files = files;
        this.columnes = columnes;
        this.conjunt_numMax = num_max;
        this.num_amagar = num_amagar;
    }

    /**
     * @brief Funció que retorna el nombre de files assignada a un TipusAdjacència concreta.
     * @return Retorna el nombre de files que s'han assignat al tipus d'adjacència.
     */
    public int  getFiles(){
        return files;
    }
    /**
     * @brief Funció que retorna les columnes assignades a un tipus d'adjacència en concret
     * @return Retorna el valor de columnes que correspon al tipus d'adjacència especificat
     */
    public int getColumnes(){
        return columnes;
    }

    /**
     * @brief Funció que retorna el número màxim que s'assigna a un tipus d'adjacència concret
     * @return Retorna un nombre aleatori entre un conjunt que serà el nombre màxim que se li assigna a l'Hidato per ser del tipus indicat.
     */
    public int getNumMax(){
        Random rand = new Random();
        int i = rand.nextInt(conjunt_numMax.length);
        return conjunt_numMax[i];
    }

    /**
     * @brief Funció que retorna la probabilitat d'amagar una tile per a un Tipus d'adjacència indicat.
     * @return Es retorna el valor de la probabilitat d'amagar un nombre segons el tipus de Dificultat que té.
     */

    public double getNumAmagar(){
        return num_amagar;
    }
}
