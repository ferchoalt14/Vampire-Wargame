 
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
import javax.swing.Timer;
import javax.swing.ToolTipManager;

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

    private boolean modoAtaqueActivo = false;
    private boolean modoInvocacionActivo = false;
    private boolean modoChuparSangreActivo = false;


    private static final int RETARDO_MENSAJE_ACCION_MS = 1600;

    public BoardPanel(GameSystem brain, Runnable onGameEnd) {
        this.brain = brain;
        this.onGameEnd = onGameEnd;

        // Configuración global de ToolTips
        ToolTipManager.sharedInstance().setInitialDelay(100);
        ToolTipManager.sharedInstance().setDismissDelay(8000);

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

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 0, 20));

        JButton btnRetirarse = LogInMenu.createButton("Retirarse");
        btnRetirarse.setPreferredSize(new Dimension(120, 32));
        btnRetirarse.addActionListener(e -> mostrarConfirmacionRetiro());

        topPanel.add(btnRetirarse, BorderLayout.EAST);
        mainContent.add(topPanel, BorderLayout.NORTH);

        JPanel centerWrapper = new JPanel(new GridBagLayout());
        centerWrapper.setOpaque(false);

        JPanel boardGrid = new JPanel(new GridLayout(6, 6, 3, 3)) {
            @Override
            protected void paintComponent(Graphics g) {}
        };
        boardGrid.setOpaque(false);
        
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

        JPanel sidePanel = new JPanel(new GridBagLayout());
        sidePanel.setOpaque(false);
        sidePanel.setPreferredSize(new Dimension(320, 680));
        sidePanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 20));

        ruletaPanel = new RuletaPanel();
        ruletaPanel.setListener(this);
        sidePanel.add(ruletaPanel);

        mainContent.add(sidePanel, BorderLayout.EAST);

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
                } catch (Exception ex) {}
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

            // Piezas Negras (Fila 0)
            board[0][0] = new Werewolf("NEGRO");
            board[0][1] = new Vampire("NEGRO");
            board[0][2] = new Death("NEGRO");
            board[0][3] = new Death("NEGRO");
            board[0][4] = new Vampire("NEGRO");
            board[0][5] = new Werewolf("NEGRO");

            // Piezas Blancas (Fila 5)
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

    private void verificarMuertesYLimpiarZombies(String bando) {
        boolean tieneMuerte = false;
        for (int r = 0; r < 6; r++) {
            for (int c = 0; c < 6; c++) {
                Piece p = board[r][c];
                if (p != null && p.getBando().equalsIgnoreCase(bando) && p instanceof Death) {
                    tieneMuerte = true;
                    break;
                }
            }
            if (tieneMuerte) break;
        }

        if (!tieneMuerte) {
            for (int r = 0; r < 6; r++) {
                for (int c = 0; c < 6; c++) {
                    Piece p = board[r][c];
                    if (p != null && p.getBando().equalsIgnoreCase(bando) && p instanceof Zombie) {
                        board[r][c] = null;
                    }
                }
            }
        }
    }

    private void prepararTurnoRuleta() {
        try {
            verificarMuertesYLimpiarZombies("BLANCO");
            verificarMuertesYLimpiarZombies("NEGRO");
            actualizarTableroVisual();

            if (verificarCondicionVictoria()) {
                return;
            }

            limpiarSeleccion();
            int piezasPerdidas = contarPiezasPerdidas(bandoActual());
            ruletaPanel.prepararNuevoTurno(jugadorActual.getUser(), bandoActual(), piezasPerdidas, obtenerTiposPiezasDisponibles(bandoActual()));
            tipoPiezaPermitida = null;
        } catch (Exception e) {
            System.err.println("Error al preparar turno: " + e.getMessage());
        }
    }

   public boolean verificarCondicionVictoria() {
    String bandoJugadorActual = bandoActual();
    boolean tienePiezasCapaces = false;

    for (int r = 0; r < 6; r++) {
        for (int c = 0; c < 6; c++) {
            Piece p = board[r][c];
            if (p != null && p.getBando() != null && p.getBando().equalsIgnoreCase(bandoJugadorActual)) {
                if (!p.getTipo().equalsIgnoreCase("ZOMBIE")) {
                    tienePiezasCapaces = true;
                    break;
                }
            }
        }
        if (tienePiezasCapaces) break;
    }

    if (!tienePiezasCapaces) {
        Player ganador = jugadorActual.equals(jugadorBlanco) ? jugadorNegro : jugadorBlanco;
        Player perdedor = jugadorActual;
        finalizarPartidaPorVictoria(ganador, perdedor, "Eliminación total de tropas enemigas");
        return true;
    }
    return false;
}

    public void finalizarPartidaPorVictoria(Player ganador, Player perdedor, String causa) {
        if (brain != null) {
            brain.registrarResultadoPartida(ganador, perdedor, causa);
        }

        WinnerOverlay winOverlay = new WinnerOverlay(
            ganador != null ? ganador.getUser() : "Jugador",
            causa,
            () -> {
                if (onGameEnd != null) {
                    onGameEnd.run();
                }
            }
        );

        this.removeAll();
        this.setLayout(new BorderLayout());
        this.add(winOverlay, BorderLayout.CENTER);
        this.revalidate();
        this.repaint();
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
        } catch (Exception e) {}
        return tipos;
    }

    // --- MÉTODOS DE LA INTERFAZ ---

    @Override
    public void onResultadoObtenido(String tipoPieza) {
        try {
            List<String> disponibles = obtenerTiposPiezasDisponibles(bandoActual());
            if (disponibles.contains(tipoPieza)) {
                this.tipoPiezaPermitida = tipoPieza;
                ruletaPanel.deshabilitarBotonGiro();
                
                String opcionesTexto;
                switch (tipoPieza) {
                    case "Muerte":
                        opcionesTexto = "¡Obtuviste Muerte! Puedes mover la Muerte, mover a un Zombie, atacar o invocar un Zombie.";
                        break;
                    case "Vampiro":
                        opcionesTexto = "¡Obtuviste Vampiro! Puedes mover la pieza, atacar o chupar sangre.";
                        break;
                    case "Hombre Lobo":
                        opcionesTexto = "¡Obtuviste Hombre Lobo! Puedes mover la pieza o atacar.";
                        break;
                    default:
                        opcionesTexto = "¡Obtuviste " + tipoPieza + "! Selecciona tu ficha en el tablero.";
                        break;
                }
                
                ruletaPanel.mostrarMensajeEstado(opcionesTexto, new Color(100, 220, 120));
             } else {
                ruletaPanel.mostrarMensajeEstado("No tienes ningún " + tipoPieza + " activo. Vuelve a girar.", new Color(230, 100, 100));
                ruletaPanel.habilitarBotonGiroSiQuedanGiros();
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
                Timer timerRetardo = new Timer(1800, e -> cambiarTurno());
                timerRetardo.setRepeats(false);
                timerRetardo.start();
            }
        } catch (Exception e) {
            System.err.println("Error al procesar fin de giros: " + e.getMessage());
        }
    }

    @Override
    public void onSolicitarAtaque() {
        if (origenFilaSeleccionada == -1) return;
        modoAtaqueActivo = true;
        modoInvocacionActivo = false;
        modoChuparSangreActivo = false;
        resaltarObjetivosAtaque();
        ruletaPanel.mostrarMensajeEstado("¡MODO ATAQUE! Selecciona una pieza enemiga alcanzable.", Color.RED);
    }

    @Override
    public void onSolicitarInvocacion() {
        if (origenFilaSeleccionada == -1) return;
        modoInvocacionActivo = true;
        modoAtaqueActivo = false;
        modoChuparSangreActivo = false;
        resaltarCasillasInvocacion();
        ruletaPanel.mostrarMensajeEstado("¡INVOCACIÓN! Selecciona una casilla vacía adyacente para colocar al Zombie.", Color.CYAN);
    }

    @Override
    public void onSolicitarChuparSangre() {
        if (origenFilaSeleccionada == -1) return;
        modoChuparSangreActivo = true;
        modoAtaqueActivo = false;
        modoInvocacionActivo = false;
        resaltarObjetivosChuparSangre();
        ruletaPanel.mostrarMensajeEstado("¡CHUPAR SANGRE! Selecciona una pieza enemiga adyacente para drenar 1 punto de vida.", new Color(255, 105, 180));
    }

    // --- LÓGICA INTERNA DE JUEGO ---
