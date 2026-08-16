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

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.awt.geom.Path2D;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.Timer;

/**
 * Ruleta circular de Vampire Wargame.
 *
 * Dibuja 6 sectores (2 por cada tipo de pieza: Hombre Lobo, Vampiro y
 * Muerte -el Zombie NO participa, según la sección 2.2 de la especificación-)
 * y anima el giro con una desaceleración tipo "ruleta de casino" hasta
 * detenerse exactamente bajo el puntero fijo ubicado en la parte superior.
 *
 * La clase nunca deja escapar una excepción: cualquier fallo (imagen no
 * encontrada, error de pintado, etc.) se captura y se resuelve con un
 * respaldo visual, para cumplir con el requisito de que el programa nunca
 * debe terminar abruptamente.
 */
public final class RuletaWheel extends JPanel {

    /** Orden fijo de tipos alrededor de la ruleta (única fuente de verdad). */
    public static final String[] ORDEN_TIPOS = {
        "Hombre Lobo", "Vampiro", "Muerte",
        "Hombre Lobo", "Vampiro", "Muerte"
    };

    private static final Color[] COLORES_SECTOR = {
        new Color(70, 25, 30), new Color(35, 45, 55),
        new Color(45, 30, 55), new Color(70, 25, 30),
        new Color(35, 45, 55), new Color(45, 30, 55)
    };

    private final Image[] imagenes = new Image[ORDEN_TIPOS.length];
    private String bandoActual = "BLANCO";

    /** Ángulo actual de la ruleta en radianes (0 = sector 0 alineado a la derecha). */
    private double anguloActual = 0.0;

    private Timer timerGiro;
    private volatile boolean girando = false;

    public RuletaWheel() {
        setOpaque(false);
        Dimension tamano = new Dimension(190, 190);
        // IMPORTANTE: se fija tamaño mínimo y máximo además del preferido.
        // Un JPanel sin hijos (como este, que solo pinta con paintComponent)
        // tiene por defecto un tamaño mínimo casi de 0x0, lo que provoca que
        // GridBagLayout/BoxLayout lo aplasten cuando el contenedor anda
        // corto de espacio. Fijar los tres tamaños evita ese colapso visual.
        setPreferredSize(tamano);
        setMinimumSize(tamano);
        setMaximumSize(tamano);
        cargarImagenes("BLANCO");
    }

    /**
     * Recarga los íconos de la ruleta según el color del jugador en turno.
     * Si alguna imagen no se encuentra, esa posición simplemente queda sin
     * imagen y se dibuja un respaldo textual (nunca lanza excepción).
     */
    public void actualizarBando(String bando) {
        try {
            this.bandoActual = (bando != null) ? bando : "BLANCO";
            cargarImagenes(this.bandoActual);
            repaint();
        } catch (Exception e) {
            // Falla silenciosa y controlada: la ruleta sigue funcionando sin íconos.
        }
    }

    private void cargarImagenes(String bando) {
        String sufijo = "BLANCO".equalsIgnoreCase(bando) ? "B" : "N";
        for (int i = 0; i < ORDEN_TIPOS.length; i++) {
            imagenes[i] = cargarImagenSegura(ORDEN_TIPOS[i], sufijo);
        }
    }

    private Image cargarImagenSegura(String tipo, String sufijo) {
        try {
            String archivo;
            switch (tipo) {
                case "Hombre Lobo": archivo = "lobo" + sufijo + ".png"; break;
                case "Vampiro": archivo = "vampiro" + sufijo + ".png"; break;
                case "Muerte": archivo = "muerte" + sufijo + ".png"; break;
                default: return null;
            }
            java.net.URL url = getClass().getResource("/Images/" + archivo);
            if (url == null) return null;
            return new ImageIcon(url).getImage();
        } catch (Exception e) {
            return null;
        }
    }

    public boolean isGirando() {
        return girando;
    }

    /** Callback que entrega el índice REAL (0..5) que quedó bajo el puntero al detenerse la ruleta. */
    public interface OnGiroFinalizado {
        void onFinalizado(int indiceReal);
    }

