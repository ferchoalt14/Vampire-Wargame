/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Game;

/**
 *
 * @author User
 */
public class Werewolf extends Piece{
    public Werewolf(String bando) {
        // Hombre Lobo: Ataque 5, Vida 5, Escudo 2
        super(bando, "Hombre Lobo", 5, 5, 2);
    }

    @Override
    public boolean esMovimientoValido(int origenFila, int origenCol, int destinoFila, int destinoCol) {
        int difFila = Math.abs(destinoFila - origenFila);
        int difCol = Math.abs(destinoCol - origenCol);
        
        // El Hombre Lobo puede desplazarse de 1 a 2 casillas vacías en cualquier dirección
        return (difFila <= 2 && difCol <= 2) && !(difFila == 0 && difCol == 0);
    }
}

