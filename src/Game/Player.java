/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Game;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 *
 * @author User
 */
public class Player {
    public String user;
    public String password;
    public int puntos = 0;
    public LocalDateTime fechaIngreso;
    public boolean activo = true;

    public Player(String username, String password) {
        this.user = username;
        this.password = password;
        this.puntos = 0; 
        this.fechaIngreso = LocalDateTime.now(); 
        this.activo = true;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getPuntos() {
        return puntos;
    }

    public void setPuntos(int puntos) {
        this.puntos = puntos;
    }

    public LocalDateTime getFechaIngreso() {
        return fechaIngreso;
    }

    // Método formateador de fecha para la interfaz
    public String getFechaIngresoFormateada() {
        if (fechaIngreso == null) return "-";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        return fechaIngreso.format(formatter);
    }

    public void setFechaIngreso(LocalDateTime fechaIngreso) {
        this.fechaIngreso = fechaIngreso;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    public static boolean esPasswordValido(String pass) {
        return pass != null && pass.length() == 5;
    }

    public void sumarPuntosVictoria() {
        this.puntos += 3;  
    }
}