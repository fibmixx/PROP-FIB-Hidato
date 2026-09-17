package edu.upc.prop.clusterxx.domain.model;

import java.time.LocalDateTime;

public class CompetitiveGame extends Game {

    /**
     * @param user   Usuari que ha iniciat el Game
     * @param hidato Mapa d'hidato que s'està jugant en aquest Game
     * @brief Creadora de la classe Game on se li pasen un Usuari i in hidato al que estan relacionats
     * @post Es crea un Game amb un Timer pròpi, es registra la data i hora de creació i s'associa amb l'Usuari que l'ha creat i l'hidato que s'està jugant.
     */
    /**
     * @brief Puntuació que ha obtingut l'usuari que ha desafiat
     */
    private int PuntuacioDesafiador;
    /**
     * @brief Usuari al que han desafiat
     */
    private Usuari usuariDesafiat;

    /**
     * @breif Funció creadora que permet crear un Game Competitiu
     * @param user Usuari que ha desafiat
     * @param hidato Hidato en que es durà a terme aquest joc competitiu
     * @param desafiat Usuari al que han desafiat amb aquest joc competitiu.
     * @post S'ha creat un joc competitiu amb els dos usuaris i l'hidato amb el que jugaran
     */
    public CompetitiveGame(Usuari user, Hidato hidato, Usuari desafiat) {
        super(user, hidato);
        usuariDesafiat = desafiat;
        PuntuacioDesafiador = 0;
    }

    public CompetitiveGame(Usuari user, Hidato hidato, boolean finished, int puntuation, long tempsAcumulat, LocalDateTime temps_ini, String[][] tauler) {
        super(user, hidato, finished, puntuation, tempsAcumulat, temps_ini, tauler);
        usuariDesafiat = user;
    }

    public CompetitiveGame(Usuari user, Hidato hidato, boolean finished, int puntuation, long tempsAcumulat, LocalDateTime temps_ini, String[][] tauler, Usuari desafiat) {
        super(user, hidato, finished, puntuation, tempsAcumulat, temps_ini, tauler);
        usuariDesafiat = desafiat;
    }

    /**
     * @breif Funció per obtenir l'Usuari que ha estat desafiat
     * @return Retorna l'usuari que ha estat desafiat
     */

    public Usuari getUsuariDesafiat() {
        return usuariDesafiat;
    }

    /**
     * @breif Funció que permet establir la puntuació que ha obtingut l'usuari desafiador
     * @param PuntuacioDesafiador Puntuació obtinguda per l'usuari desafiador.
     * @post S'ha establert la puntuació del desafiador al valor PuntuacioDesafiador
     */

    public void setPuntuacioDesafiador(int PuntuacioDesafiador) {
        if(PuntuacioDesafiador>0){
            this.PuntuacioDesafiador = PuntuacioDesafiador;
        }
    }

    /**
     * @breif Funció que permet obtenir la puntuació que ha obtinugt el desafiador.
     * @return Retorna la puntuació obtinguda pel desafiador.
     */
    public int getPuntuacioDesafiador() {
        return PuntuacioDesafiador;
    }
}
