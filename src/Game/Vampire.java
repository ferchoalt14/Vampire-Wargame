/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Game;

public class Vampire extends Piece {
    public Vampire(String bando) {
        // Vampiro: Ataque 3, Vida 4, Escudo 5
        super(bando, "Vampiro", 3, 4, 5);
    }

    @Override
    public boolean esMovimientoValido(int origenFila, int origenCol, int destinoFila, int destinoCol) {
        int difFila = Math.abs(destinoFila - origenFila);
        int difCol = Math.abs(destinoCol - origenCol);
        
        // El Vampiro se desplaza 1 casilla adyacente en cualquier dirección
        return (difFila <= 1 && difCol <= 1) && !(difFila == 0 && difCol == 0);
    }

    public void chuparSangre(Piece enemigo) {
        if (enemigo != null && enemigo.estaViva()) {
            // Le resta 1 punto de vida directo al rival y cura 1 al Vampiro
            enemigo.recibirDano(1, true); 
            this.curarVida(1);
        }
    }
}
