package edu.upc.prop.clusterxx.presentation.controladors;

import edu.upc.prop.clusterxx.domain.controladors.CtrlDomini;
import edu.upc.prop.clusterxx.domain.model.Game;
import edu.upc.prop.clusterxx.domain.model.Hidato;
import edu.upc.prop.clusterxx.domain.model.Usuari;
import edu.upc.prop.clusterxx.domain.model.Repositori;
import edu.upc.prop.clusterxx.domain.model.Ranking;

import edu.upc.prop.clusterxx.domain.model.RankingGlobal;
import edu.upc.prop.clusterxx.domain.model.CoopGame;
import edu.upc.prop.clusterxx.domain.model.CompetitiveGame;
import edu.upc.prop.clusterxx.domain.enumerations.TipusDificultat;
import edu.upc.prop.clusterxx.domain.excepcions.HidatoInvalidException;
import edu.upc.prop.clusterxx.presentation.paneles.*;
import edu.upc.prop.clusterxx.presentation.windows.MainWindow;

import javax.swing.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map;

/**
 * @file CtrlPresentacio.java
 * @brief Definició i implementació de la classe CtrlPresentacio.
 *
 * Actua com a controlador central de la capa de presentació utilitzant el patró Singleton,
 * coordinant la navegació entre panells i comunicant-se amb la capa de domini.
 */
public class CtrlPresentacio {
    /**
     * @brief Instància única del Controlador de Presentació.
     */
    private static CtrlPresentacio instance;

    /**
     * @brief Referència al controlador de domini.
     */
    private CtrlDomini ctrlDomini;

    /**
     * @brief Referència a la vista de la finestra principal.
     */
    private MainWindow mainWindow;

    /**
     * @brief Booleà que indica si el mode oscur està activat.
     */
    private boolean nightMode;

    /**
     * @brief Creadora de la classe CtrlPresentacio, privada per evitar la instanciació externa.
     */
    private CtrlPresentacio() {
        ctrlDomini = CtrlDomini.getInstance();
        nightMode = false;
    }

    /**
     * @brief Obté l'instància única de la classe CtrlPresentacio (Singleton).
     * @return L'instància de la factoria de presentació.
     */
    public static CtrlPresentacio getInstance() {
        if (instance == null) {
            instance = new CtrlPresentacio();
        }
        return instance;
    }

    /**
     * @brief Inicialitza la interfície gràfica obrint la finestra principal al panell de Login.
     * 
     * @post S'ha creat la finestra principal i s'ha mostrat el panell de Login.
     */
    public void run() {
        mainWindow = new MainWindow();
        mainWindow.setPanel(new LoginPanel());
    }