//metodo recursivo 2
    private boolean validarAtaqueDistanciaRecursivo(int fOrigen, int cOrigen, int df, int dc, int paso, int maxPaso) {
        int fActual = fOrigen + (df * paso);
        int cActual = cOrigen + (dc * paso);

        if (fActual < 0 || fActual >= 6 || cActual < 0 || cActual >= 6) {
            return false;
        }

        if (paso == maxPaso) {
            Piece objetivo = board[fActual][cActual];
            return objetivo != null && !objetivo.getBando().equalsIgnoreCase(bandoActual());
        }

        if (board[fActual][cActual] != null) {
            return false;
        }

        return validarAtaqueDistanciaRecursivo(fOrigen, cOrigen, df, dc, paso + 1, maxPaso);
    }

    private void manejarClicCasilla(int fila, int col) {
    try {
        if (modoInvocacionActivo) {
            if (Math.abs(fila - origenFilaSeleccionada) <= 1 && Math.abs(col - origenColSeleccionada) <= 1) {
                if (board[fila][col] == null) {
                    board[fila][col] = new Zombie(bandoActual());
                    ruletaPanel.mostrarMensajeEstado("¡Zombie Invocado con éxito!", Color.GREEN);
                    actualizarTableroVisual();
                    programarCambioDeTurno();
                    return;
                }
            }
            modoInvocacionActivo = false;
            Piece pSeleccionada = board[origenFilaSeleccionada][origenColSeleccionada];
            resaltarMovimientosValidos(origenFilaSeleccionada, origenColSeleccionada, pSeleccionada);
            ruletaPanel.mostrarMensajeEstado("Invocación no realizada. Selecciona una opción válida.", Color.YELLOW);
            return;
        }

        if (modoChuparSangreActivo) {
            Piece atacante = board[origenFilaSeleccionada][origenColSeleccionada];
            Piece objetivo = board[fila][col];

            if (atacante instanceof Vampire && objetivo != null && !objetivo.getBando().equalsIgnoreCase(bandoActual())) {
                int distFila = Math.abs(fila - origenFilaSeleccionada);
                int distCol = Math.abs(col - origenColSeleccionada);

                if (distFila <= 1 && distCol <= 1) {
                    ((Vampire) atacante).chuparSangre(objetivo);
                    procesarResultadoAtaque(objetivo, fila, col, "¡Has chupado sangre!", Color.MAGENTA);
                    return;
                }
            }

            modoChuparSangreActivo = false;
            resaltarMovimientosValidos(origenFilaSeleccionada, origenColSeleccionada, atacante);
            ruletaPanel.mostrarMensajeEstado("Habilidad no realizada. Selecciona un enemigo adyacente.", Color.YELLOW);
            return;
        }

        if (modoAtaqueActivo) {
            Piece atacante = board[origenFilaSeleccionada][origenColSeleccionada];
            Piece objetivo = board[fila][col];

            if (objetivo != null && !objetivo.getBando().equalsIgnoreCase(bandoActual())) {
                int distFila = Math.abs(fila - origenFilaSeleccionada);
                int distCol = Math.abs(col - origenColSeleccionada);

                if (distFila <= 1 && distCol <= 1) {
                    objetivo.recibirDano(atacante.getAtaque(), false);
                    procesarResultadoAtaque(objetivo, fila, col, null, null);
                    return;
                } 
                else if (atacante instanceof Death) {
                    int df = Integer.compare(fila, origenFilaSeleccionada);
                    int dc = Integer.compare(col, origenColSeleccionada);

                    if (validarAtaqueDistanciaRecursivo(origenFilaSeleccionada, origenColSeleccionada, df, dc, 1, 2)) {
                        int danoAjustado = Math.max(1, atacante.getAtaque() / 2);
                        objetivo.recibirDano(danoAjustado, true); 
                        procesarResultadoAtaque(objetivo, fila, col, "¡Guadaña lanzada! " + danoAjustado + " de daño directo a la vida.", Color.ORANGE);
                        return;
                    }
                }
            }

            modoAtaqueActivo = false;
            resaltarMovimientosValidos(origenFilaSeleccionada, origenColSeleccionada, atacante);
            ruletaPanel.mostrarMensajeEstado("Ataque no válido. Selecciona un enemigo alcanzable.", Color.YELLOW);
            return;
        }

        if (tipoPiezaPermitida == null) {
            ruletaPanel.mostrarMensajeEstado("Primero debes girar la ruleta para obtener una pieza.", new Color(230, 100, 100));
            return;
        }

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

            boolean esSeleccionValida = p.getTipo().equalsIgnoreCase(tipoPiezaPermitida) 
                    || (tipoPiezaPermitida.equalsIgnoreCase("Muerte") && p instanceof Zombie);

            if (!esSeleccionValida) {
                ruletaPanel.mostrarMensajeEstado("Debes mover una pieza de tipo: " + tipoPiezaPermitida, Color.YELLOW);
                return;
            }

            origenFilaSeleccionada = fila;
            origenColSeleccionada = col;
            ruletaPanel.actualizarStatsPieza(p);
            resaltarMovimientosValidos(fila, col, p);
            ruletaPanel.mostrarMensajeEstado("Ficha seleccionada. Muévela o realiza una acción.", Color.WHITE);
            return;
        }

        if (origenFilaSeleccionada == fila && origenColSeleccionada == col) {
            limpiarSeleccion();
            ruletaPanel.mostrarMensajeEstado("Selección cancelada.", Color.WHITE);
            return;
        }

        Piece piezaAMover = board[origenFilaSeleccionada][origenColSeleccionada];
        if (board[fila][col] != null || !piezaAMover.esMovimientoValido(origenFilaSeleccionada, origenColSeleccionada, fila, col, board)) {
            ruletaPanel.mostrarMensajeEstado("Movimiento no permitido. Selecciona una casilla válida.", new Color(230, 100, 100));
            return;
        }

        board[fila][col] = piezaAMover;
        board[origenFilaSeleccionada][origenColSeleccionada] = null;

        actualizarTableroVisual();
        cambiarTurno();

    } catch (Exception e) {
        System.err.println("Error en casilla: " + e.getMessage());
    }
}

   
    private void procesarResultadoAtaque(Piece objetivo, int fila, int col, String mensajeAccion, Color colorAccion) {
        String mensajeFinal;
        Color colorFinal;

        if (!objetivo.estaViva()) {
            board[fila][col] = null;
            mensajeFinal = (mensajeAccion != null)
                    ? mensajeAccion + " ¡Y has destruido la pieza enemiga!"
                    : "¡Has destruido la pieza enemiga!";
            colorFinal = Color.GREEN;
        } else {
            mensajeFinal = (mensajeAccion != null)
                    ? mensajeAccion + " Vida restante del objetivo: " + objetivo.getVida()
                    : "Acción realizada. Vida restante del objetivo: " + objetivo.getVida();
            colorFinal = (mensajeAccion != null) ? colorAccion : Color.ORANGE;
        }

        ruletaPanel.mostrarMensajeEstado(mensajeFinal, colorFinal);
        actualizarTableroVisual();
        programarCambioDeTurno();
    }

    private void programarCambioDeTurno() {
        Timer timerRetardo = new Timer(RETARDO_MENSAJE_ACCION_MS, e -> cambiarTurno());
        timerRetardo.setRepeats(false);
        timerRetardo.start();
    }

    private void resaltarMovimientosValidos(int fOri, int cOri, Piece p) {
        actualizarTableroVisual();
        gridButtons[fOri][cOri].setBorder(BorderFactory.createLineBorder(Color.YELLOW, 3));

        for (int r = 0; r < 6; r++) {
            for (int c = 0; c < 6; c++) {
                if (board[r][c] == null && p.esMovimientoValido(fOri, cOri, r, c, board)) {
                    gridButtons[r][c].setBorder(BorderFactory.createLineBorder(new Color(50, 255, 100), 2));
                }
            }
        }
    }

    private void resaltarObjetivosAtaque() {
        actualizarTableroVisual();
        gridButtons[origenFilaSeleccionada][origenColSeleccionada].setBorder(BorderFactory.createLineBorder(Color.YELLOW, 3));

        Piece atacante = board[origenFilaSeleccionada][origenColSeleccionada];

        for (int r = 0; r < 6; r++) {
            for (int c = 0; c < 6; c++) {
                Piece p = board[r][c];
                if (p != null && !p.getBando().equalsIgnoreCase(bandoActual())) {
                    int dFila = Math.abs(r - origenFilaSeleccionada);
                    int dCol = Math.abs(c - origenColSeleccionada);

                    if (dFila <= 1 && dCol <= 1) {
                        gridButtons[r][c].setBorder(BorderFactory.createLineBorder(Color.RED, 3));
                    }
                    else if (atacante instanceof Death) {
                        int df = Integer.compare(r, origenFilaSeleccionada);
                        int dc = Integer.compare(c, origenColSeleccionada);

                        if ((dFila == 2 || dCol == 2) && dFila <= 2 && dCol <= 2) {
                            if (validarAtaqueDistanciaRecursivo(origenFilaSeleccionada, origenColSeleccionada, df, dc, 1, 2)) {
                                gridButtons[r][c].setBorder(BorderFactory.createLineBorder(new Color(255, 128, 0), 3));
                            }
                        }
                    }
                }
            }
        }
    }

    private void resaltarObjetivosChuparSangre() {
        actualizarTableroVisual();
        gridButtons[origenFilaSeleccionada][origenColSeleccionada].setBorder(BorderFactory.createLineBorder(Color.YELLOW, 3));

        for (int r = 0; r < 6; r++) {
            for (int c = 0; c < 6; c++) {
                Piece p = board[r][c];
                if (p != null && !p.getBando().equalsIgnoreCase(bandoActual())) {
                    int dFila = Math.abs(r - origenFilaSeleccionada);
                    int dCol = Math.abs(c - origenColSeleccionada);

                    if (dFila <= 1 && dCol <= 1) {
                        gridButtons[r][c].setBorder(BorderFactory.createLineBorder(new Color(255, 105, 180), 3));
                    }
                }
            }
        }
    }

    private void resaltarCasillasInvocacion() {
        actualizarTableroVisual();
        gridButtons[origenFilaSeleccionada][origenColSeleccionada].setBorder(BorderFactory.createLineBorder(Color.YELLOW, 3));

        for (int r = 0; r < 6; r++) {
            for (int c = 0; c < 6; c++) {
                if (Math.abs(r - origenFilaSeleccionada) <= 1 && Math.abs(c - origenColSeleccionada) <= 1) {
                    if (board[r][c] == null) {
                        gridButtons[r][c].setBorder(BorderFactory.createLineBorder(Color.CYAN, 3));
                    }
                }
            }
        }
    }

    private void limpiarSeleccion() {
        origenFilaSeleccionada = -1;
        origenColSeleccionada = -1;
        modoAtaqueActivo = false;
        modoInvocacionActivo = false;
        modoChuparSangreActivo = false;
        ruletaPanel.actualizarStatsPieza(null);
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
        for (int r = 0; r < 6; r++) {
            for (int c = 0; c < 6; c++) {
                Piece p = board[r][c];
                gridButtons[r][c].setBorder(BorderFactory.createEmptyBorder());
                if (p != null) {
                    gridButtons[r][c].setIcon(p.getIcon());
                    String colorBando = p.getBando().equalsIgnoreCase("BLANCO") ? "#E0E0E0" : "#A0A0A0";
                    gridButtons[r][c].setToolTipText(
                        "<html><body style='background-color: #1A121A; color: #FFFFFF; padding: 5px; border: 1px solid #B08D57; font-family: Georgia;'>"
                        + "<b style='color:" + colorBando + ";'>" + p.getTipo().toUpperCase() + " (" + p.getBando() + ")</b><br>"
                        + "<hr style='border: 1px solid #702028;'>"
                        + "❤️ <b>Vida:</b> " + p.getVida() + " / " + p.getVidaMaxima() + "<br>"
                        + "🛡️ <b>Escudo:</b> " + p.getEscudo() + "<br>"
                        + "⚔️ <b>Ataque:</b> " + p.getAtaque()
                        + "</body></html>"
                    );
                } else {
                    gridButtons[r][c].setIcon(null);
                    gridButtons[r][c].setToolTipText(null);
                }
            }
        }
    }

    private void mostrarConfirmacionRetiro() {
        confirmOverlay.setVisible(true);
    }

    private void ocultarConfirmacionRetiro() {
        confirmOverlay.setVisible(false);
    }

    private void ejecutarRetiro() {
        Player oponente = jugadorActual.equals(jugadorBlanco) ? jugadorNegro : jugadorBlanco;
        finalizarPartidaPorVictoria(oponente, jugadorActual, "Retiro voluntario del oponente");
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (fondo != null) {
            g.drawImage(fondo, 0, 0, getWidth(), getHeight(), this);
        } else {
            g.setColor(new Color(15, 10, 15));
            g.fillRect(0, 0, getWidth(), getHeight());
        }
    }
}