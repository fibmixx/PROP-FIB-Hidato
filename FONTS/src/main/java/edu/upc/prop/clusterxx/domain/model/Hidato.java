package edu.upc.prop.clusterxx.domain.model;

/*
    Classe Hidato que gestiona tot el Tauler i la resolució del hidato
 */

import edu.upc.prop.clusterxx.domain.enumerations.TipusAdjacencia;
import edu.upc.prop.clusterxx.domain.enumerations.TipusDificultat;
import edu.upc.prop.clusterxx.domain.enumerations.TipusTiles;
import edu.upc.prop.clusterxx.domain.excepcions.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @file Hidato.java
 *
 * @brief Definició i implementació de la classe Hidato.
 *
 * Serveix per a proporcionar un Hidato en el qual es juguen els Hidatos.
 */
public class Hidato {
    /**
     * @brief Variable per assignar IDs únics a cada Hidato.
     */
    private static int num_hidato = 0;

    /**
     * @brief Variable que guarda l'Identificador únic del Hidato.
     */
    private int id;

    /**
     * @brief Variable que determina la dificultat del Hidato.
     */
    private TipusDificultat dificultat;

    /**
     * @brief Variable per definir el tipus de Tile que utilitza l'hidato.
     */
    private TipusTiles tipusTiles;

    /**
     * @brief Variable per guardar el tipus d'adjacència que utilitzen les Tiles de l'Hidato.
     */
    private TipusAdjacencia tipusAdj;

    /**
     * @brief Variable per definir el nombre de files que té l'Hidato.
     */
    private int num_files;

    /**
     * @brief Variable que permet definir el nombre de columnes que té l'Hidato.
     */
    private int num_cols;

    /**
     * @breif Variable que ens indica el nombre màxim que tindrà l'Hidato.
     */
    private int num_max;

    /**
     * @brief Variable per emmagatzemar el percentatge de Tiles a amagar quan es generi un hidato.
     */

    private double num_amagat;

    /**
     * @brief Variable que servei per determinar quin tipus de mapa ha escollit l'usuari 0-> Quadrat, 1->Circular i 3-> Triangular.
     */

    private int tipus_mapa;


    /**
     * @breif Variable que serveix per emmagatzemar les Tiles que formen l'Hidato.
     */
    private Map<String, Tile> mapa;

    /**
     * @brief Estructura que ens serveix per determinar les coordenades d'una Tile dins del HashMap.
     * @param primer Coordenades en l'eix "X" de la Tile.
     * @param segon Coordenades en l'eix "Y" de la Tile.
     */
    private record Pair_Int(int primer, int segon){}

    /**
     * @brief Estructura que defineix les adjacències del tipus "Triangle" (posició parell).
     * Tenen veïns a l'esquerra, a la dreta i a dalt.
     */
    static List<Pair_Int> direccions_triangle_par = List.of(
            new Pair_Int(0, -1), // Esquerra: (fila, col-1)
            new Pair_Int(0, 1),  // Dreta:    (fila, col+1)
            //new Pair_Int(-1, 0)  // A dalt:   (fila-1, col)
            new Pair_Int(1, 0)  // A dalt:   (fila+1, col)
    );

    /**
     * @brief Estructura que defineix les adjacències del tipus "Triangle" (posició senar).
     * Tenen veïns a l'esquerra, a la dreta i a baix.
     */
    static List<Pair_Int> direccions_triangle_impar = List.of(
            new Pair_Int(0, -1), // Esquerra: (fila, col-1)
            new Pair_Int(0, 1),  // Dreta:    (fila, col+1)
            //new Pair_Int(1, 0)   // A baix:   (fila+1, col)
            new Pair_Int(-1, 0)   // A baix:   (fila-1, col)
    );

