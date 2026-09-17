package edu.upc.prop.clusterxx.domain.algorismes;

import edu.upc.prop.clusterxx.domain.enumerations.TipusAdjacencia;
import edu.upc.prop.clusterxx.domain.enumerations.TipusTiles;
import edu.upc.prop.clusterxx.domain.excepcions.GeneradorNoPotGenerarCami;
import edu.upc.prop.clusterxx.domain.excepcions.ValorTileInvalidException;
import edu.upc.prop.clusterxx.domain.model.Hidato;
import edu.upc.prop.clusterxx.domain.model.Tile;

import java.util.*;

/**
 * @file Generator.java
 *
 * @brief Definició i implementació de la classe Generator.
 *
 * Serveix per a generar taulers d'Hidatos.
 */


public class Generator {

    //Per tal de Generar mapes amb hexàgons, triangles i quadrats utilitzem un map (hashMap). Per omplir-lo el tractarem com una matriu

    /*Per saber adjacències: Fem servir les matemàtiques:
    *       Quadrats:
    *           Dreta:      (x+1,y)
    *           Esquerra:   (x-1,y)
    *           A baix:     (x,y+1)
    *           A Dalt:     (x,y-1)
    *
    *       Triangles:
    *           Triangle apunta cap a dalt (base plana a baix) (x+y parell):
    *               Esquerra:   (x-1, y)
    *               Dreta:      (x+1, y)
    *               A Baix:     (x, y-1)
    *           Tirangle apuntant cap a baix (base plana amunt) (x+y imparell):
    *               Esquerra:   (x-1, y)
    *               Dreta:      (x+1, y)
    *               A Dalt:     (x, y+1)
    *
    *       Hexàgons: Considerem que l'agafem amb coordenades axials -> Com que fem servir un hashMap i no tenim estructura quadriculada és més fàcil de veure
    *                 En comptes de considerar que l'eix x i y  passen de tal forma que ens queda una base plana paral·lela a l'eix x, considerem que els eixos passaran
    *                 Perpendiculars a dos dels costats. (A efectes pràctics, girem els eixos 45º)
    *
    *               Dreta:          (x+1, y)
    *               Esquerra:       (x-1, y)
    *               Baix Dreta      (x, y+1)
    *               Dalt Esquerra   (x, y-1)
    *               Baix Esquerra   (x-1, y-1)
    *               Dalt Dreta      (x+1, y-1)
    *       Un HashMap ens permet accedir a un Tile mitjançant una clau (és una tupla) <String, Tile>
            Obtindrem una casella mitjançant l'string "X,Y" on X i Y van variant segons el valor de la Tile.
    * */

    /**
     * @brief Funció que permet passar els Hidatos generats per Generator a format estàndar que s'ha especificat des de l'assignatura.
     * @param tauler Hidato que es vol passar a format estàndard.
     * @return Retorna el Hidato passat al format estandarditzat per la seva lectura.
     */
    public static String format(Hidato tauler){
        Integer files = tauler.getNum_files();
        Integer columnes = tauler.getNum_cols();
        String adj_char;
        if(tauler.getTipusTiles().equals(TipusTiles.QUADRAT)){ //Quadrat i Quadrat_d
            adj_char = "Q";
        }
        else if(tauler.getTipusTiles().equals(TipusTiles.HEXAGON)) { //Hexagon
            adj_char = "H";
        }
        else if(tauler.getTipusTiles().equals(TipusTiles.TRIANGLE)) { //Triangle
            adj_char = "T";
        }
        else adj_char = "?";

        String diagonal;
        if(tauler.getTipusTiles().equals(TipusTiles.QUADRAT) && tauler.getTipusAdj().equals(TipusAdjacencia.VERTEXIARESTA)) diagonal = "CA";
        else diagonal = "C";
        StringBuilder sb = new StringBuilder();

        sb.append(adj_char).append(",").append(diagonal).append(",").append(files).append(",").append(columnes).append("\n");

        for(int i = 0; i < files; i++){
            for(int j = 0; j< columnes; j++){
                if(j>0) sb.append(",");
                if(!tauler.existeixTile(i,j)){
                    sb.append("#");
                }else{
                    Tile t = tauler.obtenirTile(i,j);
                    String val = t.getValor();
                    if(t.getEsForat()){ //SI és "*" -> es forat = true
                        sb.append("*");
                    }
                    else if(val == null || t.getValor().equals("#")){
                        sb.append("#");
                    }else if(!t.getStartSolved()){
                        sb.append("?");
                    }else if(val.matches("\\d+") || t.getAgafada()){
                        sb.append(val);
                    }else{
                        sb.append("#");
                    }
                }
            }
            if(i< files-1) sb.append("\n");
        }
        return sb.toString();
    }

