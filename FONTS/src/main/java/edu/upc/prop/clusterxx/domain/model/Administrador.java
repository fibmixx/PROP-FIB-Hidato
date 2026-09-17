package edu.upc.prop.clusterxx.domain.model;

/**
 * @file Administrador.java
 * 
 * @brief Definició i implementació de la subclasse Administrador
 * 
 * Serveix per a poder oferir accions especials sobre el sistema a nomès certs usuaris.
 */

public class Administrador extends Usuari {
    /**
     * @brief Creadora de la subclasse Administrador
     * 
     * @param username Nom de l'usuari que serà administrador
     * @param password Contrasenya de l'usuari que serà administrador
     *
     * @post S'ha creat un nou usuari de tipus Administrador.
     */
    public Administrador(String username, String password) {
        super(username, password);
    }

    /**
     * @brief Funció per veure si un usuari és Administrador
     * 
     * @return Retorna true.
     */
    @Override
    public boolean isAdmin() {
        return true;
    }
}