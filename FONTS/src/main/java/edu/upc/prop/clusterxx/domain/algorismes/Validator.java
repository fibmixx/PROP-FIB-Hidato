package edu.upc.prop.clusterxx.domain.algorismes;

import edu.upc.prop.clusterxx.domain.model.Hidato;
import edu.upc.prop.clusterxx.domain.model.Tile;

import java.util.List;

/**
 * @file Validator.java
 *
 * @brief Definició i implementació de la classe Validator.
 *
 * Serveix per a comprovar que un Hidato proposat per l'usuari compleix amb les característiques
 * corresponents i és resoluble.
 */
public class Validator {

    /**
     * @brief Constructor per defecte.
     * @post Crea una instància Validator buida.
     * */
    public Validator() {}

    /**
     * @brief Funció que comprova si l'Hidato compleix les restriccions de format i si és resoluble o no.
     * @param files    nombre de files de l'Hidato.
     * @param columnes nombre de columnes de l'Hidato.
     * @param map      l'Hidato a validar.
     * @param adj      tipus d'adjacència de l'Hidato.
     * @pre El tauler map no és nul, i files i columnes corresponen a les seves dimensions reals.
     * @post Retorna true si el tauler compleix totes les regles de format de l'Hidato i té almenys una solució possible.
     * Retorna false altrament. El tauler map no queda modificat independentment del resultat.
     */
    public boolean validarHidato(int files, int columnes, Hidato map, String adj) {
        // Validació de format
        if(!formatValid(files, columnes, map)) {
            System.out.println("Error de validació: El format del tauler no compleix el reglament.");
            return false;
        }

        int num_max = calcularCasellesJugables(map, files, columnes);

        // Validar si és resoluble
        // Versió d'abans del merge Hidato mapCopy = map.copiar(); // Fem una còpia del Tauler abans de passar-ho al Solver
        Hidato mapCopy = new Hidato(map); //Fa una còpia del Hidato (fins i tot les Tiles es copien)
        Solver resolutor = new Solver();
        if(!resolutor.resoldre_ini(mapCopy, num_max, files, columnes, adj)) {
            System.out.println("Error de validació: Aquest tauler no té cap solució.");
            return false;
        }

        return true;
    }

    /**
     * @brief Funció que comprova si l'Hidato compleix les restriccions de format.
     * @param files    nombre de files de l'Hidato.
     * @param columnes nombre de columnes de l'Hidato.
     * @param map      l'Hidato a validar.
     * @pre El tauler map no és nul, i files i columnes corresponen a les seves dimensions reals.
     * @post Retorna true si cap número proporcionat excedeix el límit de caselles del tauler,
     * no hi ha números repetits, s'inclou com a mínim el número 1, i cap cel·la conté caràcters no permesos.
     */
    private boolean formatValid(int files, int columnes, Hidato map) {
        boolean teInici = false;
        boolean[] numsVistos = new boolean[files * columnes + 1];

        for(int i = 0; i < files; i++) {
            for(int j = 0; j < columnes; j++) {
                if(map.existeixTile(i,j)) {
                    Tile t = map.obtenirTile(i,j);
                    String valor = t.getValor();

                    if (valor.matches("\\d+")) {
                        int num = Integer.parseInt(valor);

                        // Si el número és 1, marquem que tenim inici
                        if (num == 1) teInici = true;

                        // Comprovem que el número no superi les dimensions del tauler
                        if (num > 0) {
                            if(num >= numsVistos.length) {
                                System.out.println("Error: El número " + num + " és massa gran per a aquest tauler.");
                                return false;
                            }
                            // Comprovem si el número ja existia (duplicat)
                            if (numsVistos[num]) {
                                System.out.println("Error: El número " + num + " està repetit.");
                                return false;
                            }
                            numsVistos[num] = true;
                        }
                    }

                    // Comprovem que no hi hagi valors invàlids
                    if (!valor.matches("\\d+") && !valor.equals("?") && !valor.equals("#") && !valor.equals("*")){
                        System.out.println("Error: El tauler conté un element invàlid.");
                        return false;
                    }
                }
            }
        }

        if (!teInici) {
            System.out.println("Error: Falta el número 1.");
            return false;
        }

        return true;
    }

