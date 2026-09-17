package edu.upc.prop.clusterxx.domain.controladors;

import edu.upc.prop.clusterxx.data.*;
import edu.upc.prop.clusterxx.domain.enumerations.TipusDificultat;
import edu.upc.prop.clusterxx.domain.excepcions.HidatoInvalidException;
import edu.upc.prop.clusterxx.domain.model.*;
import edu.upc.prop.clusterxx.domain.interficies.*;

import java.util.List;

/**
 * @file CtrlDomini.java
 *
 * @brief Implementació del patró Factoria i Singleton per a la gestió de controladors.
 *
 * Aquesta classe actua com a punt central d'accés per a tota la lògica del sistema.
 * Garanteix que només existeixi una instància de cada controlador i
 * proporciona una interfície única per obtenir-los des de qualsevol punt de l'aplicació.
 */
public class CtrlDomini {
    /**
     * @brief Instància única de la Factoria
     */
    private static CtrlDomini instance;

    /**
     * @brief Referencia al controlador de gestió d'usuaris
     */
    private CtrlUsuari usuaris;

    /**
     * @brief Referencia al controlador de gestió d'hidatos
     */
    private CtrlHidato hidato;

    /**
     * @brief Referencia al controlador de gestió de repositoris
     */
    private CtrlRepositori repositori;

    /**
     * @brief Referencia al controlador de gestió de rankings
     */
    private CtrlRanking ranking;
    /**
     * @brief Referencia al controlador de gestió de jocs
     */
    private CtrlGame game;

    // ---------------------------------------------------------------
    /**
     * @brief Instància del gestor encarregat de la persistència física dels Hidatos.
     */
    private IGestorHidatos gestorHidatos;

    /**
     * @brief Instància del gestor encarregat de la persistència física dels Repositoris.
     */
    private IGestorRepositoris gestorRepositoris;

    /**
     * @brief Creadora de la classe CtrlDomini, privada per evitar la instanciació externa
     */
    private CtrlDomini() {
        // 3r Lliurament: canviar "StubGestor..." per "Gestor..."
        this.gestorHidatos = new GestorHidatos();
        this.gestorRepositoris = new GestorRepositoris();


        // Actualitzar el comptador d'IDs dels hidatos
        // per no reiniciar-les a 0 al tancar i obrir
        int maxId = 0;
        for (Integer id : gestorHidatos.all().keySet()) {
            if (id > maxId) maxId = id;
        }
        Hidato.setNumHidato(maxId + 1);
    };

    /**
     * @brief Obté l'instància única de la CtrlDomini.
     * Si no existeix, la crea.
     * @return L'instància de la factoria.
     */
    public static CtrlDomini getInstance() {
        if (instance == null) {
            instance = new CtrlDomini();
        }
        return instance;
    }

    /**
     * @brief Retorna el controlador d'usuaris.
     * Si és la primera vegada que se sol·licita, s'inicialitza el controlador.
     * @return Instància de CtrlUsuari.
     */
    public CtrlUsuari getCtrlUsuari() {
        if (usuaris == null) {
            // Ja no és un Stub, és la implementació real
            usuaris = new CtrlUsuari(new GestorUsuaris());
        }
        return usuaris;
    }

    /**
     * @brief Retorna el controlador d'Hidato.
     * Permet accedir a les funcionalitats de creació i resolució de taulers.
     * @return Instància de CtrlHidato.
     */
    public CtrlHidato getCtrlHidato() {
        if (hidato == null) {
            hidato = new CtrlHidato(this.gestorHidatos);
        }
        return hidato;
    }

    /**
     * @brief Retorna el controlador de repositoris.
     * Gestió de l'emmagatzematge de dades i fitxers.
     * @return Instància de CtrlRepositori.
     */
    public CtrlRepositori getCtrlRepositori() {
        if (repositori == null) {
            repositori = new CtrlRepositori(this.gestorRepositoris,this.gestorHidatos);
        }
        return repositori;
    }

    /**
     * @brief Retorna el controlador de rankings.
     * Permet gestionar les classificacions i puntuacions dels usuaris.
     * @return Instància de CtrlRanking.
     */
    public CtrlRanking getCtrlRanking() {
        if (ranking == null) {
            ranking = new CtrlRanking(new GestorRankings(), new GestorRankingGlobal());
        }
        return ranking;
    }

