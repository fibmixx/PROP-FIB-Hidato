package edu.upc.prop.clusterxx.domain.algorismes;

import edu.upc.prop.clusterxx.domain.model.Hidato;
import edu.upc.prop.clusterxx.domain.model.Tile;

import java.util.*;

/**
 * @file Solver.java
 *
 * @brief Definició i implementació de la classe Solver.
 *
 * Serveix per a proporcionar un mètode de resolució dels Hidatos.
 */
public class Solver {
    /**
     * @brief Array de Strings on l'índex és el número dins la tile i el valor és la coordenada on es troba ("x,y").
     * */
    private String[] solucions;

    /**
     * @brief Constructor per defecte.
     * @post Crea una instància Solver buida.
     * */
    public Solver() {}

    /**
     * @brief Funció que inicialitza el procés de comprovació per veure si l'Hidato és resoluble.
     * @param mapa l'Hidato a comprovar.
     * @param num_max el nombre màxim que es pot introduir per resoldre l'Hidato
     * @param files_total nombre total de files de l'Hidato.
     * @param columnes_total nombre total de columnes de l'Hidato.
     * @param adj tipus d'adjacència de l'Hidato.
     * @pre El tauler mapa no és nul, conté almenys el número 1, num_max és exactament el nombre de caselles
     * jugables disponibles al tauler; files_total i columnes_total corresponen a les dimensions reals del tauler.
     * @post Si el tauler té solució, retorna true i l'objecte mapa queda modificat contenint la solució completa.
     * Si no en té, retorna false i l'estat del mapa pot haver estat alterat.
     * */
    public boolean resoldre_ini(Hidato mapa, Integer num_max, Integer files_total, Integer columnes_total, String adj) {
        // +1 pq comencem des de l'index 1
        solucions = new String[num_max + 1];

        // Recorrer el mapa per obtenir els números ja col·locats i les seves coordenades
        String posInici = null;
        for (int i = 0; i < files_total; i++) {
            for (int j = 0; j < columnes_total; j++) {
                if(mapa.existeixTile(i,j)){
                    Tile tileActual = mapa.obtenirTile(i,j);

                    // Si la tile existeix i el seu valor és un número
                    if(tileActual != null && tileActual.getValor().matches("\\d+")) {
                        int valor = Integer.parseInt(tileActual.getValor());
                        String coord = i + "," + j;

                        solucions[valor] = coord;

                        if(valor == 1) {
                            posInici = coord;
                        }
                    }
                }

            }
        }

        // Si no trobem 1 al tauler
        if (posInici == null) {
            System.out.println("Error: No s'ha trobat el n�mero 1 al tauler.");
            return false; // 0 solucions
        }

        // Agafem les coordenades de la 1a tile
        String[] parts = posInici.split(",");
        Integer filIni = Integer.parseInt(parts[0]);
        Integer colIni = Integer.parseInt(parts[1]);

        // Cridem al mètode recursiu
        return solver_rec(filIni, colIni, mapa, 2, num_max, files_total, columnes_total, adj);
    }

    /**
     * @brief Funció que realitza el procés de comprovació per veure si l'Hidato és resoluble.
     * @param fil la posició de la Tile a resoldre dins el conjunt de files de tiles de l'Hidato.
     * @param col la posició de la Tile a resoldre dins el conjunt de columnes de tiles de l'Hidato.
     * @param mapa l'Hidato a comprovar.
     * @param next_num el següent número a introduir a una tile per seguir amb la resolució de l'Hidato.
     * @param num_max el nombre màxim que es pot introduir per resoldre l'Hidato
     * @param files_total nombre total de files de l'Hidato.
     * @param columnes_total nombre total de columnes de l'Hidato.
     * @param adj tipus d'adjacència de l'Hidato.
     * @pre next_num és el següent valor a col·locar o buscar (2 <= next_num <= num_max + 1).
     * L'array global solucions està correctament inicialitzat amb les posicions fixes.
     * @post Retorna true si existeix un camí vàlid des de (fil, col) fins a col·locar num_max. En cas afirmatiu,
     * les caselles del camí queden marcades al mapa. Si retorna false (no hi ha camí possible per aquesta branca),
     * el mapa queda exactament en el mateix estat que estava just abans de la crida gràcies al procés de backtracking.
     * */
    private boolean solver_rec(Integer fil, Integer col, Hidato mapa, Integer next_num, Integer num_max,
                               Integer files_total, Integer columnes_total, String adj) {
        // Comprovem fronteres
        if(fil >= files_total|| col >= columnes_total || fil < 0 || col < 0) {
            return false;
        }

        // CAS BASE: Hem col·locat i validat tots els números amb èxit
        if(next_num > num_max) {
            return true;
        }

        // Obtenim els veïns de la tile actual
        List<String> veins_valids = mapa.agafarVeins(fil, col);

        // OPCIÓ 1) Si ja tenim el número que busquem fixat a una tile
        if(solucions[next_num] != null) {
            // agafem les seves coordenades
            String coordObjectiu = solucions[next_num];

            // comprovem que la tile és veïna de la tile actual
            if(veins_valids.contains(coordObjectiu)) {
                String[] parts = coordObjectiu.split(",");
                int filNew = Integer.parseInt(parts[0]);
                int colNew = Integer.parseInt(parts[1]);

                // Saltem a la tile i busquem el seg�ent
                if(solver_rec(filNew, colNew, mapa, next_num + 1, num_max, files_total, columnes_total, adj)) {
                    return true;
                }
            }
        }

        // OPCIÓ 2) Si el número no té coordenades, intentem triar un veí buit
        else {
            for(String vei : veins_valids) {
                String[] parts = vei.split(",");
                int filNew = Integer.parseInt(parts[0]);
                int colNew = Integer.parseInt(parts[1]);

                Tile tileVei = mapa.obtenirTile(filNew, colNew);

                if(tileVei != null && tileVei.getValor().equals("?") && !tileVei.getAgafada()) {
                    // Marquem la tile
                    tileVei.setAgafada(true);
                    tileVei.setValor(String.valueOf(next_num));

                    // Crida recursiva
                    if(solver_rec(filNew, colNew, mapa, next_num+1, num_max,
                                files_total, columnes_total, adj)) {
                        return true;
                    }

                    // Backtracking
                    tileVei.setAgafada(false);
                    tileVei.setValor("?");
                }
            }
        }
        return false;
    }
}