    /**
     * @brief Intenta iniciar sessió amb el username y password proporcionats.
     * @param username El nom de l'usuari.
     * @param password La contrasenya.
     * @post Si ha pogut iniciar sessió, carrega els repositoris de l'usuari i obre el menú principal. Si no, mostra un error.
     */
    public void login(String username, String password) {
        boolean success = ctrlDomini.login(username, password);
        if (success) {
            carregarRepositorisUsuari();
            irAlMenuPrincipal();
        } else {
            JOptionPane.showMessageDialog(null, "Login fallit. Verifica les teves credencials.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * @brief Intenta registrar un compte amb el username y password proporcionats.
     * @param username El nom de l'usuari.
     * @param password La contrasenya.
     * @post Si l'usuari és vàlid, es registra a la capa de persistència. Si no, mostra un missatge d'error.
     */
    public void register(String username, String password) {
        try {
            ctrlDomini.addUsuari(username, password);
            JOptionPane.showMessageDialog(null, "Usuari registrat correctament.", "Èxit", JOptionPane.INFORMATION_MESSAGE);
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(null, "L'usuari ja existeix.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * @brief Funció per a fer tancament de sessió (logout).
     * @post L'usuari actual tanca la seva sessió activa i es redirigeix a la finestra de login.
     */
    public void logout() {
        ctrlDomini.logout();
        mainWindow.setPanel(new LoginPanel());
    }

    /**
     * @brief Obté l'usuari actual en sessió.
     * @return Retorna l'usuari en sessió actual.
     */
    public Usuari getCurrentUser() {
        return ctrlDomini.getUsuariSessio();
    }

    /**
     * @brief Wrapper per a obtenir la llista de repositoris de l'usuari en sessió, ordenada per id (ascendent).
     * @return Retorna la llista de repositoris en format "id - nom", ordenada per id ascendent. Retorna null si hi ha un error.
     */
    public List<String> llistarRepositoris() {
        List<String> raw = ctrlDomini.llistarNomsRepositoris();
        if (raw == null) return null;

        List<String> copy = new ArrayList<>(raw);
        Collections.sort(copy, new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                try {
                    int id1 = Integer.parseInt(o1.split(" - ", 2)[0].trim());
                    int id2 = Integer.parseInt(o2.split(" - ", 2)[0].trim());
                    return Integer.compare(id1, id2);
                } catch (Exception e) {
                    return o1.compareTo(o2);
                }
            }
        });
        return copy;
    }

    /**
     * @brief Funció per a obtenir la llista de repositoris detallats, indicant el nombre de hidatos totals de cadascun.
     * @return Retorna la llista de repositoris detallats.
     */
    public List<String> llistarRepositorisDetallats() {
        List<String> base = llistarRepositoris();
        List<String> detallats = new ArrayList<>();
        if (base == null) return detallats;
        for (String s : base) {
            try {
                String[] parts = s.split(" - ", 2);
                int id = Integer.parseInt(parts[0].trim());
                Repositori r = ctrlDomini.getRepositori(id);
                int n = r.getHidatos().size();
                detallats.add(id + " - " + r.getNom() + " (" + n + " hidatos)");
            } catch (Exception e) {
                detallats.add(s);
            }
        }
        return detallats;
    }

    /**
     * @brief Wrapper per a crear un repositori per l'usuari en sessió actiu sense duplicitats.
     * @param nom El nom que se li vol assignar al nou repositori.
     * @post Mostra missatges gràfics d'error o d'èxit segons el resultat.
     */
    public void crearRepositori(String nom) {
        if (getCurrentUser() == null) {
            JOptionPane.showMessageDialog(null, "No hi ha cap usuari en sessió.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        String owner = getCurrentUser().getUsername();
        List<String> existents = llistarRepositoris();
        if (existents != null) {
            for (String s : existents) {
                String[] parts = s.split(" - ", 2);
                if (parts.length == 2) {
                    String nomExist = parts[1].trim();
                    if (nomExist.equalsIgnoreCase(nom.trim())) {
                        JOptionPane.showMessageDialog(null, "Ja existeix un repositori amb aquest nom.", "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                }
            }
        }

        try {
            int id = ctrlDomini.setRepositori(nom, owner);
            JOptionPane.showMessageDialog(null, "Repositori creat amb ID: " + id, "Èxit", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error creant el repositori: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * @brief Wrapper per a eliminar un repositori per identificador únic.
     * @param id L'identificador únic del repositori que es vol eliminar.
     * @post Elimina el repositori amb l'ID especificat o llença una alerta en cas d'error.
     */
    public void eliminarRepositori(int id) {
        try {
            ctrlDomini.eliminarRepositori(id);
            JOptionPane.showMessageDialog(null, "Repositori eliminat.", "Èxit", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error esborrant el repositori: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * @brief Wrapper per carregar els repositoris de l'usuari actual des del disc local persistit.
     * @post Carrega les dades de l'usuari a la capa de domini.
     */
    public void carregarRepositorisUsuari() {
        if (getCurrentUser() == null) return;
        ctrlDomini.carregarDadesUsuari(getCurrentUser().getUsername());
    }

    /**
     * @brief Obté els hidatos d'un repositori específic.
     * @param repoId ID del repositori.
     * @return Llista de parelles (ID, Hidato) continguts dins d'aquest repositori.
     */
    public List<Map.Entry<Integer, Hidato>> getHidatosDelRepositorio(int repoId) {
        try {
            if (ctrlDomini.getRepositori(repoId) == null) return new ArrayList<>();
            return new ArrayList<>(ctrlDomini.getRepositori(repoId).getHidatos().entrySet());
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error obtenint hidatos: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return new ArrayList<>();
        }
    }

    /**
     * @brief Wrapper per a llistar els rànkings personalitzats (no el global).
     * @return Retorna una llista de strings amb format "id - Rànking id" ordenada per id.
     */
    public List<String> llistarRankings() {
        Map<Integer, Ranking> tots = ctrlDomini.getAllRankings();
        List<String> llistaOrdenada = new ArrayList<>();
        if (tots == null) return llistaOrdenada;

        List<Integer> ids = new ArrayList<>(tots.keySet());
        Collections.sort(ids);
        for (Integer id : ids) {
            llistaOrdenada.add(id + " - Rànking " + id);
        }
        return llistaOrdenada;
    }

    /**
     * @brief Wrapper per a crear un nou rànking personalitzat amb un creador i participants.
     * @param creador Nom de l'usuari creador del rànking.
     * @param participants Llista amb els noms dels usuaris participants.
     * @return Retorna l'ID del rànking creat, o -1 en cas de fallada.
     */
    public int crearRanking(String creador, List<String> participants) {
        try {
            int id = ctrlDomini.addRanking(creador, participants);
            JOptionPane.showMessageDialog(null, "Rànking creat amb ID: " + id, "Èxit", JOptionPane.INFORMATION_MESSAGE);
            return id;
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error creant el rànking: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return -1;
        }
    }

    /**
     * @brief Wrapper per a eliminar un rànking personalitzat (restringeix l'esborrat del rànking global).
     * @param id ID del rànking que es vol eliminar.
     */
    public void eliminarRanking(int id) {
        int globalId = ctrlDomini.getGlobalRankingId();
        if (id == globalId) {
            JOptionPane.showMessageDialog(null, "No es pot eliminar el rànking global.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        try {
            ctrlDomini.deleteRanking(id);
            JOptionPane.showMessageDialog(null, "Rànking eliminat.", "Èxit", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error esborrant el rànking: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * @brief Wrapper per a obtenir les puntuacions d'un rànking personalitzat específic.
     * @param id Identificador de rànking.
     * @return Llista de cadenes amb format "usuari: puntuació punts".
     */
    public List<String> obtenirPuntuacionsRanking(int id) {
        try {
            List<String> puntuacions = new ArrayList<>();
            for (Ranking.PairUsuariPuntuacio p : ctrlDomini.getRanking(id).getPuntuacions()) {
                puntuacions.add(p.usuari + ": " + p.puntuacio + " punts");
            }
            return puntuacions;
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error obtenint les puntuacions: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return new ArrayList<>();
        }
    }

    /**
     * @brief Wrapper per a obtenir les puntuacions del rànking global de la plataforma.
     * @return Llista de cadenes amb les puntuacions de tots els jugadors del rànking global.
     */
    public List<String> obtenirPuntuacionsRankingGlobal() {
        try {
            List<String> puntuacions = new ArrayList<>();
            for (Ranking.PairUsuariPuntuacio p : ctrlDomini.getRankingGlobal().getPuntuacions()) {
                puntuacions.add(p.usuari + ": " + p.puntuacio + " punts");
            }
            return puntuacions;
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error obtenint les puntuacions globals: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return new ArrayList<>();
        }
    }


    /**
     * @brief Recupera totes les partides actuals/actives de l'usuari en sessió.
     * @return Llista d'objectes Game de l'usuari.
     */
    public List<Game> getGamesActuals() {
        if (getCurrentUser() == null) return new ArrayList<>();
        List<Game> games = new ArrayList<>();
        for (int i = 0; ; ++i) {
            try {
                Game g = getCurrentUser().getGame(i);
                if (g == null) break;
                games.add(g);
            } catch (IndexOutOfBoundsException ex) { break; }
        }
        return games;
    }

    /**
     * @brief Crea una nova partida del tipus especificat i l'afegeix a l'usuari actual.
     * @param hidato L'estructura de l'hidato del nou joc.
     * @param tipo El format del joc: "classic", "coop" o "competitive".
     * @post Si el tipus és vàlid, es crea la partida. Si no, mostra un missatge d'error.
     */
    public void crearGameDelTipo(Hidato hidato, String tipo) {
        if (getCurrentUser() == null) {
            JOptionPane.showMessageDialog(null, "No hi ha usuari connectat.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            Game g = null;
            if ("coop".equalsIgnoreCase(tipo)) {
                ctrlDomini.createCoopGame(hidato, getCurrentUser());
            } else if ("competitive".equalsIgnoreCase(tipo)) {
                ctrlDomini.createCompetitiveGame(hidato,getCurrentUser(),getCurrentUser());
            } else {
                ctrlDomini.createGame(hidato, getCurrentUser());
            }
            ctrlDomini.guardarPartidaActual();
            JOptionPane.showMessageDialog(null, "Joc creat correctament.", "Èxit", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error creant el joc: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * @brief Crea una nova partida del tipus especificat amb opció de tenir usuari desafiat per a competitius.
     * @param hidato L'estructura de l'hidato del nou joc.
     * @param tipo El format del joc: "classic", "coop" o "competitive".
     * @param usuariDesafiatNombre El nom d'usuari del desafiat (només per a "competitive").
     * @post Si el tipus és vàlid, es crea la partida. Si no, mostra un missatge d'error.
     */
    public void crearGameDelTipo(Hidato hidato, String tipo, String usuariDesafiatNombre) {
        if (getCurrentUser() == null) {
            JOptionPane.showMessageDialog(null, "No hi ha usuari connectat.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            Game g = null;
            if ("coop".equalsIgnoreCase(tipo)) {
                ctrlDomini.createCoopGame(hidato, getCurrentUser());
            } else if ("competitive".equalsIgnoreCase(tipo)) {
                ctrlDomini.createCompetitiveGame(hidato,getCurrentUser(),ctrlDomini.getUsuari(usuariDesafiatNombre));
            } else {
                ctrlDomini.createGame(hidato, getCurrentUser());
            }
            g = ctrlDomini.getSelectedGame();
            ctrlDomini.guardarPartidaActual();
            JOptionPane.showMessageDialog(null, "Joc creat correctament.", "Èxit", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error creant el joc: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * @brief Modifica la partida seleccionada actualment a la capa de domini.
     * @param game L'objecte Game que es vol establir com a partida activa.
     * @post La partida activa s'ha actualitzat amb l'objecte Game proporcionat.
     */
    public void setSelectedGame(Game game) {
        ctrlDomini.setSelectedGame(game);
    }

    /**
     * @brief Obté la partida seleccionada actualment a la capa de domini.
     * @return L'objecte Game de la partida activa, o null si no n'hi ha cap de seleccionada.
     */
    public Game getSelectedGame() {
        return ctrlDomini.getSelectedGame();
    }

    /**
     * @brief Obté l'estructura de l'Hidato associat a la partida seleccionada actualment.
     * @return L'objecte Hidato actiu, o null si no hi ha cap partida seleccionada.
     */
    public Hidato getHidatoSelectedGame() {
        return getSelectedGame() != null ? getSelectedGame().getHidato() : null;
    }

    /**
     * @brief Obté la matriu bidimensional de Strings que representa el tauler de la partida actual.
     * @return Una matriu de Strings amb l'estat actual de les caselles, o null si no hi ha partida activa.
     */
    public String[][] getTaulerSelectedGame() {
        return getSelectedGame() != null ? getSelectedGame().returnTauler() : null;
    }

    /**
     * @brief Modifica el contingut d'una casella específica del tauler de la partida actual.
     * @param fila Posició de la fila (índex basat en 0).
     * @param col Posició de la columna (índex basat en 0).
     * @param valor El nou valor en format String que es vol col·locar a la casella.
     * @post Si hi ha una partida activa, s'actualitza el valor de la casella indicada a la matriu del joc.
     */
    public void setTaulerValue(int fila, int col, String valor) {
        if (getSelectedGame() != null) getSelectedGame().setTaulerValue(fila, col, valor);
    }

    /**
     * @brief Valida si un valor numèric és coherent i entra dins del rang permès per a l'Hidato actual.
     * @param valor El valor textual introduït que es vol validar.
     * @param numMax El valor màxim admès per les dimensions de l'Hidato.
     * @return Retorna true si el valor és un número vàlid dins del rang [1, numMax], o el caràcter d'incògnita.
     */
    public boolean validarValor(String valor, int numMax) {
        return ctrlDomini.validarValor(valor, numMax);
    }

    /**
     * @brief Determina si una casella concreta d'un Hidato pot ser modificada pel jugador.
     * @param hidato L'objecte Hidato que s'està consultant.
     * @param fila Índex de la fila de la casella.
     * @param col Índex de la columna de la casella.
     * @return Retorna true si la casella és buida/modificable, false si és una casella predefinida d'inici o paret (#).
     */
    public boolean esModificable(Hidato hidato, int fila, int col) {
        return ctrlDomini.esModificable(hidato, fila, col);
    }

    /**
     * @brief Inicia el cronòmetre associat a la partida del jugador actual de la capa de domini.
     * @post El comptador de temps de la partida clàssica comença a córrer de forma incremental.
     */
    public void startTimer() {
        ctrlDomini.startTimer();
    }

    /**
     * @brief Atura el cronòmetre de la partida del jugador actual a la capa de domini.
     * @post El temps de la partida clàssica es pausa sense perdre el progrés acumulat.
     */
    public void stopTimer() {
        ctrlDomini.stopTimer();
    }

    /**
     * @brief Obté el temps total acumulat transcorregut pel jugador actual.
     * @return El temps total de joc en mil·lisegons.
     */
    public long getTemps() {
        return ctrlDomini.getTemps();
    }

    /**
     * @brief Inicia el cronòmetre de la partida de l'altre jugador (en modalitats multijugador com cooperatiu/competitiu).
     * @post El comptador de temps associat al company o rival comença a comptar de forma independent.
     */
    public void startTimerCompany() {
        ctrlDomini.startTimerCompany();
    }

    /**
     * @brief Atura el cronòmetre de la partida de l'altre jugador (en modalitats multijugador).
     * @post Es pausa el recompte de temps del company o rival de manera controlada.
     */
    public void stopTimerCompany() {
        ctrlDomini.stopTimerCompany();
    }

    /**
     * @brief Obté el temps total acumulat transcorregut per l'altre jugador (company o rival).
     * @return El temps de joc de l'altre usuari registrat en mil·lisegons.
     */
    public long getTempsCompany() {
        return ctrlDomini.getTempsCompany();
    }

    /**
     * @brief Sincronitza l'estat actual de la matriu del tauler del joc amb l'hidato actiu del domini.
     */
    public void updateHidatoActiu() {
        Game g = getSelectedGame();
        if (g != null) {
            ctrlDomini.updateTauler(g.returnTauler());
            ctrlDomini.calculNumMax_actiu();
        }
    }

    /**
     * @brief Comprova si la solució col·locada per l'usuari al tauler és correcta segons les regles de l'Hidato.
     * @return Retorna true si és correcte, false en cas contrari.
     */
    public boolean comprovarSolucio(int numFiles, int numCols, String adj) {
        updateHidatoActiu();
        return ctrlDomini.comprovarSolucioTauler(numFiles, numCols, adj);
    }

    /**
     * @brief Genera o recupera de forma automàtica la solució òptima del tauler actual seleccionat.
     * @return Matriu bidimensional de Strings representant el tauler resolt.
     */
    public String[][] obtenerSolucio(int numMax, int numFiles, int numCols, String adj) {
        try {
            ctrlDomini.seleccionarHidato(ctrlDomini.getSelectedGame().getHidatoOriginal());
            String[][] sol = ctrlDomini.resoldreTaulerActual(numMax, numFiles, numCols, adj);
            ctrlDomini.seleccionarHidato(ctrlDomini.getSelectedGame().getHidato());
            return sol;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * @brief Marca la partida actual seleccionada com a acabada i registra la puntuació obtinguda al rànking global.
     * @param puntuacion La puntuació obtinguda per l'usuari en aquesta partida.
     */
    public void acabarGameSeleccionat(int puntuacion) { ctrlDomini.acabarGameSeleccionat(puntuacion); }

    /**
     * @brief Detecta automàticament els tipus de cel·les i les adjacències configurades per a un Hidato específic.
     * @param hidato Objecte hidato a analitzar.
     * @return String identificador de la topologia ("quadrat", "quadrat_d", "triangle", "hexagon").
     */
    public String detectAdjacencia(Hidato hidato) {
        try {
            if (hidato != null && hidato.getTipusTiles() != null && hidato.getTipusAdj() != null) {
                String tipusTiles = hidato.getTipusTiles().toString();
                String tipusAdj = hidato.getTipusAdj().toString();
                if (tipusTiles.equals("QUADRAT")) {
                    if (tipusAdj.equals("ARESTA")) return "quadrat";
                    else return "quadrat_d";
                }
                else if (tipusTiles.equals("TRIANGLE")) return "triangle";
                else if (tipusTiles.equals("HEXAGON")) return "hexagon";
            }
        } catch (Exception e) { }
        return "quadrat";
    }

    /**
     * @brief Calcula quin és el valor numèric més alt col·locat actualment en el tauler de joc seleccionat.
     * @return El valor numèric màxim detectat.
     */
    public int calcularNumMax() {
        int max = ctrlDomini.calculNumMax_actiu();
        return max;
    }

    /**
     * @brief Obté el nombre de files del tauler del joc seleccionat.
     * @return El nombre de files, o 0 si no hi ha cap joc seleccionat.
     */
    public int getNumFilesSelectedGame() {
        String[][] tauler = getTaulerSelectedGame();
        return tauler != null ? tauler.length : 0;
    }

    /**
     * @brief Obté el nombre de columnes del tauler del joc seleccionat.
     * @return El nombre de columnes, o 0 si no hi ha cap joc seleccionat o el tauler és buit.
     */
    public int getNumColsSelectedGame() {
        String[][] tauler = getTaulerSelectedGame();
        return (tauler != null && tauler.length > 0) ? tauler[0].length : 0;
    }

    /**
     * @brief Carrega el panell del generador automàtic d'hidatos a la finestra principal.
     * @post S'ha carregat el panell del generador automàtic d'hidatos.
     */
    public void irAlGenerador() { mainWindow.setPanel(new GeneradorPanel(instance)); }

    /**
     * @brief Genera un nou Hidato amb la dificultat i adjacència especificades (presets).
     * @param dificultat La dificultat de l'Hidato.
     * @param adj El tipus d'adjacència.
     * @post S'ha generat un nou Hidato amb la dificultat i adjacència especificades.
     */
    public void generarNouHidato(TipusDificultat dificultat, String adj, int num_mapa) {
        ctrlDomini.generarNouHidatoPreset(dificultat, adj, num_mapa);
        ctrlDomini.netejarPuntsTauler();
        ctrlDomini.amagarMapaTauler();

        //Borrar
        //System.out.println("Impimint des del controlador presentacio: ");
        //ctrlDomini.getCtrlHidato().imprimirMapaActiu();
    }

    /**
     * @Brief Generar un nou Hidato a partir de pàrametres característics de Hidato Custom.
     * @param files Número de files del Hidato
     * @param columnes Número de columnes del Hidato
     * @param num_max Número màxim de nombres que tindrà l'Hidato
     * @param num_amagat Probabilitat d'amagar una Tiles de l'hidato
     * @param adj Tipus d'adjacència del Hidato.
     */

    public void generarNouHidatoCustom(int files, int columnes, int num_max, double num_amagat, String adj, int num_mapa) throws Exception, HidatoInvalidException {
        ctrlDomini.generarNouHidatoCustom(files, columnes, num_max, num_amagat, adj, num_mapa);
        ctrlDomini.netejarPuntsTauler();
        ctrlDomini.amagarMapaTauler();
        int files_update = ctrlDomini.getHidatoActiu().getNum_files();
        int columnes_update = ctrlDomini.getHidatoActiu().getNum_cols();
        ctrlDomini.validarTauler(files_update, columnes_update, adj);
    }

    /**
     * @brief Carrega un tauler manualment a la capa de domini i el valida (comprova si és completable).
     * @param files Nombre de files del tauler.
     * @param cols Nombre de columnes del tauler.
     * @param matriu Matriu bidimensional de Strings amb els valors de les caselles.
     * @param adj Tipus d'adjacència ("quadrat", "quadrat_d", "triangle", "hexagon").
     * @return true si el tauler és completable i vàlid.
     * @throws Exception Si el tauler no compleix les regles de format o no té solució.
     */
    public boolean carregarIValidarTaulerManual(int files, int cols, String[][] matriu, String adj) throws Exception, HidatoInvalidException {
        ctrlDomini.carregarTauler(files, cols, matriu, adj);
        ctrlDomini.validarTauler(files, cols, adj);
        return true;
    }


    /**
     * @brief Guarda l'Hidato actiu actualment en un repositori específic.
     * @param repoId ID del repositori on guardar l'Hidato.
     * @post S'ha afegit l'Hidato actual al repositori seleccionat.
     */
    public void guardarHidatoEnRepositori(int repoId) throws Exception {
        ctrlDomini.setSeleccionat(repoId);
        ctrlDomini.afegirHidatoARepoActiu(ctrlDomini.getHidatoActiu());
    }

    /* @brief Funció per a anar al panel d'administrador.
     *
     * @post Carrega el panel d'administrador.
     */
    public void irAlAdministrador() {
        mainWindow.setPanel(new AdministradorPanel(instance));
    }

    /**
     * @brief Carrega el panell previ abans de començar a jugar partides.
     * @post S'ha carregat el panell previ abans de començar a jugar partides.
     */
    public void irAlHidatoGame(int rows, int cols) { mainWindow.setPanel(new HidatoGamePanel()); }

    /**
     * @brief Carrega el panell de classificació de jugadors globals i locals.
     * @post S'ha carregat el panell de classificació de jugadors globals i locals.
     */
    public void irAlRanking() { mainWindow.setPanel(new RankingPanel(instance)); }

    /**
     * @brief Carrega la llista amb els hidatos continguts dins del repositori seleccionat.
     * @param repositorioId ID del repositori a llistar.
     * @post S'ha carregat el panell amb la llista d'hidatos del repositori seleccionat.
     */
    public void irAlLlistaHidatos(int repositorioId) { mainWindow.setPanel(new LlistaHidatosRepoPanel(instance, repositorioId)); }

    /**
     * @brief Carrega el panell principal d'administració de repositoris de l'usuari.
     * @post S'ha carregat el panell principal d'administració de repositoris de l'usuari.
     */
    public void irAlRepositori() { mainWindow.setPanel(new RepositoriPanel(instance)); }

    /**
     * @brief Carrega el panell del menú d'opcions principal de la plataforma.
     * @post S'ha carregat el panell del menú d'opcions principal.
     */
    public void irAlMenuPrincipal() { mainWindow.setPanel(new MenuPrincipalPanel()); }

    /**
     * @brief Obté l'estat actual del mode oscur.
     * @return Retorna true si el mode oscur està activat, false en cas contrari.
     */
    public boolean getNightMode() { return nightMode; }

    /**
     * @brief Canvia l'estat del mode oscur (toggle) i refresca el menú principal.
     * @post S'ha canviat l'estat del mode oscur i s'ha recarregat el panell del menú principal.
     */
    public void toggleNightMode() {
        nightMode = !nightMode;
        irAlMenuPrincipal();
    }

    /**
     * @brief Carrega el panell principal de joc interactiu clàssic (Individual).
     * @post S'ha carregat el panell principal de joc interactiu clàssic (Individual).
     */
    public void irAlJugarPanel() { mainWindow.setPanel(new JugarPanel()); }

    /**
     * @brief Obre el panel de joc cooperatiu per al Game seleccionat.
     * @post S'ha carregat el panel de joc cooperatiu per al Game seleccionat.
     */
    public void irAlJugarPanelCoop() { mainWindow.setPanel(new JugarCoopPanel()); }

    /**
     * @brief Obre el panel de joc competitiu per al Game seleccionat.
     * @post S'ha carregat el panel de joc competitiu per al Game seleccionat.
     */
    public void irAlJugarPanelComp() { mainWindow.setPanel(new JugarCompPanel()); }

    /**
     * @brief Cridada de continguts/usuaris simulats per a la realització de proves internes.
     * @post Continguts/usuaris simulats carregats per a realització de proves internes.
     */
    public void carregarProba() {
        //ctrlDomini.getCtrlUsuari().carregarProba();
        ctrlDomini.getCtrlGame();
        ctrlDomini.getCtrlHidato();
        ctrlDomini.getCtrlRepositori();
        ctrlDomini.getCtrlRanking();
        ctrlDomini.getCtrlUsuari();
        //CARGAR TODOS LOS CTRL
    }

    /*
     * @brief Carrega un tauler manualment a la capa de domini i el valida (comprova si és completable).
     * @param files Nombre de files del tauler.
     * @param cols Nombre de columnes del tauler.
     * @param matriu Matriu bidimensional de Strings amb els valors de les caselles.
     * @param adj Tipus d'adjacència ("quadrat", "quadrat_d", "triangle", "hexagon").
     * @return true si el tauler és completable i vàlid.
     * @throws Exception Si el tauler no compleix les regles de format o no té solució.
     *
    public boolean carregarIValidarTaulerManual(int files, int cols, String[][] matriu, String adj) throws Exception, HidatoInvalidException {
        ctrlDomini.carregarTauler(files, cols, matriu, adj);
        ctrlDomini.validarTauler(files, cols, adj);
        return true;
    }
    */

    /**
     * @brief Wrapper per a obtenir la llista de tots els usuaris del sistema.
     *
     * @return Retorna una llista de noms d'usuaris disponibles.
     */
    public List<String> obtenirLlistaUsuaris() {
        try {
            Map<String, Usuari> tots = ctrlDomini.getAllUsuaris();
            List<String> llistaUsuaris = new ArrayList<>(tots.keySet());
            Collections.sort(llistaUsuaris);
            return llistaUsuaris;
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error obtenint els usuaris: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return new ArrayList<>();
        }
    }

    /**
     * @brief Funció per a netejar el rànking global.
     * @post S'ha netejat el rànking global.
     */
    public void netejarRankingGlobal() {
        ctrlDomini.netejarRankingGlobal();
    }

    /**
     * @brief Funció per a esborrar un usuari del sistema.
     * @param user Usuari a esborrar.
     * @post S'ha esborrat l'usuari del sistema.
     */
    public void esborrarUsuari(String user) {
        ctrlDomini.deleteUsuari(user);
    }

    /**
     * @brief Funció per a guardar la partida actual del jugador en sessió.
     * @post S'ha guardat la partida actual del jugador en sessió a la capa de persistencia
     */
    public void guardarPartidaActual() {
        ctrlDomini.guardarPartidaActual();
    }

    /**
     * @brief Aplica una pista al tauler actual del juego seleccionado.
     * Selecciona aleatoriamente 2-3 celdas que no estén resueltas y muestra sus valores correctos.
     * @return Una lista de pares (fila, col) con las celdas actualizadas, o null si no hay solución.
     */
    public List<int[]> aplicarPista() {
        if (getSelectedGame() == null) return null;
        
        String[][] taulerActual = getSelectedGame().returnTauler();
        int numFiles = taulerActual.length;
        int numCols = taulerActual[0].length;
        String adj = detectAdjacencia(getSelectedGame().getHidato());
        int numMax = calcularNumMax();

        // Obtener la solución correcta
        String[][] solucion = obtenerSolucio(numMax, numFiles, numCols, adj);
        if (solucion == null) return null;

        // Buscar celdas no resueltas (que son "?" en el tauler actual)
        List<int[]> celdasNoResueltas = new ArrayList<>();
        for (int i = 0; i < numFiles; i++) {
            for (int j = 0; j < numCols; j++) {
                if (taulerActual[i][j] != null && taulerActual[i][j].equals("?")) {
                    celdasNoResueltas.add(new int[]{i, j});
                }
            }
        }

        if (celdasNoResueltas.isEmpty()) return null;

        // Seleccionar aleatoriamente 2-3 celdas (o menos si hay pocas)
        int numerosPistas = Math.min(3, Math.max(2, celdasNoResueltas.size() / 5 + 1)); // Se puede cambiar la fórmula para ajustar el número
        List<int[]> pistaAplicada = new ArrayList<>();

        // Mezclar la lista y tomar los primeros n elementos
        Collections.shuffle(celdasNoResueltas);
        for (int i = 0; i < Math.min(numerosPistas, celdasNoResueltas.size()); i++) {
            int[] coord = celdasNoResueltas.get(i);
            int fila = coord[0];
            int col = coord[1];
            String valorCorrect = solucion[fila][col];

            // Actualizar el tauler
            setTaulerValue(fila, col, valorCorrect);
            pistaAplicada.add(new int[]{fila, col});
        }
        
        return pistaAplicada;
    }

    /**
     * @brief Función para crear una nueva partida con un usuario y un hidato específico.
     * @param u El usuario que iniciará la partida.
     * @param h El hidato que se usará en la partida.
     * @post Se ha creado una nueva partida con el usuario y el hidato especificados.
     */
    public void createGame(Usuari u, Hidato h) throws Exception {
        ctrlDomini.createGame(h, u);
    }

    /**
     * @brief Función para reiniciar el tauler de la partida seleccionada al estado inicial del hidato original.
     * @post El tauler de la partida seleccionada se ha reiniciado al estado inicial del hidato original.
     */
    public void resetTaulerSelectedGame() {
        ctrlDomini.resetTaulerSelectedGame();
    }

    /**
     * @brief Función para borrar un hidato específico del sistema.
     * @param h Identificador del Hidato a eliminar.
     * @param r Identificador del repositori del qual eliminar el Hidato.
     * @post Se ha intentado borrar el hidato especificado, mostrando un mensaje de éxito o error según corresponda.
     */
    public void borrarHidato(int h, int r) throws Exception {
        ctrlDomini.borrarHidato(h,r);
    }

    /**
     * @brief Función para compartir un repositorio con otro usuario.
     * @param repoId Identificador del repositorio a compartir.
     * @param usuariDestinatari Nombre del usuario destinatario con el que se compartirá el repositorio.
     * @post Se ha intentado compartir el repositorio especificado con el usuario destinatario, mostrando un mensaje de éxito o error según corresponda.
     */
    public void compartirRepositori(int repoId, String usuariDestinatari) throws Exception {
        ctrlDomini.compartirRepositori(repoId, usuariDestinatari);
    }
}