    // Calen dues llistes pel desplaçament de les files (Offset Coordinates)
    /**
     * @brief Estructura per definir les adjacències del tipus hexagon si aquests ocupen una posició parell.
     */
    static List<Pair_Int> direccions_hex_par = List.of(
            new Pair_Int(0, 1),  // Dreta:         (x, y+1)
            new Pair_Int(0, -1), // Esquerra:      (x, y-1)
            new Pair_Int(-1, 1), // Dalt Dreta:    (x-1, y+1)
            new Pair_Int(-1, 0), // Dalt Esquerra: (x-1, y)
            new Pair_Int(1, 1),  // Baix Dreta:    (x+1, y+1)
            new Pair_Int(1, 0)   // Baix Esquerra: (x+1, y)
    );

    /**
     * @brief Estructura que permet definir les adjacències del tipus hexagon si aquests ocupen una posició senar.
     */
    static List<Pair_Int> direccions_hex_impar = List.of(
            new Pair_Int(0, 1),   // Dreta:         (x, y+1)
            new Pair_Int(0, -1),  // Esquerra:      (x, y-1)
            new Pair_Int(-1, 0),  // Dalt Dreta:    (x-1, y)
            new Pair_Int(-1, -1), // Dalt Esquerra: (x-1, y-1)
            new Pair_Int(1, 0),   // Baix Dreta:    (x+1, y)
            new Pair_Int(1, -1)   // Baix Esquerra: (x+1, y-1)
    );
    /**
     * @brief Estructura que defineix les adjacències del tipus "Quadrat".
     */
    static List<Pair_Int> direccions_quadrat = List.of(
            new Pair_Int(1,0),//Dreta:          (x+1,y)
            new Pair_Int(-1,0),//Esquerra:      (x-1,y)
            new Pair_Int(0,1),//A baix:         (x,y+1)
            new Pair_Int(0,-1)//A Dalt:         (x,y-1)
    );

    /**
     * @brief Estructura que defineix les adjacències del tipus "Quadrat Costats i Vèrtex".
     */
    static List<Pair_Int> direccions_quadrat_diagonal = List.of(
            new Pair_Int(1,0),//Dreta:          (x+1,y)
            new Pair_Int(-1,0),//Esquerra:      (x-1,y)
            new Pair_Int(0,1),//A baix:         (x,y+1)
            new Pair_Int(0,-1),//A Dalt:        (x,y-1)
            new Pair_Int(-1,-1),//Superior esq  (x-1,y-1)
            new Pair_Int(1,-1),//Superior dret  (x+1,y-1)
            new Pair_Int(-1,1),//Inferior esq   (x-1,y+1)
            new Pair_Int(1,1)//Inferior dret    (x+1,y+1)
    );

    /**
     * @brief Constructora de la classe Hidato.
     * @return Retorna un nou objecte Hidato buit.
     */
    public Hidato(){
        this.id = num_hidato++;
        this.mapa = new HashMap<>();
        // Valors per defecte per evitar el NullPointerException als tests
        this.tipusTiles = TipusTiles.QUADRAT;
        this.tipusAdj = TipusAdjacencia.ARESTA;
        setDificultat(TipusDificultat.FACIL);
        this.tipus_mapa = 0;
    }

    /**
     * @brief Funció que permet crear i construir un hidato a partir de paràmetres passats.
     * @param dificultat Dificultat que tindrà l'Hidato creat.
     * @param tipusTiles Tipus de tile que tindrà el Hidato creat.
     * @param tipusAdj Tipus d'adjacència que tindrà l'Hidato creat.
     */
    public Hidato(TipusDificultat dificultat, TipusTiles tipusTiles, TipusAdjacencia tipusAdj){ //Constructora del la classe Hidato
        this.id = num_hidato++;
        setDificultat(dificultat);
        this.tipusTiles = tipusTiles;
        this.tipusAdj = tipusAdj;
        this.num_files = dificultat.getFiles();
        this.num_cols = dificultat.getColumnes();
        this.mapa = new HashMap<>();
        this.tipus_mapa = 0;
    }

    /**
     * @Brief Funció que permet la creació d'un nou Hidato partint de totes les dades.
         @param id Identificador de l'hidato.
         @param files Numero de files de l'hidato.
         @param columnes Número de columes del Hidato.
         @param Td Tipus de dificultat del Hidato.
         @param Tt Tipus de tile del Hidato.
         @param Ta Tipus d'adjacència del Hidato.
         @param num_amagat Probabilitat (sobre 1) d'amagar una Tile.
     */

