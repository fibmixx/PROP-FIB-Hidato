package edu.upc.prop.clusterxx.domain.controladors;

import edu.upc.prop.clusterxx.domain.model.Hidato;
import edu.upc.prop.clusterxx.domain.model.Repositori;
import edu.upc.prop.clusterxx.domain.interficies.IGestorHidatos;
import edu.upc.prop.clusterxx.domain.interficies.IGestorRepositoris;

import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;

/**
 * @file CtrlRepositori.java
 *
 * @brief Controlador per gestionar múltiples instàncies de Repositori.
 *
 * Gestiona els repositoris, afegeix/esborra Hidatos ja creats en aquests.
 */
public class CtrlRepositori {
    /**
     * @brief Estructura per guardar tots els repositoris creats a memòria.
     * La clau és l'identificador (ID) del repositori.
     */
    private final Map<Integer, Repositori> conjRepositoris;

    /**
     * @brief Repositori actiu actualment.
     */
    private Repositori seleccionat;

    /**
     * @brief Instància del gestor encarregat de la persistència física dels Hidatos.
     */
    private IGestorHidatos gestorH; // Atributs per a la persistència

    /**
     * @brief Instància del gestor encarregat de la persistència física dels Repositoris.
     */
    private IGestorRepositoris gestorR;

    /**
     * @brief Creadora per defecte.
     *
     * @param gestorR Enllaç al sistema de persistència de repositoris.
     * @param gestorH Enllaç al sistema de persistència de taulers d'Hidato.
     *
     * @post Inicialitza un HashMap buit pel conjunt de repositoris i no té cap repositori seleccionat.
     */
    public CtrlRepositori(IGestorRepositoris gestorR, IGestorHidatos gestorH) {
        this.conjRepositoris = new HashMap<>();
        seleccionat = null;
        this.gestorH = gestorH;
        this.gestorR = gestorR;
    }

    /**
     * @brief Crea un nou repositori i l'afegeix al sistema.
     * @param nom Nom del nou repositori.
     * @param owner Nom de l'usuari a qui li pertany el repositori.
     * @return Retorna l'ID del repositori creat.
     */
    public int setRepositori(String nom, String owner) {
        Repositori nouRepo = new Repositori(nom);
        conjRepositoris.put(nouRepo.getIdentificador(), nouRepo);

        // Guardem repositori a disc per no perdre-ho al tancar-obrir
        gestorR.put(nouRepo.getIdentificador(), nouRepo, owner);

        return nouRepo.getIdentificador();
    }

    /**
     * @brief Obté un repositori a partir del seu ID. Llença una excepció si el repositori amb aquella id no existeix.
     * @param id Identificador del repositori.
     * @return El repositori demanat, o null si no existeix.
     * @throws Exception si el repositorj amb la id indicada no existeix.
     */
    public Repositori getRepositori(int id) throws Exception {
        if (!conjRepositoris.containsKey(id)) {
            throw new Exception("Error: El repositori amb ID " + id + " no existeix.");
        }
        return conjRepositoris.get(id);
    }

    /**
     * @brief Elimina un repositori del sistema. Llença una excepció si el repositori amb aquella id no existeix.
     * @param id Identificador del repositori a eliminar.
     * @throws Exception si el repositorj amb la id indicada no existeix.
     */
    public void eliminarRepositori(int id) throws Exception {
        if (!conjRepositoris.containsKey(id)) {
            throw new Exception("Error: No es pot eliminar. El repositori amb ID " + id + " no existeix.");
        }

        Map<Integer, Hidato> hidatosDelRepo = gestorH.carregarPerRepositori(id);
        for (Integer idHidato : hidatosDelRepo.keySet()) {
            gestorH.remove(idHidato);
        }

        // Si estem eliminant el repositori que tenim seleccionat, el de-seleccionem
        if (seleccionat != null && seleccionat.getIdentificador() == id) {
            seleccionat = null;
        }

        conjRepositoris.remove(id);

        //L'esborrem també de disc
        gestorR.remove(id);
    }

    /**
     * @brief Llista el nom de tots els repositoris actius.
     * @return Llista de strings amb els noms.
     */
    public List<String> llistarNomsRepositoris() {
        List<String> noms = new ArrayList<>();
        for (Repositori repo : conjRepositoris.values()) {
            noms.add(repo.getIdentificador() + " - " + repo.getNom());
        }
        return noms;
    }