    /**
     * @breif Funció que ens permet treure Tiles que han quedat marcades com a "Forat" però que després de la generació han quedat completament fora del Hidato (molt lluny d'on s'han generat els nombres).
     * @param tauler Hidato que volem netejar de punts que han quedat despenjats.
     */
    public static void netejaPunts(Hidato tauler){
        int files = tauler.getNum_files();
        int columnes = tauler.getNum_cols();
        for(int i = 0; i < files; i++){
            for(int j = 0; j < columnes; j++){
                if(tauler.existeixTile(i,j)){
                    Tile aux = tauler.obtenirTile(i,j);
                    if(aux.getEsForat()){
                        List<String> veins = tauler.agafarVeins(i,j);
                        boolean teVei = false;
                        for(int z = 0; z< veins.size(); ++z){ //Iterem per tots els veins
                            String vei_aux =  veins.get(z);
                            String[] parts = vei_aux.split(",");
                            Tile vei = tauler.obtenirTile(Integer.parseInt(parts[0]),Integer.parseInt(parts[1])); //Obtenim la tile
                            if(vei.getAgafada() && !vei.getEsForat()){
                                teVei = true;
                            }
                        }
                        if(!teVei){
                            aux.setAgafada(false);
                            aux.setEsForat(false);
                            aux.setValor("#");
                        }
                    }
                }
            }
        }
    }

    /**
     * @brief Funció que permet amagar els nombres de cada tile un cop ja s'ha generat el camí, segons una probabilitat especificada.
     * @param tauler Hidato del qual volem amagar les Tiles per poder-lo jugar.
     */
    public static void amagaMapa(Hidato tauler){
        //Prob serveix per definir quantes Tiles s'amaguen, s'ha de definir com varia depenent de la dificultat del hidato proposat.
        double prob = tauler.getNum_amagat();
        System.out.println("Probabilitat amagat: " + prob);
        if(prob < 0.00 || prob > 1.0){
            throw new IllegalArgumentException("Probabilitat no vàlida");
        }
        int num_amagats = 0;
        int num_max = tauler.getNum_max();
        int files = tauler.getNum_files();
        int columnes = tauler.getNum_cols();
        Random rand = new Random();
        for(int i = 0; i<files; i++){
            for(int j = 0; j<columnes; j++){
                if(tauler.existeixTile(i,j)){
                    Tile aux = tauler.obtenirTile(i,j);
                    String valor = aux.getValor();
                    if(aux.getAgafada() && !aux.getEsForat() && !valor.equals("1") && !valor.equals(String.valueOf(num_max))){
                        if(rand.nextDouble()<prob){
                            aux.setStartSolved(false); //establim que encara que tingui el valor, es quedarà amagada amb un '?'
                            ++num_amagats;
                        }
                    }
                }
            }
        }
        System.out.println("Totals: " + num_max);
        System.out.println("Amagat: " + num_amagats);
    }

    /**
     * @breif Funció que permet calcular el grau d'adjacència d'una Tile donada
     * @param tauler Hidato que conté la Tile que volem avaluar
     * @param fila Fila que ocupa la Tile que volem avaluar
     * @param columna Columna que ocupa la Tile que volem avaluar.
     * @return Retorna el grau d'adjacència de la Tile especificada.
     */

    private static int calcularGrau(Hidato tauler, int fila, int columna){
        int grau = 0;
        List<String> veins = tauler.agafarVeins(fila, columna);
        for(int i = 0; i<veins.size(); i++){
            String aux =  veins.get(i);
            String[] parts =  aux.split(",");
            int x_vei =  Integer.parseInt(parts[0]);
            int y_vei = Integer.parseInt(parts[1]);
            if(tauler.existeixTile(x_vei,y_vei) && !tauler.obtenirTile(x_vei,y_vei).getEsForat()){ //Donat que encara no hem començat a omplir de nombres, no poden estar ocupades les Tiles!
                grau++;
            }
        }
        return grau;
    }