    /**
     * @brief Comprova si un tauler completament ple és una solució vàlida.
     * @param files nombre de files.
     * @param columnes nombre de columnes.
     * @param map l'Hidato resolt a comprovar.
     * @param adj tipus d'adjacència de l'Hidato.
     * @return true si la solució és correcta, false si hi ha errors.
     */
    public boolean comprovarHidatoResolt(int files, int columnes, Hidato map, String adj) {
        int casellesJugables = 0;

        // Comptem les caselles directament (així evitem errors si la funció externa falla)
        for (int i = 0; i < files; i++) {
            for (int j = 0; j < columnes; j++) {
                Tile t = map.obtenirTile(i, j);
                if (t != null && !t.getValor().equals("*") && !t.getValor().equals("#")) {
                    casellesJugables++;
                }
            }
        }

        // Si no ha pogut llegir el tauler, parem immediatament
        if (casellesJugables <= 1) {
            System.out.println("Error: No s'ha pogut llegir bé el tauler o està buit. Revisa com es guarden les coordenades a Hidato!");
            return false;
        }

        String[] posicions = new String[casellesJugables + 1];

        // Registrem on està cada número
        for (int i = 0; i < files; i++) {
            for (int j = 0; j < columnes; j++) {
                Tile t = map.obtenirTile(i, j);
                if (t != null) {
                    String valor = t.getValor();

                    if (valor.equals("?")) {
                        System.out.println("Error: El tauler no està acabat. Queden caselles buides (?).");
                        return false;
                    }

                    if (!valor.equals("*") && !valor.equals("#")) {
                        try {
                            int num = Integer.parseInt(valor);
                            if (num < 1 || num > casellesJugables) {
                                System.out.println("Error: El número " + num + " està fora de rang (Hauria de ser màxim " + casellesJugables + ").");
                                return false;
                            }
                            if (posicions[num] != null) {
                                System.out.println("Error: El número " + num + " està repetit al tauler!");
                                return false;
                            }

                            posicions[num] = i + "," + j;
                        } catch (NumberFormatException e) {
                            return false;
                        }
                    }
                }
            }
        }

        // Comprovem els salts un a un
        for (int k = 1; k < casellesJugables; k++) {
            String posActual = posicions[k];
            String posSeguent = posicions[k + 1];

            if (posActual == null || posSeguent == null) {
                System.out.println("Error: Falta algun número intermedi a la seqüència.");
                return false;
            }

            String[] partsActual = posActual.split(",");
            int filAct = Integer.parseInt(partsActual[0]);
            int colAct = Integer.parseInt(partsActual[1]);

            String[] partsSeguent = posSeguent.split(",");
            int filSeg = Integer.parseInt(partsSeguent[0]);
            int colSeg = Integer.parseInt(partsSeguent[1]);

            boolean esAdjacent = false;

            // Comprovació 1: Usar la funció getVeins
            List<String> veins = map.agafarVeins(filAct, colAct);
            if (veins != null && veins.contains(posSeguent)) {
                esAdjacent = true;
            }
            // Comprovació 2: Si getVeins falla, calculem la distància directament.
            else {
                if (adj.equals("quadrat")) {
                    // Costats: exactament distància 1
                    if (Math.abs(filAct - filSeg) + Math.abs(colAct - colSeg) == 1) esAdjacent = true;
                } else if (adj.equals("quadrat_d")) {
                    // Diagonals i costats: distància d'1 pas en qualsevol sentit
                    if (Math.abs(filAct - filSeg) <= 1 && Math.abs(colAct - colSeg) <= 1) esAdjacent = true;
                }
            }

            // Si falla per totes bandes, llança l'error exacte
            if (!esAdjacent) {
                System.out.println("Error de salt: El número " + (k + 1) + " a la posició (" + filSeg + "," + colSeg + ") NO és adjacent al " + k + " a (" + filAct + "," + colAct + ").");
                return false;
            }
        }

        return true;
    }

    /**
     * @brief Funció que calcula quantes caselles són jugables (caselles buides).
     * @param mapa      l'Hidato a validar.
     * @param files    nombre de files de l'Hidato.
     * @param columnes nombre de columnes de l'Hidato.
     * @pre El tauler map no és nul i el seu format ja ha estat validat prèviament.
     * @post Retorna el nombre exacte de cel·les del tauler que no són forats (*) ni cel·les nul·les (#).
     */
    private int calcularCasellesJugables(Hidato mapa, int files, int columnes) {
        int max = 0;
        for (int i = 0; i < files; i++) {
            for (int j = 0; j < columnes; j++) {
                if (mapa.existeixTile(i, j)) {
                    String valor = mapa.obtenirTile(i, j).getValor();
                    if (!valor.equals("*") && !valor.equals("#")) {
                        max++;
                    }
                }
            }
        }
        return max;
    }
}
