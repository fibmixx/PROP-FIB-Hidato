package edu.upc.prop.clusterxx.domain.model;
import edu.upc.prop.clusterxx.domain.controladors.CtrlDomini;
import edu.upc.prop.clusterxx.domain.controladors.CtrlRepositori;

import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;

/**
 * @file Usuari.java
 *
 * @brief Definició i implementació de la classe Usuari.
 *
 * Serveix per a que qualsevol persona pugui interactuar amb el sistema.
 */
public class Usuari {
    /**
     * @brief Nom de l'usuari, que serà el seu identificador únic al sistema.
     */
    private String username;
    /**
     * @brief Contrasenya de l'usuari.
     */
    private String password;
    /**
     * @brief Millor puntuació a una partida
     */
    private int personalBest;
    //private Map<Integer, Ranking> rankingsPersonals;
    /**
     * @brief Ranking de tots els jugadors
     */
    private Ranking rankingGlobal;
    /**
     * @brief Tots els repositoris de l'usuari
     */
    private Map<Integer, Repositori> repositoris;

    /**
     * @brief Tots els jocs de l'usuari
     */
    private List<Game> games;

    /**
     * @brief Funció per obtenir el nom de l'usuari.
     *
     * @return Retorna el nom de l'usuari.
     */
    public String getUsername() {
        return username;
    }

    /**
     * @brief Funció per obtenir la contrasenya de l'usuari.
     *
     * @return Retorna la contrasenya de l'usuari.
     */
    public String getPassword() {
        return this.password;
    }

    /**
     * @brief Creadora de la classe Usuari.
     * @param username És el nom de l'usuari.
     * @param password És la contrasenya de l'usuari.
     *
     * @post Crea un Usuari amb nom username i contrasenya password, amb rècord personal 0, amb un ranking global i un conjunt buit de repositoris i jocs.
     */
    public Usuari(String username, String password) {
        this.username = username;
        this.password = password;
        this.personalBest = 0;
        //this.rankingsPersonals = new HashMap<Integer, Ranking>();
        this.rankingGlobal = RankingGlobal.getInstance();
        this.repositoris = new HashMap<>();
        CtrlRepositori cr = CtrlDomini.getInstance().getCtrlRepositori();
        /*int rep = cr.setRepositori(this.username + " repositori", this.username);
        try {
            this.repositoris.put(rep, cr.getRepositori(rep));
        } catch (Exception e) {
            System.out.println("Error al crear repositori per defecte: " + e.getMessage());
        }*/
        this.games = new ArrayList<>();
        //Random r = new Random();
        //int puntuacionAleatoria = r.nextInt(1000);
        updatePersonalBest(0);
    }

    /**
     * @brief Funció per a mirar si la contrasenya introduïda és la contrasenya associada a l'usuari.
     *
     * @param password
     * @return Retorna cert si el paràmetre passat és igual a la contrasenya del usuari.
     */
    public boolean isPassword(String password) {
        return this.password.equals(password);
    }

    /**
     * @brief Funció per obtenir el rècord de puntuació de l'usuari.
     *
     * @return Retorna el rècord de puntuació de l'usuari.
     */
    public int getPersonalBest(){
        return this.personalBest;
    }

    /**
     * @brief Creadora de la classe Usuari.
     * @param pb És el nou record personal.
     *
     * @post Actualitza el record personal de l'usuari i actualitza el ranking global.
     */
    public void updatePersonalBest(int pb) {
        this.personalBest = pb;
        this.rankingGlobal.updateScore(this.username, pb);
        /*for (Ranking r : rankingsPersonals.values()) {
            r.updateUser(this.username, pb);
        }*/
    }

    /**
     * @brief Funció per obtenir un repositori de l'usuari a partir del seu identificador.
     *
     * @param id És l'identificador del repositori que es vol obtenir.
     *
     * @post Retorna el repositori associat a l'identificador passat com a paràmetre, o null si no existeix cap repositori amb aquest identificador.
     */
    public Repositori getRepositori(int id) {
        return this.repositoris.get(id);
    }