    /**
     * @brief Retorna el controlador de jocs.
     * Permet gestionar el joc actualment seleccionat i les funcionalitats associades.
     * @return Instància de CtrlGame.
     */
    public CtrlGame getCtrlGame() {
        if (game == null) {
            game = new CtrlGame(getCtrlUsuari(),getCtrlHidato(),new GestorGames());
        }
        return game;
    }


    // METODOS CTRLPRESENTACIÓ, ES SOLO INVOCAR METODOS DE OTROS CONTROLADORES
    /**
     * @brief Inicia sessió al sistema amb un usuari i contrasenya.
     * @param username Nom de l'usuari.
     * @param password Contrasenya de l'usuari.
     * @return Cert si l'inici de sessió és correcte, fals en cas contrari.
     */
    public boolean login(String username, String password) {
        return usuaris.login(username, password);
    }

    /**
     * @brief Registra un nou usuari al sistema.
     * @param username Nom del nou usuari.
     * @param password Contrasenya del nou usuari.
     * @throws IllegalArgumentException Si el nom d'usuari ja existeix o dades invàlides.
     * @post S'ha registrat un nou usuari al sistema.
     */
    public void addUsuari(String username, String password) throws IllegalArgumentException {
        usuaris.addUsuari(username, password);
    }

    /**
     * @brief Tanca la sessió de l'usuari actualment actiu.
     * @post S'ha tancat la sessió de l'usuari actualment actiu.
     */
    public void logout() {
        usuaris.logout();
    }

    /**
     * @brief Obté l'usuari que té la sessió activa actualment.
     * @return L'objecte Usuari loguejat (o null si no n'hi ha cap).
     */
    public Usuari getUsuariSessio() {
        return usuaris.getUsuariSessio();
    }

    /**
     * @brief Cerca i retorna un usuari pel seu nom.
     * @param username Nom de l'usuari a cercar.
     * @return L'objecte Usuari trobat.
     */
    public Usuari getUsuari(String username) {
        return usuaris.getUsuari(username);
    }

    /**
     * @brief Obté tots els usuaris registrats al sistema.
     * @return Un mapa amb tots els usuaris (clau: username).
     */
    public java.util.Map<String, Usuari> getAllUsuaris() {
        return usuaris.getAllUsuaris();
    }

    /**
     * @brief Elimina un usuari del sistema.
     * @param username Nom de l'usuari a eliminar.
     * @post S'ha eliminat l'usuari del sistema.
     */
    public void deleteUsuari(String username) {
        usuaris.deleteUsuari(username);
    }

    /**
     * @brief Neteja o reinicia el rànquing global del sistema.
     * @post S'ha netejat o reiniciat el rànquing global.
     */
    public void netejarRankingGlobal() {
        usuaris.netejarRankingGlobal();
    }

    /**
     * @brief Obté una llista amb els noms de tots els repositoris.
     * @return Llista de cadenes amb els noms dels repositoris.
     */
    public List<String> llistarNomsRepositoris() {
        return repositori.llistarNomsRepositoris();
    }

    /**
     * @brief Obté un repositori a partir del seu identificador.
     * @param id Identificador del repositori.
     * @return L'objecte Repositori.
     * @throws Exception Si el repositori no existeix.
     */
    public Repositori getRepositori(int id) throws Exception {
        return repositori.getRepositori(id);
    }

    /**
     * @brief Crea un nou repositori assignat a un usuari.
     * @param nom Nom del nou repositori.
     * @param owner Nom de l'usuari propietari.
     * @return L'identificador del nou repositori creat.
     * @throws Exception Si hi ha algun problema en la creació.
     */
    public int setRepositori(String nom, String owner) throws Exception {
        return repositori.setRepositori(nom, owner);
    }

    /**
     * @brief Elimina un repositori del sistema de forma permanent.
     * @param id Identificador del repositori a eliminar.
     * @post S'ha eliminat el repositori del sistema.
     * @throws Exception Si el repositori no existeix.
     */
    public void eliminarRepositori(int id) throws Exception {
        repositori.eliminarRepositori(id);
    }

    /**
     * @brief Carrega a memòria els repositoris i dades associades a un usuari.
     * @param username Nom de l'usuari.
     * @post S'han carregat a memòria els repositoris i dades associades a un usuari.
     */
    public void carregarDadesUsuari(String username) {
        repositori.carregarDadesUsuari(username);
    }

