/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Game;

import java.text.SimpleDateFormat;
import java.util.Date;

public class GameMatch {
    private final String ganador;
    private final String perdedor;
    private final String causa; 
    private final Date fecha;

    public GameMatch(String ganador, String perdedor, String causa) {
        this.ganador = ganador != null ? ganador : "Desconocido";
        this.perdedor = perdedor != null ? perdedor : "Desconocido";
        this.causa = causa != null ? causa : "Fin de partida";
        this.fecha = new Date();
    }

    public String getGanador() { return ganador; }
    public String getPerdedor() { return perdedor; }
    public String getCausa() { return causa; }

    public String getFechaFormateada() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        return sdf.format(fecha);
    }
}