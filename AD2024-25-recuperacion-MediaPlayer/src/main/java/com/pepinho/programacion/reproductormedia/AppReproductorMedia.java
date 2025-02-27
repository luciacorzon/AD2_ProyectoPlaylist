package com.pepinho.programacion.reproductormedia;

import com.pepinho.programacion.reproductormedia.model.PlayList;
import com.pepinho.programacion.reproductormedia.model.IPlayList;
import com.pepinho.programacion.reproductormedia.control.ControlLista;
import com.pepinho.programacion.reproductormedia.control.PlayerController;
import com.pepinho.programacion.reproductormedia.vista.VReproductor;

import javax.swing.JDialog;
import javax.swing.JFrame;

/**
 *
 * @author pepecalo
 */
public class AppReproductorMedia {

    public static void main(String[] args) {
        
        // Se inicia la plataforma de Java FX
        com.sun.javafx.application.PlatformImpl.startup(() -> {
        });
        
        /* Para el Look & Fell Nimbus.
        Nimbus fue introducido en la versión JavaSE 6. Si no está disponible
        muestra la apariencia por defecto. Más información:
        http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
        */
        try { // Comprobamos si está disponible:
            for (javax.swing.UIManager.LookAndFeelInfo info : 
                    javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            System.out.println("Non está disponible Nimbus L&F." + 
                    ex.getMessage());
        } catch (InstantiationException ex) {
            System.out.println("Error al iniciar el L&F Nimbus." + 
                    ex.getMessage());
        } catch (IllegalAccessException ex) {
            System.out.println("Acceso no permitido para Nimbus L&F." + 
                    ex.getMessage());
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            System.out.println("Nimbus L&F no está soportado" + 
                    ex.getMessage());
        }       
        

        /* Creamos el formulario */
        java.awt.EventQueue.invokeLater(() -> {
            JFrame.setDefaultLookAndFeelDecorated(true);
            JDialog.setDefaultLookAndFeelDecorated(true);

            //Modelo
            IPlayList lista = new PlayList();

            // Controlador
            ControlLista control = new PlayerController(lista);

            // Vista:
            VReproductor reproductor = new VReproductor(control, lista);

            // Añadimos la vista a la lista de observadores del disco.
            lista.addViewObserver(reproductor);
        });
        
//        com.sun.javafx.application.PlatformImpl.exit();
    }
    
}
