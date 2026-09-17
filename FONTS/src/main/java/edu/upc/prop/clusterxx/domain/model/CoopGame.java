package edu.upc.prop.clusterxx.domain.model;

import java.time.LocalDateTime;

/**
 * @file CoopGame.java
 *
 * @brief Definició i implementació de la classe CoopGame.
 *
 * Serveix per a proporcionar la modalitat de joc CoopGame, una extensió de la partida base (Game) que afegeix
 * la gestió d'un segon cronòmetre independent per al seguiment de temps del jugador company
 * */
public class CoopGame extends Game {

    /**
     * @brief Objecte Timer associat al segon jugador.
     */
    private Timer timerCompany;

    /**
     * @param user   Usuari que ha iniciat el Game
     * @param hidato Mapa d'hidato que s'està jugant en aquest Game
     * @brief Creadora de la classe Game on se li passen un Usuari i in hidato al que estan relacionats
     * @post Es crea un Game amb un Timer pròpi, es registra la data i hora de creació i s'associa amb l'Usuari que l'ha creat i l'hidato que s'està jugant.
     */
    public CoopGame(Usuari user, Hidato hidato) {
        super(user, hidato);
        timerCompany = new Timer();
    }

    public CoopGame(Usuari user, Hidato hidato, boolean finished, int puntuation, long tempsAcumulat, LocalDateTime temps_ini, String[][] tauler) {
        super(user, hidato, finished, puntuation, tempsAcumulat, temps_ini, tauler);
        timerCompany = new Timer();
    }

    /**
     * @breif Creadora que permet crear un nou coopgame assignant el temps del company al Timer concret
     * @param user
     * @param hidato
     * @param finished
     * @param puntuation
     * @param tempsAcumulat
     * @param temps_ini
     * @param tauler
     * @param tempsCompany
     */

    public CoopGame(Usuari user, Hidato hidato, boolean finished, int puntuation, long tempsAcumulat, LocalDateTime temps_ini, String[][] tauler, long tempsCompany) {
        super(user, hidato, finished, puntuation, tempsAcumulat, temps_ini, tauler);
        timerCompany = new Timer(tempsCompany);
    }

    /**
     * @breif Funció que permet activar el timer del company que està jugant la mateixa partida cooperativa
     * @post S'ha iniciat el Timer associat al company de la partida que s'està executant actualment
     */
    public void startTimerCompany() {
        timerCompany.iniciarTimer();
    }

    /**
     * @breif Funció que permet parar el timer del company que està jugant la mateixa partida cooperativa
     * @post S'ha parat el timer del company que està jugant la mateixa partida cooperativa
     */
    public void stopTimerCompany() {
        timerCompany.pausarTimer();
    }

    /**
     * @breif Funció que permet reiniciar el timer del company que està jugant la mateixa partida cooperativa
     * @post S'ha reiniciat el timer del company que està jugant la mateixa partida cooperativa a valors per defecte
     */
    public void resetTempsCompany() {
        timerCompany.reiniciarTimer();
    }

    /**
     * @brief Funció que permet obtenir el temps del timer del company de la mateixa partida cooperativa
     * @return Es retorna el temps emprat fins al moment de la crida pel company de la partida cooperativa.
     */
    public long getTempsCompany() {
        return timerCompany.getTemps();
    }

}