    /**
     * @brief Funció que permet generar forats dins del mapa de forma aleatòria i tenint en compte que no es posi en Tiles on es deixaria a veins amb grau d'adjacència inferior a 2.
     * @param tauler Hidato al que volem afegir-hi els forats
     * @param num_forats Nombre total de forats que podem posar-hi.
     * @post S'han incorporat forats dins del Hidato. Com a màxim num_forats forats.
     */

    /*
    Forats reals -> valor = "*" i esForat = true
    Forats frontera/per fer la forma -> valor = "#" i esForat = true
     */

    private static void genForats(Hidato tauler, int num_forats){
        Random rand = new Random();
        int num_posats = 0;
        int num_error = 0;

        while (num_posats < num_forats && num_error < 100){
            int r = rand.nextInt(tauler.getNum_files());
            int c = rand.nextInt(tauler.getNum_cols());

            if(tauler.existeixTile(r,c) && !tauler.obtenirTile(r,c).getEsForat()){
                boolean valid = true;
                List<String> veins = tauler.agafarVeins(r,c);

                for(int i = 0; i< veins.size(); i++){
                    String aux = veins.get(i);
                    String[] parts = aux.split(",");
                    int vei_x = Integer.parseInt(parts[0]);
                    int vei_y = Integer.parseInt(parts[1]);

                    if(!tauler.obtenirTile(vei_x,vei_y).getEsForat()){
                        int grau = calcularGrau(tauler,  vei_x, vei_y)-1; //Calculem el grau del veí i li restem un (suposem que nosaltres serem forat)
                        if(grau<2){
                            valid = false;
                            break; //No cal que continuem, ja que sabem que com a mínim un dels veins es quedaria aïllat si nosaltres som un forat
                        }
                    }
                }
                if(valid){
                    tauler.obtenirTile(r,c).setEsForat(true);
                    tauler.obtenirTile(r,c).setValor("*");
                    num_posats++;
                }else{
                    num_error++;
                }
            }
            System.out.println("Num forats posats: "+num_forats);
        }
    }

    /**
     * @brief Funció recursiva per backtracking que permet generar els camins de l'hidato.
     * @param fil Número de fila de la Tile última que ha tractat l'algorisme
     * @param col Número de columna de la Tile última que ha tractat l'algorisme
     * @param mapa Hidato sobre el que treballa l'algorisme
     * @param next_num Següent número a posar dins de l'hidato
     * @param tempsInici Temps en que ha comneçat a executar-se l'algorisme
     * @param tempsMaxim  Temps màxim que l'algorisme pot utilitzar per fer el càlcul.
     * @return Es retorna l'Hidato amb el seu tauler omplert de tal forma que es crea un camí hamiltonià amb nombres creixents fins a num_max.
     */


