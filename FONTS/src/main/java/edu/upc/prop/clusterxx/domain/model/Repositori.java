package edu.upc.prop.clusterxx.domain.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @file Repositori.java
 * 
 * @brief Definició i implementació de la classe Repositori.
 * 
 * Serveix com a unitat d'emmagatzematge de Hidatos ja generats.
 */

public class Repositori {
    /**
     * @brief Identificador del repositori.
     */
    private final int identificador;

    /**
     * @brief Nom del repositori.
     */
    private String nom_repositori;

    /**
     * @brief Quantitat de repositoris creats.
     */
    private static int num_repositoris = 0;

    /**
     * @brief Comptador intern per assignar IDs únics als Hidatos d'aquest repositori.
     */
    private int contador_hidatos;

    /**
     * @brief Llista d'Hidatos pertanyents a un repositori, identificats per un enter únic.
     */
    private HashMap<Integer, Hidato> hidatos_list;

    /**
     * @brief Creadora de la classe Repositori.
     *
     * @param nom Nom del repositori.
     *
     * @post Es crea un nou repositori amb el nom donat.
     */
    public Repositori(String nom) {
        this.identificador = num_repositoris;
        this.nom_repositori = nom;
        this.hidatos_list = new HashMap<>();
        //this.contador_hidatos = 0;
        ++num_repositoris;
    }

    /**
     * @brief Funció per obtenir la quantitat de repositoris creats
     *
     * @return Retorna la quantitat de repositoris creats.
     */
    public static int getNumRepositoris() {
        return num_repositoris;
    }

    // num_repositoris és estàtic, cada cop que s'encen el programa es reinicia
    // necessitem mantenir actualitzat el valor pq els ids depenen d'aquest
    /**
     * @brief Funció per actualitzar la quantitat de repositoris creats.
     *
     * @post Actualitza la quantitat de repositoris creats.
     */
    public static void setNumRepositoris(int i) {
        num_repositoris = i;
    }

    /**
     * @brief Funció per obtenir el nom del repositori.
     *
     * @return Retorna el nom del repositori.
     */
    public String getNom() {
        return nom_repositori;
    }

    /**
     * @brief Funció per obtenir el identificador del repositori.
     *
     * @return Retorna el identificador del repositori.
     */
    public int getIdentificador() {
        return identificador;
    }

    /**
     * @brief Funció per canviar el nom del repositori.
     *
     * @param nou_nom El nou nom del repositori
     */
    public void setNom(String nou_nom) {
        this.nom_repositori = nou_nom;
    }

    // MÈTODES PER GESTIONAR ELS HIDATOS DEL REPOSITORI - EN CONSTRUCCIÓ
    /**
    * @brief Afegeix un nou Hidato al repositori.
     * @param hidato L'objecte Hidato a emmagatzemar.
     */
    //     * @return L'identificador (ID) assignat a l'Hidato dins d'aquest repositori.
    public void afegirHidato(Hidato hidato) {
        //int idAssignat = contador_hidatos;
        hidatos_list.put(hidato.getId(), hidato);
        //contador_hidatos++;
        //return idAssignat;
    }

    /**
     * @brief Retorna el mapa d'hidatos.
     */
    public HashMap<Integer, Hidato> getHidatos() {
        return hidatos_list;
    }

    /**
     * @brief Funció per esborrar un hidato del repositori.
     *
     * @param id Identificador del hidato a esborrar.
     *
     * @throws Exception si el hidato amb la id indicada no existeix en aquest repositori.
     */
    public void esborrarHidato(int id) throws Exception {
        if (hidatos_list.containsKey(id)) {
            hidatos_list.remove(id);
        } else {
            throw new Exception("Error: No es pot esborrar. El hidato amb ID " + id + " no existeix en aquest repositori.");
        }
    }
}
