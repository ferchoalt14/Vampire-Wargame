/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Game;
/**
 *
 * @author User
 */
public class Werewolf extends Piece {
    public Werewolf(String bando) {
        // Hombre Lobo: Ataque 5, Vida 5, Escudo 2
        super(bando, "Hombre Lobo", 5, 5, 2);
    }
    @Override
    public boolean esMovimientoValido(int origenFila, int origenCol, int destinoFila, int destinoCol, Piece[][] tablero) {
        int difFila = destinoFila - origenFila;
        int difCol = destinoCol - origenCol;
        int distFila = Math.abs(difFila);
        int distCol = Math.abs(difCol);

        if (distFila == 0 && distCol == 0) {
            return false;
        }
        // Movimiento Horizontal (adelante/atrás en fila) de 1 a 2 casillas
        boolean esHorizontal = (distFila == 0 && distCol <= 2);
        // Movimiento Vertical (lados en columna) de 1 a 2 casillas
        boolean esVertical = (distCol == 0 && distFila <= 2);
        // Movimiento Diagonal de 1 a 2 casillas 
        boolean esDiagonal = (distFila == distCol && distFila <= 2);

        if (!(esHorizontal || esVertical || esDiagonal)) {
            return false;
        }


        if (distFila == 2 || distCol == 2) {
            int pasoFila = Integer.compare(difFila, 0);
            int pasoCol = Integer.compare(difCol, 0);

            int filaIntermedia = origenFila + pasoFila;
            int colIntermedia = origenCol + pasoCol;

            if (tablero[filaIntermedia][colIntermedia] != null) {
                return false;
            }
        }

        return true;
    }
}