    public Hidato(int id, int files, int columnes, TipusDificultat Td, TipusTiles Tt, TipusAdjacencia Ta, double num_amagat, int num_mapa){
        this.id = id;
        this.num_files = files;
        this.num_cols = columnes;
        this.dificultat = Td;
        this.tipusTiles = Tt;
        this.tipusAdj = Ta;
        this.num_amagat = num_amagat;
        this.mapa = new HashMap<>(); //Per posar les tiles noves;
        this.tipus_mapa = 0;
        setTipusMapa(num_mapa);
    }

    /**
     * @brief Constructora per crear un nou objecte Hidato a partir d'un ja existent a basa de copiar-lo.
     * @param original Hidato el qual volem copiar.
     * @return Retorna una còpia de l'objecte tauler proposat.
     */
    public Hidato(Hidato original){ //Per poder copiar Taulers
        if(original != null){
            this.id = original.getId();
            this.dificultat = original.dificultat;
            this.tipusTiles = original.tipusTiles;
            this.tipusAdj = original.tipusAdj;
            this.num_files = original.num_files;
            this.num_cols = original.num_cols;
            this.num_max = original.num_max;
            this.mapa = new HashMap<>();
            this.tipus_mapa = original.tipus_mapa;

            for(Map.Entry<String,Tile> entry: original.mapa.entrySet()){
                Tile t_original = entry.getValue();
                Tile t_copia = new Tile(this, t_original);
                this.mapa.put(entry.getKey(), t_copia);
            }
        }else{
            throw new IllegalArgumentException("L'hidato no pot ser null");
        }
    }

    /**
     * @brief Funció que permet obtenir el Id del Hidato en concret.
     * @return Retorna el valor de l'ID del Hidato.
     */
    public int getId() {
        return id;
    }

    /**
     * @brief Funció que ens permet modificar la dificultat del Hidato i establir el nombre de files i columnes segons el tipus de dificultat.
     * @param dificultat Valor que volem que obtingui el paràmetre dificultat del hidato.
     */
    public void setDificultat(TipusDificultat dificultat) {
        this.dificultat = dificultat;
        this.num_files = dificultat.getFiles();
        this.num_cols = dificultat.getColumnes();
        this.num_max = dificultat.getNumMax();
        this.num_amagat = dificultat.getNumAmagar();
    }

    /**
     * @brief Funció que ens permet obtenir la dificultat d'un Hidato.
     * @return Retorna la dificultat que té l'Hidato.
     */
    public TipusDificultat getDificultat() {
        return dificultat;
    }

    /**
     * @brief Funció que ens permet modificar el tipus de Tile que té l'Hidato.
     * @param tipusTiles Valor que tindrà el paràmetre tipusTiles de l'Hidato.
     * @post S'ha modificat la variable tipusTile de l'Hidato especificat.
     */
    public void setTipusTiles(TipusTiles tipusTiles) {
        this.tipusTiles = tipusTiles;
    }

    /**
     * @brief Funció que ens permet obtenir el tipus de Tile del que està compost un Hidato.
     * @return Retorna el tipus de tile que té l'Hidato.
     */
    public TipusTiles getTipusTiles() {
        return tipusTiles;
    }

    /**
     * @brief Funció que ens permet definir el tipus d'adjacència que té l'Hidato.
     * @param tipusAdj Valor que obtindrà el paràmetre tipusAdj de l'Hidato.
     * @post S'ha modificat l'Hidato per tal que passi a tenir el tipusAdj especificat.
     */
    public void setTipusAdj(TipusAdjacencia tipusAdj) {
        this.tipusAdj = tipusAdj;
    }

    /**
     * @brief Funció que ens permet obtenir el tipus d'adjacència que té un Hidato especificat.
     * @return Retorna el tipus d'adjacència que té un Hidato especificat.
     */
    public TipusAdjacencia getTipusAdj() {
        return tipusAdj;
    }

