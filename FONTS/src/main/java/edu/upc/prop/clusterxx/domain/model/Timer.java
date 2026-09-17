package edu.upc.prop.clusterxx.domain.model;

/**
 * @file Timer.java
 *
 * @brief Definició i implementació de la classe Timer
 * Serveix per dona suport a la classe Game, on necessitem mesurar el temps transcorregut.
 *
 */
public class Timer {
    /**
     * @brief Long que ens permet obtenir referència de l'instant d'inici del timer.
     */
    private long tempsInicial;
    /**
     * @brief Long que ens permet anar sumant el temps transcorregut des de l'inici del timer cada cop que el manipulem.
     */

    private long tempsAcumulat;
    /**
     * @brief Booleà que ens permet saber si el timer està actiu o no.
     */
    private boolean activat;

    /**
     * @brief Creadora de la classe Timer. Inicialitza el Timer amb tempsAcumulat = 0 i activat = false
     *
     * @post Es crea un nou objecte timer amb les variables tempsAcumulat i activat inicialitzades
     */
    public Timer(){
        this.tempsAcumulat=0;
        this.activat = false;
    }

    /**
     * @brief Creadora de la classe Timer. Inicialitza el Timer amb un valor concret a tempsAcumulat i activat = false.
     *
     * @post Es crea un nou objecte timer amb les variables tempsAcumulat i activat inicialitzades
     */
    public Timer(long tempsAcumulat){
        this.tempsAcumulat=tempsAcumulat;
        this.activat = false;
    }

    /*
    * temps_inicial guarda el valor de referència sobre el que prenem la mesura
    * cada cop que fem un pause del timer, guardem el temps passat a tempsAcumulat
    * i quan tornem a iniciar el timer, com que no reiniciem tempsAcumulat, simplement
    * tornem a agafar una referència de temps per poder continuar afegint diferències de temps
    * a tempsAcumulat.
    * */

    /**
     * @brief Funció que inicialitza les estructures del timer per començar a prendre mesures del temps transcorregut.
     *
     * @post S'ha establert tempsInicial al temps del sistema (ajustat a les milèssimes) actual. S'ha establert que el timer està activat
     */
    public void iniciarTimer(){
        if(!activat){
            tempsInicial = System.currentTimeMillis(); //Obtenim el temps inicial del sistema (en milisegons)
            activat = true;
        }else{
            throw new IllegalStateException("No es pot iniciar el timer si ja està activat");
        }
    }

    /**
     * @brief Funció que pausa el timer.
     *
     * @post Si el timer estava activat, se suma el temps que ha transcorregut des de l'inici/represa del timer fins a l'instant actual al tempsAcumulat
     */
    public void pausarTimer(){
        if(activat){
            tempsAcumulat = (tempsAcumulat + System.currentTimeMillis()) - tempsInicial; //Temps acumulat és el temps des de l'inici del timer
            activat = false;
        }
    }

    /**
     * @brief Funció que reinicialitza un timer per poder tornar a utilitzar-lo de nou sense cap registre prèvi.
     *
     * @post El timer ha tornat a l'estat inicial (tempsAcumulat = 0 i activat = false)
     */

    public void reiniciarTimer(){
        tempsAcumulat = 0;
        activat = false;
    }

    /**
     * @brief Funció que ens retorna el temps que ha transcorregut des de l'inici del temporitzador sense necessitat de parar-lo.
     *
     * @post La funció retorna el temps transcorregut des de l'inici del timer fins al moment de la crida de la funció.
     */

    public long getTemps(){
        if(activat){ //Si està activat, cal fer la resta i obtenir-lo de la suma de temps acumulat
            return tempsAcumulat + (System.currentTimeMillis() - tempsInicial);
        }else{
            return tempsAcumulat;
        }
    }

    /**
     * @breif Funció que permet setejar un temps acumulat específic
     * @param tempsAcumulat Temps que s'ha de guardar al Timer
     * @post S'ha establert el valor desitjat a tempsAcumulat
     */
    public void setTempsAcumulat(long tempsAcumulat) {
        this.tempsAcumulat = tempsAcumulat;
    }
}
