/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Game;

import java.awt.Image;
import java.net.URL;
import javax.swing.ImageIcon;

public abstract class Piece {

    protected String bando; // "BLANCO" o "NEGRO"
    protected String tipo;  // "Vampiro", "Hombre Lobo", "Muerte", "Zombie"
    protected int ataque;
    protected int vida;
    protected int vidaMaxima; // Límite máximo de vida para evitar sobrecuraciones
    protected int escudo;
    protected ImageIcon icon;

    public Piece(String bando, String tipo, int ataque, int vida, int escudo) {
        this.bando = bando;
        this.tipo = tipo;
        this.ataque = ataque;
        this.vida = vida;
        this.vidaMaxima = vida; // Se inicializa la vida máxima con el valor inicial
        this.escudo = escudo;
        this.icon = cargarIcono();
    }

    private ImageIcon cargarIcono() {
        try {
            String prefijo = "";
            switch (tipo) {
                case "Hombre Lobo":
                    prefijo = "lobo";
                    break;
                case "Vampiro":
                    prefijo = "vampiro";
                    break;
                case "Muerte":
                    prefijo = "muerte";
                    break;
                case "Zombie":
                    prefijo = "zombie";
                    break;
            }

            String sufijo = bando.equalsIgnoreCase("BLANCO") ? "B" : "N";
            String path = "/Images/" + prefijo + sufijo + ".png";

            URL imgURL = getClass().getResource(path);
            if (imgURL != null) {
                ImageIcon rawIcon = new ImageIcon(imgURL);
                Image img = rawIcon.getImage().getScaledInstance(85, 85, Image.SCALE_SMOOTH);
                return new ImageIcon(img);
            } else {
                System.err.println("No se encontro la imagen en la ruta: " + path);
                return null;
            }
            
        } catch (Exception e) {
            System.err.println("Error al cargar la imagen de la pieza: " + e.getMessage());
            return null;
        }
    }

    // Funciones finales 
    public final ImageIcon getIcon() {
        return icon;
    }

    public final String getBando() {
        return bando;
    }

    public final String getTipo() {
        return tipo;
    }

    public final int getAtaque() {
        return ataque;
    }

    public final int getVida() {
        return vida;
    }

    public final int getVidaMaxima() {
        return vidaMaxima;
    }

    public final int getEscudo() {
        return escudo;
    }

    public final boolean estaViva() {
        return this.vida > 0;
    }

    // Setters para modificaciones temporales o permanentes de estadísticas
    public void setAtaque(int ataque) {
        this.ataque = ataque;
    }

    public void setEscudo(int escudo) {
        this.escudo = escudo;
    }

    // Aplicación de daño 
    public void recibirDano(int dano, boolean ignorarEscudo) {
        if (ignorarEscudo) {
            this.vida -= dano;
        } else {
            if (this.escudo >= dano) {
                this.escudo -= dano;
            } else {
                int danoRestante = dano - this.escudo;
                this.escudo = 0;
                this.vida -= danoRestante;
            }
        }
        if (this.vida < 0) {
            this.vida = 0;
        }
    }

    // Curación controlada respetando el tope de vida máxima
    public void curarVida(int puntos) {
        this.vida += puntos;
        if (this.vida > this.vidaMaxima) {
            this.vida = this.vidaMaxima;
        }
    }

    // Método para activar o ejecutar habilidades especiales según la pieza
    public void usarHabilidadEspecial(Piece objetivo) {
        // Implementación base vacía o personalizable en las subclases
    }

    // Método abstracto 
    public abstract boolean esMovimientoValido(int origenFila, int origenCol, int destinoFila, int destinoCol);
}