    /**
     * @brief Estableix un repositori com a actiu o seleccionat.
     * @param repoId Identificador del repositori.
     * @post S'ha establert un repositori com a actiu.
     * @throws Exception Si l'identificador no és vàlid.
     */
    public void setSeleccionat(int repoId) throws Exception {
        repositori.setSeleccionat(repoId);
    }

    /**
     * @brief Afegeix un objecte Hidato al repositori que està actualment actiu.
     * @param h L'Hidato a afegir.
     * @post S'ha afegit un Hidato al repositori actiu.
     * @throws Exception Si no hi ha cap repositori actiu.
     */
    public void afegirHidatoARepoActiu(Hidato h) throws Exception {
        repositori.afegirHidatoARepoActiu(h);
    }

    /**
     * @brief Obté tots els rànquings individuals del sistema.
     * @return Mapa amb tots els rànquings.
     */
    public java.util.Map<Integer, Ranking> getAllRankings() {
        return ranking.getAllRankings();
    }

    /**
     * @brief Crea un nou rànquing associat a diversos participants.
     * @param creador Nom de l'usuari creador.
     * @param participants Llista de noms dels usuaris participants.
     * @return L'identificador del rànquing creat.
     * @throws Exception Si els paràmetres són invàlids.
     */
    public int addRanking(String creador, List<String> participants) throws Exception {
        return ranking.addRanking(creador, participants);
    }

    /**
     * @brief Retorna l'identificador únic del rànquing global.
     * @return ID del rànquing global.
     */
    public int getGlobalRankingId() {
        return ranking.getGlobalRankingId();
    }

    /**
     * @brief Elimina un rànquing del sistema.
     * @param id Identificador del rànquing a esborrar.
     * @post S'ha eliminat el rànquing del sistema.
     * @throws Exception Si el rànquing no existeix.
     */
    public void deleteRanking(int id) throws Exception {
        ranking.deleteRanking(id);
    }

    /**
     * @brief Obté un rànquing específic per la seva ID.
     * @param id Identificador del rànquing.
     * @return L'objecte Ranking.
     */
    public Ranking getRanking(int id) {
        return ranking.getRanking(id);
    }

    /**
     * @brief Obté la instància única del Rànquing Global.
     * @return L'objecte RankingGlobal.
     */
    public Ranking getRankingGlobal() {
        return RankingGlobal.getInstance();
    }

    /**
     * @brief Crea una nova partida normal (1 jugador).
     * @param h Hidato sobre el qual es jugarà.
     * @param u Usuari que jugarà la partida.
     * @post S'ha creat una partida normal.
     * @throws Exception Si no es pot crear la partida.
     */
    public void createGame(Hidato h, Usuari u) throws Exception {
        game.createGame(h, u);
    }

    /**
     * @brief Crea una partida cooperativa.
     * @param h Hidato sobre el qual es jugarà.
     * @param u Usuari principal de la partida.
     * @post S'ha creat una partida cooperativa.
     * @throws Exception Si no es pot crear la partida.
     */
    public void createCoopGame(Hidato h, Usuari u) throws Exception {
        game.createCoopGame(h, u);
    }

    /**
     * @brief Crea una partida competitiva entre dos jugadors.
     * @param h Hidato sobre el qual es jugarà.
     * @param u1 Primer usuari.
     * @param u2 Segon usuari.
     * @post S'ha creat una partida competitiva.
     * @throws Exception Si no es pot crear la partida.
     */
    public void createCompetitiveGame(Hidato h, Usuari u1, Usuari u2) throws Exception {
        game.createCompetitiveGame(h, u1, u2);
    }

    /**
     * @brief Desa l'estat de la partida actualment activa.
     * @post S'ha desat la partida actual.
     */
    public void guardarPartidaActual() {
        game.guardarPartidaActual();
    }

    /**
     * @brief Estableix un objecte Game existent com a partida activa.
     * @param g L'objecte Game a seleccionar.
     * @post S'ha establert una partida com a activa.
     */
    public void setSelectedGame(Game g) {
        game.setSelectedGame(g);
    }

    /**
     * @brief Obté la partida actualment activa o seleccionada.
     * @return L'objecte Game actiu.
     */
    public Game getSelectedGame() {
        return game.getSelectedGame();
    }

    /**
     * @brief Valida si un valor introduït per l'usuari compleix els límits de l'Hidato.
     * @param valor El valor introduit en format String.
     * @param numMax El número màxim permès en aquest tauler.
     * @return Cert si el valor és vàlid, fals altrament.
     */
    public boolean validarValor(String valor, int numMax) {
        return game.validarValor(valor, numMax);
    }

