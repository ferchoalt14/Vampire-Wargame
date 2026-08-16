/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Game;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

public class RuletaPanel extends JPanel {

    private final JLabel lblTurno;
    private final RuletaWheel ruedaGrafica;
    private final JLabel lblResultado;
    private final JLabel lblGirosRestantes;
    private final JLabel lblMensajeEstado;
    private final JButton btnGirar;

    private final JLabel lblStatsPieza;
    private final JButton btnAtacar;
    private final JButton btnInvocarZombie;
    private final JButton btnChuparSangre;

    /** Orden de tipos que representa la ruleta (única fuente de verdad, ver RuletaWheel). */
    private static final String[] TIPOS_RULETA = RuletaWheel.ORDEN_TIPOS;

    private String resultadoSeleccionado = null;
    private String bandoActual = "BLANCO";

    private int girosRestantes = 1;
    private OnRuletaFinishedListener listener;

    public interface OnRuletaFinishedListener {
        void onResultadoObtenido(String tipoPieza);
        void onSinGirosDisponibles();
        void onSolicitarAtaque();
        void onSolicitarInvocacion();
        void onSolicitarChuparSangre();
    }

    public RuletaPanel() {
        // Panel contenedor: apila verticalmente dos tarjetas independientes
        // (la ruleta y la info/acciones), una debajo de la otra, sin que se
        // encimen ni se aplasten entre sí.
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setOpaque(false);
        setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));

        JPanel tarjetaRuleta = crearTarjeta();
        JPanel tarjetaInfo = crearTarjeta();

        // ---------- Contenido de la tarjeta "Ruleta" ----------
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(3, 4, 3, 4);
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        lblTurno = new JLabel("Turno: ---", SwingConstants.CENTER);
        lblTurno.setForeground(new Color(230, 230, 230));
        lblTurno.setFont(new Font("Georgia", Font.BOLD, 14));

        JLabel lblTitulo = new JLabel("RULETA DE ACCIÓN", SwingConstants.CENTER);
        lblTitulo.setForeground(new Color(210, 170, 110));
        lblTitulo.setFont(new Font("Georgia", Font.BOLD, 13));

        ruedaGrafica = new RuletaWheel();

        // La fila de la ruleta NO se estira/encoge horizontalmente como las demás,
        // para que conserve siempre su tamaño fijo (ver RuletaWheel).
        GridBagConstraints gbcRueda = (GridBagConstraints) gbc.clone();
        gbcRueda.fill = GridBagConstraints.NONE;

        lblResultado = new JLabel("¡Gira la ruleta!", SwingConstants.CENTER);
        lblResultado.setForeground(Color.WHITE);
        lblResultado.setFont(new Font("Georgia", Font.ITALIC, 13));

        lblGirosRestantes = new JLabel("Giros restantes: 1", SwingConstants.CENTER);
        lblGirosRestantes.setForeground(new Color(180, 180, 180));
        lblGirosRestantes.setFont(new Font("Georgia", Font.PLAIN, 11));

        btnGirar = LogInMenu.createButton("Girar Ruleta");
        btnGirar.setPreferredSize(new Dimension(150, 32));
        btnGirar.addActionListener(e -> iniciarGiro());

        gbc.gridy = 0; tarjetaRuleta.add(lblTurno, gbc);
        gbc.gridy = 1; tarjetaRuleta.add(lblTitulo, gbc);
        gbcRueda.gridy = 2; tarjetaRuleta.add(ruedaGrafica, gbcRueda);
        gbc.gridy = 3; tarjetaRuleta.add(lblResultado, gbc);
        gbc.gridy = 4; tarjetaRuleta.add(lblGirosRestantes, gbc);
        gbc.gridy = 5; tarjetaRuleta.add(btnGirar, gbc);

        // ---------- Contenido de la tarjeta "Info y acciones" ----------
        GridBagConstraints gbc2 = new GridBagConstraints();
        gbc2.insets = new Insets(3, 4, 3, 4);
        gbc2.gridx = 0;
        gbc2.fill = GridBagConstraints.HORIZONTAL;

        JLabel lblTituloInfo = new JLabel("ESTADO Y ACCIONES", SwingConstants.CENTER);
        lblTituloInfo.setForeground(new Color(210, 170, 110));
        lblTituloInfo.setFont(new Font("Georgia", Font.BOLD, 13));

        lblMensajeEstado = new JLabel("<html><center>Selecciona 'Girar' para iniciar turno.</center></html>", SwingConstants.CENTER);
        lblMensajeEstado.setForeground(new Color(220, 190, 120));
        lblMensajeEstado.setFont(new Font("Georgia", Font.PLAIN, 11));
        lblMensajeEstado.setPreferredSize(new Dimension(260, 45));

        lblStatsPieza = new JLabel("<html><center><b>STATS FICHA SELECCIONADA</b><br>Vida: - | Escudo: - | Atq: -</center></html>", SwingConstants.CENTER);
        lblStatsPieza.setForeground(new Color(180, 220, 255));
        lblStatsPieza.setFont(new Font("Georgia", Font.PLAIN, 11));

        btnAtacar = LogInMenu.createButton("Atacar");
        btnAtacar.setPreferredSize(new Dimension(80, 28));
        btnAtacar.setEnabled(false);
        btnAtacar.setVisible(false);
        btnAtacar.addActionListener(e -> {
            try {
                if (listener != null) listener.onSolicitarAtaque();
            } catch (Exception ex) {
                mostrarMensajeEstado("No se pudo iniciar el ataque.", Color.RED);
            }
        });

        btnInvocarZombie = LogInMenu.createButton("Invocar Zombie");
        btnInvocarZombie.setPreferredSize(new Dimension(130, 28));
        btnInvocarZombie.setEnabled(false);
        btnInvocarZombie.setVisible(false);
        btnInvocarZombie.addActionListener(e -> {
            try {
                if (listener != null) listener.onSolicitarInvocacion();
            } catch (Exception ex) {
                mostrarMensajeEstado("No se pudo iniciar la invocación.", Color.RED);
            }
        });

        btnChuparSangre = LogInMenu.createButton("Chupar Sangre");
        btnChuparSangre.setPreferredSize(new Dimension(120, 28));
        btnChuparSangre.setEnabled(false);
        btnChuparSangre.setVisible(false);
        btnChuparSangre.addActionListener(e -> {
            try {
                if (listener != null) listener.onSolicitarChuparSangre();
            } catch (Exception ex) {
                mostrarMensajeEstado("No se pudo iniciar la habilidad.", Color.RED);
            }
        });

        JPanel panelAcciones = new JPanel();
        panelAcciones.setOpaque(false);
        panelAcciones.add(btnAtacar);
        panelAcciones.add(btnInvocarZombie);
        panelAcciones.add(btnChuparSangre);

        gbc2.gridy = 0; tarjetaInfo.add(lblTituloInfo, gbc2);
        gbc2.gridy = 1; tarjetaInfo.add(lblMensajeEstado, gbc2);
        gbc2.gridy = 2; tarjetaInfo.add(lblStatsPieza, gbc2);
        gbc2.gridy = 3; tarjetaInfo.add(panelAcciones, gbc2);

        // ---------- Ensamblado final: ruleta arriba, info abajo, con espacio ----------
        tarjetaRuleta.setAlignmentX(Component.CENTER_ALIGNMENT);
        tarjetaInfo.setAlignmentX(Component.CENTER_ALIGNMENT);

        add(tarjetaRuleta);
        add(Box.createRigidArea(new Dimension(0, 14)));
        add(tarjetaInfo);
        add(Box.createVerticalGlue());
    }

    private JPanel crearTarjeta() {
        JPanel tarjeta = new JPanel(new GridBagLayout());
        tarjeta.setBackground(new Color(20, 15, 20, 230));
        tarjeta.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(100, 40, 50), 2),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        return tarjeta;
    }

    public void setListener(OnRuletaFinishedListener listener) {
        this.listener = listener;
    }

    /**
     * Prepara un nuevo turno. Firma idéntica a la versión anterior, por lo
     * que BoardPanel.java NO necesita modificarse.
     */
    public void prepararNuevoTurno(String nombreJugador, String bando, int piezasPerdidas, List<String> piezasDisponibles) {
        try {
            this.bandoActual = (bando != null) ? bando : "BLANCO";
            this.resultadoSeleccionado = null;

            try {
                ruedaGrafica.detenerAnimacion();
                ruedaGrafica.actualizarBando(this.bandoActual);
            } catch (Exception ex) {
                // La ruleta puede seguir operando aunque falle la carga visual.
            }

            if (piezasPerdidas >= 4) {
                this.girosRestantes = 3;
            } else if (piezasPerdidas >= 2) {
                this.girosRestantes = 2;
            } else {
                this.girosRestantes = 1;
            }

            lblTurno.setText("Turno: " + (nombreJugador != null ? nombreJugador : "---") + " (" + this.bandoActual + ")");
            lblGirosRestantes.setText("Giros restantes: " + girosRestantes);
            lblResultado.setText("¡Gira la ruleta!");
            mostrarMensajeEstado("Gira la ruleta para obtener una pieza.", Color.WHITE);

            btnGirar.setEnabled(true);
            btnAtacar.setVisible(false);
            btnInvocarZombie.setVisible(false);
            btnChuparSangre.setVisible(false);
            actualizarStatsPieza(null);
        } catch (Exception e) {
            mostrarMensajeEstado("Error al preparar el turno. Intenta girar de nuevo.", Color.RED);
        }
    }

    private void iniciarGiro() {
        try {
            if (ruedaGrafica.isGirando() || girosRestantes <= 0) {
                return;
            }

            girosRestantes--;
            lblGirosRestantes.setText("Giros restantes: " + girosRestantes);
            btnGirar.setEnabled(false);
            mostrarMensajeEstado("Girando la ruleta...", new Color(220, 190, 120));

            int indiceElegido = (int) (Math.random() * TIPOS_RULETA.length);
            if (indiceElegido < 0 || indiceElegido >= TIPOS_RULETA.length) {
                indiceElegido = 0; // salvaguarda extra, nunca debería ocurrir
            }
            final int indiceObjetivoVisual = indiceElegido;

            // El índice "objetivo" solo se usa para saber hacia dónde animar el giro.
            // El resultado real del juego SIEMPRE se toma del índice que la ruleta
            // reporta como "indiceReal" (leído del ángulo final físico de la rueda),
            // así la imagen que ves y la pieza que obtienes nunca pueden desincronizarse.
            ruedaGrafica.girarHacia(indiceObjetivoVisual, (indiceReal) -> {
                try {
                    int idx = (indiceReal >= 0 && indiceReal < TIPOS_RULETA.length) ? indiceReal : 0;
                    resultadoSeleccionado = TIPOS_RULETA[idx];
                    lblResultado.setText("¡" + resultadoSeleccionado + "!");

                    if (listener != null) {
                        listener.onResultadoObtenido(resultadoSeleccionado);
                    }

                    if (girosRestantes > 0) {
                        btnGirar.setEnabled(true);
                    } else if (listener != null) {
                        listener.onSinGirosDisponibles();
                    }
                } catch (Exception exInterno) {
                    mostrarMensajeEstado("Ocurrió un problema al procesar el giro.", Color.RED);
                    if (girosRestantes > 0) {
                        btnGirar.setEnabled(true);
                    }
                }
            });
        } catch (Exception e) {
            mostrarMensajeEstado("No se pudo girar la ruleta. Intenta de nuevo.", Color.RED);
            btnGirar.setEnabled(true);
        }
    }

    public void deshabilitarBotonGiro() {
        try {
            btnGirar.setEnabled(false);
        } catch (Exception e) {
            // no-op
        }
    }

    public void mostrarMensajeEstado(String msj, Color color) {
        try {
            lblMensajeEstado.setText("<html><center>" + (msj != null ? msj : "") + "</center></html>");
            lblMensajeEstado.setForeground(color != null ? color : Color.WHITE);
        } catch (Exception e) {
            // no-op
        }
    }

    public void actualizarStatsPieza(Piece p) {
        try {
            if (p == null) {
                lblStatsPieza.setText("<html><center><b>STATS FICHA SELECCIONADA</b><br>Vida: - | Escudo: - | Atq: -</center></html>");
                btnAtacar.setVisible(false);
                btnInvocarZombie.setVisible(false);
                btnChuparSangre.setVisible(false);
                return;
            }

            lblStatsPieza.setText("<html><center><b>" + p.getTipo().toUpperCase() + " (" + p.getBando() + ")</b><br>"
                    + "Vida: " + p.getVida() + " | Escudo: " + p.getEscudo() + " | Atq: " + p.getAtaque() + "</center></html>");

            btnAtacar.setVisible(true);
            btnAtacar.setEnabled(true);

            if (p instanceof Death) {
                btnInvocarZombie.setVisible(true);
                btnInvocarZombie.setEnabled(true);
                btnChuparSangre.setVisible(false);
            } else if (p instanceof Vampire) {
                btnChuparSangre.setVisible(true);
                btnChuparSangre.setEnabled(true);
                btnInvocarZombie.setVisible(false);
            } else {
                btnInvocarZombie.setVisible(false);
                btnChuparSangre.setVisible(false);
            }
        } catch (Exception e) {
            lblStatsPieza.setText("<html><center><b>STATS FICHA SELECCIONADA</b><br>Vida: - | Escudo: - | Atq: -</center></html>");
            btnAtacar.setVisible(false);
            btnInvocarZombie.setVisible(false);
            btnChuparSangre.setVisible(false);
        }
    }
}