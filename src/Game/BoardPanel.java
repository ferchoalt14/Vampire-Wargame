/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Game;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Image;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

public class BoardPanel extends JPanel {

    private Player jugadorBlanco; // Quien tiene la sesión iniciada
    private Player jugadorNegro;  // El oponente seleccionado
    private final JButton[][] gridButtons = new JButton[6][6];
    private final Piece[][] board = new Piece[6][6];
    private final GameSystem brain;
    private final Runnable onGameEnd;

    // Componentes de interfaz
    private final JLabel lblTurno;
    private final JLabel lblRuletaResultado;
    private final JButton btnGirarRuleta;

    private final Image fondo;
    private final JPanel confirmOverlay;
    private final JLabel lblConfirmMsg;

    public BoardPanel(GameSystem brain, Runnable onGameEnd) {
        this.brain = brain;
        this.onGameEnd = onGameEnd;

        this.fondo = new ImageIcon(getClass().getResource("/Images/Tablero.png")).getImage();

        setLayout(new BorderLayout());

        // JLAYEREDPANE PARA OVERLAY FLOTANTE
        JLayeredPane layeredPane = new JLayeredPane();
        layeredPane.setLayout(null);

        // PANEL CONTENEDOR PRINCIPAL
        JPanel mainContent = new JPanel(new BorderLayout(10, 10));
        mainContent.setOpaque(false);

        // 1. BARRA SUPERIOR
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 0, 20));

        JButton btnRetirarse = LogInMenu.createButton("Retirarse");
        btnRetirarse.setPreferredSize(new Dimension(120, 32));
        btnRetirarse.addActionListener(e -> mostrarConfirmacionRetiro());

        topPanel.add(btnRetirarse, BorderLayout.EAST);
        mainContent.add(topPanel, BorderLayout.NORTH);

        // 2. PANEL CENTRADO PARA EL TABLERO (AMPLIADO A 660x660)
        JPanel centerWrapper = new JPanel(new GridBagLayout());
        centerWrapper.setOpaque(false);

        JPanel boardGrid = new JPanel(new GridLayout(6, 6, 3, 3)) {
            @Override
            protected void paintComponent(Graphics g) {
                // Previene borrado del fondo
            }
        };
        boardGrid.setOpaque(false);
        // Se aumenta a 660x660 para un tamaño más grande y legible de casilla
        boardGrid.setPreferredSize(new Dimension(660, 660));

        for (int r = 0; r < 6; r++) {
            for (int c = 0; c < 6; c++) {
                JButton btn = new JButton();
                btn.setFocusable(false);
                btn.setFont(new Font("Georgia", Font.BOLD, 12));
                btn.setContentAreaFilled(false);
                btn.setOpaque(false);

                Color bgOscuro = ((r + c) % 2 == 0) ? new Color(45, 25, 30, 190) : new Color(20, 15, 20, 190);
                btn.setBackground(bgOscuro);

                btn.setUI(new javax.swing.plaf.basic.BasicButtonUI() {
                    @Override
                    public void update(Graphics g, javax.swing.JComponent c) {
                        if (c.isOpaque()) {
                            super.update(g, c);
                        } else {
                            g.setColor(c.getBackground());
                            g.fillRect(0, 0, c.getWidth(), c.getHeight());
                            paint(g, c);
                        }
                    }
                });

                gridButtons[r][c] = btn;
                boardGrid.add(btn);
            }
        }
        centerWrapper.add(boardGrid);
        mainContent.add(centerWrapper, BorderLayout.CENTER);

        // 3. PANEL LATERAL DERECHO (RULETA Y BOTONES)
        JPanel sidePanel = new JPanel(new GridBagLayout());
        sidePanel.setOpaque(false);
        sidePanel.setPreferredSize(new Dimension(320, 660));
        sidePanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 20));

        lblTurno = new JLabel("Turno: ---", SwingConstants.CENTER);
        lblTurno.setForeground(new Color(230, 230, 230));
        lblTurno.setFont(new Font("Georgia", Font.BOLD, 16));

        JLabel lblRuletaTitulo = new JLabel("RULETA DE ACCIÓN", SwingConstants.CENTER);
        lblRuletaTitulo.setForeground(new Color(210, 170, 110));
        lblRuletaTitulo.setFont(new Font("Georgia", Font.BOLD, 15));

        lblRuletaResultado = new JLabel("Esperando giro...", SwingConstants.CENTER);
        lblRuletaResultado.setForeground(Color.WHITE);
        lblRuletaResultado.setFont(new Font("Georgia", Font.ITALIC, 14));

        btnGirarRuleta = LogInMenu.createButton("Girar Ruleta");
        btnGirarRuleta.setPreferredSize(new Dimension(170, 42));
        btnGirarRuleta.addActionListener(e -> girarRuleta());

        JPanel ruletaBox = new JPanel(new GridLayout(4, 1, 10, 10));
        ruletaBox.setBackground(new Color(20, 15, 20, 220));
        ruletaBox.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(100, 40, 50), 2),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));

        ruletaBox.add(lblTurno);
        ruletaBox.add(lblRuletaTitulo);
        ruletaBox.add(lblRuletaResultado);
        ruletaBox.add(btnGirarRuleta);

        sidePanel.add(ruletaBox);
        mainContent.add(sidePanel, BorderLayout.EAST);

        // 4. OVERLAY FLOTANTE DE CONFIRMACIÓN
        confirmOverlay = new JPanel(new GridLayout(2, 1, 10, 10));
        confirmOverlay.setBackground(new Color(25, 10, 15, 245));
        confirmOverlay.setBorder(BorderFactory.createLineBorder(new Color(140, 40, 50), 2));
        confirmOverlay.setSize(380, 120);

        lblConfirmMsg = new JLabel("¿Deseas retirarte de la partida?", SwingConstants.CENTER);
        lblConfirmMsg.setForeground(Color.WHITE);
        lblConfirmMsg.setFont(new Font("Georgia", Font.BOLD, 13));

        JPanel panelBotonesConfirm = new JPanel();
        panelBotonesConfirm.setOpaque(false);

        JButton btnSi = LogInMenu.createButton("Sí, retirarme");
        JButton btnNo = LogInMenu.createButton("Cancelar");
        btnSi.setPreferredSize(new Dimension(120, 32));
        btnNo.setPreferredSize(new Dimension(120, 32));

        btnSi.addActionListener(e -> ejecutarRetiro());
        btnNo.addActionListener(e -> ocultarConfirmacionRetiro());

        panelBotonesConfirm.add(btnSi);
        panelBotonesConfirm.add(btnNo);

        confirmOverlay.add(lblConfirmMsg);
        confirmOverlay.add(panelBotonesConfirm);
        confirmOverlay.setVisible(false);

        // AGREGAR CAPAS AL LAYEREDPANE
        mainContent.setBounds(0, 0, 1280, 720);
        layeredPane.add(mainContent, JLayeredPane.DEFAULT_LAYER);
        layeredPane.add(confirmOverlay, JLayeredPane.MODAL_LAYER);

        // REAJUSTAR COMPONENTES Y CENTRAR OVERLAY
        addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentResized(java.awt.event.ComponentEvent e) {
                int w = getWidth();
                int h = getHeight();
                mainContent.setBounds(0, 0, w, h);
                confirmOverlay.setLocation((w - confirmOverlay.getWidth()) / 2, (h - confirmOverlay.getHeight()) / 2);
                revalidate();
            }
        });

        add(layeredPane, BorderLayout.CENTER);
    }

    public void iniciarNuevaPartida(Player oponente) {
        this.jugadorBlanco = brain.getJugadorActivo();
        this.jugadorNegro = oponente;

        ocultarConfirmacionRetiro();

        if (this.jugadorBlanco != null) {
            lblTurno.setText("Turno: " + this.jugadorBlanco.getUser());
        }

        for (int r = 0; r < 6; r++) {
            for (int c = 0; c < 6; c++) {
                board[r][c] = null;
            }
        }

        board[0][0] = new Werewolf("NEGRO");
        board[0][1] = new Vampire("NEGRO");
        board[0][2] = new Death("NEGRO");
        board[0][3] = new Death("NEGRO");
        board[0][4] = new Vampire("NEGRO");
        board[0][5] = new Werewolf("NEGRO");

        board[5][0] = new Werewolf("BLANCO");
        board[5][1] = new Vampire("BLANCO");
        board[5][2] = new Death("BLANCO");
        board[5][3] = new Death("BLANCO");
        board[5][4] = new Vampire("BLANCO");
        board[5][5] = new Werewolf("BLANCO");

        actualizarTableroVisual();
        revalidate();
        repaint();
    }

    private void girarRuleta() {
        String[] acciones = {"Mover 1 casilla", "Ataque especial", "Paso de turno", "Mover 2 casillas"};
        int index = (int) (Math.random() * acciones.length);
        lblRuletaResultado.setText("<html><center>Resultado:<br><b>" + acciones[index] + "</b></center></html>");
    }

    private void actualizarTableroVisual() {
        for (int r = 0; r < 6; r++) {
            for (int c = 0; c < 6; c++) {
                Piece p = board[r][c];
                JButton btn = gridButtons[r][c];

                if (p != null) {
                    if (p.getIcon() != null) {
                        btn.setIcon(p.getIcon());
                        btn.setText("");
                    } else {
                        btn.setIcon(null);
                        btn.setText("<html><center>" + p.getTipo() + "<br><font size='2'>(" + p.getBando() + ")</font></center></html>");
                    }
                } else {
                    btn.setIcon(null);
                    btn.setText("");
                }
            }
        }
    }

    private void mostrarConfirmacionRetiro() {
        lblConfirmMsg.setText("¿Deseas retirarte de la partida?");
        confirmOverlay.setLocation((getWidth() - confirmOverlay.getWidth()) / 2, (getHeight() - confirmOverlay.getHeight()) / 2);
        confirmOverlay.setVisible(true);
        repaint();
    }

    private void ocultarConfirmacionRetiro() {
        confirmOverlay.setVisible(false);
        repaint();
    }

    private void ejecutarRetiro() {
        Player activoActual = brain.getJugadorActivo();

        Player ganador = null;

        if (activoActual != null && activoActual.equals(jugadorBlanco)) {
            ganador = jugadorNegro;
        } else {
            ganador = jugadorBlanco;
        }

        if (ganador != null) {
            ganador.setPuntos(ganador.getPuntos() + 3);
        }

        if (onGameEnd != null) {
            onGameEnd.run();
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (fondo != null) {
            g.drawImage(fondo, 0, 0, getWidth(), getHeight(), this);
        }
    }
}