    /**
     * @brief Comprova si una casella concreta del tauler pot ser modificada per l'usuari.
     * @param h L'Hidato a comprovar.
     * @param f Fila de la casella.
     * @param c Columna de la casella.
     * @return Cert si és modificable, fals si és fixa o invàlida.
     */
    public boolean esModificable(Hidato h, int f, int c) {
        return game.esModificable(h, f, c);
    }

    /**
     * @brief Inicia o reprèn el cronòmetre de la partida actual.
     * @post S'ha iniciat o reprès el cronòmetre.
     */
    public void startTimer() { game.startTimer(); }

    /**
     * @brief Atura el cronòmetre de la partida actual.
     * @post S'ha aturat el cronòmetre.
     */
    public void stopTimer() { game.stopTimer(); }

    /**
     * @brief Obté el temps transcorregut en la partida actual.
     * @return Temps en mil·lisegons.
     */
    public long getTemps() { return game.getTemps(); }

    /**
     * @brief Inicia el cronòmetre per al segon jugador (Mode competitiu).
     * @post S'ha iniciat el cronòmetre per al segon jugador.
     */
    public void startTimerCompany() { game.startTimerCompany(); }

    /**
     * @brief Atura el cronòmetre per al segon jugador (Mode competitiu).
     * @post S'ha aturat el cronòmetre per al segon jugador.
     */
    public void stopTimerCompany() { game.stopTimerCompany(); }

    /**
     * @brief Obté el temps transcorregut del segon jugador (Mode competitiu).
     * @return Temps en mil·lisegons.
     */
    public long getTempsCompany() { return game.getTempsCompany(); }

    /**
     * @brief Finalitza la partida actual i actualitza les puntuacions.
     * @param puntuacio Puntuació obtinguda al finalitzar.
     * @post S'ha finalitzat la partida actual i s'han actualitzat les puntuacions.
     */
    public void acabarGameSeleccionat(int puntuacio) {
        game.acabarGameSeleccionat(puntuacio);
    }

    /**
     * @brief Actualitza l'estat del tauler de l'Hidato actiu amb una nova matriu.
     * @param tauler Matriu de cadenes amb els nous valors.
     * @post S'ha actualitzat l'estat del tauler de l'Hidato actiu.
     */
    public void updateTauler(String[][] tauler) {
        hidato.updateTauler(tauler);
    }

    /**
     * @brief Calcula i retorna el número màxim actiu de l'Hidato seleccionat.
     * @return Número màxim.
     */
    public int calculNumMax_actiu() {
        hidato.imprimirMapaActiu();
        return hidato.calculNumMax_actiu();

    }

    /**
     * @brief Comprova si la solució actual del tauler és vàlida segons les regles de l'Hidato.
     * @param f Nombre de files.
     * @param c Nombre de columnes.
     * @param adj Tipus d'adjacència emprada.
     * @return Cert si la solució és vàlida i completa.
     */
    public boolean comprovarSolucioTauler(int f, int c, String adj) {
        return hidato.comprovarSolucioTauler(f, c, adj);
    }

    /**
     * @brief Estableix un Hidato específic com a Hidato actiu al controlador.
     * @param h L'Hidato a seleccionar.
     * @post S'ha establert un Hidato específic com a Hidato actiu.
     */
    public void seleccionarHidato(Hidato h) {
        hidato.seleccionarHidato(h);
    }

    /**
     * @brief Resol l'Hidato actiu actual i en retorna la solució.
     * @param max Valor màxim de l'Hidato.
     * @param f Nombre de files.
     * @param c Nombre de columnes.
     * @param adj Tipus d'adjacència.
     * @return Una matriu representant el tauler resolt.
     * @throws Exception Si no es pot resoldre.
     */
    public String[][] resoldreTaulerActual(int max, int f, int c, String adj) throws Exception {
        return hidato.resoldreTaulerActual(max, f, c, adj);
    }

    /**
     * @brief Obté l'Hidato que s'està manipulant o jugant actualment.
     * @return L'objecte Hidato actiu.
     */
    public Hidato getHidatoActiu() {
        return hidato.getHidatoActiu();
    }

