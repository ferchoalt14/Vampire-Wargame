/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Game;

/**
 *
 * @author User
 */
public class Death extends Piece {
    public Death(String bando) {
        // Muerte/Necrómante: Ataque 4, Vida 2, Escudo 1
        super(bando, "Muerte", 4, 2, 1);
    }

    @Override
    public boolean esMovimientoValido(int origenFila, int origenCol, int destinoFila, int destinoCol) {
        int difFila = Math.abs(destinoFila - origenFila);
        int difCol = Math.abs(destinoCol - origenCol);
        
        // La Muerte se desplaza 1 casilla adyacente en cualquier dirección
        return (difFila <= 1 && difCol <= 1) && !(difFila == 0 && difCol == 0);
    }
}
