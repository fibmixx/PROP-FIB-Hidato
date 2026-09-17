package edu.upc.prop.clusterxx.domain.controladors;

import edu.upc.prop.clusterxx.domain.algorismes.Generator;
import edu.upc.prop.clusterxx.domain.algorismes.Solver;
import edu.upc.prop.clusterxx.domain.algorismes.Validator;
import edu.upc.prop.clusterxx.domain.enumerations.TipusDificultat;
import edu.upc.prop.clusterxx.domain.excepcions.GeneradorNoPotGenerarCami;
import edu.upc.prop.clusterxx.domain.excepcions.HidatoInvalidException;
import edu.upc.prop.clusterxx.domain.excepcions.HidatoSenseSolucioException;
import edu.upc.prop.clusterxx.domain.model.Hidato;
import edu.upc.prop.clusterxx.domain.model.Tile;
import edu.upc.prop.clusterxx.domain.interficies.IGestorHidatos;

import java.util.NoSuchElementException;

/**
 * @file CtrlHidato.java
 *
 * @brief Controlador per gestionar múltiples instàncies d'Hidatos.
 *
 * És on l'usuari està creant o jugant un hidato. S'encarrega de cridar a Validator i Solver.
 * No necessita IGestorHidatos (persistència) perquè no desa res al disc dur, hidatoActiu és a la RAM.
 */
public class CtrlHidato {

    // El controlador guarda l'estat del sistema (el tauler amb què estem treballant)
    /**
     * @brief Estructura per desar l'estat del Tauler amb què estem treballant.
     */
    private Hidato hidatoActiu;
    /**
     * @brief Estructura per desar l'estat del Validator amb què estem treballant.
     */
    private Validator validator;
    /**
     * @brief Estructura per desar l'estat del Solver amb què estem treballant.
     */
    private Solver solver;
    /**
     * @brief Estructura per desar l'estat del Generator amb què estem treballant.
     */
    private Generator generator;

    // ---------------------------------------------
    /**
     * @brief Instància del gestor encarregat de la persistència física dels Hidatos.
     */
    private IGestorHidatos gestor;

    /**
     * @brief Constructora per defecte.
     * @param gestor Enllaç al sistema de persistència d'hidatos.
     * @post Inicialitza els atributs de la classe amb instàncies noves de cadascun.
     */
    public CtrlHidato(IGestorHidatos gestor) {
        this.validator = new Validator();
        this.solver = new Solver();
        this.hidatoActiu = new Hidato();
        this.generator = new Generator();
        this.gestor = gestor;
    }

    /**
     * @brief Rep les dades de la presentació i construeix el tauler amb la seva geometria.
     * @param files      nombre de files de l'Hidato.
     * @param columnes   nombre de columnes de l'Hidato.
     * @param matriuCrua matriu que conté els valors a posar en l'Hidato.
     * @param adj        tipus d'adjacència.
     * @return Retorna l'identificador de l'hidato actiu.
     */
    public int carregarTauler(int files, int columnes, String[][] matriuCrua, String adj) {
        this.hidatoActiu = new Hidato();
        this.hidatoActiu.setNum_files(files);
        this.hidatoActiu.setNum_cols(columnes);

        // Configurem l'adjacència i el tipus de caselles abans de res
        this.hidatoActiu.trad_adj(adj);
        // Un cop configurat, ja podem llegir els números
        this.hidatoActiu.interpretarMatriu(matriuCrua);

        return this.hidatoActiu.getId();
    }

    /**
     * @brief Funció que peremet obentir un Hidato de persistència i servir-lo
     * @param idHidato Identificador de l'Hidato que volem recuperar
     * @return Retorna un Objecte Hidato amb l'hidato especificat.
     */
    public Hidato getHidato(int idHidato) {
        Hidato result = gestor.get(idHidato);
        if (result == null) {
            System.out.println("Error: No existeix l'Hidato");
            throw new NoSuchElementException("No existeix Hidato");
        }
        return result;
    }

    /**
     * @brief Crida l'algorisme de validació de l'Hidato. Llença una excepció si no és vàlid.
     * @param files    nombre de files de l'Hidato.
     * @param columnes nombre de columnes de l'Hidato.
     * @param adj      tipus d'adjacència de l'Hidato.
     * @throws HidatoInvalidException Si hi ha números repetits, fora de rang o caràcters invàlids, o no té solució.
     */
    public void validarTauler(int files, int columnes, String adj) throws HidatoInvalidException {
        boolean esValid = validator.validarHidato(files, columnes, this.hidatoActiu, adj);
        if (!esValid) {
            throw new HidatoInvalidException("El tauler introduït no compleix les regles o no té solució.");
        }
    }