    /**
     * @brief Genera un nou Hidato amb la dificultat i adjacència especificades.
     * @param d Tipus de dificultat que volem passar a l'hidato.
     * @param adj Tipus d'adjacència que volem per a l'Hidato
     * @param num_mapa Tipus de mapa seleccionat per a l'Hidato.
     * @post S'ha generat un Hidato aleatoriament amb la dificultat i adjacencia passades com a parametre.
     *
     */
    public void generarNouHidatoPreset(TipusDificultat d, String adj, int num_mapa) {
        hidato.generarNouHidatoPreset(d, adj, num_mapa);
    }

    /**
     * @brief Genera un nou Hidato amb les dimensions, el valor màxim, la probabilitat i l'adjacència especificades.
     * @param f Numero de files del nou hidato.
     * @param c Numero de columnes del nou hidato
     * @param max Numero màxim a posar dins del hidato
     * @param amagat Probabilitat d'amagar una tile
     * @param adj Tipus d'adjacència que volem pel nou Hidato Generat
     * @param num_mapa Selecciona quin mapa volem utilitzar.
     * @post S'ha generat un Hidato aleatoriament amb les dimensions, el valor màxim, la probabilitat i l'adjacencia passades com a parametre.
     */
    public void generarNouHidatoCustom(int f, int c, int max, double amagat, String adj, int num_mapa) {
        hidato.generarNouHidatoCustom(f, c, max, amagat, adj, num_mapa);
    }

    /**
     * @brief Neteja totes les caselles modificables del tauler (reinicia l'Hidato al seu estat inicial).
     * @post S'han netejat totes les caselles modificables del tauler.
     */
    public void netejarPuntsTauler() {
        hidato.netejarPuntsTauler();
    }

    /**
     * @brief Amaga valors del tauler segons la probabilitat configurada a l'Hidato.
     * @post S'han amagat valors del tauler segons la probabilitat configurada.
     */
    public void amagarMapaTauler() {
        hidato.amagarMapaTauler();
    }

    /**
     * @brief Carrega un tauler d'Hidato a partir de dimensions, una matriu i regles d'adjacència.
     * @param f Nombre de files.
     * @param c Nombre de columnes.
     * @param matriu Matriu de dades del tauler.
     * @param adj Tipus d'adjacència.
     * @post S'ha carregat un tauler d'Hidato.
     * @throws Exception Si hi ha errors estructurals en la matriu.
     */
    public void carregarTauler(int f, int c, String[][] matriu, String adj) throws Exception {
        hidato.carregarTauler(f, c, matriu, adj);
    }

    /**
     * @brief Valida que el tauler carregat sigui teòricament resoluble i vàlid.
     * @param f Nombre de files.
     * @param c Nombre de columnes.
     * @param adj Tipus d'adjacència.
     * @throws Exception Si hi ha algun altre error de procés.
     * @post S'ha validat que el tauler carregat sigui teòricament resoluble i vàlid.
     * @throws HidatoInvalidException Si el tauler no compleix les regles de l'Hidato.
     */
    public void validarTauler(int f, int c, String adj) throws Exception, HidatoInvalidException {
        hidato.validarTauler(f, c, adj);
    }

    /**
     * @brief Reinicia o neteja el tauler del joc actualment seleccionat, deixant només les caselles fixes i buidant les modificables.
     * @post S'ha reiniciat o netejat el tauler del joc actualment seleccionat.
     */
    public void resetTaulerSelectedGame() {
        game.resetTaulerSelectedGame();
    }

    /**
     * @brief Elimina un Hidato del repositori actiu.
     * @param h Identificador del Hidato a eliminar.
     * @param r Identificador del repositori del qual eliminar el Hidato.
     * @post S'ha eliminat el Hidato del repositori actiu.
     * @throws Exception Si no hi ha cap repositori actiu o si el Hidato no existeix en el repositori.
     */
    public void borrarHidato(int h, int r) throws Exception {
        repositori.esborrarHidato(h, r);
    }

    /**
     * @brief Comparteix un repositori amb un altre usuari.
     * @param idRepo Identificador del repositori a compartir.
     * @param nomUser Nom de l'usuari amb qui es vol compartir el repositori.
     * @post S'ha compartit el repositori amb l'usuari destinatari.
     * @throws Exception Si el repositori no existeix o si hi ha algun error durant el procés de compartició.
     */
    public void compartirRepositori(int idRepo, String nomUser) throws Exception {
        repositori.compartirRepositori(idRepo, nomUser);
    }
}
