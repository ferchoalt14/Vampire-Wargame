/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Game;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.util.ArrayList;
import java.util.function.Consumer;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

/**
 *
 * @author User
 */
public class MainMenu extends JPanel {

    private final Image fondo;
    private final JLabel lblStatusMsg;

    // Paneles para vistas dentro del mismo menú
    private final JPanel mainButtonsPanel;
    private final JPanel selectOpponentPanel;
    private final JComboBox<String> cbOponentes;

    public MainMenu(GameSystem brain, Runnable onLogout, Runnable onOpenAccount, Runnable onOpenReports, Consumer<Player> onStartGame) {
        this.fondo = new ImageIcon(getClass().getResource("/Images/MainHall.png")).getImage();
        setLayout(new GridBagLayout());

        // PANEL PRINCIPAL DE OPCIONES
        mainButtonsPanel = new JPanel(new GridLayout(6, 1, 10, 10));
        mainButtonsPanel.setOpaque(false);
        mainButtonsPanel.setPreferredSize(new Dimension(280, 280));

        JLabel lblTitle = new JLabel("MENÚ PRINCIPAL", SwingConstants.CENTER);
        lblTitle.setForeground(new Color(230, 230, 230));
        lblTitle.setFont(new Font("Georgia", Font.BOLD, 18));

        lblStatusMsg = new JLabel(" ", SwingConstants.CENTER);
        lblStatusMsg.setFont(new Font("Georgia", Font.BOLD, 11));

        JButton btnJugar = LogInMenu.createButton("Jugar");
        JButton btnPerfil = LogInMenu.createButton("Mi cuenta");
        JButton btnRanking = LogInMenu.createButton("Reportes");
        JButton btnCerrarSesion = LogInMenu.createButton("Cerrar Sesión");

        mainButtonsPanel.add(lblTitle);
        mainButtonsPanel.add(btnJugar);
        mainButtonsPanel.add(btnPerfil);
        mainButtonsPanel.add(btnRanking);
        mainButtonsPanel.add(btnCerrarSesion);
        mainButtonsPanel.add(lblStatusMsg);

        // PANEL DE SELECCIÓN DE OPONENTE
        selectOpponentPanel = new JPanel(new GridLayout(5, 1, 10, 10));
        selectOpponentPanel.setOpaque(false);
        selectOpponentPanel.setVisible(false);
        selectOpponentPanel.setPreferredSize(new Dimension(280, 250));

        JLabel lblSelectTitle = new JLabel("SELECCIONAR OPONENTE", SwingConstants.CENTER);
        lblSelectTitle.setForeground(new Color(230, 230, 230));
        lblSelectTitle.setFont(new Font("Georgia", Font.BOLD, 16));

        cbOponentes = new JComboBox<>();
        cbOponentes.setBackground(new Color(30, 30, 30));
        cbOponentes.setForeground(new Color(230, 230, 230));

        JButton btnIniciarPartida = LogInMenu.createButton("Comenzar Batalla");
        JButton btnCancelarSeleccion = LogInMenu.createButton("Volver");

        selectOpponentPanel.add(lblSelectTitle);
        selectOpponentPanel.add(cbOponentes);
        selectOpponentPanel.add(btnIniciarPartida);
        selectOpponentPanel.add(btnCancelarSeleccion);

        btnJugar.addActionListener(e -> {
            ArrayList<Player> oponentes = brain.getOponentesDisponibles();
            if (oponentes.isEmpty()) {
                lblStatusMsg.setForeground(new Color(255, 100, 100));
                lblStatusMsg.setText("<html><center>Se requiere al menos otro<br>jugador registrado.</center></html>");
            } else {
                lblStatusMsg.setText(" ");
                cbOponentes.removeAllItems();
                for (Player p : oponentes) {
                    cbOponentes.addItem(p.getUser());
                }
                mainButtonsPanel.setVisible(false);
                selectOpponentPanel.setVisible(true);
            }
        });

        btnIniciarPartida.addActionListener(e -> {
            String oponenteSeleccionado = (String) cbOponentes.getSelectedItem();
            if (oponenteSeleccionado != null) {
                Player oponente = brain.buscarJugador(oponenteSeleccionado);
                if (oponente != null && onStartGame != null) {
                    resetStatus();
                    onStartGame.accept(oponente);
                }
            }
        });

        btnCancelarSeleccion.addActionListener(e -> {
            selectOpponentPanel.setVisible(false);
            mainButtonsPanel.setVisible(true);
        });

        // 11. Mi Cuenta
        btnPerfil.addActionListener(e -> {
            lblStatusMsg.setText(" ");
            if (onOpenAccount != null) {
                onOpenAccount.run();
            }
        });

        // 12. Reportes (Ranking)
        btnRanking.addActionListener(e -> {
            lblStatusMsg.setText(" ");
            if (onOpenReports != null) {
                onOpenReports.run();
            }
        });

        btnCerrarSesion.addActionListener(e -> {
            lblStatusMsg.setText(" ");
            brain.cerrarSesion();
            if (onLogout != null) {
                onLogout.run();
            }
        });

        add(mainButtonsPanel);
        add(selectOpponentPanel);
    }

    public void resetStatus() {
        lblStatusMsg.setText(" ");
        selectOpponentPanel.setVisible(false);
        mainButtonsPanel.setVisible(true);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (fondo != null) {
            g.drawImage(fondo, 0, 0, getWidth(), getHeight(), this);
        }
    }
}