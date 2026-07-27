/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Game;

import java.util.ArrayList;

/**
 *
 * @author User
 */
public class GameSystem {

    private ArrayList<Player> players;
    private Player playerActivo;

    public GameSystem() {
        this.players = new ArrayList<>();
        this.playerActivo = null;
    }

    public ArrayList<Player> getJugadores() {
        return players;
    }

    public Player getJugadorActivo() {
        return playerActivo;
    }

    public void setJugadorActivo(Player jugador) {
        this.playerActivo = jugador;
    }

    public Player buscarJugador(String user) {
        for (Player j : players) {
            if (j.getUser().equalsIgnoreCase(user)) {
                return j;
            }
        }
        return null;
    }

    public String crearPlayer(String user, String password) {
        if (user == null || user.trim().isEmpty()) {
            return "El nombre de usuario no puede estar vacio ";
        }
        if (buscarJugador(user) != null) {
            return "Nombre de usuario ya existente";
        }
        if (password == null || password.length() != 5) {
            return "La contrasenia debe tener exactamente 5 caracteres";
        }

        Player nuevo = new Player(user, password);
        players.add(nuevo);
        this.playerActivo = nuevo;
        this.playerActivo = nuevo;
        return null;
    }

    public void cerrarSesion() {
        this.playerActivo = null;
    }
}