    /**
     * @brief Funció per obtenir tots els repositoris de l'usuari.
     *
     * @return Retorna una còpia del mapa de repositoris de l'usuari.
     */
    public Map<Integer, Repositori> getRepositoris() {
        return new HashMap<>(this.repositoris);
    }

    /**
     * @brief Funció per crear un repositori de l'usuari a partir del seu nom.
     *
     * @param nom És el nom del repositori que es vol crear.
     *
     * @post Es crea un nou repositori amb el nom passat com a paràmetre i s'associa a l'usuari. El repositori es pot recuperar posteriorment a través del seu identificador.
     */
    public void crearRepositori(String nom) {
        Repositori r = new Repositori(nom);
        this.repositoris.put(r.getIdentificador(), r);
    }

    /**
     * @brief Funció per afegir un repositori a l'usuari.
     *
     * @param r És el repositori que es vol afegir a l'usuari.
     *
     * @post S'afegeix el repositori passat com a paràmetre a la llista de repositoris de l'usuari. El repositori es pot recuperar posteriorment a través del seu identificador.
     */
    public void afegirRepositori(Repositori r) {
        this.repositoris.put(r.getIdentificador(), r);
    }

    /**
     * @brief Funció per eliminar un repositori de l'usuari.
     *
     * @param id És l'identificador del repositori que es vol eliminar.
     *
     * @post S'elimina el repositori associat a l'identificador passat com a paràmetre, o no fa res si no existeix cap repositori amb aquest identificador.
     */
    public boolean borrarRepositori(int id) {
        /*
        if (this.repositoris.containsKey(id)) {
            if(this.repositoris.size() == 1) {
                System.out.println("No pots eliminar l'únic repositori que tens.");
                return false;
            }
            this.repositoris.remove(id);
            return true;
        } else {
            System.out.println("No existeix cap repositori amb aquest identificador.");
        }
        return false;

         */

        if (this.repositoris.containsKey(id)) {
            this.repositoris.remove(id);
            return true;
        } else {
            System.out.println("No existeix cap repositori amb aquest identificador.");
            return false;
        }
    }

    /**
     * @brief Funció per obtenir un joc de l'usuari a partir del seu identificador.
     *
     * @param id És l'identificador del joc que es vol obtenir.
     *
     * @post Retorna el joc associat a l'identificador passat com a paràmetre, o null si no existeix cap joc amb aquest identificador.
     */
    public Game getGame(int id) {
        return this.games.get(id);
    }

    /**
     * @brief Funció per afegir un joc a l'usuari.
     *
     * @param g És el joc que es vol afegir a l'usuari.
     *
     * @post S'afegeix el joc passat com a paràmetre a la llista de jocs de l'usuari. El joc es pot recuperar posteriorment a través del seu identificador.
     */
    public void addGame(Game g) {
        this.games.add(g);
    }

    /**
     * @brief Funció per crear un joc de l'usuari a partir d'un hidato.
     *
     * @param h És el hidato que es vol jugar en el nou joc.
     *
     * @post Es crea un nou joc associat a l'usuari i al hidato passat com a paràmetre, i s'associa a l'usuari. El joc es pot recuperar posteriorment a través del seu identificador.
     */
    public void crearGame (Hidato h) {
        Game g = new Game(this,h);
        this.games.add(g);
    }

    /**
     * @brief Funció per verificar si l'usuari és administrador.
     *
     * @return Retorna cert si l'usuari és administrador.
     */
    public boolean isAdmin() {
        return false;
    }

    /**
     * @brief Funció per imprimir els jocs de l'usuari.
     *
     * @post S'imprimeixen els jocs de l'usuari a la consola.
     */
    public void printGames() {
        if (games.isEmpty()) {
            System.out.println("No tens cap joc creat.");
        } else {
            System.out.println("Els teus jocs:");
            for (int i = 0; i < games.size(); ++i) {
                Game g = games.get(i);
                System.out.println("- Joc " + i + ": " + g.getHidato().getId() + " Puntuació: " + g.getPuntuation() + " Acabat: " + g.getFinished());
            }
        }
    }

    /**
     * @brief Funció per obtenir el ranking global de l'usuari.
     *
     * @return Retorna el ranking global de l'usuari.
     */
    public Ranking getRankingGlobal() {
        return this.rankingGlobal;
    }
}