package Game;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.net.URL;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.Timer;

public class RuletaPanel extends JPanel {

    public interface OnRuletaFinishedListener {
        void onResultadoObtenido(String tipoPieza);
        void onSinGirosDisponibles();
        void onSolicitarAtaque();
        void onSolicitarInvocacion();
        void onSolicitarChuparSangre();
    }

    private OnRuletaFinishedListener listener;

    private final JLabel lblTurno;
    private final JLabel lblGiros;
    private final JLabel lblMensaje;
    private final JPanel panelMensajeContenedor;
    private final JButton btnGirar;

    private final JPanel panelAcciones;
    private final JButton btnAtacar;
    private final JButton btnChuparSangre;
    private final JButton btnInvocarZombie;

    private final JLabel lblNombrePieza;
    private final JLabel lblVida;
    private final JLabel lblAtaque;
    private final JLabel lblEscudo;

    private int girosRestantes = 0;
    private double anguloActual = 0;
    private Timer timerGiro;
    private boolean girando = false;
    private String bandoActual = "BLANCO";

    private final String[] secciones = {"Hombre Lobo", "Muerte", "Vampiro", "Hombre Lobo", "Muerte", "Vampiro"};
    private final Color[] colores = {
        new Color(75, 20, 30), new Color(30, 20, 45), new Color(90, 30, 25),
        new Color(75, 20, 30), new Color(30, 20, 45), new Color(90, 30, 25)
    };

    private Image imgLoboB, imgLoboN;
    private Image imgVampiroB, imgVampiroN;
    private Image imgMuerteB, imgMuerteN;