    /**
     * @brief Crida l'algorisme de resolució de l'Hidato. Llença una excepció si no és resoluble.
     * @param numMax   nombre màxim que es pot introduir per resoldre l'Hidato.
     * @param files    nombre de files de l'Hidato.
     * @param columnes nombre de columnes de l'Hidato.
     * @param adj      tipus d'adjacència de l'Hidato.
     * @return Retorna la còpia sense resoldre de l'Hidato, o null si no es pot resoldre.
     * @throws HidatoSenseSolucioException Si el tauler no té cap solució.
     */
    public String[][] resoldreTaulerActual(int numMax, int files, int columnes, String adj) throws HidatoSenseSolucioException {
        Hidato copia = new Hidato(this.hidatoActiu); // Còpia de seguretat
        Solver resolutor = new Solver();
        boolean resolt = resolutor.resoldre_ini(this.hidatoActiu, numMax, files, columnes, adj);

        if (!resolt) {
            this.hidatoActiu = copia; // Restaurem l'estat original si no hi ha solució (rollback)
            throw new HidatoSenseSolucioException("L'algorisme no ha pogut trobar cap solució per a aquest tauler.");
        }

        // Retornem l'estat en format matriu de Strings
        return obtenirEstatTauler(files, columnes);
    }

    /**
     * @brief Obté l'estat del tauler en un format bàsic perquè la presentació l'imprimeixi.
     * @param files    nombre de files de l'Hidato.
     * @param columnes nombre de columnes de l'Hidato.
     * @return Retorna l'adaptació de l'Hidato en forma de matriu.
     */
    public String[][] obtenirEstatTauler(int files, int columnes) {
        String[][] matriuSortida = new String[files][columnes];
        if (this.hidatoActiu == null) return matriuSortida;

        for (int i = 0; i < files; i++) {
            for (int j = 0; j < columnes; j++) {
                if(hidatoActiu.existeixTile(i,j)){
                    Tile t = hidatoActiu.obtenirTile(i, j);
                    if (t == null) {
                        matriuSortida[i][j] = "#"; // Fora del tauler
                    } else {
                        matriuSortida[i][j] = t.getValor();
                    }
                }else{
                    matriuSortida[i][j] = "#";
                }
            }
        }
        return matriuSortida;
    }

    /**
     * @brief Funció per crear un hidato nou dins del Controlador d'Hidato amb Preset i generar el seu tauler.
     * @param dificultat nivell de dificultat que tindrà l'Hidato.
     * @param adj      tipus d'adjacència que utilitzarà l'Hidato.
     * @param num_mapa Tipus de mapa que volem generar (Rectangular, Hexagonal o triangular)
     * @throws GeneradorNoPotGenerarCami Si el Generador no pot generar el camí.
     */

    //NUM_MAPA: 0-> Rectangular 1 -> Hexagonal /circular 2->Triangular
    public void generarNouHidatoPreset(TipusDificultat dificultat, String adj, int num_mapa) {
        this.hidatoActiu = new Hidato();
        this.hidatoActiu.setDificultat(dificultat);
        this.hidatoActiu.trad_adj(adj);
        this.hidatoActiu.setTipusMapa(num_mapa);
        try {
            Generator.Generator_ini(this.hidatoActiu);
        } catch (Exception e) {
            throw new GeneradorNoPotGenerarCami("Ha retornat error al controlador");
        }
    }

    /**
     * @Brief Funció que permet crear un hidato amb els atributs establerts de forma Custom i generar el seu tauler.
     * @param fila Número de files que tindrà l'hidato.
     * @param columna Número de columnes del hidato.
     * @param num_max Número màxim de nombre que tindrà l'hidato.
     * @param prob_amagat Probabilitat d'amagar les Tiles de l'hidato.
     * @param adj Tipus d'adjacència que tindrà l'hidato.
     * @throws GeneradorNoPotGenerarCami Si el Generador no pot generar el camí.
     */

