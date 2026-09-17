package edu.upc.prop.clusterxx.domain.controladors;

import edu.upc.prop.clusterxx.domain.enumerations.TipusGame;
import edu.upc.prop.clusterxx.domain.model.*;
import edu.upc.prop.clusterxx.domain.interficies.IGestorGames;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * @file CtrlGame.java
 * @brief Controlador per a la gestió de jocs seleccionats.
 *
 * Aquesta classe manté una referència al joc actualment seleccionat i proporciona mètodes per gestionar-lo.
 * És una part essencial del sistema per permetre la interacció amb el joc seleccionat a l'hora de jugar una partida.
 */
public class CtrlGame {
    /**
     * @brief Gestor per dotar de persistència els Games.
     */
    private IGestorGames gestorGames;

    /**
     * @breif Instància del controlador d'usuaris que està utilitzant el joc
     */

    private CtrlUsuari ctrlUsuari;

    /**
     * @brief Instància del controlador d'Hidatos que està utilitzant el joc.
     */

    private CtrlHidato ctrlHidato;

    /**
     * @brief Referència al joc actualment seleccionat.
     */
    private Game selectedGame;

    /**
     * @brief Creadora de la classe CtrlGame, inicialitza el joc seleccionat a null i instacia els gestors que utilitzarà.
     * @param ctrlUsuari
     * @param ctrlHidato
     * @param gestorGames
     * @post
     */
    public CtrlGame(CtrlUsuari ctrlUsuari, CtrlHidato ctrlHidato, IGestorGames gestorGames) {
        selectedGame = null;
        this.gestorGames = gestorGames;
        this.ctrlUsuari = ctrlUsuari;
        this.ctrlHidato = ctrlHidato;
        carregarGames();
    }

    /**
     * @brief Funció que permet guardar el game que actualment és el seleccionat a Disc.
     * @post Es desa la partida actual a disc.
     */
    public void guardarPartidaActual(){
        if(this.selectedGame != null){
            this.selectedGame.getTimer().pausarTimer(); //Pausem el Timer per que no segueixi corrent i es guardi el temps
            this.gestorGames.guardarGame(this.selectedGame); //Enviem al gestor de Games per tal que guardi el Game a Disc.
            this.selectedGame = null;
        }
    }

    /**
     * @brief Funció que permet posar que un Game és el seleccionat
     * @param selectedGame Game que volem posar com a seleccionat
     * @post S'ha assignat selectedGame com a game que està seleccionat
     */
    public void setSelectedGame(Game selectedGame) {
        this.selectedGame = selectedGame;
        if(selectedGame != null && selectedGame.getHidato() != null){
            this.ctrlHidato.seleccionarHidato(selectedGame.getHidato());
        }
    }

    /**
     * @brief Funció que permet obtenir el game que és el seleccionat
     * @return Retorna el game que està ara mateix com a seleccionat.
     */
    
    public Game getSelectedGame() {
        return selectedGame;
    }

    /**
     * @brief Funció que permet crear un nou Game des del controlador de Game
     * @param h Hidato al que està relacionat el nou game
     * @param u Usuari amb el que està relacionat el nou game.
     * @post S'ha creat el nou Game i està relacionat amb el Hidato h i l'usuari u
     */
    public void createGame(Hidato h, Usuari u) {
        // PREGUNTA: no s'hauria d'usar una copia de l'hidato???
        // Hidato copiaJugar = new Hidato(h);
        Game g = new Game(u, h);
        g.setTipusGame(TipusGame.CLASSIC);
        gestorGames.guardarGame(g);
        u.addGame(g);
        setSelectedGame(g);
    }

    /**
     * @brief Funció que permet crear un nou CompetitiveGame des del controlador de Game
     * @param h Hidato al que està relacionat el nou game
     * @param u Usuari amb el que està relacionat el nou game.
     * @param desafiat Usuari que és el desafiat en aquest CompetitiveGame
     * @post S'ha creat el nou CompetitiveGame i està relacionat amb el Hidato h, l'usuari u i el desafiat.
     */
    public void createCompetitiveGame(Hidato h, Usuari u, Usuari desafiat) {
        Game g = new CompetitiveGame(u, h, desafiat);
        g.setTipusGame(TipusGame.COMP);
        gestorGames.guardarGame(g);
        u.addGame(g);
        setSelectedGame(g);
    }

