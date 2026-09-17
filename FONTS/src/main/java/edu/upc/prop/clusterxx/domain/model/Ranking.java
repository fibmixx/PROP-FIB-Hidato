package edu.upc.prop.clusterxx.domain.model;
import java.util.ArrayList;
import java.util.List;

import edu.upc.prop.clusterxx.domain.controladors.CtrlDomini;

/**
 * @file Ranking.java
 *
 * @brief Definició i implementació de la classe Ranking.
 *
 * Serveix per a poder emmagatzemar i comparar les puntuacions de diferents Usuaris al jugar diferents Hidatos.
 */

public class Ranking {
    /**
     * @brief Classe interna que representa una parella de (usuari, puntuació).
     */
    public static class PairUsuariPuntuacio {
        public String usuari;
        public int puntuacio;

        public PairUsuariPuntuacio(String usuari, int puntuacio) {
            this.usuari = usuari;
            this.puntuacio = puntuacio;
        }
    }

    /**
     * @brief Identificador del Rànking.
     */
    private int identificador;

    /**
     * @brief Array de parelles (usuari, puntuació) ordenat de manera decreixent per puntuació.
     */
    private List<PairUsuariPuntuacio> puntuacions;

    /**
     * @brief Quantitat de Rànkings creats.
     */
    private static int num_rankings = 1;

    // ---------- INTENT DE FIX: fer que els rankings siguin accessibles per l'owner i una llista de users --
    private String creadorUsername; // Creador del ranking (owner)
    private List<String> usersInclosos; // Users que poden accedir al ranking

    /**
     * @brief Controlador de domini.
     */
    private CtrlDomini ctrlDomini;

    /**
     * @brief Creadora de la classe Rànking.
     *
     * @param creadorUsername Nom d’usuari del creador (i propietari) del ranking.
     * @param usersInclosos Llista de noms dels usuaris que participen al ranking (i que poden accedir a aquest).
     *
     * @post Es crea una instància de Rànking amb un identificador únic i un array de puntuacions buit.
     */
    public Ranking(String creadorUsername, List<String> usersInclosos) {
        this.identificador = num_rankings;
        this.puntuacions = new ArrayList<>();
        this.ctrlDomini = CtrlDomini.getInstance();
        //----- AFEGIT---
        this.creadorUsername = creadorUsername;
        this.usersInclosos = new ArrayList<String>(usersInclosos);
        if(!this.usersInclosos.contains(creadorUsername)) {
            this.usersInclosos.add(creadorUsername);
        }

        for (String user : this.usersInclosos) {
            this.puntuacions.add(new PairUsuariPuntuacio(user, ctrlDomini.getCtrlUsuari().getUsuari(user).getPersonalBest()));
        }
        // ---- FI AFEGIT ----
        ++num_rankings;
    }

    /**
     * @brief Creadora de la classe Rànking.
     *
     * @param identificador valor enter que identifica el ranking.
     *
     * @post Es crea una instància de Rànking amb un identificador concret i un array de puntuacions buit.
     */
    public Ranking(int identificador) {
        this.identificador = identificador;
        this.puntuacions = new ArrayList<>();
        this.ctrlDomini = ctrlDomini;
        if(identificador >=  num_rankings){ //Per actualitzar num_rankings
            num_rankings = identificador+1; //Donat que el següent no ha de tenir el mateix nombre que el que acabem de crear amb id proporicionat.
        }
    }

    /**
     * @brief Funció per obtenir la quantitat de rànkings creats.
     *
     * @return Retorna la quantitat de rànkings.
     */
    public static int getNumRankings() {
        return num_rankings;
    }

    /**
     * @brief Funció per obtenir el identificador de rànkings
     *
     * @return Retorna l'identificador del rànking.
     */
    public int getIdentificador() {
        return identificador;
    }

    /**
     * @brief Funció per obtenir les puntuacions associades a un rànking.
     *
     * @return Retorna la llista de parelles (usuari, puntuació) ordenades de manera decreixent.
     */
    public List<PairUsuariPuntuacio> getPuntuacions() {
        return new ArrayList<>(puntuacions);
    }

    /**
     * @brief Funció per buidar un rànking.
     *
     * @post Es buida el rànking.
     */
    public void emptyRanking() {
        puntuacions.clear();
    }

    /**
     * @brief Funció per actualitzar els valors en el rànking d'un usuari.
     * @param user Usuari del rànking.
     * @param score Puntuació nova de l'usuari.
     *
     * @post S'actualitza la puntuació de l'usuari al rànking, només si la nova puntuació és millor que l'anterior.
     *       L'array es manté ordenat de manera decreixent automàticament.
     */
    public void updateScore(String user, int score) {
        // Buscar si l'usuari ja existeix
        for (int i = 0; i < puntuacions.size(); ++i) {
            if (puntuacions.get(i).usuari.equals(user)) {
                // Si la nova puntuació és millor, actualitzar-la
                if (score > puntuacions.get(i).puntuacio) {
                    puntuacions.get(i).puntuacio = score;
                    insertarPosicioCorrecta(i);
                }
                return;
            }
        }
        // Usuari no existeix, així que afegir a posició correcta
        PairUsuariPuntuacio novaParella = new PairUsuariPuntuacio(user, score);
        int posicio = 0;
        while (posicio < puntuacions.size() && puntuacions.get(posicio).puntuacio > score) {
            ++posicio;
        }
        puntuacions.add(posicio, novaParella);
    }

    /**
     * @brief Funció privada per reubicació d'un element a la posició correcta dins de l'array ordenat.
     * @param indexActual Índex de l'element a reposicionar.
     *
     * @post L'element a posicio indexActual es mou a la seva posició correcta mantenint un ordre decreixent.
     */
    private void insertarPosicioCorrecta(int indexActual) {
        PairUsuariPuntuacio parella = puntuacions.get(indexActual);
        // Moure l'element cap amunt si la seva puntuació és més gran que la dels anteriors
        int posicio = indexActual;
        while (posicio > 0 && puntuacions.get(posicio - 1).puntuacio < parella.puntuacio) {
            puntuacions.set(posicio, puntuacions.get(posicio - 1));
            --posicio;
        }
        
        // Moure l'element cap avall si la seva puntuació és menor que la dels següents
        while (posicio < puntuacions.size() - 1 && puntuacions.get(posicio + 1).puntuacio > parella.puntuacio) {
            puntuacions.set(posicio, puntuacions.get(posicio + 1));
            ++posicio;
        }
        
        // Insertar l'element en la seva posició correcta
        puntuacions.set(posicio, parella);
    }

    /**
     * @brief Funció que permet escriure per pantalla els rankings
     * @post S'ha imprès per sortida estàndard les entrades del rànking específic.
     */

    public void print() {
        	System.out.println("Ranking " + identificador + ":");
            for (PairUsuariPuntuacio p : puntuacions) {
                System.out.println(p.usuari + ": " + p.puntuacio);
            }
    }
}