    /**
     * @brief Funció que ens permet modificar el nombre de files que té l'Hidato en concret.
     * @param num_files Valor que passarà a valer num_files del Hidato.
     * @post S'ha modificat el valor de num_files pel valor especificat.
     */
    public void setNum_files(int num_files) {
        if(num_files>0){
            this.num_files = num_files;
        }else{
            throw new DimensionsInvalidesException("El nombre de files ha de ser major que zero i menor o igual a 9. Rebut: " + num_files);
        }
    }

    /**
     * @brief Funció que tradueix String de tipus d'adjacència als valors que s'ha n de definir en les estructures de l'hidato per poder ser llegides correctament pel programa.
     * @param adjacencia Tipus d'adjacència concreta qu
     */

    public void trad_adj(String adjacencia) {
        if (adjacencia.equals("quadrat")) {
            this.tipusAdj = TipusAdjacencia.ARESTA;
            this.tipusTiles = TipusTiles.QUADRAT;
        } else if (adjacencia.equals("quadrat_d")) {
            this.tipusAdj = TipusAdjacencia.VERTEXIARESTA;
            this.tipusTiles = TipusTiles.QUADRAT;
        } else if (adjacencia.equals("triangle")) {
            this.tipusTiles=TipusTiles.TRIANGLE;
            this.tipusAdj=TipusAdjacencia.ARESTA;
        } else if (adjacencia.equals("hexagon")) {
            this.tipusTiles=TipusTiles.HEXAGON;
            this.tipusAdj = TipusAdjacencia.ARESTA;
        }else{
            throw new IllegalArgumentException("El tipus d'adjacència no és correcte. Rebut: "+adjacencia);
        }
    }

    /**
     * @brief Funció que permet obtenir el nombre de files d'un Hidato en concret.
     * @return Es retorna el nombre de files del hidato especificat.
     */
    public int getNum_files() {
        return num_files;
    }

    /**
     * @brief Funció que ens permet establir el nombre de columnes de l'Hidato.
     * @param num_cols Valor que tindrà el paràmetre num_cols del Hidato.
     * @post S'ha establert paràmetre num_cols al valor passat "num_cols".
     */
    public void setNum_cols(int num_cols) {
        if(num_cols > 0){
            this.num_cols = num_cols;
        }else{
            throw new DimensionsInvalidesException("El nombre de columnes ha de ser major que zero i menor o igual a 9. Rebut: " + num_cols);
        }
    }

    /**
     * @brief Funció que ens permet obtenir el nombre de columnes que té l'Hidato.
     * @return Retorna el número de columnes de l'Hidato especificat.
     */
    public int getNum_cols() {
        return num_cols;
    }

    /**
     * @brief Funció que permet posar el nombre màxim que hi ha dins de l'Hidato.
     * @param num_max Número màxim que hi haurà dins de l'Hidato.
     * @post Si num_max és superior a 1, el valor passat passa a ser num_max de l'Hidato actual.
     */
    public void setNum_max(int num_max) {
        if(num_max>=1 && num_max <= (num_files*num_cols)){
            this.num_max = num_max;
        }else{
            throw new DimensionsInvalidesException("EL num_max introduït no és vàlid. Rebut: " + num_max);
        }
    }

    /**
     * @brief Funció que retorna el nombre màxim de l'Hidato actual.
     * @return Es retorna el valor de num_max de l'Hidato.
     */
    public int getNum_max() {
        return num_max;
    }

    /**
     * @brief Funció per afegir casella dins d'un Hidato (HashMap).
     * @param x Coordenada X on es troba la nova Tile dins del HashMap.
     * @param y Coordenada Y on es troba la nova tile dins del HashMap.
     * @param tile Tile nova que la funció posa dins del HasMap en la posició (x,y).
     * @post La tile proporcionada s'ha posat dins del Hidato.
     */
    public void afegirTile(int x, int y, Tile tile) { // Per poder posar Tiles dins del mapa
        if(tile != null){
            String clau = x + "," + y; // La clau per trobar una casella en concret ser� la seva fila + la seva columna
            mapa.put(clau, tile);
        }else{
            throw new IllegalArgumentException("La tile és null");
        }
    }