    /**
     * @brief Selecciona un repositori com a actiu.
     * @param id Identificador del repositori a seleccionar.
     * @throws Exception si el repositori amb la id indicada no existeix.
     */
    public void setSeleccionat(int id) throws Exception {
        if (conjRepositoris.containsKey(id)) {
                seleccionat = conjRepositoris.get(id);
        } else {
            throw new Exception("Error: No es pot seleccionar. El repositori amb ID " + id + " no existeix.");
        }
    }

    /**
     * @brief Carrega de disc tots els repositoris i hidatos d'un usuari concret.
     * @param owner Nom de l'usuari que ha fet login.
     * @post Neteja la memòria cau local anterior i sincronitza l'estat amb els fitxers de disc del perfil
     * de l'usuari aportat.
     */
    public void carregarDadesUsuari(String owner) {
        // Netejar per si queden dades d'usuaris anteriors
        this.conjRepositoris.clear();
        this.seleccionat = null;

        // Demanar al gestor els repos d'aquest usuari
        Map<Integer, Repositori> reposUsuari = gestorR.carregarPerUsuari(owner);

        // Ficar els repos al mapa en memòria i carregar els hidatos de cadascun
        for(Repositori r : reposUsuari.values()) {
            conjRepositoris.put(r.getIdentificador(), r);

            Map<Integer, Hidato> hidatosRepo = gestorH.carregarPerRepositori(r.getIdentificador());
            for(Hidato h : hidatosRepo.values()) {
                r.afegirHidato(h); // Ficar hidato dins de repo
            }
        }
    }

    // --------------- GESTIÓ D'HIDATOS A REPOSITORIS -----------------------------
    /**
     * @brief Funció per afegir un hidato concret al repositori actual.
     *
     * @param hidato L'objecte Hidato a guardar.
     *
     * @post Afegeix un Hidato al repositori seleccionat i el desa al disc.
     *
     * @throws Exception Si no hi ha cap repositori seleccionat prèviament com a actiu.
     */
    public void afegirHidatoARepoActiu(Hidato hidato) throws Exception {
        if (seleccionat == null) {
            throw new Exception("Error: Has de seleccionar un repositori abans de guardar-hi un Hidato.");
        }

        // Afegim l'Hidato a l'objecte Repositori
        seleccionat.afegirHidato(hidato);
        // Guardem al fitxer .txt
        gestorH.put(hidato.getId(), hidato, seleccionat.getIdentificador());
    }

    /**
     * @brief Funció per esborrar un hidato concret d'un repositori.
     *
     * @param h Identificador del hidato a esborrar.
     * @param r Identificador del repositori del qual es vol esborrar el hidato.
     *
     * @post Esborra un Hidato del repositori seleccionat i l'elimina del disc.
     *
     * @throws Exception Si el repositori o el hidato amb les id indicades no existeixen.
     */
    public void esborrarHidato(int h, int r) throws Exception {
        Repositori repo = getRepositori(r);
        repo.esborrarHidato(h);
        gestorH.remove(h);
    }

    /**
     * @brief Funció per compartir un repositori amb un altre usuari.
     *
     * @param idRepo Identificador del repositori a compartir.
     * @param nomUser Nom de l'usuari amb qui es vol compartir el repositori.
     *
     * @post Crea una còpia del repositori i dels seus hidatos associats, i els assigna a l'usuari destinatari.
     *
     * @throws Exception Si el repositori amb la id indicada no existeix o si hi ha algun error durant el procés de compartició.
     */
    public void compartirRepositori(int idRepo, String nomUser) throws Exception {
        Repositori repo = getRepositori(idRepo);
        Repositori copiaRepo = new Repositori(repo.getNom());
        gestorR.put(copiaRepo.getIdentificador(), copiaRepo, nomUser);
        Map<Integer, Hidato> hidatosOriginals = gestorH.carregarPerRepositori(idRepo);
        System.out.println("Hidatos originals del repo a compartir: " + hidatosOriginals.keySet());
        for (Hidato hOriginal : hidatosOriginals.values()) {
            Hidato copiaHidato = new Hidato(hOriginal);
            copiaHidato.asignar_nou_ID();
            copiaRepo.afegirHidato(copiaHidato);
            gestorH.put(copiaHidato.getId(), copiaHidato, copiaRepo.getIdentificador());
        }
        CtrlDomini.getInstance().getCtrlUsuari().getUsuari(nomUser).afegirRepositori(copiaRepo);
    }
}
