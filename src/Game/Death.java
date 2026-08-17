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
        // Muerte Ataque 4, Vida 2, Escudo 1
        super(bando, "Muerte", 4, 2, 1);
    }
    @Override
    public boolean esMovimientoValido(int origenFila, int origenCol, int destinoFila, int destinoCol, Piece[][] tablero) {
        int difFila = Math.abs(destinoFila - origenFila);
        int difCol = Math.abs(destinoCol - origenCol);
        
        
        return (difFila <= 1 && difCol <= 1) && !(difFila == 0 && difCol == 0);
    }
}
