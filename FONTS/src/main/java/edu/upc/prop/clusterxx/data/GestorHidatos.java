package edu.upc.prop.clusterxx.data;

import edu.upc.prop.clusterxx.domain.enumerations.TipusAdjacencia;
import edu.upc.prop.clusterxx.domain.enumerations.TipusDificultat;
import edu.upc.prop.clusterxx.domain.enumerations.TipusTiles;
import edu.upc.prop.clusterxx.domain.model.Hidato;
import edu.upc.prop.clusterxx.domain.model.Tile;
import edu.upc.prop.clusterxx.domain.interficies.IGestorHidatos;

import java.io.*;
import java.util.*;


//Ordre guardat: id_hidato, id_repo, num_files, num_columnes, dificultat, tipus tiles, tipus adj, num_amagat, tauler

public class GestorHidatos implements IGestorHidatos {
    /**
     * @brief Ruta on es desarà el fitxer de dades.
     */
    private final String PATH = "data/hidatos.txt";
    /**
     * @brief Mapa on es guarden tots els hidatos identificats pel seu id.
     */
    private Map<Integer, Hidato> hidatos;
    /**
     * @brief Mapa on es guarden totes les relacions Hidato-Repositori.
     */
    private Map<Integer, Integer> hidatoToRepo;

    /**
     * @brief Creadora de la classe GestorHidatos.
     *
     * @post Crea un GestorHidatos amb dos mapes buits i carrega des de disc les dades dels usuaris.
     */
    public GestorHidatos() {
        File fitxer = new File(PATH); //Crea el directori
        File carpeta = fitxer.getParentFile();
        if (!carpeta.exists()) {
            carpeta.mkdirs(); //Ens assegurem que es creei la carpeta en concret
        }
        this.hidatos = new HashMap<>();
        this.hidatoToRepo = new HashMap<>();
        carregarDeDisc();
    }

    /**
     * @brief Funció per afegir un hidato.
     *
     * @param id     És l'identificador de l'Hidato.
     * @param h      És l'Hidato que volem afegir.
     * @param idRepo És l'identificador del repositori on es troba l'hidato.
     *
     * @post Afegeix l'Hidato h.
     */
    @Override
    public void put(int id, Hidato h, int idRepo) {
        hidatos.put(id, h);
        hidatoToRepo.put(id, idRepo);
        guardarADisc();
    }

    /**
     * @brief Funció per eliminar un hidato.
     *
     * @param id És l'identificador de l'Hidato.
     *
     * @post Elimina el hidato amb identificador id.
     */
    @Override
    public void remove(int id) {
        hidatos.remove(id);
        hidatoToRepo.remove(id);
        guardarADisc();
    }

    /**
     * @brief Funció per obtenir un hidato
     *
     * @param id És l'identificador de l'Hidato.
     *
     * @post Retorna l'Hidato amb identificador id.
     */
    @Override
    public Hidato get(int id) {
        return hidatos.get(id);
    }

    /**
     * @brief Retorna tots els hidatos carregats a memòria.
     * @return Un mapa amb tots els hidatos, on la clau és el seu ID.
     */
    @Override
    public Map<Integer, Hidato> all() {
        return this.hidatos;
    }

    /**
     * @brief Obté tots els hidatos associats a un repositori concret.
     *
     * @param idRepo Identificador del repositori.
     *
     * @return Un mapa amb els Hidatos que pertanyen al repositori idRepo.
     */
    // Un cop s'han carregat els repositoris de l'usuari, es carreguen els hidatos als quals pertanyen
    public Map<Integer, Hidato> carregarPerRepositori(int idRepo) {
        Map<Integer, Hidato> resultat = new HashMap<>();

        // Recorrem el mapa de relacions (idHidato -> idRepo)
        for (Map.Entry<Integer, Integer> entry : hidatoToRepo.entrySet()) {
            // Si el valor (id del repo) coincideix amb el que busquem
            if (entry.getValue() == idRepo) {
                Integer idHidato = entry.getKey();
                // Busquem l'objecte Hidato real al mapa principal i l'afegim al resultat
                resultat.put(idHidato, hidatos.get(idHidato));
            }
        }

        return resultat;
    }

