/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Game;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.Timer;

public class RuletaPanel extends JPanel {

    private final JLabel lblTurno;
    private final JLabel lblImagenPieza;
    private final JLabel lblResultado;
    private final JLabel lblGirosRestantes;
    private final JLabel lblMensajeEstado;
    private final JButton btnGirar;

    private Timer timerAnimacion;
    private int ticksAnimacion = 0;
    private boolean enAnimacion = false;

    private final String[] tiposRuleta = {"Hombre Lobo", "Vampiro", "Muerte", "Hombre Lobo", "Vampiro", "Muerte"};
    private String resultadoSeleccionado = null;
    private String bandoActual = "BLANCO"; // Guarda el bando para mostrar la imagen correcta

    private int girosRestantes = 1;
    private OnRuletaFinishedListener listener;

    public interface OnRuletaFinishedListener {
        void onResultadoObtenido(String tipoPieza);
        void onSinGirosDisponibles();
    }

    public RuletaPanel() {
        setLayout(new GridBagLayout());
        setBackground(new Color(20, 15, 20, 230));
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(100, 40, 50), 2),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        setPreferredSize(new Dimension(280, 460));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 5, 4, 5);
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        lblTurno = new JLabel("Turno: ---", SwingConstants.CENTER);
        lblTurno.setForeground(new Color(230, 230, 230));
        lblTurno.setFont(new Font("Georgia", Font.BOLD, 15));

        JLabel lblTitulo = new JLabel("RULETA DE ACCIÓN", SwingConstants.CENTER);
        lblTitulo.setForeground(new Color(210, 170, 110));
        lblTitulo.setFont(new Font("Georgia", Font.BOLD, 14));

        lblImagenPieza = new JLabel("", SwingConstants.CENTER);
        lblImagenPieza.setPreferredSize(new Dimension(90, 90));
        lblImagenPieza.setBorder(BorderFactory.createLineBorder(new Color(140, 40, 50), 1));
        lblImagenPieza.setOpaque(true);
        lblImagenPieza.setBackground(new Color(10, 8, 10));

        lblResultado = new JLabel("¡Gira la ruleta!", SwingConstants.CENTER);
        lblResultado.setForeground(Color.WHITE);
        lblResultado.setFont(new Font("Georgia", Font.ITALIC, 13));

        lblGirosRestantes = new JLabel("Giros restantes: 1", SwingConstants.CENTER);
        lblGirosRestantes.setForeground(new Color(180, 180, 180));
        lblGirosRestantes.setFont(new Font("Georgia", Font.PLAIN, 12));

        lblMensajeEstado = new JLabel("<html><center>Selecciona 'Girar' para iniciar turno.</center></html>", SwingConstants.CENTER);
        lblMensajeEstado.setForeground(new Color(220, 190, 120));
        lblMensajeEstado.setFont(new Font("Georgia", Font.PLAIN, 11));
        lblMensajeEstado.setPreferredSize(new Dimension(240, 45));

        btnGirar = LogInMenu.createButton("Girar Ruleta");
        btnGirar.setPreferredSize(new Dimension(160, 36));
        btnGirar.addActionListener(e -> iniciarGiro());

        gbc.gridy = 0; add(lblTurno, gbc);
        gbc.gridy = 1; add(lblTitulo, gbc);
        gbc.gridy = 2; gbc.fill = GridBagConstraints.NONE; add(lblImagenPieza, gbc);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridy = 3; add(lblResultado, gbc);
        gbc.gridy = 4; add(lblGirosRestantes, gbc);
        gbc.gridy = 5; add(btnGirar, gbc);
        gbc.gridy = 6; add(lblMensajeEstado, gbc);

        inicializarTimer();
        mostrarIconoRepresentativo("Hombre Lobo");
    }

    public void setListener(OnRuletaFinishedListener listener) {
        this.listener = listener;
    }

    public void mostrarMensajeEstado(String mensaje, Color colorText) {
        try {
            lblMensajeEstado.setForeground(colorText);
            lblMensajeEstado.setText("<html><center>" + mensaje + "</center></html>");
        } catch (Exception e) {
            // Control defensivo
        }
    }

    public void prepararNuevoTurno(String nombreJugador, String bando, int piezasPerdidas, List<String> tiposPiezasDisponibles) {
        try {
            if (timerAnimacion != null && timerAnimacion.isRunning()) {
                timerAnimacion.stop();
            }
            enAnimacion = false;
            this.bandoActual = bando; // Actualizamos el bando del jugador actual

            lblTurno.setText("Turno: " + nombreJugador + " (" + bando + ")");

            if (piezasPerdidas >= 4) {
                this.girosRestantes = 3;
            } else if (piezasPerdidas >= 2) {
                this.girosRestantes = 2;
            } else {
                this.girosRestantes = 1;
            }

            lblGirosRestantes.setText("Giros restantes: " + girosRestantes);
            lblResultado.setText("¡Gira la ruleta!");
            mostrarMensajeEstado("Haz clic en 'Girar Ruleta' para obtener una pieza.", new Color(220, 190, 120));
            btnGirar.setEnabled(true);
            resultadoSeleccionado = null;

            // Muestra imagen inicial correspondiente a su bando
            mostrarIconoRepresentativo("Hombre Lobo");
        } catch (Exception e) {
            System.err.println("Error al preparar turno en ruleta: " + e.getMessage());
        }
    }

    private void inicializarTimer() {
        try {
            timerAnimacion = new Timer(70, e -> {
                try {
                    ticksAnimacion++;
                    int idx = (int) (Math.random() * tiposRuleta.length);
                    String tipoActual = tiposRuleta[idx];
                    mostrarIconoRepresentativo(tipoActual);

                    if (ticksAnimacion >= 20) {
                        timerAnimacion.stop();
                        enAnimacion = false;
                        finalizarGiro(tipoActual);
                    }
                } catch (Exception ex) {
                    if (timerAnimacion != null) timerAnimacion.stop();
                    enAnimacion = false;
                    btnGirar.setEnabled(true);
                }
            });
        } catch (Exception e) {
            System.err.println("Error al inicializar Timer: " + e.getMessage());
        }
    }

    private void iniciarGiro() {
        try {
            if (enAnimacion || girosRestantes <= 0) return;

            enAnimacion = true;
            btnGirar.setEnabled(false);
            girosRestantes--;
            lblGirosRestantes.setText("Giros restantes: " + girosRestantes);
            lblResultado.setText("Girando...");
            mostrarMensajeEstado("Seleccionando pieza...", Color.LIGHT_GRAY);

            ticksAnimacion = 0;
            timerAnimacion.start();
        } catch (Exception e) {
            enAnimacion = false;
            btnGirar.setEnabled(true);
            lblResultado.setText("Error al girar.");
        }
    }

    private void finalizarGiro(String resultado) {
        try {
            this.resultadoSeleccionado = resultado;
            lblResultado.setText("<html><center>Obtenido:<br><b>" + resultado + "</b></center></html>");

            if (listener != null) {
                listener.onResultadoObtenido(resultado);
            }

            if (girosRestantes > 0 && resultadoSeleccionado != null) {
                btnGirar.setEnabled(true);
            } else if (girosRestantes <= 0 && resultadoSeleccionado != null) {
                btnGirar.setEnabled(false);
                if (listener != null) {
                    listener.onSinGirosDisponibles();
                }
            }
        } catch (Exception e) {
            System.err.println("Error al finalizar giro: " + e.getMessage());
        }
    }

    private void mostrarIconoRepresentativo(String tipo) {
        try {
            String prefijo = "";
            switch (tipo) {
                case "Hombre Lobo": prefijo = "lobo"; break;
                case "Vampiro":     prefijo = "vampiro"; break;
                case "Muerte":      prefijo = "muerte"; break;
            }

            // Selecciona el B o N según el bando actual del jugador
            String sufijo = bandoActual.equalsIgnoreCase("BLANCO") ? "B" : "N";
            String path = "/Images/" + prefijo + sufijo + ".png";

            java.net.URL imgURL = getClass().getResource(path);
            if (imgURL != null) {
                ImageIcon raw = new ImageIcon(imgURL);
                Image img = raw.getImage().getScaledInstance(80, 80, Image.SCALE_SMOOTH);
                lblImagenPieza.setIcon(new ImageIcon(img));
                lblImagenPieza.setText("");
            } else {
                lblImagenPieza.setIcon(null);
                lblImagenPieza.setText(tipo);
            }
        } catch (Exception e) {
            lblImagenPieza.setIcon(null);
            lblImagenPieza.setText(tipo);
        }
    }

    public String getResultadoSeleccionado() {
        return resultadoSeleccionado;
    }

    public void deshabilitarBotonGiro() {
        try {
            btnGirar.setEnabled(false);
        } catch (Exception e) {
            // Manejo silencioso defensivo
        }
    }
}