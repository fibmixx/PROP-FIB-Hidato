package edu.upc.prop.clusterxx.domain.interficies;

import edu.upc.prop.clusterxx.domain.model.Game;

import java.util.List;

/**
 * @file IGestorGames.java
 *
 * @brief Interficie per guardar la informació dels Games a disc
 *
 * Permet guardar tota la informació d'una instància Game a disc.
 */

public interface IGestorGames {
    /**
     * @brief Funció per desar a disc una Partida en concret
     * @param game
     * @post S'ha guardat la partida a Disc en un format recuperable
     */
    void guardarGame(Game game);

    /**
     * @breif Funció que s'encarrega de llegir les dades d'un Game i retornar-les en format de llista.
     * @param idGame Identificador del Game que volem carregar
     * @return Retorna les dades que corresponen al game indicat
     */
    List<String> agafarDadesGame(String idGame); //Ha de ser CtrlGame el que acabi creant el nou Game, ja que necessitem els objectes Hidato i timer


    /**
     * @breif Funció que permet recuperar tots els IDs de Games que li corresponen a un usuari en concret
     * @param nomUser Identificador de l'usuari el qual volem recuperar tots els seus games
     * @return retorna tots els Ids dels games de l'usuari recuperats de disc.
     */
    List<String> agafarPartidesUser(String nomUser);

    /**
     * @breif Funció per esborrar un game de disc
     * @param idGame Identificador del Game que volem esborrar definitivament
     * @post S?ha esborrat el game amb identificador idGame de la persistència
     */

    void borrarGame(String idGame);

    /**
     * @brief Funció que permet saber si existeix una partida guardada amb l'identificador proporcionat
     * @param idGame Identificador del Game que volem localitzar
     * @return Retorna ture si existeix un game amb aquest id o bé false de no ser així.
     */
    boolean teGame(String idGame);

    List<List<String>> agafarTotsGames();
}