    private static boolean generator_rec(Integer fil, Integer col, Hidato mapa, Integer next_num, long tempsInici, long tempsMaxim) throws ValorTileInvalidException { //Funció que ha retornar el mapa del hidato fet
        if(System.currentTimeMillis()-tempsInici>tempsMaxim){
            return false; //No perdem més el temps en una distribució de tauler dolenta.
        }
        Integer num_max = mapa.getNum_max();
        record Pair_S_Int(String clau, Integer grau_lliure){}
        if (fil >= mapa.getNum_files() || col >= mapa.getNum_cols() || fil < 0 || col < 0) { // Comprovem que no estiguem "intentant" accedir a parts no vàlides del mapa!
            return false;
        }
        if(!mapa.existeixTile(fil,col)){
            return false; //Per si de cas es cola algun nombre d'una Tile que no existeix.
        }
        if (mapa.obtenirTile(fil, col).getAgafada() || mapa.obtenirTile(fil,col).getEsForat()) { // Si ja està agafada o és un forat, retornem (PODA)
            return false;
        }
        mapa.obtenirTile(fil, col).setAgafada(true); // Diem que ja està agafada
        mapa.obtenirTile(fil, col).setValor(next_num.toString()); // Posem el valor que li pertocaria
        mapa.obtenirTile(fil, col).setEsForat(false);
        mapa.obtenirTile(fil, col).setStartSolved(true);

        if(next_num.equals(1) || next_num.equals(num_max)){
            mapa.obtenirTile(fil, col).setStartSolved(true); // Diem que ja està agafada
            System.out.println("Hem entrat a posar 1 i nummax");
        }

        //Cas base -> Si la cel·la actual li hem possat ja el nombre màxim, ho hem aconseguit!!
        if (mapa.obtenirTile(fil, col).getValor().equals(num_max.toString())) {
            return true;
        }
        //2. Explorar totes les possibles extensions per la sol·lució actual
        //2.1 Per tal de generar un mapa "original" cal afegir factor de sort -> fer sort dels Pairs que tenim al vector d'adjacències
        //Després de provar, surt més a compte utilitzar heurísitca Warnsdorff híbrida (aleatòria i Warnsdorff no són tant eficients)
            /*Aplicant únicament heurísitca de Warnsdorff ens acaba retornant mapes molt predictibles (es solucionen en línia recta gairebé)
             * Podem aplicar el que es coneix com a Warnsdorff híbrid
             * Només apliquem Warnsdorff si una de les caselles veïnes només li queda 1 grau lliure -> Vol dir que si no l'agafem
             * ara té poques possibilitats de poder ser ocupada més tard*/
            List<Pair_S_Int> veins_grau = new ArrayList<>(); // List on posarem tots els veïns que estan lliures (igual que en el cas normal)
            List<String> veins_valids = mapa.agafarVeins(fil, col); //Llista on tenim tots els veïns
            for(int i = 0; i < veins_valids.size(); i++){ //Iterem per a tots els veïns lliures de la Tile actual per anar mirant el seu grau de connexitat
                String vei_eval = veins_valids.get(i); //Obtenim el valor del veí en concret
                String[] parts = vei_eval.split(",");
                int fil_new = Integer.parseInt(parts[0]);
                int col_new = Integer.parseInt(parts[1]); //Tenim les coord d'un dels veïns
                if(mapa.existeixTile(fil_new,col_new) && !mapa.obtenirTile(fil_new, col_new).getAgafada() && !mapa.obtenirTile(fil_new,col_new).getEsForat()){ //Comprovem primer que el veí no està ja agafat (si ja ho està aleshores no cal fer res!)
                    List<String> veins_2 = mapa.agafarVeins(fil_new, col_new); //agafem tots els veïns del candidat
                    Integer grau_lliure = 0;
                    for(int j= 0; j<veins_2.size(); ++j){ //Iterem per totes les Tiles del voltant del veí per veure si estan lliures
                        String[] p = veins_2.get(j).split(",");
                        int x = Integer.parseInt(p[0]);
                        int y = Integer.parseInt(p[1]);
                        if(mapa.existeixTile(x,y) && !mapa.obtenirTile(x, y).getAgafada() && !mapa.obtenirTile(x,y).getEsForat()){//Si la tile que estem mirant no està agafada, suma al grau
                            grau_lliure++;
                        }
                    }//Acaba amb el recompte del grau de connexitat d'un dels veins de la Tile actual.
                    veins_grau.add(new Pair_S_Int(veins_valids.get(i), grau_lliure)); //Afegim el parell Nom-grau lliure
                }
            }
            //Aquí és on apliquem la lògica del Warnsdorff, mirem si algun dels graus és 1, si ho és l'apliquem, sinó aleatòriament
            Collections.shuffle(veins_grau); //Barregem per tal de canviar d'ordre les Tiles amb mateix número de grau de llibertat
            veins_grau.sort(Comparator.comparingInt(Pair_S_Int::grau_lliure)); //Ordenem totes les Tiles per grau (però ja no serà el mateix que si no fèssim el shuffle)
            if(veins_grau.size()>=2){
                /*Per tal de refinar una mica més el mètode, si sempre apliquem Warsnsdorff tendirà a agafar una línia recta
                 * que circumbala tot el mapa
                 * Per evitar-ho, només apliquem Warnsdorff si tenim una Tile la qual només té un grau de llibertat (vol dir q si no l'agafem nosaltres
                 * no es podrà tornar a utilitzar.
                 * Si no apliquem Warnsdorff, escollim aleatòriament entre la primera i la segona Tile */
                int primer_v = veins_grau.get(0).grau_lliure();
                int seg_v = veins_grau.get(1).grau_lliure();

                if(primer_v >=2 &&Math.abs(primer_v-seg_v)<=1){ //Mirem que el primer veí (el que té menys grau de llibertat) té mès de 2 veins disponibles. També mirem que la diferencia entre el primer i el segon no sigui molt gran i que sigui gairebé igaul quin escollir dels dos
                    if(Math.random() > 0.6){ //SI toquem aquesta probabilitat, afectem la linealitat dels resultats! (escollir primer el segon en comptes del primer)
                        Pair_S_Int temp = veins_grau.get(0);
                        veins_grau.set(0, veins_grau.get(1));
                        veins_grau.set(1, temp);
                    }
                }
            }
            for(int i = 0; i < veins_grau.size(); i++){
                String next = veins_grau.get(i).clau();
                String[] parts = next.split(",");
                int fil_new = Integer.parseInt(parts[0]);
                int col_new = Integer.parseInt(parts[1]);
                if(generator_rec(fil_new, col_new, mapa, next_num+1, tempsInici, tempsMaxim)){ //següent crida recursiva
                    return true;
                }
            }

        //Si arribem aquí, vol dir que cap de les recursives ha aconseguit posar tots els números, per tant no estem pel bon camí.
        mapa.obtenirTile(fil, col).setAgafada(false); //Marquem que la Tile no està agafada
        mapa.obtenirTile(fil, col).setValor("#");
        mapa.obtenirTile(fil, col).setStartSolved(false);
        return false;
    }