    /**
     * @brief Funció que permet agafar el valor de probabilitat d'amagar les Tiles del hidato
     * @return Retorna el valor que l'hidato té establert per amagar les seves Tiles.
     */
    public double getNum_amagat(){
        return this.num_amagat;
    }

    /**
     * @breif Funció que permet establir quin valor ha d'establir-se com a probabilitat per amagar les Tiles del hidato en concret
     * @param num_amagat Probabilitat d'amagar una Tile del Hidato
     * @post S'ha establert la probabilitat d'amagar una Tile del Hidato al valor que es proporciona amb "num_amagat".
     */

    public void setNum_amagat(double num_amagat) {
        if(num_amagat>=0 && num_amagat<=1){
            this.num_amagat = num_amagat;
        }else{
            throw new DimensionsInvalidesException("Num_amagat ha d'estar entre 0 i 1. Rebut: " + num_amagat);
        }
    }
    /**
     * @brief Funció per comprovar si existeix una tile dins del mapa.
     * @param x Coordenada X on es troba la nova Tile dins del HashMap.
     * @param y Coordenada Y on es troba la nova tile dins del HashMap.
     * @return Retorna bool indicant si la tile existeix o no dins del Hidato.
     */
    public boolean existeixTile(int x, int y){
        return mapa.containsKey(x + "," + y);
    }

    /**
     * @brief Funció que retorna una Tile indicada per una posició fila, columna.
     * @param x Coordenada X on es troba la nova Tile dins del HashMap.
     * @param y Coordenada Y on es troba la nova tile dins del HashMap.
     * @return Retorna la Tile especificada.
     */
    public Tile obtenirTile(int x, int y){
        String clau = x + "," + y;
        if(mapa.containsKey(clau)){ //Primer comprovem que existeixi
            return mapa.get(clau); //En java sempre passarem un punter per passar l'objecte, per tant, sabem que estem modificant segur la tile en concret.
        }else{
            throw new CoordenadesInvalidesException("No s'ha pogut obtenir la Tile demanada a obtenirTile: "+x +" "+y);
        }
    }

    /**
     * @brief Funció per imprimir un Hidato per pantalla.
     * @return Retorna la Tile especificada.
     */
    public void imprimirMapa(){
        for(int i = 0; i<this.num_files; i++){
            if(this.tipusTiles.equals(TipusTiles.HEXAGON)&& i%2 != 0){
                System.out.print(" ");
            }
            for(int j = 0; j<this.num_cols; j++){

                if(existeixTile(i, j)){
                    Tile casella = this.obtenirTile(i, j);
                    // Comprovem primer si no està agafada o és una paret
                    if(casella.getValor().equals("#")) {
                        System.out.print("# ");
                    }
                    // Després mirem si és un forat
                    else if(casella.getEsForat()) {
                        System.out.print("* ");
                    }
                    // Mirem si la casella s'ha de mostrar resolta
                    else if(casella.getStartSolved()){ //Si volem que ens imprimeixi com si sortís ja amagat
                    //else if(casella.getStartSolved() || casella.getAgafada()){
                        if(this.tipusTiles.equals(TipusTiles.TRIANGLE)){
                            char dir_triangle;
                            if((i+j) % 2 == 0){
                                dir_triangle = '▲';
                            }else {
                                dir_triangle = '▼';
                            }
                            System.out.print(casella.getValor() + dir_triangle + " ");
                        } else {
                            System.out.print(casella.getValor() + " ");
                        }
                    }
                    else {
                        System.out.print("? ");
                    }
                } else {
                    System.out.print("   ");
                }
            }
            System.out.println();
        }
    }

    /**
     * @brief Funció per interpretar una matriu donada i la transforma en un hidato.
     * @param matriu Matriu que serà interpretada per la funció.
     * @post s'afegeixen totes les Tiles que componen la matriu a l'hidato actual.
     */
    public void interpretarMatriu(String[][] matriu) {
        int files = matriu.length;
        if (files == 0) return;
        int columnes = matriu[0].length;

        for (int i = 0; i < files; i++) {
            for (int j = 0; j < columnes; j++) {
                String valor = matriu[i][j];
                Tile t = new Tile(this);
                t.setValor(valor);
                if(valor.equals("*")){
                    t.setEsForat(true);
                }
                // Si és un número inicial (no és forat ni està buit), l'agafem
                if (!valor.equals("#") && !valor.equals("?") /*&& !valor.equals("*")*/) {
                    t.setAgafada(true);
                    t.setStartSolved(true);
                }
                // Afegim la casella al HashMap utilitzant la teva funció existent
                this.afegirTile(i, j, t);
            }
        }
    }

