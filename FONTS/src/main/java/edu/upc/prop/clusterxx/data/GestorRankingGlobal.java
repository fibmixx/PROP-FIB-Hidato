package edu.upc.prop.clusterxx.data;

import edu.upc.prop.clusterxx.domain.model.Ranking;
import edu.upc.prop.clusterxx.domain.model.RankingGlobal;
import edu.upc.prop.clusterxx.domain.interficies.IGestorRankingGlobal;

import java.io.*;

/**
 * @file GestorRankingGlobal.java
 *
 * @brief Implementació de la capa de Persistencia de la classe RankingGlobal.
 * Serveix per tal de donar suport a l'emmagatzematge a disc de la classe RankingGlobal
 */
public class GestorRankingGlobal implements IGestorRankingGlobal {

    /**
     * @brief Direcció on s'ha de guardar el RankingGlobal.
     */
    private final String PATH_FITXER = "data/RankingGlobal.txt";

    /**@brief Constructora del Gestor del Ranking Global
     * @post S'ha creat la carpeta data i el fitxer RaningGlobal.txt si no existien
     */
    public GestorRankingGlobal() {
        //En un principi el ctrlRanking ja manté una instància de rànking Global a memòria, no cal que ho tornem a fer
        File fitxer = new File(PATH_FITXER);
        File directori = fitxer.getParentFile(); //Obtenim carpeta data
        if(directori != null && !directori.exists()){ //Per crear la carpeta data si no existeix.
            directori.mkdirs();
        }
        if(!fitxer.exists()){ //Si el fitxer no existeix el creem
            try {
                fitxer.createNewFile();
            } catch (IOException e) {
                System.err.println("Error creant l'arxiu RankingGlobal.txt: "+ e.getMessage());
            }
        }
        //Llegir el rànking serà cosa del CtrlRanking ja que no tenim la instància de Ranking global on s'ha de mantenir
        //La informació mentre el joc s'executa.
    }
    /**
     * @breif Funció que guardarà una nova entrada a ranking global
     * @param rankingGlobal Ranking global que volem guardar a disc
     */
    @Override
    public void guardaRankingGlobal(RankingGlobal rankingGlobal){
        try(PrintWriter writer = new PrintWriter(new FileWriter(PATH_FITXER))){
            for(Ranking.PairUsuariPuntuacio p : rankingGlobal.getPuntuacions()){
                writer.println(p.usuari+","+ p.puntuacio);
            }
        }catch (IOException e){
            System.err.println("Error lectura RankingGlobal. No s'ha "); //Dona error aquí en intentar llegir
        }
    }

    /**
     * @breif Funció que s'encarregarà de carregar el Ranking Global des de Disc a les estructures de RAM
     * @param rankingGlobal instància del rànking global que s'està executant.
     */
    @Override
    public void agafarRankingGlobal(RankingGlobal rankingGlobal){
        File fitxer = new File(PATH_FITXER);
        File directori = fitxer.getParentFile(); //Obtenim carpeta data
        if(directori != null && !directori.exists()){
            directori.mkdirs();
        }
        if(!fitxer.exists()){ //Si el fitxer no existeix el creem
            try {
                fitxer.createNewFile();
            } catch (IOException e) {
                System.err.println("Error creant l'arxiu RankingGlobal.txt: "+ e.getMessage());
            }
        }
        try(BufferedReader reader = new BufferedReader(new FileReader(fitxer))){
            //LLegim lina a línia, ja que cada linia és un pair <nom_user, punts}
            String linia;
            while((linia = reader.readLine()) != null){
                String[] parts = linia.split(","); //Separem usuari de puntuació
                if(parts.length == 2){
                    String user = parts[0];
                    int punts = Integer.parseInt(parts[1]);
                    rankingGlobal.updateScore(user, punts);
                }
            }
        }catch (IOException e){
            System.err.println("Error lectura RankingGlobal");
        } catch( NumberFormatException e){
            System.err.println("Error Format lectura ranquing global");
        }
    }
}