    /**
     * @brief Funció que permet preparar el mapa del hidato i preparar tots els paràmetres per a començar la generació del camí del hidato.
     * @param tauler Hidato sobre el que es crearà el camí.
     * @post S'ha generat un camí dins de l'hidato especificat i l'hidato ja és jugable.
     */
    public static void Generator_ini(Hidato tauler) throws Exception {
        //Generant un mapa quadrat/rectangularfa que costi molt trobar camins, ja que podem aprofitar-nos menys de Warnsdorff, intentarem que la creació de mapes
        //Sigui de forma aleatòria i que tingui tantes tiles com li demanem (sense que tingui forma quadrada)
        Random r = new Random();
        int files = tauler.getNum_files();
        int columnes = tauler.getNum_cols();
        int num_max = tauler.getNum_max();
        boolean trobat = false;
        int intents_maxim = 500;
        int intents = 0;
        long tempsMaxim = 5000;
        int num_forats;
        //Si una distribució de forats no ens dona resultat ràpid, es crea una altra (per evitar que una distribució dolenta ens perjudiqui)
        if(!(tauler.getTipusTiles() == TipusTiles.TRIANGLE)){
            num_forats = Math.min((files*columnes)-(num_max+4), 10); //Si no posem un màxim, quan no omplim tot el tauler creixen linialment i això provoca que l'hidato sigui molt difícil de generar.
        }else{
            num_forats = Math.min((files*columnes)-(num_max+4), 8); //Per triangles
        }

        if(files <= 0 || columnes <=0){
            throw new IllegalArgumentException("Les dimensions del hidato no són correctes");
        }

        if(num_max > (files*columnes)){
            throw new IllegalArgumentException("Num_max (" + num_max +") no pot ser superior a l'àrea del hidato ("+ (files*columnes)+")");
        }

        if(tauler.getTipusMapa() == 0){ //Mapa quadrat
            for (int i = 0; i < files; i++) {
                for (int j = 0; j < columnes; j++) {
                    tauler.afegirTile(i, j, new Tile(tauler)); //Cridem el mètode que omple el HashMap del tauler per tal que hi vagi posant els nous objectes Tiles
                }
            }
        }else if(tauler.getTipusMapa() == 1 && tauler.getTipusTiles() == TipusTiles.HEXAGON){ //mapa "circular"
            int centreFila = files / 2;
            int centreCol = columnes / 2;

            int centreQ = centreCol;
            int centreR = centreFila;
            int centreS = -centreQ - centreR;
            record CoordHex(int f, int c, int dist, double angle){}
            List<CoordHex> coords = new ArrayList<>();
            for (int i = 0; i < files; i++) {
                for (int j = 0; j < columnes; j++) {
                    int q = j ;
                    int ra = i;
                    int s = -q - ra;
                    // Distància de Manhattan
                    int dist = Math.max(Math.max(Math.abs(q - centreQ), Math.abs(ra - centreR)), Math.abs(s - centreS));
                    double angle = Math.atan2(centreFila-i, j-centreCol);
                    coords.add(new CoordHex(i, j, dist, angle));
                }
            }
            coords.sort(Comparator.comparingInt(CoordHex::dist).thenComparingDouble(CoordHex::angle));

            int casellesAlocatar = Math.min((num_max+num_forats), files*columnes);
            for(int k = 0; k<casellesAlocatar; k++){
                CoordHex coord = coords.get(k);
                tauler.afegirTile(coord.f(), coord.c(), new Tile(tauler));
            }
        }else if (tauler.getTipusMapa() == 2 && tauler.getTipusTiles() == TipusTiles.TRIANGLE){
            int extres = (int) (num_max * 0.20); //Fem més tiles de les justes i necessaries per tal que sigui més fàcil resoldre el HIdato
            int num_tiles_generar = num_max + extres;

            int altura = (int)Math.ceil(Math.sqrt(num_tiles_generar));

            int cols_necessaries = 2*altura -1;

            if (files < altura){
                tauler.setNum_files(altura);
                files = altura;
            }
            if(columnes < cols_necessaries){
                tauler.setNum_cols(cols_necessaries);
                columnes = cols_necessaries;
            }

            int centreC = columnes/2;
            if(centreC % 2 != 0){
                centreC--;
            }
            for(int i = 0; i< altura; i++){
                for(int j = centreC-i; j<=centreC+i; j++){
                    if(i == altura-1 && (j==centreC -i || j == centreC+i)){
                        continue;
                    }
                    if(i>=0 && i< files && j>= 0&& j<columnes){
                        tauler.afegirTile(i, j, new Tile(tauler));
                    }
                }
            }
            num_forats = 0; // Si fem el mapa triangular, no volem forats, ja que sinó costa moltíssim (encara més) generar un camí.
        }
        tauler.imprimirMapa();
        //Fem els càlculs per posar els forats i calcular els camins
        while(!trobat){
            long tempsInici = System.currentTimeMillis(); //Utilitzat per tal de tallar l'algorisme si tarda molt i es queda potencialment calculant backtrakings infinits
            if(intents>intents_maxim /*|| index_pos >= pos_ini_val.size()*/){
            throw new GeneradorNoPotGenerarCami("Generador no ha generat camí intents_max");
            }
            genForats(tauler,num_forats);
            List<String> pos_ini_val = new ArrayList<>();
            for(int i = 0; i< files; ++i){
                for(int j = 0; j < columnes; ++j){
                    if(tauler.existeixTile(i,j) && !tauler.obtenirTile(i, j).getEsForat()){ //En aquest punt encara no hem possat cap numero, per tant no cal que mirem si està agafat o si hi ha algun número.
                        pos_ini_val.add(i + "," + j);
                    }
                }
            }
            Collections.shuffle(pos_ini_val, r); //Barregem totes les posicions vàlides per escollir una aleatòriament.
            //Anem iterant per a totes les cel·les que estan lliures intentant trobar un camí vàlid:
            for (int i = 0; i< pos_ini_val.size(); i++){ //Anem buscant camins amb posicions aleatòries.
                String[] parts = pos_ini_val.get(i).split(",");
                int fil_new = Integer.parseInt(parts[0]);
                int col_new = Integer.parseInt(parts[1]);

                try{
                    trobat = generator_rec(fil_new,col_new, tauler, 1, tempsInici, tempsMaxim );
                    if(trobat) break;
                }catch(Exception e){
                    System.err.println("Algoritme ha fallat a fila: "+ fil_new+ "i columna: "+col_new);
                    throw e;
                }
                if(System.currentTimeMillis()-tempsInici>=tempsMaxim){
                    System.out.println("Break d'emergencia contra tauler mal generat");
                    break; //Si gastem molt de temps (5segons) en una distribució de forats, l'esborrem i tornem a generar els forats.
                }
            }
            //Esborrem tots els possibles forats que hi hagin
            if(!trobat){
                for(int x = 0; x<files; ++x) {
                    for (int y = 0; y < columnes; ++y) {
                        //if (tauler.existeixTile(x, y) && !tauler.obtenirTile(x, y).getEsForat()) {
                        if(tauler.existeixTile(x,y) && !tauler.obtenirTile(x,y).getValor().equals("#")){
                            tauler.obtenirTile(x, y).setAgafada(false);
                            tauler.obtenirTile(x, y).setValor("#");
                            tauler.obtenirTile(x, y).setStartSolved(false);
                            tauler.obtenirTile(x,y).setEsForat(false);
                        }
                    }
                }
            }
            ++intents;
            System.out.println("Una passada "+intents);
        }
        System.out.println("Imprimint mapa: ");
        tauler.imprimirMapa(); //Només per a debug
    }
}