    /**
     * @brief Guarda tot el mapa al fitxer de text.
     * @post Es guarda a disc el Hidato especificat per tal de mantenir persistència.
     */
    private void guardarADisc() {
        try (PrintWriter pw = new PrintWriter(new FileWriter(PATH))) {
            for (Integer id : hidatos.keySet()) {
                Hidato h = hidatos.get(id);
                int idRepo = hidatoToRepo.get(id);
                String taulerStr = serialitzarTauler(h);

                pw.println(id + "," + idRepo + "," + h.getNum_files() + "," + h.getNum_cols() + "," +
                        h.getDificultat() + "," + h.getTipusTiles() + "," + h.getTipusAdj() + "," + h.getNum_amagat()+","+ h.getTipusMapa()+","+ taulerStr);
            }
        } catch (IOException e) { e.printStackTrace(); }
    }

    /**
     * @brief Llegeix el fitxer i omple el mapa a l'inici.
     */
    private void carregarDeDisc() {
        System.out.println("carregant Hidato disc");

        File file = new File(PATH);
        if (!file.exists()) return;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String linia;
            while ((linia = br.readLine()) != null) {
                String[] d = linia.split(",",-1);
                if (d.length < 10) continue; // Seguretat contra línies corruptes

                // Extreure dades bàsiques de l'hidato
                int id = Integer.parseInt(d[0]);
                int idRepo = Integer.parseInt(d[1]);
                int files = Integer.parseInt(d[2]);
                int cols = Integer.parseInt(d[3]);

                //Extreure dificultat, tipusTile, tipusAdjacencia
                TipusDificultat dif = TipusDificultat.valueOf(d[4]);
                TipusTiles tT = TipusTiles.valueOf(d[5]);
                TipusAdjacencia tA = TipusAdjacencia.valueOf(d[6]);
                double num_amagat = Double.parseDouble(d[7]);
                int num_mapa = Integer.parseInt(d[8]);

                // Crear l'hidato
                //Hidato h = new Hidato(dif, tT, tA, files, cols);
                Hidato h = new Hidato(id,files, cols, dif, tT, tA, num_amagat, num_mapa);

                // Extreure Tiles
                String[] infoTiles = d[9].split(" ");
                int index = 0;
                for(int i = 0; i < files; i++) {
                    for(int j = 0; j < cols; j++) {
                        if (index >= infoTiles.length) {
                            System.err.println("Error: El fitxer té menys cel·les de les esperades.");
                            break;
                        }

                        // Separem el valor del booleà (ex: "10_1")
                        String[] dadaTile = infoTiles[index++].split("_");
                        String valorStr = dadaTile[0];

                        boolean esFixa = dadaTile[1].equals("1"); // Cal?

                        // Creem la Tile usant el teu constructor: public Tile(Hidato hidato)
                        Tile t = new Tile(h);

                        // setValor ja gestiona internament startsSolved
                        try {
                            t.setValor(valorStr);
                            t.setStartSolved(esFixa); // PROVES
                        } catch (Exception e) {
                            System.err.println("Error en carregar valor: " + valorStr);
                        }

                        h.afegirTile(i, j, t);
                    }
                }

                // Calcular num màxim
                System.out.println("Calculant num_max a persistencia");
                try {
                    h.calcNumMax();
                    System.out.println("NUmero_max :"+h.getNum_max());
                } catch (Exception e) {
                    System.err.println("Error calculant el màxim de l'hidato ID " + id + ": " + e.getMessage());
                    // Si peta aquí, és perquè el tauler s'ha guardat buit o amb '#' a tot arreu
                }

                // Desar mapes a memoria
                hidatos.put(id, h);
                hidatoToRepo.put(id, idRepo);
            }
        } catch (IOException e) { e.printStackTrace(); }
    }

    /**
     * @brief Converteix el tauler de l'Hidato en un String lineal per a la seva persistència.
     *
     * @param h L'objecte Hidato a serialitzar.
     *
     * @return Un String amb la representació lineal del tauler.
     */
    private String serialitzarTauler(Hidato h) {
        // A mutable sequence of characters
        StringBuilder sb = new StringBuilder();
        int f = h.getNum_files();
        int c = h.getNum_cols();

        for (int i = 0; i < f; i++) {
            for (int j = 0; j < c; j++) {
                if (h.existeixTile(i, j)) {
                    Tile t = h.obtenirTile(i, j);
                    String valor = t.getValor();
                    String fixa = t.getStartSolved() ? "1" : "0";
                    sb.append(valor).append("_").append(fixa).append(" ");
                } else {
                    sb.append("#_1 "); // Representació per a cel·la buida/fora
                }
            }
        }
        return sb.toString().trim();
    }
}

