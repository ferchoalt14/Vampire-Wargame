/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Game;

/**
 *
 * @author User
 */
public final class Zombie extends Piece{
    public Zombie(String bando) {
        // Zombie: Ataque 0, Vida 1, Escudo 0
        super(bando, "Zombie", 0, 1, 0);
    }

    @Override
    public boolean esMovimientoValido(int origenFila, int origenCol, int destinoFila, int destinoCol) {
        // El Zombie no se puede mover por sí solo en el tablero
        return false;
    }
}
