package edu.upc.prop.clusterxx.domain.model;

/**
 * @file Game.java
 *
 * @brief Definició i implementació de la classe Game.
 *
 * Serveix per proporcionar un Game, que és la unitat bàsica de gestió d'una partida i que conté el timer d'aquesta.
 */

import edu.upc.prop.clusterxx.domain.enumerations.TipusGame;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Game {
    /**
     * @brief LocalDateTime que ens permet saber en quin instant s'ha creat un Game en concret.
     */
    private LocalDateTime startTime;


    /**
     * @brief Timer pròpi del Game, que ens permet saber el temps emprat per jugar-lo.
     */
    private Timer timer;

    /**
     * @brief Usuari que ha iniciat el Game.
     */
    private Usuari user;

    /**
     * @brief Hidato al que s'està jugant en aquesta partida.
     */
    private Hidato hidato; //relació 1a* entre Hidato i Game

    /**
     * @brief Boolean que ens permet saber si un joc s'ha acabat o no.
     */
    private boolean finished;

    /**
     * @brief Puntuació al acabar la partida, comença en 0.
     */
    private int puntuation;

    /**
     * @brief Tauler de joc, que es va actualitzant a mesura que el jugador va jugant.
     */
    private String[][] tauler; //Estat actual de la partida, Hidato només ens guarda "l'estructura" del hidaot, no com estan els números per a una partida concreta

    /**
     * @brief Hidato original, que no es modificarà per poder obtenir la solució.
     */
    private Hidato hidatoOG;

    /**
     * @brief Indica quin tipus de game és el Game que estem utilitzant
     */
    private TipusGame tipusGame;

    /**
     * @brief Creadora de la classe Game on se li passen un Usuari i in hidato al que estan relacionats.
     *
     * @param user Usuari que ha iniciat el Game.
     * @param hidato Mapa d'hidato que s'està jugant en aquest Game.
     *
     * @post Es crea un Game amb un Timer pròpi, es registra la data i hora de creació i s'associa amb l'Usuari que l'ha creat i l'Hidato que s'està jugant.
     */
    public Game(Usuari user,Hidato hidato){ //Creadora de la classe Game per quan es crea de 0
        if (user == null){
            throw new NullPointerException("L'usuari és null!");
        }
        if (hidato == null){
            throw new NullPointerException("Hidato és null!");
        }
        this.startTime = LocalDateTime.now();
        this.timer = new Timer(); //Creem un nou timer.
        this.user = user; //Donat que a l'esquema UML es diu que cada Game té associat un Usuari
        this.hidato = new Hidato(hidato); //Donat que a l'esquema UML es diu que cada Game té associat un únic Hidato. Es crea copia per poder crear més d'un joc amb un hidato
        this.hidatoOG = hidato; //Guardem el hidato original per a poder obtenir la solució quan sigui necessari
        this.puntuation = 0;
        this.tipusGame = TipusGame.CLASSIC;
        tauler = hidato.generarMatriu();
    }

    /**
     * @brief Constructor per a recuperar una partida des de disc.
     *
     * @param user Usuari que juga la partida.
     * @param hidato Hidato original associat.
     * @param finished Estat de finalització de la partida.
     * @param puntuation Puntuació aconseguida.
     * @param tempsAcumulat Temps transcorregut en segons.
     * @param tauler Estat de la matriu del tauler a mitjà jugar.
     *
     * @throws NullPointerException Si l'usuari o l'Hidato són nuls.
     */
    public Game(Usuari user, Hidato hidato, boolean finished, int puntuation, long tempsAcumulat, LocalDateTime temps_ini,String[][] tauler) { //Constructora per recuperar un game de Disc
        if (user == null){
            throw new NullPointerException("L'usuari és null!");
        }
        if (hidato == null){
            throw new NullPointerException("Hidato és null!");
        }
        this.startTime = temps_ini;
        this.timer = new Timer(tempsAcumulat);
        this.puntuation = puntuation;
        this.user = user;
        this.hidato = new Hidato(hidato);
        this.hidatoOG = hidato;
        this.tipusGame = TipusGame.CLASSIC;
        this.finished = finished;
        this.tauler = tauler;
    }

    /**
     * @breif Funció que retorna l'identificador del Game en format string (Username+hidatoId+datainici).
     * @return retorna en format string l'identificador del Game
     */
    public String getIdentificador(){
        DateTimeFormatter format_temps = DateTimeFormatter.ofPattern("ddMMyyyy_HHmmss");
        String dataFormat = startTime.format(format_temps); //Obtenim el startTime formatejat sense ":" que pot generar problemes a Windows si l'escribim com a nom de fitxer
        return this.user.getUsername() + "_" + this.hidatoOG.getId()+ "_" + dataFormat; //Retornem l'identificador que és el username+HidatoID+startTime (clau primària)
    }

    /**
     * @brief Funció per definir si un Game ha estat acabat o no.
     * @param finished Valor que ha de prendre el valor finished del Game.
     */
    public void setFinished(boolean finished){
        this.finished = finished;
    }

    /**
     * @breif Funció que ens retorna si el Game ha estat acabat o no.
     * @return Retorna True si el Game ha estat acabat o False si no és així.
     */
    public boolean getFinished(){
        return this.finished;
    }

    /**
     * @brief Getter que ens retorna l'instant d'inici del Game.
     *
     * @post Es retorna un LocalDateTime amb l'instant de l'inici de la partida.
     */
    public LocalDateTime getStartTime() {
        return startTime;
    }

    /**
     * @brief Funció que ens permet iniciar el timer associat amb el Game.
     *
     * @post S'inicialitza i inicia el cronòmetre associat a Game.
     */
    public void startTimer(){
        timer.iniciarTimer(); //Iniciem el timer
    }

    /**
     * @brief Funció que ens permet parar el Timer associat a la instància de Game.
     *
     * @post el timer associat a la instància de Game s'ha parat.
     */
    public void stopTimer(){
        timer.pausarTimer();
    }

    /**
     * @brief Getter que ens permet accedir al temps que s'ha emprat fins al moment utilitzant el Timer de la instància.
     *
     * @post Es retorna un long amb el temps (milèssimes de segon) transcorreguts des de l'inici del timer associat a la instància.
     */
    public long getTemps(){
        return timer.getTemps();
    }

    /**
     * @brief Funció que ens permet reiniciar a l'estat d'orígen al timer associat a la instància de Game.
     *
     * @post El timer associat a la instància s'ha reiniciat al seu estat inicial.
     */
    public void resetTemps(){
        timer.reiniciarTimer();
    }

    /**
     * @brief Getter que ens retorna el Timer del Game en concret.
     *
     * @post Es retorna el Timer del Game.
     */
    public Timer getTimer(){
        return timer;
    }

    /**
     * @brief Getter que ens retorna l'Usuari associat a aquest Game.
     *
     * @post Es retorna un Usuari que és el que està associat a la instància de Game concreta.
     */
    public Usuari getUser(){
        return user;
    }

    /**
     * @brief Getter que ens retorna l'Hidato al que està associat el Game.
     *
     * @post Es retorna un Hidato que és l'Hidato al que està associat el Game.
     */
    public Hidato getHidato(){
        return hidato;
    }

    /**
     * @Brief: Getter que ens retorna la puntuació actual del joc que s'està jugant.
     * @return: retorna la puntuació de la partida del moment en el qual es demana.
     */
    public int getPuntuation() {
        return this.puntuation;
    }

    /**
     * @Brief: Funció que retorna el tauler tal com està desenvolupada la partida en el moment de demanar-lo.
     * @return Retorna una matriu amb l'estat actual del mapa de l'Hidato.
     */
    public String[][] returnTauler() {
        return tauler;
    }

    /**
     * @Brief: Funció que ens permet modificar el tauler de joc a mesura que el jugador va jugant.
     * @param i Fila del tauler on es vol modificar el valor.
     * @param j Columna del tauler on es vol modificar el valor.
     * @param value Valor que es vol posar a la posició (i,j) del tauler.
     *
     * @post El tauler de joc s'ha actualitzat amb el nou valor a la posició (i,j).
     */
    public void setTaulerValue(int i, int j, String value) {
        if (i >= 0 && i < tauler.length && j >= 0 && j < tauler[i].length) {
            tauler[i][j] = value;
        }
    }

    /**
     * @brief: Funció que ens permet acabar un Game, establint el seu estat a acabat i actualitzant la puntuació final.
     * @param p Puntuació final que s'ha obtingut al acabar el Game.
     *
     * @post El Game s'ha marcat com a acabat i la puntuació final s'ha actualitzat. Si la puntuació final és superior al millor resultat personal de l'usuari, aquest es actualitza també.
     */
    public void acabarGame(int p) {
        finished = true;
        puntuation = p;
        if (user.getPersonalBest() < p) {
            user.updatePersonalBest(p);
        }
    }
    /**
     * @brief Funció que permet setejar el valor del paràmtere puntuació.
     * @param puntuation
     * @post S'ha definit el valor puntuació al valor que s'especifica
     */
    public void setPuntuation(int puntuation) {
        this.puntuation = puntuation;
    }

    /**
     * @brief Funció que ens permet obtenir l'objecte Hidato original de la partida.
     * @return Retorna l'objecte Hidato original.
     */
    public Hidato getHidatoOriginal() {
        return hidatoOG;
    }

    /**
     * @breif Funció per definir de quin tipus és un Game
     * @param tipusGame Tipus de Game que volem que sigui el game selccionat.
     */
    public void setTipusGame(TipusGame tipusGame) {
        this.tipusGame = tipusGame;
    }

    /**
     * @breif Funció per obtenir el tipus de game que és un Game en concret
     * @return Retorna el tipus de Game que és el game
     */
    public TipusGame getTipusGame() {
        return tipusGame;
    }

    /**
     * @brief Funció que ens permet reiniciar el tauler de joc a l'estat original del hidato, utilitzant el hidato original guardat a la instància.
     *
     * @post El tauler de joc s'ha reiniciat a l'estat original del hidato.
     */
    public void resetTauler(){
        this.tauler = hidatoOG.generarMatriu();
    }
}