    /**
     * @brief Funció per convertir l'estructura actual del Hidato (HashMap) a una matriu de Strings.
     *
     * @return Retorna una matriu bidimensional String[][] amb els valors de les caselles.
     */
    public String[][] generarMatriu() {
        String[][] matriu = new String[this.num_files][this.num_cols];
        for(int i = 0; i<this.num_files; i++){
            for(int j = 0; j<this.num_cols; j++){
                if(existeixTile(i, j)){
                    Tile t = this.obtenirTile(i, j);
                    if(t.getValor().equals("#") || t.getValor().equals("*") || t.getEsForat()) {
                        matriu[i][j]  =t.getValor();
                    }
                    else if (t.getStartSolved()) {
                        matriu[i][j] = t.getValor();
                    }else{
                        matriu[i][j] = "?";
                    }
                }else{
                    matriu[i][j] = "#";
                }
            }
        }
        for(int i = 0; i<this.num_files; i++){
            for(int j = 0; j<this.num_cols; j++){
                if(this.existeixTile(i, j)){
                    Tile t = this.obtenirTile(i, j);
                    System.out.print(t.getValor() + " ");
                }else{
                    System.out.print("  ");
                }
            }
            System.out.println();
        }
        return matriu;
    }

    /**
     * @brief Funció per retornar tots els veïns d'una Tile especificada.
     * @param x Coordenada X on es troba la Tile de la que volem obtenir tots els veïns.
     * @param y Coordenada Y on es troba la Tile de la que volem obtenir tots els veïns.
     * @return Retorna una Llist amb tots els veïns de la Tile indicada.
     */
    public List<String> agafarVeins(int x, int y){ //
        List<String> valids = new ArrayList<>();
        //Definim TipusTiles i TipusAdj per a cada tipus:
        /*
        Triangle: Adj -> És indiferent (només el podem fer d'arestes)
                    Tile-> Triangle
        Hexagon: Adj -> Indiferent (només podem fer aresta)
                 Tile -> Hexagon
        Quadrat: Adj ->Aresta (normal)
                 Adj ->ArestaVertex (si també volem diagonals)
                 Tile-> Quadrat
         */
        if(this.tipusTiles.equals(TipusTiles.TRIANGLE)){
            if((x+y) % 2 == 0 ){
                for(int i = 0; i<direccions_triangle_par.size(); i++){
                    String clau = (direccions_triangle_par.get(i).primer+x)+","+(direccions_triangle_par.get(i).segon+y);
                    if(mapa.containsKey(clau)){
                        valids.add(clau); //Afegim la clau com a un vei valid de la casella que hem posat
                    }
                }
            }else{
                for(int i = 0; i<direccions_triangle_impar.size(); i++){
                    String clau = (direccions_triangle_impar.get(i).primer+x)+","+(direccions_triangle_impar.get(i).segon+y);
                    if(mapa.containsKey(clau)){
                        valids.add(clau);//Afegim la clau com a un vei valid de la casella que hem posat
                    }
                }
            }
            return valids;
        }
        else if(this.tipusTiles.equals(TipusTiles.HEXAGON)){
            if (x % 2 == 0) {
                for(int i = 0; i<direccions_hex_par.size(); i++){
                    String clau = (direccions_hex_par.get(i).primer+x)+","+(direccions_hex_par.get(i).segon+y);
                    if(mapa.containsKey(clau)){
                        valids.add(clau);
                    }
                }
            } else {
                for(int i = 0; i<direccions_hex_impar.size(); i++){
                    String clau = (direccions_hex_impar.get(i).primer+x)+","+(direccions_hex_impar.get(i).segon+y);
                    if(mapa.containsKey(clau)){
                        valids.add(clau);
                    }
                }
            }
            return valids;
        }
        else if(this.tipusTiles.equals(TipusTiles.QUADRAT) && this.tipusAdj.equals(TipusAdjacencia.VERTEXIARESTA)){ //quadrat_d
            for(int i = 0; i<direccions_quadrat_diagonal.size(); i++){
                String clau = (direccions_quadrat_diagonal.get(i).primer+x)+","+(direccions_quadrat_diagonal.get(i).segon+y);
                if(mapa.containsKey(clau)){
                    valids.add(clau);
                }
            }
            return valids;
        }else if(this.tipusTiles.equals(TipusTiles.QUADRAT) && this.tipusAdj.equals(TipusAdjacencia.ARESTA)){
            for(int i = 0; i<direccions_quadrat.size(); i++){
                String clau = (direccions_quadrat.get(i).primer+x)+","+(direccions_quadrat.get(i).segon+y);
                if(mapa.containsKey(clau)){
                    valids.add(clau);
                }
            }
            return valids;
        }else{
            //tractament error
            throw new NoEsPotAgafarAdjacencia("Error en agafar veins, tipus adjacència i tipus tile incorrectes");
        }
    }

