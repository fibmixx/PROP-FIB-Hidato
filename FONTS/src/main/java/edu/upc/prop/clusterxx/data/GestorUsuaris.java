package edu.upc.prop.clusterxx.data;

import edu.upc.prop.clusterxx.domain.model.Administrador;
import edu.upc.prop.clusterxx.domain.model.Usuari;
import edu.upc.prop.clusterxx.domain.interficies.IGestorUsuaris;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 * @file GestorUsuaris.java
 *
 * @brief Implementació real que guarda els usuaris en un fitxer de text (csv).
 *
 * S'implementa l'ús de la cache per operacions intermitges.
 * Serveix per tenir tota l'informació dins de l'estructura de dades d'aquest.
 */
public class GestorUsuaris implements IGestorUsuaris {
    // Quan la classe es crea, es llegeix el fitxer de disc i omple un HashMap
    // Les lectures les fan des del HashMap en memòria cache
    // Les escriptures actualitzen el mapa en memòria i guarden el HashMap a disc

    /**
     * @brief Ruta on es desarà el fitxer de dades.
     */
    private final String PATH_FITXER = "data/usuaris.txt";
    /**
     * @brief Mapa on es guarden tots els usuaris identificats pel seu username.
     */
    private Map<String, Usuari> usuaris;

    /**
     * @brief Creadora de la classe GestorUsuaris.
     *
     * @post Crea un GestorUsuaris amb un mapa buit i carrega des de disc les dades dels usuaris.
     */
    public GestorUsuaris() {
        this.usuaris = new HashMap<>();
        carregarDeDisc();
    }

    /**
     * @brief Funció per afegir un usuari al mapa
     *
     * @param username És el nom de l'usuari.
     * @param u És l'usuari que volem afegir.
     *
     * @post Afegeix l'usuari u al mapa.
     */
    @Override
    public void put(String username, Usuari u) {
        this.usuaris.put(username, u);
        guardarADisc();
    }

    /**
     * @brief Funció que retorna el mapa amb tots els usuaris
     *
     * @post Retorna el mapa amb tots els usuaris
     */
    @Override
    public Map<String, Usuari> all() {
        // Importante devolver una copia y no el original, ya que sino devolverias el puntero y
        // te lo podrian modificar
        return new HashMap<>(usuaris);
    }

    /**
     * @brief Funció per comprovar si un usuari existeix al mapa
     *
     * @param key És el nom de l'usuari.
     *
     * @post Retorna true si l'usuari existeix, fals si no.
     */
    @Override
    public boolean containsKey(String key) {
        return this.usuaris.containsKey(key);
    }

    /**
     * @brief Funció per eliminar un usuari del mapa
     *
     * @param key És el nom de l'usuari.
     *
     * @post Elimina l'usuari amb username key.
     */
    @Override
    public void remove(String key) {
        this.usuaris.remove(key);
        guardarADisc();
    }

    /**
     * @brief Funció per obtenir un usuari del mapa
     *
     * @param u És el nom de l'usuari.
     *
     * @post Retorna l'usuari amb username u.
     */
    @Override
    public Usuari get(String u) {
        return this.usuaris.get(u);
    }

    // MÈTODES PRIVATS DE LECTURA/ESCRIPTURA
    /**
     * @brief Guarda tot el mapa al fitxer de text.
     */
    private void guardarADisc() {
        File file = new File(PATH_FITXER);
        // Creem la carpeta si no existeix
        file.getParentFile().mkdirs();

        // Creates a buffered character-output stream that uses a default-sized output buffer.
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file))) {
            for (Usuari u : usuaris.values()) {
                // Format fitxer: username, password, personalBest, admin(?)
                String linia = u.getUsername() + "," + u.getPassword() + "," + u.getPersonalBest() + "," + u.isAdmin();
                bw.write(linia);
                bw.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error al guardar els usuaris a disc: " + e.getMessage());
        }
    }

    /**
     * @brief Llegeix el fitxer i omple el mapa a l'inici.
     */
    private void carregarDeDisc() {
        File file = new File(PATH_FITXER);
        if (!file.exists()) return; // Si és el primer cop que obrim, no hi ha res a carregar

        //Creates a buffering character-input stream that uses a default-sized input buffer.
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String linia;
            while ((linia = br.readLine()) != null) {
                String[] dades = linia.split(",");
                if (dades.length >= 3) {
                    String user = dades[0];
                    String pass = dades[1];
                    int pb = Integer.parseInt(dades[2]);

                    // Creem l'usuari amb les dades llegides (username, password, personalBest, isAdmin)
                    boolean isAdmin = Boolean.parseBoolean(dades[3]);
                    Usuari u;
                    if(isAdmin) {
                        u = new Administrador(user, pass);
                    } else {
                        u = new Usuari(user, pass);
                    }

                    // Actualitzem record llegit de l'arxiu
                    u.updatePersonalBest(pb);

                    this.usuaris.put(user, u);
                }
            }
        } catch (IOException | NumberFormatException e) {
            System.err.println("Error al carregar els usuaris de disc: " + e.getMessage());
        }
    }
}