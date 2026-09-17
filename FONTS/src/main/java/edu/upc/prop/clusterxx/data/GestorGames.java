package edu.upc.prop.clusterxx.data;

import edu.upc.prop.clusterxx.domain.model.CompetitiveGame;
import edu.upc.prop.clusterxx.domain.model.CoopGame;
import edu.upc.prop.clusterxx.domain.model.Game;
import edu.upc.prop.clusterxx.domain.interficies.IGestorGames;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GestorGames implements IGestorGames {
    /**
     * @brief Direcció on guardarem els diferents games
     */
    private final String PATH_FITXER = "data/Games";

    /**
     * @breif Mapa que ens permet tenir les dades de Games a memòria en comptes d'haver d'anar a buscar-les a disc cada vegada.
     */

    private Map<String, List<String>> games;

    /**
     * @breif Constructora del mètode GestorGames.
     * @post Es crea una nova instància de GestorGames.
     */
    public GestorGames(){
        this.games = new HashMap<>();
        File carpeta = new File(PATH_FITXER); //Crea el directori
        if (!carpeta.exists()) {
            carpeta.mkdirs(); //Ens assegurem que es creei la carpeta en concret
        }
        carregarDeDisc();
    }

    /**
     * @brief Funció que permet guardar a disc qüalsevol objecte Game
     * @param game Instància de Game que volem guardar a disc
     * @post S'ha guardat a disc la instància de Game indicada.
     */
    @Override
    public void guardarGame(Game game) {
        /*
        * Estructura per guardar starTime, tempsAcumulat, idUsuari, idHidato, finished, punctuation, files, columnes, tipusGame, tauler, [tempsCompany/username]
        * */
        String identificador = game.getIdentificador();
        List<String> dadesGame = new ArrayList<>();
        dadesGame.add(String.valueOf(game.getStartTime()));
        dadesGame.add(String.valueOf(game.getTimer().getTemps()));
        dadesGame.add(game.getUser().getUsername());
        dadesGame.add(String.valueOf(game.getHidatoOriginal().getId()));
        dadesGame.add(String.valueOf(game.getFinished()));
        dadesGame.add(String.valueOf(game.getPuntuation()));
        dadesGame.add(String.valueOf(game.getHidato().getNum_files()));
        dadesGame.add(String.valueOf(game.getHidato().getNum_cols()));
        dadesGame.add(String.valueOf(game.getTipusGame()));

        int files = game.getHidato().getNum_files();
        int columnes = game.getHidato().getNum_cols();
        String[][] tauler = game.returnTauler();
        for(int i = 0; i< files; i++){
            StringBuilder string_fila = new StringBuilder();
            for(int j = 0; j< columnes; j++){
                string_fila.append(tauler[i][j]);
                if(j<columnes-1){
                    string_fila.append(",");
                }
            }
            dadesGame.add(string_fila.toString());
        }
        if(game instanceof CoopGame){
            dadesGame.add(String.valueOf(((CoopGame) game).getTempsCompany()));
        }else if (game instanceof CompetitiveGame){
            dadesGame.add(String.valueOf(((CompetitiveGame) game).getUsuariDesafiat().getUsername()));
        }else{
            dadesGame.add(String.valueOf(0));
        }

        //Guardem el Game a la memòria:
        games.put(identificador, dadesGame);
        //Escribim a disc per mantenir coherència:
        escriureADisc(identificador, dadesGame);
    }

    /**
     * @brief Funció que permet obtenir les dades guardades d'un Game concret
     * @param idGame Identificador del Game que volem carregar
     * @return retorna una Llista amb tots els elements que s'han llegit de Disc per tal que CtrlGame pugui restaurar de forma correcta el Game amb els Hidatos i els Usuaris concrets.
     */
    @Override
    public List<String> agafarDadesGame(String idGame){
        if(games.containsKey(idGame)){
            return new ArrayList<>(games.get(idGame));
        }
        return new ArrayList<>();
    }

    /**
     * @biref Funció que busca totes les Partides (Games) que té guardats un usuari.
     * @param nomUser Identificador de l'usuari el qual volem recuperar tots els seus games
     * @return Retorna una llista amb tots els IDs de tots els games que corresponen a l'usuari especificat.
     */
    @Override
    public List<String> agafarPartidesUser(String nomUser){
        System.out.println("Estic agafar");
        List<String> gamesUsuari = new ArrayList<>();
        for( String idGame : games.keySet()){
            if(idGame.startsWith(nomUser+"_")){
                gamesUsuari.add(idGame);
            }
        }
        return gamesUsuari;
    }

    /**
     * @breif Funció que permet obtenir tots els games que es troben a persistència
     * @return Retorna una List amb tots els games que hi ha guardats a persistència.
     */
    @Override
    public List<List<String>> agafarTotsGames(){
        return new ArrayList<>(this.games.values());
    }

    /**
     * @brief Funció que esborra de disc el Game especificat.
     * @param idGame Identificador del Game que volem esborrar definitivament
     * @post S'ha esborrat de disc el Game que s'ha especificat.
     */
    @Override
    public void borrarGame(String idGame) {
        //Borrem de memòria:
        games.remove(idGame);
        File fitxer_borrar  =  new File(PATH_FITXER+"/"+idGame+".txt");
        if(fitxer_borrar.exists()){
            if(fitxer_borrar.delete()){
                System.out.println(idGame+" Esborrat");
            }else{
                System.out.println(idGame+" NO s'ha pogut esborrar");
            }
        }
    }

    /**
     * @brief Funció que permet saber si un game està guardat a disc o no
     * @param idGame Identificador del Game que volem localitzar
     * @return Retorna true si el game està guardat a disc, false si no és així.
     */

    @Override
    public boolean teGame(String idGame){
        return games.containsKey(idGame);
    }

    /**
     * @breif Funció per carregar tots els games que estan escrits a disc a les estructures de memòria del Gestor
     * @post S'han carregat tots els games preexistents a memòria.
     */
    private void carregarDeDisc(){
        System.out.println("Estic llegint Games de disc");
        File directori = new File(PATH_FITXER);
        if(directori.exists() && directori.isDirectory()){
            File[] files = directori.listFiles();
            if(files!=null){
                for(int i = 0; i<files.length; i++){
                    File arxiu  = files[i];
                    if(arxiu.isFile() && arxiu.getName().endsWith(".txt")){
                        String idGame = arxiu.getName().replace(".txt", "");
                        List<String> elem_game = new ArrayList<>();
                        try(BufferedReader reader = new BufferedReader(new FileReader(arxiu))){
                            String line;
                            while((line = reader.readLine()) != null){
                                elem_game.add(line);
                            }
                            this.games.put(idGame,elem_game);
                            System.out.println("JOC CARREGAT: "+ idGame);
                        }catch (IOException e){
                            System.err.println("Error al llegir game: "+ idGame);
                        }
                    }
                }
            }
        }
    }

    /**
     * @Breif Funció que permet escriure un Game a disc
     * @param id Identificador del Game
     * @param dadesGame LLista amb tots els elements del Games
     * @post S'ha escrit a disc les dades del game en un nou fitxer
     */

    private void escriureADisc(String id, List<String> dadesGame){
        try(PrintWriter writer = new PrintWriter(new FileWriter(PATH_FITXER+"/"+id+".txt"))){
            for(String linia : dadesGame){
                writer.println(linia);
            }
        }catch(IOException e){
            System.err.println("Error al escriure game a Disc: "+ id);
        }
    }
}