    /**
     * @return Retorna el nombre màxim
     * @brief Funció per calcular num_max un cop ja hem generat tot el mapa de Tiles del Hidato
     * @post Canvia el valor de num_max al valor més gran de les Tiles que conté l'Hidato
     */
    public int calcNumMax(){
        int max = 0;
        Tile aux;
        for(int i = 0; i<this.num_files; ++i){
            for(int j = 0; j<this.num_cols; ++j){
                if (this.existeixTile(i,j)){
                    aux = this.obtenirTile(i, j);
                    if(!(aux.getValor().equals("#")) && !(aux.getValor().equals("?")) && !(aux.getValor().equals("*")) && Integer.parseInt(aux.getValor())>= max){
                        max = Integer.parseInt(aux.getValor());
                    }
                }
            }
        }
        System.out.println("Max: " + max);
        this.setNum_max(max);
        return max;
    }
    /**
     * @brief Permet actualitzar el comptador d'IDs des de fora (per exemple, en carregar des de disc).
     * @param nouValor El valor del ID més alt trobat al fitxer + 1.
     * @post S'ha assignat el número d'hidato del Hidato actual al valor proporcionat
     */
    public static void setNumHidato(int nouValor) {
        num_hidato = nouValor;
    }

    /**
     * @brief Retorna el mapa intern de les Tiles de l'Hidato.
     * @return Un Map amb les claus "x,y" i les seves Tiles.
     */
    public Map<String, Tile> getMapa() {
        return this.mapa;
    }

    /**
     * @Brief Funció per tal d'obtenir quin tipus de mapa utilitzarem
     * @return Retorna el nombre indicant el tipus de mapa.
     */
    public int getTipusMapa(){
        return this.tipus_mapa;
    }
    /**
     * @Brief Funció per tal d'assignar un tipus de mapa a l'hidato.
     * @param tipusMapa Tipus de mapa que voldrem que es generi a l'hidato.
     * @post S'ha modificat el valor de l'atribut tipus_mapa de l'hidato pel valor proporcionat.
     */

    public void setTipusMapa(int tipusMapa){
        if(this.tipusTiles.equals(TipusTiles.HEXAGON) && tipusMapa == 1){
            this.tipus_mapa = tipusMapa;
        }else if(this.tipusTiles.equals(TipusTiles.TRIANGLE) && tipusMapa == 2){
            this.tipus_mapa = tipusMapa;
        }else if(tipusMapa == 0){
            this.tipus_mapa = tipusMapa;
        }else{
            throw new NoEsMapaCorrecte("La forma de mapa que has intentat posar no és possible amb la forma de Tile que tens!");
        }
    }

    /**
     * @brief Funció per assignar un nou ID a un Hidato.
     * @post S'ha assignat un nou ID al Hidato proporcionat.
     */
    public void asignar_nou_ID() {
        this.id = num_hidato++;
    }
}

