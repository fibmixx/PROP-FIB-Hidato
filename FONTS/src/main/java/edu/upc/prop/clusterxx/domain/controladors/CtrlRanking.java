package edu.upc.prop.clusterxx.domain.controladors;

import edu.upc.prop.clusterxx.data.GestorRankingGlobal;
import edu.upc.prop.clusterxx.domain.model.Ranking;
import edu.upc.prop.clusterxx.domain.model.RankingGlobal;
import edu.upc.prop.clusterxx.domain.interficies.IGestorRankingGlobal;
import edu.upc.prop.clusterxx.domain.interficies.IGestorRankings;

import java.util.Map;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * @file CtrlRanking.java
 *
 * @brief Controlador per gestionar múltiples instàncies de Ranking.
 *
 * Gestiona els rànkings individuals i el rànquing global del sistema. Permet crear, esborrar i consultar puntuacions,
 * coordinant-se amb la capa de persistència.
 */
public class CtrlRanking {
    /**
     * @brief Gestor per dotar de persistència als rànkings.
     */
    private IGestorRankings rankings;

    /**
     * @brief Gestor per dotar de persistència al rànking global
     */

    private IGestorRankingGlobal GestorrankingGlobal;

    /**
     * @brief Rànking global del sistema, que conté les puntuacions de tots els usuaris.
     */
    private RankingGlobal global;
    /**
     * @brief Funció per a crear el controlador de la classe Ranking
     * @param rankings Enllaç al sistema de persistència de rankings.
     * @param rankingGlobal Enllaç al sistema de persistència del ranking global.
     * @post Crea el controlador de la classe Ranking, creant un mapa de rankings buit.
     */
    public CtrlRanking(IGestorRankings rankings, IGestorRankingGlobal rankingGlobal) {
        this.rankings = rankings; //Carreguem una instància de rànking individual -> Al crear la instància ja executa GestorRankings i els carrega tots de disc
        this.GestorrankingGlobal = rankingGlobal; //Carreguem una instància del Gestor rànking global

        this.global = RankingGlobal.getInstance(); //Creem la instància del rànking global (on estarà la info mentre no estigui a disc).
        // Aquest no seria this.global = RankingGlobal.getInstance();?
        this.GestorrankingGlobal.agafarRankingGlobal(this.global);
    }

    /**
     * @brief Funció per a obtenir el rànking associat amb un identificador donat.
     *
     * @param id Identificador del rànking
     * @return Retorna el rànking associat amb l'identificador donat. Si el rànking no existeix, es llança una excepció.
     */
    public Ranking getRanking(int id) {
        Ranking ranking = rankings.agafarRanking(id);
        if (ranking == null) {
            System.out.println("Error: El Ranking no Existe");
            throw new NoSuchElementException("Ranking No Existe");
        }
        return ranking;
    }

    /**
     * @brief Funció per a obtenir tots els rànkings existents.
     *
     * @return Retorna un mapa amb tots els rànkings existents, associant cadascun amb el seu identificador.
     */
    public Map<Integer, Ranking> getAllRankings() {
       //Abans de Persistència return new HashMap<>(rankings);
        return rankings.agafarTotsRankings();
    }

    /**
     * @brief Funció per a crear un nou rànking i afegir-lo al mapa de rànkings existents.
     *
     * @param creador El username de qui crea el ranking (normalment l'usuari loguejat).
     * @param participants Llista de usernames triats per entrar al ranking.
     *
     * @return Retorna l'identificador del ranking nou.
     */

    public int addRanking(String creador, List<String> participants) {
        Ranking new_r = new Ranking(creador, participants);
        int idRanking = new_r.getIdentificador();
        rankings.guardaRanking(idRanking, new_r);
        return idRanking;
    }

    /**
     * @brief Funció per a esborrar un rànking existent.
     *
     * @param id Identificador del rànking.
     * @post Esborra el rànking associat amb l'identificador donat.
     * @throws NoSuchElementException si el rànking no existeix.
     */
    public void deleteRanking(int id) {
        if (!rankings.teClau(id)) {
            System.out.println("Error: El Ranking no Existe");
            throw new NoSuchElementException("Ranking No Existe");
        }

        rankings.delRanking(id);
    }

    /**
     * @brief Funció per a obtenir el identificador del rànking global.
     *
     * @return Retorna el identificador del rànking global.
     */
    public int getGlobalRankingId() {
        //RankingGlobal r = RankingGlobal.getInstance(); // Cal?
        //GestorrankingGlobal.agafarRankingGlobal(r); // Cal?
        //return r.getIdentificador();
        return this.global.getIdentificador();
    }


    /**
     * @Brief Funció que permet actualitzar la puntuació d'un usuari en el rànking indicat.
     * @param idRanking identificador del rànking que volem actualitzar
     * @param username Nom de l'usuari que volem actualitzar el score
     * @param punts Número de punts que ha obtingut l'usuari
     * @post S'ha afegit una puntuació al rànking
     */

    public void afegirPuntuacioRanking(int idRanking, String username, int punts){
        try{
            Ranking r = getRanking(idRanking);
            r.updateScore(username, punts);
            rankings.guardaRanking(idRanking, r);
        }catch (Exception e){
            System.err.println("Error a l'afegir puntaució al ranking");
        }
    }

    /**
     * @brief Funció que permet guardar totes les puntuacions del ranking global al Disc (persistencia)
        @post S'ha guardat totes les puntuacions de rànking global a disc
     */

    public void guardaRankingGlobalDisc(){
        GestorrankingGlobal.guardaRankingGlobal(this.global);
    }
}
