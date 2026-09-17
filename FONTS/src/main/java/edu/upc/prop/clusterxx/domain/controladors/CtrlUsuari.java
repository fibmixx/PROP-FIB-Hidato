package edu.upc.prop.clusterxx.domain.controladors;
import edu.upc.prop.clusterxx.domain.model.*;
import edu.upc.prop.clusterxx.domain.interficies.IGestorUsuaris;

import java.util.ArrayList;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.List;

/**
 * @file CtrlUsuari.java
 *
 * @brief Controlador per gestionar múltiples instàncies d'Usuaris.
 *
 * S'encarrega del registre, eliminació i perfils dels usuaris, així com de
 * gestionar la sessió activa (login/logout) i coordinar-se amb la persistència.
 */
public class CtrlUsuari {
    /**
     * @brief Estructura on es guarden tots els usuaris.
     */
    private IGestorUsuaris usuaris;
    /**
     * @brief Usuari amb la sessió iniciada
     */
    private Usuari usuariSessio;
    /**
     * @brief Creadora del controlador d'usuari
     * @param usuaris Enllaç al sistema de persistència d'usuaris.
     * @post Crea un controlador d'usuari amb un gestor de dades buit i sense cap usuari amb la sessió iniciada.
     * També es crea un administrador per defecte amb username "admin" i password "admin".
     */
    public CtrlUsuari(IGestorUsuaris usuaris) {
        usuariSessio = null;
        this.usuaris = usuaris; //Lo unico a modificar cuando se cambie la interfaz
        if(!existsUsuari("admin")) addAdmin("admin", "admin");
    }

    // ?????????
    public void carregarProba() {
        Usuari u = getUsuari("admin");
        String[][] s = {
                {"#","1","2","3","#"},
                {"11","10","9","4","5"},
                {"12","13","8","7","6"},
                {"23","14","15","16","17"},
                {"?","?","20","19","18"}
        };
        int idh = CtrlDomini.getInstance().getCtrlHidato().carregarTauler(5, 5, s, "triangle");

        Map<Integer, Repositori> x = u.getRepositoris();
        x.forEach((id, repositori) -> {
            repositori.afegirHidato(CtrlDomini.getInstance().getCtrlHidato().getHidatoActiu());
            CtrlDomini.getInstance().getCtrlHidato().guardarHidatoActiu(idh, id);
        });
    }

    /**
     * @brief Funció per obtenir un usuari.
     *
     * @param username És el nom de l'usuari.
     *
     * @post Retorna l'usuari amb nom d'usuari indicat amb username.
     *
     * @exception NoSuchElementException Si l'usuari no existeix.
     */
    public Usuari getUsuari(String username) {
        Usuari result = usuaris.get(username);
        if (result == null) {
            System.out.println("Error: El Usuario no Existe");
            throw new NoSuchElementException("Usuario No Existe");
        }
        return result;
    }

    /**
     * @brief Funció per comprovar si un usuari existeix
     *
     * @param username És el nom de l'usuari.
     *
     * @return Retorna true si l'usuari existeix, fals si no.
     */
    public boolean existsUsuari(String username) {
        return usuaris.containsKey(username);
    }

    /**
     * @brief Funció per obtenir un mapa amb tots els usuaris.
     *
     * @return Retorna un mapa amb tots els usuaris.
     */
    public Map<String,Usuari> getAllUsuaris() {
        return usuaris.all();
    }

    /**
     * @brief Funció per afegir un usuari
     *
     * @param username És el nom de l'usuari.
     * @param password És la contrasenya de l'usuari.
     *
     * @post Afegeix un usuari amb nom username i contrasenya password
     *
     * @exception IllegalArgumentException Si l'usuari ja existeix.
     */
    public void addUsuari(String username, String password) {
        if (usuaris.containsKey(username)) {
            System.out.println("Error: El Usuario ya Existe");
            throw new IllegalArgumentException("Usuario Ya Existe");
        }
        Usuari nu = new Usuari(username,password);
        usuaris.put(username,nu);
    }

    /**
     * @brief Funció per afegir un administrador
     *
     * @param username És el nom de l'administrador.
     * @param password És la contrasenya de l'administrador.
     *
     * @post Afegeix un administrador amb nom username i contrasenya password
     *
     * @exception IllegalArgumentException Si l'administrador ja existeix.
     */
    public void addAdmin(String username, String password) {
        if (usuaris.containsKey(username)) {
            System.out.println("Error: El Administrador ya Existe");
            throw new IllegalArgumentException("Administrador Ya Existe");
        }
        Administrador na = new Administrador(username,password);
        usuaris.put(username,na);
    }

    /**
     * @brief Funció per eliminar un usuari
     *
     * @param username És el nom de l'usuari.
     *
     * @return Retorna true si s'ha eliminat correctament. Altrament, retorna false.
     *
     * @exception NoSuchElementException Si l'usuari no existeix.
     */
    public boolean deleteUsuari(String username) {
        if (!usuaris.containsKey(username)) {
            System.out.println("Error: El Usuario no Existe");
            throw new NoSuchElementException("Usuario No Existe");
        }
        if (usuariSessio != null && username.equals(usuariSessio.getUsername())) {
            return false;
        }
        usuaris.remove(username);
        return true;
    }

