/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Game;

/**
 *
 * @author User
 */
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
}