    /**
     * Calcula, a partir del ángulo actual de la ruleta, qué sector está
     * físicamente alineado con el puntero fijo en este instante.
     *
     * Este cálculo es la ÚNICA fuente de verdad sobre "qué salió": se hace
     * siempre a partir del ángulo final real de la rueda, nunca al revés
     * (nunca se asume que "si le pedimos ir al índice X, ahí se quedó").
     * Así es imposible que la imagen mostrada y el resultado del juego
     * queden desincronizados.
     */
    public int calcularIndiceBajoPuntero() {
        try {
            int n = ORDEN_TIPOS.length;
            double anguloSector = 2 * Math.PI / n;
            double anguloFinal = normalizarAngulo(anguloActual);
            // Ángulo local (antes de rotar) que en este momento coincide con el puntero (-90°).
            double anguloLocalBajoPuntero = normalizarAngulo(-Math.PI / 2.0 - anguloFinal);
            int idx = (int) Math.floor(anguloLocalBajoPuntero / anguloSector);
            if (idx < 0) idx = 0;
            if (idx >= n) idx = n - 1;
            return idx;
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * Gira la ruleta apuntando (visualmente) hacia el sector
     * {@code indiceObjetivo} (0..5, según {@link #ORDEN_TIPOS}) y, al
     * terminar, ejecuta {@code alTerminar} en el hilo de Swing entregando
     * el índice REAL leído bajo el puntero (ver {@link #calcularIndiceBajoPuntero()}).
     * Ese índice real -no el objetivo original- es el que debe usarse
     * siempre para decidir la pieza obtenida, garantizando que lo que se
     * ve coincida siempre con lo que se juega.
     * Si el índice es inválido o ocurre cualquier error, se resuelve el
     * callback de inmediato para no dejar la interfaz bloqueada.
     */
    public void girarHacia(int indiceObjetivo, OnGiroFinalizado alTerminar) {
        try {
            if (girando) {
                return;
            }
            if (indiceObjetivo < 0 || indiceObjetivo >= ORDEN_TIPOS.length) {
                if (alTerminar != null) alTerminar.onFinalizado(calcularIndiceBajoPuntero());
                return;
            }

            girando = true;

            final int n = ORDEN_TIPOS.length;
            final double anguloSector = 2 * Math.PI / n;
            final double anguloCentroSector = indiceObjetivo * anguloSector + anguloSector / 2.0;

            // Queremos que: anguloFinal + anguloCentroSector  ≡  -90°  (puntero fijo arriba)
            final double anguloFinalBase = normalizarAngulo(-Math.PI / 2.0 - anguloCentroSector);
            final double anguloInicial = normalizarAngulo(anguloActual);
            final double vueltasExtra = 4 + Math.random() * 2; // 4 a 6 vueltas completas
            final double deltaCorto = normalizarAngulo(anguloFinalBase - anguloInicial);
            final double anguloTotalAGirar = deltaCorto + vueltasExtra * 2 * Math.PI;

            final double anguloOrigen = anguloActual;
            final long duracionMs = 3200L;
            final long inicio = System.currentTimeMillis();

            if (timerGiro != null && timerGiro.isRunning()) {
                timerGiro.stop();
            }

            timerGiro = new Timer(16, null);
            timerGiro.addActionListener(e -> {
                try {
                    long transcurrido = System.currentTimeMillis() - inicio;
                    double t = Math.min(1.0, transcurrido / (double) duracionMs);
                    double suavizado = 1 - Math.pow(1 - t, 3); // ease-out cúbico

                    anguloActual = anguloOrigen + anguloTotalAGirar * suavizado;
                    repaint();

                    if (t >= 1.0) {
                        timerGiro.stop();
                        anguloActual = normalizarAngulo(anguloOrigen + anguloTotalAGirar);
                        girando = false;
                        repaint();
                        if (alTerminar != null) {
                            // Se reporta el índice leído del ángulo final real,
                            // no el índice objetivo original.
                            alTerminar.onFinalizado(calcularIndiceBajoPuntero());
                        }
                    }
                } catch (Exception ex) {
                    try {
                        if (timerGiro != null) timerGiro.stop();
                    } catch (Exception ignore) { /* no-op */ }
                    girando = false;
                    if (alTerminar != null) {
                        alTerminar.onFinalizado(calcularIndiceBajoPuntero());
                    }
                }
            });
            timerGiro.start();

        } catch (Exception e) {
            girando = false;
            if (alTerminar != null) {
                alTerminar.onFinalizado(calcularIndiceBajoPuntero());
            }
        }
    }

    /** Detiene cualquier animación en curso de forma segura. */
    public void detenerAnimacion() {
        try {
            if (timerGiro != null) {
                timerGiro.stop();
            }
        } catch (Exception e) {
            // no-op
        } finally {
            girando = false;
        }
    }

    private double normalizarAngulo(double a) {
        double dosPi = 2 * Math.PI;
        double r = a % dosPi;
        if (r < 0) r += dosPi;
        return r;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        try {
            int w = getWidth();
            int h = getHeight();
            int diametro = Math.max(40, Math.min(w, h) - 36);
            int radio = diametro / 2;
            int cx = w / 2;
            int cy = h / 2 + 8;

            dibujarSectores(g, cx, cy, radio);
            dibujarCentro(g, cx, cy);
            dibujarPuntero(g, cx, cy, radio);
        } catch (Exception e) {
            // Si algo falla al pintar, se evita romper la interfaz gráfica.
            g.setColor(Color.DARK_GRAY);
            g.fillOval(10, 10, 40, 40);
        }
    }

    private void dibujarSectores(Graphics g, int cx, int cy, int radio) {
        Graphics2D g2 = (Graphics2D) g.create();
        try {
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.translate(cx, cy);
            g2.rotate(anguloActual);

            int n = ORDEN_TIPOS.length;
            double anguloSector = 2 * Math.PI / n;
            int pasosArco = 10;

            for (int i = 0; i < n; i++) {
                double a0 = i * anguloSector;
                double a1 = (i + 1) * anguloSector;

                Path2D.Double sector = new Path2D.Double();
                sector.moveTo(0, 0);
                for (int k = 0; k <= pasosArco; k++) {
                    double a = a0 + (a1 - a0) * k / (double) pasosArco;
                    sector.lineTo(Math.cos(a) * radio, Math.sin(a) * radio);
                }
                sector.closePath();

                g2.setColor(COLORES_SECTOR[i % COLORES_SECTOR.length]);
                g2.fill(sector);
                g2.setColor(new Color(210, 170, 110));
                g2.setStroke(new BasicStroke(1.4f));
                g2.draw(sector);

                dibujarContenidoSector(g2, i, a0, anguloSector, radio);
            }
        } catch (Exception e) {
            // no-op: se conserva lo ya pintado
        } finally {
            g2.dispose();
        }
    }

    private void dibujarContenidoSector(Graphics2D g2, int indice, double anguloInicio, double anguloSector, int radio) {
        try {
            double anguloMedio = anguloInicio + anguloSector / 2.0;
            double distancia = radio * 0.62;
            int cx = (int) Math.round(Math.cos(anguloMedio) * distancia);
            int cy = (int) Math.round(Math.sin(anguloMedio) * distancia);
            int tam = Math.max(20, (int) (radio * 0.42));

            Image img = imagenes[indice];
            if (img != null) {
                g2.drawImage(img, cx - tam / 2, cy - tam / 2, tam, tam, this);
            } else {
                // Respaldo: inicial del tipo de pieza, para que nunca quede un sector vacío.
                String letra = ORDEN_TIPOS[indice].isEmpty() ? "?" : ORDEN_TIPOS[indice].substring(0, 1);
                g2.setColor(Color.WHITE);
                g2.setFont(new Font("Georgia", Font.BOLD, Math.max(12, tam / 2)));
                FontMetrics fm = g2.getFontMetrics();
                int tx = cx - fm.stringWidth(letra) / 2;
                int ty = cy + fm.getAscent() / 2 - 2;
                g2.drawString(letra, tx, ty);
            }
        } catch (Exception e) {
            // no-op
        }
    }

    private void dibujarCentro(Graphics g, int cx, int cy) {
        Graphics2D g2 = (Graphics2D) g.create();
        try {
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int r = 16;
            g2.setColor(new Color(15, 10, 15));
            g2.fillOval(cx - r, cy - r, r * 2, r * 2);
            g2.setColor(new Color(210, 170, 110));
            g2.setStroke(new BasicStroke(2f));
            g2.drawOval(cx - r, cy - r, r * 2, r * 2);
        } catch (Exception e) {
            // no-op
        } finally {
            g2.dispose();
        }
    }

    private void dibujarPuntero(Graphics g, int cx, int cy, int radio) {
        Graphics2D g2 = (Graphics2D) g.create();
        try {
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // Borde exterior de la ruleta.
            g2.setColor(new Color(140, 40, 50));
            g2.setStroke(new BasicStroke(3f));
            g2.drawOval(cx - radio, cy - radio, radio * 2, radio * 2);

            // Puntero fijo (no rota junto con la ruleta) apuntando hacia el centro.
            int puntaY = cy - radio + 4;
            Polygon triangulo = new Polygon();
            triangulo.addPoint(cx - 12, puntaY - 20);
            triangulo.addPoint(cx + 12, puntaY - 20);
            triangulo.addPoint(cx, puntaY + 4);

            g2.setColor(new Color(230, 60, 60));
            g2.fillPolygon(triangulo);
            g2.setColor(new Color(20, 15, 20));
            g2.setStroke(new BasicStroke(1.5f));
            g2.drawPolygon(triangulo);
        } catch (Exception e) {
            // no-op
        } finally {
            g2.dispose();
        }
    }
}