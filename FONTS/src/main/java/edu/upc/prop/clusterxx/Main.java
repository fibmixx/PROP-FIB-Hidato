package edu.upc.prop.clusterxx;
import edu.upc.prop.clusterxx.domain.controladors.CtrlDomini;
import edu.upc.prop.clusterxx.presentation.controladors.CtrlPresentacio;

import javax.swing.*;

/**
 * @file Main.java
 *
 * @brief Programa principal del projecte.
 */
public class Main {
    public static void main(String[] args) {

        //Inicialitza la finestra principal del programa
        SwingUtilities.invokeLater(() ->{
            CtrlPresentacio cp = CtrlPresentacio.getInstance();
            cp.carregarProba();
            cp.run();
        });
    }
}