    //NUM_MAPA: 0-> Rectangular 1 -> Hexagonal /circular 2->Triangular
    public void generarNouHidatoCustom(int fila, int columna, int num_max, double prob_amagat, String adj, int map){
        this.hidatoActiu = new Hidato();
        if(fila != columna && adj.equals("triangle")){
            throw new GeneradorNoPotGenerarCami("Si ves vol generar Hidato triangluar, s'ha de generar un tauler simètric");
        }
        this.hidatoActiu.trad_adj(adj);
        this.hidatoActiu.setNum_files(fila);
        this.hidatoActiu.setNum_cols(columna);
        this.hidatoActiu.setNum_max(num_max);
        this.hidatoActiu.setNum_amagat(prob_amagat);
        this.hidatoActiu.setTipusMapa(map);
        try {
            Generator.Generator_ini(this.hidatoActiu);
        } catch (Exception e) {
            throw new GeneradorNoPotGenerarCami("Ha retornat error al controlador");
        }
    }

    /**
     * @brief Funció que crida al mètode de netejarPunts de la classe hidato del hidato actual.
     */
    public void netejarPuntsTauler() {
        Generator.netejaPunts(this.hidatoActiu);
    }

    /**
     * @brief Funció que amaga les Tiles del tauler segons una probabilitat proporcionada.
     */
    public void amagarMapaTauler() {
        Generator.amagaMapa(this.hidatoActiu);
    }

    /**
     * @brief Funció que tradueix l'estat de l'Hidato actual al format estàndard.
     * @return Retorna un string amb el format de l'Hidato.
     */
    public String obtenirFormat() {
        return Generator.format(this.hidatoActiu);
    }

    /**
     * @brief Funció que ens permet imprimir el mapa actiu per pantalla.
     * @throws NullPointerException si l'Hidato proporcionat és null.
     */
    public void imprimirMapaActiu() {
        if (this.hidatoActiu == null) throw new NullPointerException("l'Hidato és null!");
        else {
            this.hidatoActiu.imprimirMapa();
        }
    }

    /**
     * @brief Comprova si el tauler actual està ben solucionat.
     * @param files    nombre de files.
     * @param columnes nombre de columnes.
     * @param adj      tipus d'adjacència.
     * @return true si està ben solucionat, false si no.
     */
    public boolean comprovarSolucioTauler(int files, int columnes, String adj) {
        if (this.hidatoActiu == null) return false;
        return this.validator.comprovarHidatoResolt(files, columnes, this.hidatoActiu, adj);

    }

    /**
     * @brief Getter que retorna el tauler actiu del controlador d'Hidato.
     * @return Retorna el tauler actiu del controlador d'Hidato.
     */
    public Hidato getHidatoActiu() {
        return hidatoActiu;
    }

    /**
     * @brief Funció que actualitza el tauler actiu del controlador d'Hidato amb una nova matriu.
     * @param matriu Matriu que conté els valors a posar en l'Hidato.
     */
    public void updateTauler(String[][] matriu) {
        this.hidatoActiu.interpretarMatriu(matriu);
    }

    /**
     * @return Retorna el número màxim de l'Hidato actiu.
     * @brief Funció que calcula el número màxim de l'Hidato actiu.
     * @post Canvia el valor de num_max al valor més gran de les Tiles que conté l'Hidato actiu.
     */
    public int calculNumMax_actiu() {
        //return hidatoActiu.calcNumMax();

        // ------------ FENT PROVES-----------------------
        // Si l'hidato ja té guardat el seu num_max, demana el getter, NO el torna a calcular
        if (hidatoActiu.getNum_max() > 0) {
            return hidatoActiu.getNum_max();
        }
        // Si fos 0, es recalcula
        return hidatoActiu.calcNumMax();

    }

    // -------------- FUNCIONS GESTOR HIDATOS
    /**
     * @brief Desa l'Hidato actiu actual al disc dur mitjançant el gestor.
     * @param idHidato L'identificador que li volem posar a l'Hidato.
     * @param idRepo L'identificador del repositori/carpeta on es desa.
     * @post Es desa l'Hidato actiu amb les seves dades a disc.
     */
    public void guardarHidatoActiu(int idHidato, int idRepo) {
        if (this.hidatoActiu != null) {
            // Crida al gestor que ja tenim com a atribut
            this.gestor.put(idHidato, this.hidatoActiu, idRepo);
        }
    }

    /**
     * @brief Funció que actualitza el hidato actiu del controlador d'Hidato amb un nou hidato.
     *
     * @param h El nou hidato que es vol posar com a actiu.
     *
     * @post El hidato actiu del controlador d'Hidato s'ha actualitzat amb el nou hidato proporcionat.
     *
     * @throws IllegalArgumentException si l'Hidato seleccionat és null.
     */
    public void seleccionarHidato(Hidato h){
        if (h != null) {
            this.hidatoActiu = h;
        } else {
            throw new IllegalArgumentException("L'Hidato seleccionat és null.");
        }
    }
}
