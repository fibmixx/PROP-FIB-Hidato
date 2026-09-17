package edu.upc.prop.clusterxx.data;

import edu.upc.prop.clusterxx.domain.model.Repositori;
import edu.upc.prop.clusterxx.domain.interficies.IGestorRepositoris;

import java.io.*;
import java.util.*;

public class GestorRepositoris implements IGestorRepositoris {
    /**
     * @brief Ruta on es desarà el fitxer de dades.
     */
    private final String PATH_FITXER = "data/repositoris.txt";
    /**
     * @brief Mapa on es guarden tots els repositoris identificats pel seu id.
     */
    private Map<Integer, Repositori> repositoris;
    /**
     * @brief Mapa on es guarden tots els usuaris owners dels repos (id_repo, owner).
     */
    private Map<Integer, String> owners;

    /**
     * @brief Creadora de la classe GestorRepositoris.
     *
     * @post Crea un GestorRepositoris amb un mapa buit i carrega des de disc les dades dels usuaris.
     */
    public GestorRepositoris() {
        this.repositoris = new HashMap<>();
        this.owners = new HashMap<>();

        // Crear la carpeta 'data' si no existeix
        File folder = new File("data");
        if (!folder.exists()) folder.mkdirs();

        carregarDeDisc();
    }

    /**
     * @brief Funció per afegir un repositori al mapa
     *
     * @param id És l'identificador del repositori.
     * @param r És el Repositori que volem afegir.
     * @param owner És l'usuari owner del repositori r.
     *
     * @post Afegeix el Repositori r al mapa.
     */
    @Override
    public void put(int id, Repositori r, String owner) {
        repositoris.put(id, r);
        owners.put(id, owner);
        guardarADisc();
    }

    /**
     * @brief Funció que busca i carrega del mapa owners els repositoris d'un usuari en concret.
     *
     * @param owner  És l'usuari a qui li pertany el repositori r.
     *
     * @return Retorna un HashMap amb tots els repositoris de l'usuari.
     */
    @Override
    public Map<Integer, Repositori> carregarPerUsuari(String owner) {
        Map<Integer, Repositori> resultat = new HashMap<>();
        for (Map.Entry<Integer, String> entry : owners.entrySet()) {
            if (entry.getValue().equals(owner)) {
                int id = entry.getKey();
                resultat.put(id, repositoris.get(id));
            }
        }
        return resultat;
    }

    /**
     * @brief Funció per eliminar un repositori.
     *
     * @param id És l'identificador del Repositori.
     *
     * @post Elimina el repositori amb identificador id.
     */
    @Override
    public void remove(int id) {
        repositoris.remove(id);
        owners.remove(id);
        guardarADisc();
    }

    /**
     * @brief Funció per obtenir un repositori del mapa
     *
     * @param id És l'identificador del Repositori.
     *
     * @post Retorna el Repositori amb identificador id.
     */
    @Override
    public Repositori get(int id) {
        return repositoris.get(id);
    }

    // MÈTODES PRIVATS DE LECTURA/ESCRIPTURA
    /**
     * @brief Guarda tot el mapa al fitxer de text.
     */
    private void guardarADisc() {
        File file = new File(PATH_FITXER);
        // Al utilitzar try no cal tancar el fitxer amb pw.close()
        // PrintWriter: Converteix les dades de l'objecte en una sola cadena de text.
        try (PrintWriter pw = new PrintWriter(new FileWriter(file))) {
            // keySet: retorna un Set de les keys del HashMap
            for (Integer id : repositoris.keySet()) {
                Repositori r = repositoris.get(id);
                String owner = owners.get(id);
                // Format: id,nombre,owner
                pw.println(id + "," + r.getNom() + "," + owner);
            }
        } catch (IOException e) {
            System.err.println("Error al guardar repositoris: " + e.getMessage());
        }
    }

    /**
     * @brief Llegeix el fitxer i omple el mapa a l'inici.
     */
    private void carregarDeDisc() {
        File file = new File(PATH_FITXER);
        if (!file.exists()) return; // Si és el primer cop que obrim, no hi ha res a carregar

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String linia;
            int maxId = -1;
            while ((linia = br.readLine()) != null) {
                String[] d = linia.split(",");
                int id = Integer.parseInt(d[0]);
                String nom = d[1];
                String owner = d[2];

                // Manteniment d'identificadors autoincrementals
                if (id > maxId) maxId = id;

                // Creem el repositori amb les dades llegides (nom)
                Repositori r = new Repositori(nom);
                repositoris.put(id, r);
                owners.put(id, owner);
            }
            //Actualitzem el nombre de repositoris (+1 pq comença en 0)
            Repositori.setNumRepositoris(maxId + 1);
        } catch (IOException e) {
            System.err.println("Error al carregar repositoris: " + e.getMessage());
        }
    }
}