    /**
     * @brief Funció per iniciar sessió d'un usuari
     *
     * @param username És el nom de l'usuari.
     * @param password És la contrasenya de l'usuari.
     *
     * @return Retorna true si la sessió ha estat iniciada correctament i assigna l'usuari com l'usuari amb sessió Iniciada.
     *  Retorna false si ha fallat l'inici de sessió.
     */
    public boolean login(String username, String password) {
        if (existsUsuari(username)) {
            Usuari u = usuaris.get(username);
            if (u.isPassword(password)) {
                this.usuariSessio = u;
                return true;
            }
        }
        return false;
    }

    /**
     * @brief Funció per tancar la sessió d'un usuari
     *
     * @post Tanca sessió de l'usuari que tenia la sessió iniciada
     */
    public void logout() {
        this.usuariSessio = null;
    }

    /**
     * @brief Funció per obtenir l'usuari amb la sessió iniciada
     *
     * @return Retorna l'usuari amb la sessió iniciada
     */
    public Usuari getUsuariSessio() {
        return usuariSessio;
    }

    /**
     * @brief Funció per comprovar si hi ha una sessió activa
     *
     * @return Retorna true si hi ha una sessió activa, fals si no
     */
    public boolean isSessioActiva() {
        return this.usuariSessio != null;
    }

    /**
     * @brief Funció per imprimir tots els usuaris del sistema
     *
     * @post Imprimeix tots els usuaris del sistema amb la seva puntuació
     */
    public void printAllUsuaris() {
        for (Map.Entry<String, Usuari> entry : usuaris.all().entrySet()) {
            String username = entry.getKey();
            Usuari u = entry.getValue();
            System.out.println("- " + username + " Puntuació: " + u.getPersonalBest());
        }
    }

    /**
     * @brief Funció per actualitzar el record personal d'un usuari
     *
     * @param username És el nom de l'usuari.
     * @param pb És el nou record personal de l'usuari.
     *
     * @post Actualitza el record personal de l'usuari amb username username a pb. Si l'usuari no existeix, no fa res.
     */
    public void updatePersonalBest(String username, int pb) {
        if (existsUsuari(username)) {
            Usuari u = usuaris.get(username);

            if (pb >= u.getPersonalBest() || pb == 0) {
                u.updatePersonalBest(pb);
                usuaris.put(username, u); // Actualitzem user a usuaris.txt
                CtrlDomini.getInstance().getCtrlRanking().guardaRankingGlobalDisc();
            }

        }
    }

    /**
     * @brief Funció per imprimir tots els repositoris de l'usuari amb sessió iniciada
     *
     * @post Imprimeix tots els repositoris de l'usuari amb sessió iniciada, o un missatge indicant que no hi ha cap usuari amb sessió iniciada.
     */
    public void printRepositorisUsuariSeleccionat() {
        if (isSessioActiva()) {
            Map<Integer, Repositori> repositoris = usuariSessio.getRepositoris();
            if (repositoris.isEmpty()) {
                System.out.println("No tens cap repositori creat.");
            } else {
                System.out.println("Els teus repositoris:");
                for (Map.Entry<Integer, Repositori> entry : repositoris.entrySet()) {
                    System.out.println("- ID: " + entry.getKey() + ", Nom: " + entry.getValue().getNom());
                }
            }
        } else {
            System.out.println("No hi ha cap usuari amb sessió iniciada.");
        }
    }

    /**
     * @brief Funció per imprimir tots els jocs de l'usuari amb sessió iniciada
     *
     * @post Imprimeix tots els jocs de l'usuari amb sessió iniciada
     */
    public void printGamesUsuariSeleccionat() {
        if(isSessioActiva()) {
            usuariSessio.printGames();
        }
        else {
            System.out.println("No hi ha cap usuari amb sessió iniciada.");
        }
    }

    /**
     * @brief Funció per buidar el Ranking Global.
     * @post Si l'usuari actiu és administrador, es buida el Ranking Global i s'actualitza el personalBest dels
     * usuaris a 0.
     */
    public void netejarRankingGlobal() {
        if (isSessioActiva() && usuariSessio.isAdmin()) {
            //List l = usuariSessio.getRankingGlobal().getPuntuacions();
            List<Ranking.PairUsuariPuntuacio> l = new ArrayList<>(usuariSessio.getRankingGlobal().getPuntuacions());

            for (int i = 0; i < l.size(); ++i) {
                Ranking.PairUsuariPuntuacio p = (Ranking.PairUsuariPuntuacio) l.get(i);
                String username = p.usuari;
                if (existsUsuari(username)) {
                    updatePersonalBest(username, 0);
                }
            }

            usuariSessio.getRankingGlobal().emptyRanking(); // !!!!!

        } else {
            System.out.println("No hi ha cap admin amb sessió iniciada.");
        }
    }
}