    /**
     * @brief Funció que permet crear un nou CoopGame des del controlador de Game
     * @param h Hidato al que está relacionado el nuevo game
     * @param u Usuario con el que está relacionado el nuevo game.
     * @post Se ha creado el nuevo CoopGame y está relacionado con el Hidato h y el usuario u
     */
    public void createCoopGame(Hidato h, Usuari u) {
        Game g = new CoopGame(u, h);
        g.setTipusGame(TipusGame.COOP);
        gestorGames.guardarGame(g);
        u.addGame(g);
        setSelectedGame(g);
    }

    /**
     * @brief Valida que el valor introducido sea correcto para el Hidato.
     * El valor debe ser un número entre 1 y numMax, o '?' para dejar en blanco.
     * @param valor String del valor a validar
     * @param numMax Número máximo permitido en el Hidato
     * @return true si el valor es válido, false en caso contrario
     */
    public boolean validarValor(String valor, int numMax) {
        if (valor.equals("?")) {
            return true;
        }
        try {
            int numValor = Integer.parseInt(valor);
            return numValor >= 1 && numValor <= numMax;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * @brief Comprueba si una casilla es modificable (no comienza resuelta/fija).
     * Las casillas que no son modificables son aquellas con startsSolved = true,
     * que incluyen valores iniciales fijos, forats (#) y huecos (*).
     * @param hidato Hidato del que se obtienen las Tiles
     * @param fila Número de fila de la casilla
     * @param col Número de columna de la casilla
     * @return true si la casilla es modificable, false si es fija
     */
    public boolean esModificable(Hidato hidato, int fila, int col) {
        if(hidato.existeixTile(fila,col)){
            Tile tile = hidato.obtenirTile(fila, col);
            if (tile == null) {
                return false;
            }
            return !tile.getStartSolved();
        }else{
            throw new RuntimeException("NO existeix la Tile (esModificable");
        }

    }

    /**
     * @brief Valida que las coordenadas (fila, columna) estén dentro del rango válido del tablero.
     * @param fila Número de fila
     * @param col Número de columna
     * @param numFiles Número total de filas del tablero
     * @param numCols Número total de columnas del tablero
     * @return true si las coordenadas son válidas, false en caso contrario
     */
    public boolean validarCoordenadas(int fila, int col, int numFiles, int numCols) {
        return fila >= 0 && fila < numFiles && col >= 0 && col < numCols;
    }

    /**
     * @brief Acaba el juego seleccionado con la puntuación dada.
     * @param puntuacion Puntuación obtenida al finalizar el juego
     * @post El juego seleccionado se marca como acabado y se actualiza su puntuación.
     */
    public void acabarGameSeleccionat(int puntuacion) {
        selectedGame.acabarGame(puntuacion);
        Usuari jugador = ctrlUsuari.getUsuariSessio();
        gestorGames.guardarGame(selectedGame); //Guardem el joc quan acabem
        if (jugador != null) {
            ctrlUsuari.updatePersonalBest(jugador.getUsername(), puntuacion);
            CtrlDomini.getInstance().getCtrlRanking().afegirPuntuacioRanking(selectedGame.getHidatoOriginal().getId(), jugador.getUsername(), puntuacion);
        }
    }

    /**
     * @brief Función para iniciar el timer del juego seleccionado.
     * Inicia el timer si el juego seleccionado no es null, lanza una excepción en caso contrario.
     */
    public void startTimer() {
        selectedGame.startTimer();
    }

    /**
     * @brief Función para detener el timer del juego seleccionado.
     * Detiene el timer si el juego seleccionado no es null, lanza una excepción en caso contrario.
     */
    public void stopTimer() {
        selectedGame.stopTimer();
    }

    /**
     * @brief Función para obtener el tiempo transcurrido del juego seleccionado.
     * @return Devuelve el tiempo transcurrido en milisegundos desde que se inició el timer del juego seleccionado.
     */
    public long getTemps() {
        return selectedGame.getTemps();
    }

    /**
     * @brief Función para iniciar el timer del compañero en un juego cooperativo.
     * Lanza una excepción si el juego seleccionado no es un CoopGame.
     */
    public void startTimerCompany() {
        if (selectedGame instanceof CoopGame) {
            ((CoopGame) selectedGame).startTimerCompany();
        }
        else {
            throw new IllegalStateException("El juego seleccionado no es un CoopGame.");
        }
    }

    /**
     * @brief Función para detener el timer del compañero en un juego cooperativo.
     * Detiene el timer del compañero si el juego seleccionado es un CoopGame, lanza una excepción en caso contrario.
     * @throws IllegalStateException si el juego seleccionado no es un juego cooperativo.
     */
    public void stopTimerCompany() {
        if (selectedGame instanceof CoopGame) {
            ((CoopGame) selectedGame).stopTimerCompany();
        }
        else {
            throw new IllegalStateException("El juego seleccionado no es un CoopGame.");
        }
    }

    /**
     * @brief Función para obtener el tiempo del compañero en un juego cooperativo.
     * @return Devuelve el tiempo del compañero si el juego seleccionado es un CoopGame, lanza una excepción en caso contrario.
     * @throws IllegalStateException si el juego seleccionado no es un juego cooperativo.
     */
     public long getTempsCompany() {
        if (selectedGame instanceof CoopGame) {
            return ((CoopGame) selectedGame).getTempsCompany();
        }
        else {
            throw new IllegalStateException("El juego seleccionado no es un CoopGame.");
        }

     }


    /**
     * @Brief Funció que retorna tots els objectes Game recuperats de memòria per la persistència.
     * @return Es retorna una llista amb tots els Games creats.
     */
    //Fer que retorni tots els games dins d'una llista de Games.
    public List<Game> getGames(){
        List<Game> llistagames = new ArrayList<>();
        List<List<String>> dades = gestorGames.agafarTotsGames();
        for(int i = 0; i < dades.size(); i++){
            List<String> game = dades.get(i);
            try{
                LocalDateTime tempsIni = LocalDateTime.parse(String.valueOf(game.get(0)));
                long tempsAcumulat = Long.parseLong(String.valueOf(game.get(1)));
                String idUsuari = String.valueOf(game.get(2));
                String idHidato = String.valueOf(game.get(3));
                boolean finished = Boolean.parseBoolean(String.valueOf(game.get(4)));
                int puntuacion = Integer.parseInt(String.valueOf(game.get(5)));
                int files =  Integer.parseInt(String.valueOf(game.get(6)));
                int columnes = Integer.parseInt(String.valueOf(game.get(7)));
                TipusGame Tgame = TipusGame.valueOf(game.get(8));
                String[][] tauler = new String[files][columnes];
                int idx = 9;
                for(int x = 0; x < files; ++x) {
                    String[] filaElements = game.get( idx + x).split(",");
                    for (int y = 0; y < columnes; ++y) {
                        tauler[x][y] = filaElements[y];
                    }
                }
                Usuari userPartida = ctrlUsuari.getUsuari(idUsuari);
                Hidato hidatoPartida = ctrlHidato.getHidato(Integer.parseInt(idHidato));


                if (Tgame==TipusGame.COOP) {
                    CoopGame newGame = new CoopGame(userPartida, hidatoPartida, finished, puntuacion, tempsAcumulat, tempsIni, tauler, Long.parseLong(String.valueOf(game.get(idx+files))));
                    newGame.setTipusGame(Tgame);
                    llistagames.add(newGame);
                }
                else if (Tgame==TipusGame.COMP) {
                    Game newGame = new CompetitiveGame(userPartida, hidatoPartida, finished, puntuacion, tempsAcumulat, tempsIni, tauler, ctrlUsuari.getUsuari(String.valueOf(game.get(idx+files))));
                    newGame.setTipusGame(Tgame);
                    llistagames.add(newGame);
                }
                else {
                    Game newGame = new Game(userPartida, hidatoPartida, finished, puntuacion, tempsAcumulat,tempsIni ,tauler);
                    newGame.setTipusGame(Tgame);
                    llistagames.add(newGame);
                }


            }catch (Exception e){
                System.err.println("Error reconstruint el Game en el índice " + i);
                e.printStackTrace();
            }
        }

        return llistagames;
    }

    /**
     * @brief Funció que carrega els Games a cada Usuari associat a aquests Games.
     * @post Cada Game de la llista ha estat associat al seu Usuari corresponent, afegint el Game a la llista de Games de l'Usuari.
     */
    public void carregarGames(){
        List<Game> games = getGames();
        System.out.println("Games carregats: " + games.size()); //Aqui me da 0
        for ( Game g : games){
            g.getUser().addGame(g);
        }
    }

    /**
     * @brief Funció que reinicia el tauler del joc seleccionat al seu estat inicial.
     * @post El tauler del joc seleccionat ha estat reiniciat al seu estat inicial, amb les caselles resoltas y no resoltas segons el hidato original.
     */
    public void resetTaulerSelectedGame() {
        if (selectedGame != null) {
            selectedGame.resetTauler();
        }
    }
}
