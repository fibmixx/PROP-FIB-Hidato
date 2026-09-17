package edu.upc.prop.clusterxx.data;

import edu.upc.prop.clusterxx.domain.model.Ranking;
import edu.upc.prop.clusterxx.domain.interficies.IGestorRankings;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class GestorRankings implements IGestorRankings {

    /**
     * @brief Direcció on guardar els rànkings
     */
    private final String PATH_FITXER = "data/rankings"; //Direcció on guardarem en un .txt cada ranking
    /**
     * @brief Mapa on guardar els diferents rànkings que es creein.
     */
    private Map<Integer, Ranking> rankings;

    /**@breif Creadora de la classe GestorRankings
     *
     * @post Crea un GestorRankings amb un mapa buit i carrega des de disc les dades que tenim guardades
     */

    public GestorRankings() {
       this.rankings = new HashMap<>();
       File carpeta = new File(PATH_FITXER); //Crea el directori
        if (!carpeta.exists()) {
            carpeta.mkdirs(); //Ens assegurem que es creei la carpeta en concret
        }
        llegirDeDisc(); //Començem sent coherents amb les dades que tenim a Disc per mantenir-ho a RAM
    }

    /**
     * @brief Funció per guardar rankings que tenim a les estructures de memòria a disc
     * @param id Nombre que identifica de forma única a un Ranking
     * @param ranking És el rànking que volem guardar per primera vegada o bé sobreescriure
     */
    @Override
    public void guardaRanking(int id, Ranking ranking){
        //1. Afegir-lo al map que estem mantenint
        rankings.put(ranking.getIdentificador(), ranking);
        //2. Guardar tot el map a disc per preservar persistència i que no es perdi.
        escriureADisc(); //Sempre que modifiquem la nostra còpia a RAM (rankings) cal escriure-la a disc
    }

    /**
     * @brief Funció que retorna un rànking especificat des de les estructures de memòria
     * @param id_ranking Identificador que permet saber de forma única quin rànking volem
     * @return Retorna un Ra
     */
    @Override
    public Ranking agafarRanking(int id_ranking){
        return rankings.get(id_ranking);
    }

    /**
     * @brief Funció que retorna tots els Rànkings guardats a les estructures de memòria.
     * @return Retorna tots els rànkings dins d'un mapa amb clau = id i valor = Ranking
     */
    @Override
    public Map<Integer, Ranking> agafarTotsRankings(){
        /*List<Ranking> rankings_aux = new ArrayList<>();
        for(Map.Entry<Integer, Ranking> entry : rankings.entrySet()){
            rankings_aux.add(entry.getValue());
        }*/
        return rankings;
    }

    /**
     * @brief Funció que permet eliminar un rànking de les estructures de persistència
     * @param id_ranking Identificador únic del rànquing que volem eliminar.
     * @post S'ha esborrat el ranking indicat de les estructures de persistència
     */
    @Override
    public void delRanking(int id_ranking){
        rankings.remove(id_ranking);
         escriureADisc();//-> Com que hem modificat la nostra còpia a memòria cal que siguem coherents amb el que tenim a disc i actualitzar l'estat per si l'aplicació es tanca de forma sobtada
        //OPCIO B: Borrem només el fitxer de rànquing concret (així evitem que hagim de tronar a escriure tots els rankings)

    }

    /**
     * @brief Funció que permet saber si dins de les estructures de persistència de Rankings s'hi troba un ranking en concret
     * @param id_ranking nombre únic que permet identificar a un Ranking de forma única
     * @return retorna true si sí que conté el ranking, fals de no ser així.
     */
    @Override
    public boolean teClau(int id_ranking){
        return rankings.containsKey(id_ranking);
    }

    /**
     * @breif Funció pròpia de la persistència per escriure a disc tots els rankings que hi ha
     * @post S'han escrit tots els rankings a disc
     */
    private void escriureADisc(){
        File directori  = new File(PATH_FITXER);
        if(!directori.exists()){
            directori.mkdirs();
        }
        //////ES POT SUBSTITUIR PER ELIMINIAR DIRECTAMENT EL RANKING A "DEL"
        //1. Primer cal esborrar tots els fitxers que existeixen per evitar que es coli un repositori que ja ha estat eliminat
        if(directori.exists()){
            File[] fitxers = directori.listFiles();
            if(fitxers != null){
                for(int i = 0; i < fitxers.length; i++){
                    File fitxer = fitxers[i];
                    if(!fitxer.isDirectory()){ //Potser esborra el "." i el ".."?
                        fitxer.delete(); //esborrem tots els fitxers que contingui /data/rankings
                    }
                }
            }
        }
        //2.Un cop esborrat tots els fitxers existents per evitar que es guardi un ranking que ja no existeix.
        //Podem procedir a escriure de nou els rànkings
        for(Map.Entry<Integer, Ranking> entry : rankings.entrySet()){
            String dir_ranking = PATH_FITXER + "/" + entry.getKey() + ".txt";
            try(PrintWriter fw = new PrintWriter(new FileWriter(dir_ranking))){//Suposem que crea el fitxer? -> SÍ
                Ranking rank =  entry.getValue();
                List<Ranking.PairUsuariPuntuacio> punts = rank.getPuntuacions();
                for(int i = 0; i < punts.size(); i++){
                    Ranking.PairUsuariPuntuacio p = punts.get(i); //Obtenim la puntuació concreta i l'usuari
                    fw.println(p.usuari+","+p.puntuacio); //Per cad linia tindrem el format usuari, puntuació
                }
            }catch(IOException e){
                System.err.println("S'ha produït un error en escriure a disc: " + e.getMessage());
            }
        }
    }

    /**
     * @brief Funció que permet llegir de Disc tots els rànkings que hi estan guardats
     * @post S'han carregat tots els rankings guardats a disc a les estructures de memòria de persistència.
     */
    private void llegirDeDisc(){
        File directori = new File(PATH_FITXER);
        if(!directori.exists()){
            directori.mkdirs();
        }
        File[] fitxers = directori.listFiles();
        if(fitxers != null){
            for(int i = 0; i < fitxers.length; i++){
                File fitxer = fitxers[i];
                if(fitxer.isFile() && fitxer.getName().endsWith(".txt")){ //Aquí ja tenim un dels rànkings
                    try {
                        Integer id = Integer.parseInt(fitxer.getName().replace(".txt",""));
                        Ranking ranking = new Ranking(id);
                        try(BufferedReader reader = new BufferedReader(new FileReader(fitxer))){
                            String linia;
                            while((linia = reader.readLine()) != null){
                                if(linia.length() >2){
                                    String[] meitats = linia.split(",");
                                    if(meitats.length == 2){
                                        String usuari = meitats[0];
                                        Integer puntuacio = Integer.parseInt(meitats[1]);
                                        ranking.updateScore(usuari, puntuacio);
                                    }
                                }
                            }
                        }
                        rankings.put(id, ranking);
                    }catch(IOException e){
                        System.err.println("Error processant fitxer " + fitxer.getName());
                    }catch(NumberFormatException e){
                        System.err.println("Error processant el nom del fitxer (no es un numero)");
                    }
                }
            }

        }
    }
}