    public RuletaPanel() {
        setOpaque(false);
        setLayout(new GridBagLayout());

        cargarImagenesIconos();

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;

        // Panel de Turno y Giros
        JPanel headerPanel = crearPanelGotico();
        headerPanel.setLayout(new GridLayout(2, 1, 2, 2));

        lblTurno = new JLabel("Turno de: -", SwingConstants.CENTER);
        lblTurno.setFont(new Font("Georgia", Font.BOLD, 13));
        lblTurno.setForeground(new Color(240, 220, 180));

        lblGiros = new JLabel("Giros disponibles: 0", SwingConstants.CENTER);
        lblGiros.setFont(new Font("Georgia", Font.ITALIC, 11));
        lblGiros.setForeground(new Color(180, 180, 180));

        headerPanel.add(lblTurno);
        headerPanel.add(lblGiros);

        gbc.gridy = 0;
        add(headerPanel, gbc);

        // Canvas de la Ruleta
        WheelCanvas wheelCanvas = new WheelCanvas();
        wheelCanvas.setPreferredSize(new Dimension(180, 180));
        wheelCanvas.setMinimumSize(new Dimension(180, 180));
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.CENTER;
        add(wheelCanvas, gbc);

        // Botón Girar Ruleta
        btnGirar = crearBotonEstilizado("Girar Ruleta", new Color(130, 30, 40));
        btnGirar.setPreferredSize(new Dimension(160, 34));
        btnGirar.addActionListener(e -> iniciarGiro());

        gbc.gridy = 2;
        add(btnGirar, gbc);

        // Panel Fijo de Advertencias / Estado
        panelMensajeContenedor = crearPanelGotico();
        panelMensajeContenedor.setLayout(new BorderLayout());
        
        // Fijar dimensiones strictly para evitar redimensionamientos
        Dimension dimMensaje = new Dimension(220, 68);
        panelMensajeContenedor.setPreferredSize(dimMensaje);
        panelMensajeContenedor.setMinimumSize(dimMensaje);
        panelMensajeContenedor.setMaximumSize(dimMensaje);

        lblMensaje = new JLabel("¡Gira la ruleta para comenzar!", SwingConstants.CENTER);
        lblMensaje.setFont(new Font("Georgia", Font.BOLD, 11));
        lblMensaje.setForeground(Color.LIGHT_GRAY);

        panelMensajeContenedor.add(lblMensaje, BorderLayout.CENTER);

        gbc.gridy = 3;
        add(panelMensajeContenedor, gbc);

        // Panel Contenedor de Acciones
        panelAcciones = crearPanelGotico();
        panelAcciones.setLayout(new GridLayout(3, 1, 6, 6));
        panelAcciones.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(140, 40, 50), 1),
                " Acciones Disponibles ",
                0, 0,
                new Font("Georgia", Font.BOLD, 11),
                new Color(212, 175, 55)
            ),
            BorderFactory.createEmptyBorder(8, 10, 10, 10)
        ));

        btnAtacar = crearBotonEstilizado("Atacar", new Color(110, 25, 35));
        btnChuparSangre = crearBotonEstilizado("Chupar Sangre", new Color(120, 30, 80));
        btnInvocarZombie = crearBotonEstilizado("Invocar Zombie", new Color(30, 90, 100));

        btnAtacar.addActionListener(e -> {
            if (listener != null) listener.onSolicitarAtaque();
        });
        btnChuparSangre.addActionListener(e -> {
            if (listener != null) listener.onSolicitarChuparSangre();
        });
        btnInvocarZombie.addActionListener(e -> {
            if (listener != null) listener.onSolicitarInvocacion();
        });

        panelAcciones.add(btnAtacar);
        panelAcciones.add(btnChuparSangre);
        panelAcciones.add(btnInvocarZombie);

        gbc.gridy = 4;
        add(panelAcciones, gbc);

        // Panel Ficha Seleccionada
        JPanel panelStats = crearPanelGotico();
        panelStats.setLayout(new BorderLayout(5, 5));
        panelStats.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(140, 40, 50), 1),
                " Ficha Seleccionada ",
                0, 0,
                new Font("Georgia", Font.BOLD, 11),
                new Color(212, 175, 55)
            ),
            BorderFactory.createEmptyBorder(8, 10, 10, 10)
        ));

        lblNombrePieza = new JLabel("Ninguna", SwingConstants.LEFT);
        lblNombrePieza.setFont(new Font("Georgia", Font.BOLD, 12));
        lblNombrePieza.setForeground(Color.WHITE);

        JPanel detailsGrid = new JPanel(new GridLayout(3, 1, 2, 2));
        detailsGrid.setOpaque(false);

        lblVida = new JLabel("Vida: -");
        lblAtaque = new JLabel("Ataque: -");
        lblEscudo = new JLabel("Escudo: -");

        Font fontStats = new Font("Georgia", Font.PLAIN, 11);
        Color colStats = new Color(200, 200, 200);

        lblVida.setFont(fontStats); lblVida.setForeground(colStats);
        lblAtaque.setFont(fontStats); lblAtaque.setForeground(colStats);
        lblEscudo.setFont(fontStats); lblEscudo.setForeground(colStats);

        detailsGrid.add(lblVida);
        detailsGrid.add(lblAtaque);
        detailsGrid.add(lblEscudo);

        panelStats.add(lblNombrePieza, BorderLayout.NORTH);
        panelStats.add(detailsGrid, BorderLayout.CENTER);

        gbc.gridy = 5;
        add(panelStats, gbc);

        actualizarStatsPieza(null);
    }

    private JButton crearBotonEstilizado(String texto, Color bgBase) {
        JButton btn = new JButton(texto) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                Color c1 = getModel().isPressed() ? bgBase.darker() : (getModel().isRollover() ? bgBase.brighter() : bgBase);
                g2.setColor(c1);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);

                g2.setColor(new Color(212, 175, 55, 180));
                g2.setStroke(new BasicStroke(1.2f));
                g2.drawRoundRect(1, 1, getWidth() - 3, getHeight() - 3, 7, 7);

                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setFont(new Font("Georgia", Font.BOLD, 11));
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setOpaque(false);
        btn.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(160, 30));
        return btn;
    }

    private JPanel crearPanelGotico() {
        JPanel p = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(20, 10, 15, 230));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.setColor(new Color(120, 35, 45));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
                g2.dispose();
            }
        };
        p.setOpaque(false);
        p.setBorder(BorderFactory.createEmptyBorder(6, 10, 6, 10));
        return p;
    }

    private Image cargarImagenSegura(String ruta) {
        try {
            URL url = getClass().getResource(ruta);
            if (url != null) {
                return new ImageIcon(url).getImage();
            }
        } catch (Exception e) {}
        return null;
    }

    private void cargarImagenesIconos() {
        // Nombres ajustados estrictamente a la convención camelCase del proyecto
        imgLoboB = cargarImagenSegura("/Images/loboB.png");
        imgLoboN = cargarImagenSegura("/Images/loboN.png");
        imgVampiroB = cargarImagenSegura("/Images/vampiroB.png");
        imgVampiroN = cargarImagenSegura("/Images/vampiroN.png");
        imgMuerteB = cargarImagenSegura("/Images/muerteB.png");
        imgMuerteN = cargarImagenSegura("/Images/muerteN.png");
    }

    public void setListener(OnRuletaFinishedListener listener) {
        this.listener = listener;
    }

    public void prepararNuevoTurno(String nombreJugador, String bando, int piezasPerdidas, List<String> tiposDisponibles) {
        this.bandoActual = bando;
        lblTurno.setText("Turno de: " + nombreJugador + " (" + bando + ")");
        this.girosRestantes = Math.max(1, piezasPerdidas);
        lblGiros.setText("Giros disponibles: " + girosRestantes);

        btnGirar.setEnabled(true);
        panelAcciones.setVisible(false);
        mostrarMensajeEstado("¡Haz girar la ruleta para tu turno!", Color.WHITE);
        actualizarStatsPieza(null);
        repaint();
    }

      public void deshabilitarBotonGiro() {
        btnGirar.setEnabled(false);
    }

    public void habilitarBotonGiroSiQuedanGiros() {
        if (girosRestantes > 0) {
            btnGirar.setEnabled(true);
        }
    }

    public void mostrarMensajeEstado(String msj, Color color) {
        lblMensaje.setText("<html><center>" + msj + "</center></html>");
        lblMensaje.setForeground(color);
    }

    public void actualizarStatsPieza(Piece p) {
        if (p == null) {
            lblNombrePieza.setText("Ninguna seleccionada");
            lblVida.setText("Vida: -");
            lblAtaque.setText("Ataque: -");
            lblEscudo.setText("Escudo: -");
            panelAcciones.setVisible(false);
        } else {
            lblNombrePieza.setText(p.getTipo().toUpperCase() + " (" + p.getBando() + ")");
            lblVida.setText("Vida: " + p.getVida() + " / " + p.getVidaMaxima());
            lblAtaque.setText("Ataque: " + p.getAtaque());
            lblEscudo.setText("Escudo: " + p.getEscudo());

            btnAtacar.setVisible(true);
            btnChuparSangre.setVisible(p instanceof Vampire);
            btnInvocarZombie.setVisible(p instanceof Death);

            panelAcciones.setVisible(true);
            revalidate();
            repaint();
        }
    }

    private void iniciarGiro() {
        if (girando || girosRestantes <= 0) return;

        girando = true;
        girosRestantes--;
        lblGiros.setText("Giros disponibles: " + girosRestantes);
        btnGirar.setEnabled(false);

        double velocidadInicial = 25.0 + Math.random() * 15.0;
        final double[] vel = {velocidadInicial};

        timerGiro = new Timer(20, e -> {
            anguloActual = (anguloActual + vel[0]) % 360;
            vel[0] *= 0.97;
            repaint();

            if (vel[0] < 0.3) {
                ((Timer) e.getSource()).stop();
                girando = false;
                procesarResultado();
            }
        });
        timerGiro.start();
    }

    private void procesarResultado() {
        double anguloNormalizado = (360 - (anguloActual % 360) + 90) % 360;
        int index = (int) (anguloNormalizado / 60) % 6;
        String resultado = secciones[index];

        if (listener != null) {
            listener.onResultadoObtenido(resultado);
            if (girosRestantes == 0) {
                listener.onSinGirosDisponibles();
            }
        }
    }

    private class WheelCanvas extends JPanel {
        public WheelCanvas() {
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

            int size = Math.min(getWidth(), getHeight()) - 16;
            int x = (getWidth() - size) / 2;
            int y = (getHeight() - size) / 2;

            g2.setColor(new Color(0, 0, 0, 180));
            g2.fillOval(x + 3, y + 3, size, size);

            boolean esBlanco = bandoActual.equalsIgnoreCase("BLANCO");

            for (int i = 0; i < 6; i++) {
                g2.setColor(colores[i]);
                g2.fillArc(x, y, size, size, (int) (anguloActual + i * 60), 60);

                g2.setColor(new Color(180, 140, 60, 120));
                g2.setStroke(new BasicStroke(1.5f));
                g2.drawArc(x, y, size, size, (int) (anguloActual + i * 60), 60);

                double midAngleRad = Math.toRadians(anguloActual + i * 60 + 30);
                int iconSize = Math.max(20, size / 6);
                int imgX = (int) (x + size / 2.0 + (size / 3.1) * Math.cos(midAngleRad)) - (iconSize / 2);
                int imgY = (int) (y + size / 2.0 - (size / 3.1) * Math.sin(midAngleRad)) - (iconSize / 2);

                Image imgTarget = null;
                switch (secciones[i]) {
                    case "Hombre Lobo":
                        imgTarget = esBlanco ? imgLoboB : imgLoboN;
                        break;
                    case "Vampiro":
                        imgTarget = esBlanco ? imgVampiroB : imgVampiroN;
                        break;
                    case "Muerte":
                        imgTarget = esBlanco ? imgMuerteB : imgMuerteN;
                        break;
                }

                if (imgTarget != null) {
                    g2.drawImage(imgTarget, imgX, imgY, iconSize, iconSize, this);
                }
            }

            g2.setStroke(new BasicStroke(4.0f));
            g2.setColor(new Color(140, 30, 40));
            g2.drawOval(x, y, size, size);

            g2.setStroke(new BasicStroke(1.5f));
            g2.setColor(new Color(212, 175, 55));
            g2.drawOval(x - 1, y - 1, size + 2, size + 2);

            int indicatorY = y - 2;
            int[] px = {getWidth() / 2 - 9, getWidth() / 2 + 9, getWidth() / 2};
            int[] py = {indicatorY, indicatorY, indicatorY + 14};
            g2.setColor(new Color(230, 180, 50));
            g2.fillPolygon(px, py, 3);
            g2.setColor(Color.BLACK);
            g2.drawPolygon(px, py, 3);

            g2.dispose();
        }
    }
}