/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Game;

import javax.swing.ImageIcon;
import java.awt.Image;

public class Zombie extends Piece {

    public Zombie(String bando) {
        super(bando, "Zombie", 1, 1, 0);
    }

    protected void cargarIcono() {
        try {
            String sufijo = bando.equalsIgnoreCase("BLANCO") ? "B" : "N";
            String path = "/Images/zombie" + sufijo + ".png";
            java.net.URL imgURL = getClass().getResource(path);
            if (imgURL != null) {
                ImageIcon raw = new ImageIcon(imgURL);
                Image img = raw.getImage().getScaledInstance(75, 75, Image.SCALE_SMOOTH);
                this.icon = new ImageIcon(img);
            }
        } catch (Exception e) {
            this.icon = null;
        }
    }

  @Override
    public boolean esMovimientoValido(int fOri, int cOri, int fDes, int cDes) {
        int dFila = Math.abs(fDes - fOri);
        int dCol = Math.abs(cDes - cOri);
        // El zombie se mueve 1 casilla en cualquier dirección
        return (dFila <= 1 && dCol <= 1) && !(dFila == 0 && dCol == 0);
    }
}