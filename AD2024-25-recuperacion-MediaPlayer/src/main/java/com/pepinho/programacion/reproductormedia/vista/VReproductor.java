/*
 * Autor: Pepe Calo
 * Realizado con fines educativos.
 * Puede modificarlo siempre que no lo haga con fines comerciales.
 */
package com.pepinho.programacion.reproductormedia.vista;

import com.pepinho.programacion.reproductormedia.model.IPlayList;
import com.pepinho.programacion.reproductormedia.model.PlayListObserver;
import java.awt.BorderLayout;
//import java.awt.Dimension;

import java.awt.HeadlessException;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
//import javax.swing.JPanel;
//import javax.swing.JSlider;
//import javax.swing.JSpinner;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;
//import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import com.pepinho.programacion.reproductormedia.control.ControlLista;

/**
 *
 * @author pepecalo
 */
public class VReproductor extends JFrame implements ActionListener,
        ChangeListener, WindowListener, PlayListObserver {
    
    /**
     * Título por defecto. "Reproductor Multimedia".
     */
    
    public static final String TITULO = "Reproductor Multimedia";

    /**
     * Iconos de la aplicación, de tipo ImageIcon. Para acceder a ellos se hace
     * por clave: ICONOS.get("clave"). Claves: "add", "play", "anterior",
     * "stop", "siguiente", "lista", "aleatorio", "pause". Se almacenan en un
     * objeto de tipo LinkedHashMap. Se podría iniciar de muchos módos, el más
     * sencillo sería por medio de un método estático. Por medio de dobles
     * llaves no es lo más recomendado pero lo más sencillo.
     *
     */
    public static final Map<String, ImageIcon> ICONOS
            = new LinkedHashMap<String, ImageIcon>() {
        {
            put("add", new ImageIcon(
                    this.getClass().getResource("/iconos/32/add.png")));
            put("play", new ImageIcon(
                    this.getClass().getResource("/iconos/48/play.png")));
            put("anterior", new ImageIcon(
                    this.getClass().getResource("/iconos/32/anterior.png")));
            put("stop", new ImageIcon(
                    this.getClass().getResource("/iconos/32/stop.png")));
            put("siguiente", new ImageIcon(
                    this.getClass().getResource("/iconos/32/siguiente.png")));
            put("lista", new ImageIcon(
                    this.getClass().getResource("/iconos/32/lista.png")));
            put("aleatorio", new ImageIcon(
                    this.getClass().getResource("/iconos/32/aleatorio.png")));
            put("mute", new ImageIcon(
                    this.getClass().getResource("/iconos/32/mute.png")));
            put("pause", new ImageIcon(
                    this.getClass().getResource("/iconos/48/pause.png")));
            put("formulario", new ImageIcon(
                    this.getClass().getResource("/iconos/32/play.png")));
            put("volume", new ImageIcon(
                    this.getClass().getResource("/iconos/32/volume.png")));
        }
    };
    
    
    /**
     * 
     */
    private IPlayList lista; // modelo
    private ControlLista control; // controlador
    
    /**
     * Botones de la aplicación, de la barra de herramientas. Tienen el mismo
     * tamaño que el número de iconos-3, pues el icono de "play" y "pause"
     * aparecen en el mismo botón, y los otros son el del formulario y el
     * volumen.
     */
    private final JButton[] btControl = new JButton[ICONOS.size() - 3];

    // barra inferior de tiempo.
    private final BarraTempo barraTempo = new BarraTempo();

    // Elementos del menús sobre los que interesa realizar accioness
    private final JMenuItem mnuEngadir = new JMenuItem("Añadir");
    private final JMenuItem mnuSair = new JMenuItem("Salir");
    private final JMenuItem mnuAcerca = new JMenuItem("About..");

    // Barra de volumen (JSlider)
    private final BarraVolumen volume = new BarraVolumen();
    
    // Si está mute o no. Por defecto tiene sonido 
    private boolean mute = false;


    public VReproductor(ControlLista control, IPlayList lista)
            throws HeadlessException {
        this(TITULO, control, lista);
    }

    public VReproductor(String title, ControlLista control, IPlayList lista)
            throws HeadlessException {
        
        super(title);
        
        this.lista = lista; // modelo
        this.control = control; //control
        
        // Icono del fomulario
        setIconImage(ICONOS.get("formulario").getImage());
        
        crearGUI();
    }

    private void crearGUI() {
        // No deseamos que se pueda cambiar el tamaño.
        setResizable(false);
        
        // Creación de la barra de herramientas
        JToolBar jtbReproductor = new JToolBar();

        // Botones:
        int i = 0;
        for (Map.Entry<String, ImageIcon> icono : ICONOS.entrySet()) {
            btControl[i] = new JButton(icono.getValue());
            if (i == 1 || i == 2 || i == 5 || i == 7) {
                jtbReproductor.addSeparator();
            }
            btControl[i].setEnabled(i == 0);
            btControl[i].addActionListener(this);
            jtbReproductor.add(btControl[i]);
            i++;
            if (i >= btControl.length) {
                break;
            }
        }
        jtbReproductor.addSeparator();
        jtbReproductor.add(new JLabel(ICONOS.get("volume")));
        jtbReproductor.add(volume);
        volume.addChangeListener(this);
        volume.setEnabled(false);
        this.add(jtbReproductor, BorderLayout.CENTER);


        this.add(barraTempo, BorderLayout.PAGE_END);

        
        // Barra de menú
        JMenuBar jMenu = new JMenuBar();
        JMenu mnArquivo = new JMenu("Arquivo");
        mnArquivo.add(mnuEngadir);
        mnuEngadir.addActionListener(this);
        mnArquivo.addSeparator();
        mnArquivo.add(mnuSair);
        mnuSair.addActionListener(this);
        JMenu mnAxuda = new JMenu("Axuda");
        mnAxuda.add(mnuAcerca);
        mnuAcerca.addActionListener(this);
        // Bla, bla, bla..
        
        jMenu.add(mnArquivo);
        jMenu.add(mnAxuda);
        setJMenuBar(jMenu);
        
        // centro de la pantalla:
        setLocationRelativeTo(null);
        pack(); // menor espacio.
        setResizable(false);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE); // gestionamos por eventos
        addWindowListener(this);
        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btControl[1]
                && btControl[1].getIcon() == ICONOS.get("play")) {
            btControl[1].setIcon(ICONOS.get("pause"));
            control.play();
        } else if (e.getSource() == btControl[1]
                && btControl[1].getIcon() == ICONOS.get("pause")) {
            btControl[1].setIcon(ICONOS.get("play"));
            control.pause();
        } else if (e.getSource() == btControl[2]) {
            control.retroceder();
        } else if (e.getSource() == btControl[3]) {
            control.stop(true);
            btControl[1].setIcon(ICONOS.get("play"));
        } else if (e.getSource() == btControl[4]) {
            control.avanzar();
        } else if (e.getSource() == btControl[7]) {
            control.setMute(!mute);
            mute = !mute;
        } else if (e.getSource() == mnuSair) {
            if (sair()) {
                System.exit(0);
            }
        } else if (e.getSource() == mnuAcerca) {
            JOptionPane.showMessageDialog(this,
                    "Reproductor DAW Distancia\n versión 0.0.0.1",
                    "Reproductor Multimedia", JOptionPane.INFORMATION_MESSAGE);
        } else if (e.getSource() == btControl[0] || e.getSource() == mnuEngadir) {
            JFileChooser fc = new JFileChooser();
            int returnVal = fc.showOpenDialog(this);

            if (returnVal == JFileChooser.APPROVE_OPTION) {
                File file = fc.getSelectedFile();
                control.addCancion(file.toPath());
                setEnabledBotones(true);
                control.setVolume(volume.getVolume());
            } else {

            }
        }

    }

    /**
     * Habilita o deshabilita todos los botones menos el primero.
     *
     * @param habilitar si habilita o deshabilita los botones.
     */
    private void setEnabledBotones(boolean habilitar) {
        for (int i = 1; i < btControl.length; i++) {
            btControl[i].setEnabled(habilitar);
        }
        volume.setEnabled(habilitar);
    }

    /**
     * Pregunta si se queire salir de la aplicación.
     *
     * @return verdadero si se pulsa "sí",
     */
    private boolean sair() {
        return JOptionPane.showConfirmDialog(this, "¿Está seguro que desea "
                + "cerrar el reproductor?", "¿Cerrar el reproductor?",
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) 
                == JOptionPane.YES_OPTION;
    }

    /**
     * Si cambia el estado de volumen.
     * @param e 
     */
    @Override
    public void stateChanged(ChangeEvent e) {
        if(e.getSource()==volume){
            control.setVolume(volume.getVolume());
        }
    }

    @Override
    public void windowOpened(WindowEvent e) {

    }

    /**
     * Se llama al pulsar la X. Pregunta si salir.
     * @param e 
     */
    @Override
    public void windowClosing(WindowEvent e) {
        if (sair()) {
            System.exit(0);
        }
    }

    @Override
    public void windowClosed(WindowEvent e) {
        // Intentamos salir de la plataforma Java FX
        try {
            com.sun.javafx.application.PlatformImpl.exit();
        } catch (Exception ex) {
        }
    }

    @Override
    public void windowIconified(WindowEvent e) {

    }

    @Override
    public void windowDeiconified(WindowEvent e) {

    }

    @Override
    public void windowActivated(WindowEvent e) {

    }

    @Override
    public void windowDeactivated(WindowEvent e) {

    }

    @Override
    public void actualizarTiempo(int segundos) {
       SwingUtilities.invokeLater(() -> {
           barraTempo.situarBarra(segundos);
       });
    }

    @Override
    public void actualizarDuracion(int segundos) {
        barraTempo.setDuracion(segundos);
    }

}
