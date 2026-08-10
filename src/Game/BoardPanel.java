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
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

public class BoardPanel extends JPanel implements RuletaPanel.OnRuletaFinishedListener {

    private Player jugadorBlanco; 
    private Player jugadorNegro;  
    private Player jugadorActual; 
    
    private final JButton[][] gridButtons = new JButton[6][6];
    private final Piece[][] board = new Piece[6][6];
    private final GameSystem brain;
    private final Runnable onGameEnd;

    private final RuletaPanel ruletaPanel;

    private Image fondo;
    private final JPanel confirmOverlay;
    private final JLabel lblConfirmMsg;

    private String tipoPiezaPermitida = null; 
    
    private int origenFilaSeleccionada = -1;
    private int origenColSeleccionada = -1;

    public BoardPanel(GameSystem brain, Runnable onGameEnd) {
        this.brain = brain;
        this.onGameEnd = onGameEnd;

        try {
            java.net.URL fondoURL = getClass().getResource("/Images/Tablero.png");
            if (fondoURL != null) {
                this.fondo = new javax.swing.ImageIcon(fondoURL).getImage();
            }
        } catch (Exception e) {
            this.fondo = null;
        }

        setLayout(new BorderLayout());

        JLayeredPane layeredPane = new JLayeredPane();
        layeredPane.setLayout(null);

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

        // 2. PANEL CENTRADO Y FIJO PARA EL TABLERO
        JPanel centerWrapper = new JPanel(new GridBagLayout());
        centerWrapper.setOpaque(false);

        JPanel boardGrid = new JPanel(new GridLayout(6, 6, 3, 3)) {
            @Override
            protected void paintComponent(Graphics g) {
                // Previene parpadeos
            }
        };
        boardGrid.setOpaque(false);
        
        // FIJAR TAMAÑO RIGIDO PARA EVITAR QUE EL TABLERO SE DEFORME O DEFORME SUS CASILLAS
        Dimension boardSize = new Dimension(600, 600);
        boardGrid.setPreferredSize(boardSize);
        boardGrid.setMinimumSize(boardSize);
        boardGrid.setMaximumSize(boardSize);

        for (int r = 0; r < 6; r++) {
            for (int c = 0; c < 6; c++) {
                JButton btn = new JButton();
                btn.setFocusable(false);
                btn.setFont(new Font("Georgia", Font.BOLD, 11));
                btn.setContentAreaFilled(false);
                btn.setOpaque(false);
                
                // Fijar tamaño exacto a cada casilla
                Dimension btnSize = new Dimension(95, 95);
                btn.setPreferredSize(btnSize);
                btn.setMinimumSize(btnSize);
                btn.setMaximumSize(btnSize);

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

                final int fila = r;
                final int columna = c;
                btn.addActionListener(e -> manejarClicCasilla(fila, columna));

                gridButtons[r][c] = btn;
                boardGrid.add(btn);
            }
        }
        centerWrapper.add(boardGrid);
        mainContent.add(centerWrapper, BorderLayout.CENTER);

        // 3. PANEL LATERAL DERECHO
        JPanel sidePanel = new JPanel(new GridBagLayout());
        sidePanel.setOpaque(false);
        sidePanel.setPreferredSize(new Dimension(320, 660));
        sidePanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 20));

        ruletaPanel = new RuletaPanel();
        ruletaPanel.setListener(this);
        sidePanel.add(ruletaPanel);

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

        mainContent.setBounds(0, 0, 1280, 720);
        layeredPane.add(mainContent, JLayeredPane.DEFAULT_LAYER);
        layeredPane.add(confirmOverlay, JLayeredPane.MODAL_LAYER);

        addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentResized(java.awt.event.ComponentEvent e) {
                try {
                    int w = getWidth();
                    int h = getHeight();
                    mainContent.setBounds(0, 0, w, h);
                    confirmOverlay.setLocation((w - confirmOverlay.getWidth()) / 2, (h - confirmOverlay.getHeight()) / 2);
                    revalidate();
                } catch (Exception ex) {
                    // Control defensivo
                }
            }
        });

        add(layeredPane, BorderLayout.CENTER);
    }

    public void iniciarNuevaPartida(Player oponente) {
        try {
            this.jugadorBlanco = brain.getJugadorActivo();
            this.jugadorNegro = oponente;
            this.jugadorActual = jugadorBlanco;

            ocultarConfirmacionRetiro();

            for (int r = 0; r < 6; r++) {
                for (int c = 0; c < 6; c++) {
                    board[r][c] = null;
                }
            }

            // Piezas Negras (Línea 0)
            board[0][0] = new Werewolf("NEGRO");
            board[0][1] = new Vampire("NEGRO");
            board[0][2] = new Death("NEGRO");
            board[0][3] = new Death("NEGRO");
            board[0][4] = new Vampire("NEGRO");
            board[0][5] = new Werewolf("NEGRO");

            // Piezas Blancas (Línea 5)
            board[5][0] = new Werewolf("BLANCO");
            board[5][1] = new Vampire("BLANCO");
            board[5][2] = new Death("BLANCO");
            board[5][3] = new Death("BLANCO");
            board[5][4] = new Vampire("BLANCO");
            board[5][5] = new Werewolf("BLANCO");

            actualizarTableroVisual();
            prepararTurnoRuleta();

            revalidate();
            repaint();
        } catch (Exception e) {
            ruletaPanel.mostrarMensajeEstado("Error al iniciar la partida.", Color.RED);
        }
    }

    private void prepararTurnoRuleta() {
        try {
            limpiarSeleccion();
            int piezasPerdidas = contarPiezasPerdidas(bandoActual());
            // Pasamos bandoActual() para que la ruleta muestre los iconos en color correcto
            ruletaPanel.prepararNuevoTurno(jugadorActual.getUser(), bandoActual(), piezasPerdidas, obtenerTiposPiezasDisponibles(bandoActual()));
            tipoPiezaPermitida = null;
        } catch (Exception e) {
            System.err.println("Error al preparar turno: " + e.getMessage());
        }
    }

    private String bandoActual() {
        return (jugadorActual != null && jugadorActual.equals(jugadorBlanco)) ? "BLANCO" : "NEGRO";
    }

    private int contarPiezasPerdidas(String bando) {
        int cont = 0;
        try {
            for (int r = 0; r < 6; r++) {
                for (int c = 0; c < 6; c++) {
                    Piece p = board[r][c];
                    if (p != null && p.getBando().equalsIgnoreCase(bando) && !(p instanceof Zombie)) {
                        cont++;
                    }
                }
            }
        } catch (Exception e) {
            return 0;
        }
        return 6 - cont;
    }

    private List<String> obtenerTiposPiezasDisponibles(String bando) {
        List<String> tipos = new ArrayList<>();
        try {
            for (int r = 0; r < 6; r++) {
                for (int c = 0; c < 6; c++) {
                    Piece p = board[r][c];
                    if (p != null && p.getBando().equalsIgnoreCase(bando)) {
                        if (!tipos.contains(p.getTipo())) {
                            tipos.add(p.getTipo());
                        }
                    }
                }
            }
        } catch (Exception e) {
            // Captura defensiva
        }
        return tipos;
    }

    @Override
    public void onResultadoObtenido(String tipoPieza) {
        try {
            List<String> disponibles = obtenerTiposPiezasDisponibles(bandoActual());
            if (disponibles.contains(tipoPieza)) {
                this.tipoPiezaPermitida = tipoPieza;
                ruletaPanel.deshabilitarBotonGiro();
                ruletaPanel.mostrarMensajeEstado("¡Obtuviste " + tipoPieza + "! Selecciona tu ficha en el tablero.", new Color(100, 220, 120));
            } else {
                ruletaPanel.mostrarMensajeEstado("No tienes ningun " + tipoPieza + " activo. Vuelve a girar.", new Color(230, 100, 100));
            }
        } catch (Exception e) {
            System.err.println("Error en callback de resultado: " + e.getMessage());
        }
    }

    @Override
    public void onSinGirosDisponibles() {
        try {
            if (tipoPiezaPermitida == null) {
                ruletaPanel.mostrarMensajeEstado("Sin giros disponibles. Se cambia de turno.", new Color(230, 100, 100));
                cambiarTurno();
            }
        } catch (Exception e) {
            System.err.println("Error al procesar fin de giros: " + e.getMessage());
        }
    }

    private void manejarClicCasilla(int fila, int col) {
        try {
            if (tipoPiezaPermitida == null) {
                ruletaPanel.mostrarMensajeEstado("Primero debes girar la ruleta para obtener una pieza.", new Color(230, 100, 100));
                return;
            }

            // SELECCIÓN DE PIEZA (Primer Clic)
            if (origenFilaSeleccionada == -1 && origenColSeleccionada == -1) {
                Piece p = board[fila][col];

                if (p == null) {
                    ruletaPanel.mostrarMensajeEstado("Casilla vacía. Selecciona una de tus piezas.", Color.YELLOW);
                    return;
                }

                if (!p.getBando().equalsIgnoreCase(bandoActual())) {
                    ruletaPanel.mostrarMensajeEstado("Esa pieza le pertenece a tu oponente.", new Color(230, 100, 100));
                    return;
                }

                if (!p.getTipo().equalsIgnoreCase(tipoPiezaPermitida)) {
                    ruletaPanel.mostrarMensajeEstado("Debes mover una pieza de tipo: " + tipoPiezaPermitida, Color.YELLOW);
                    return;
                }

                origenFilaSeleccionada = fila;
                origenColSeleccionada = col;
                resaltarCasillaSeleccionada(fila, col);
                ruletaPanel.mostrarMensajeEstado("Ficha seleccionada. Haz clic en la casilla de destino.", Color.WHITE);
                return;
            }

            // CANCELACIÓN DE SELECCIÓN
            if (origenFilaSeleccionada == fila && origenColSeleccionada == col) {
                limpiarSeleccion();
                ruletaPanel.mostrarMensajeEstado("Selección cancelada. Vuelve a elegir tu ficha.", Color.WHITE);
                return;
            }

            // MOVIMIENTO DE PIEZA (Segundo Clic)
            Piece piezaAMover = board[origenFilaSeleccionada][origenColSeleccionada];
            Piece piezaDestino = board[fila][col];

            if (piezaDestino != null) {
                ruletaPanel.mostrarMensajeEstado("Selecciona otra casilla.", new Color(230, 100, 100));
                return;
            }

            if (!piezaAMover.esMovimientoValido(origenFilaSeleccionada, origenColSeleccionada, fila, col)) {
                ruletaPanel.mostrarMensajeEstado("Movimiento no permitido para " + piezaAMover.getTipo() + ". Selecciona otra casilla.", new Color(230, 100, 100));
                return;
            }

            // EJECUTAR MOVIMIENTO
            board[fila][col] = piezaAMover;
            board[origenFilaSeleccionada][origenColSeleccionada] = null;

            actualizarTableroVisual();
            ruletaPanel.mostrarMensajeEstado("Movimiento realizado", new Color(100, 220, 120));

            // Cambiar turno tras mover la pieza exitosamente
            cambiarTurno();

        } catch (Exception e) {
            System.err.println("Error en movimiento de casillas: " + e.getMessage());
        }
    }

    private void resaltarCasillaSeleccionada(int fila, int col) {
        actualizarTableroVisual();
        gridButtons[fila][col].setBorder(BorderFactory.createLineBorder(Color.YELLOW, 3));
    }

    private void limpiarSeleccion() {
        origenFilaSeleccionada = -1;
        origenColSeleccionada = -1;
        actualizarTableroVisual();
    }

    private void cambiarTurno() {
        try {
            jugadorActual = (jugadorActual.equals(jugadorBlanco)) ? jugadorNegro : jugadorBlanco;
            prepararTurnoRuleta();
        } catch (Exception e) {
            System.err.println("Error al cambiar turno: " + e.getMessage());
        }
    }

    private void actualizarTableroVisual() {
        try {
            for (int r = 0; r < 6; r++) {
                for (int c = 0; c < 6; c++) {
                    Piece p = board[r][c];
                    JButton btn = gridButtons[r][c];
                    
                    btn.setBorder(BorderFactory.createLineBorder(new Color(60, 30, 35), 1));

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
        } catch (Exception e) {
            System.err.println("Error al actualizar tablero visual: " + e.getMessage());
        }
    }

    private void mostrarConfirmacionRetiro() {
        try {
            lblConfirmMsg.setText("¿Deseas retirarte de la partida?");
            confirmOverlay.setLocation((getWidth() - confirmOverlay.getWidth()) / 2, (getHeight() - confirmOverlay.getHeight()) / 2);
            confirmOverlay.setVisible(true);
            repaint();
        } catch (Exception e) {
            // Control defensivo
        }
    }

    private void ocultarConfirmacionRetiro() {
        try {
            confirmOverlay.setVisible(false);
            repaint();
        } catch (Exception e) {
            // Control defensivo
        }
    }

    private void ejecutarRetiro() {
        try {
            Player ganador = (jugadorActual != null && jugadorActual.equals(jugadorBlanco)) ? jugadorNegro : jugadorBlanco;

            if (ganador != null) {
                ganador.setPuntos(ganador.getPuntos() + 3);
            }

            if (onGameEnd != null) {
                onGameEnd.run();
            }
        } catch (Exception e) {
            System.err.println("Error al ejecutar retiro: " + e.getMessage());
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        try {
            if (fondo != null) {
                g.drawImage(fondo, 0, 0, getWidth(), getHeight(), this);
            }
        } catch (Exception e) {
            // Previene fallos si la imagen de fondo no se ubica
        }
    }
}