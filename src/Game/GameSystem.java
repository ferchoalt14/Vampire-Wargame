/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Game;

import java.util.ArrayList;

/**
 *
 * @author Fernando Altamirano
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
            return "El usuario no puede estar vacío";
        }
        if (buscarJugador(user) != null) {
            return "Nombre de usuario ya existente";
        }
        if (!Player.esPasswordValido(password)) {
            return "La contraseña debe tener 5 caracteres";
        }

        Player nuevo = new Player(user, password);
        players.add(nuevo);
        this.playerActivo = nuevo;
        return null;
    }

    public void cerrarSesion() {
        this.playerActivo = null;
    }

    // Cambiar contraseña del jugador activo
    public String cambiarPassword(String passActual, String nuevaPass) {
        if (playerActivo == null) return "No hay sesión activa";
        if (!playerActivo.getPassword().equals(passActual)) {
            return "La contraseña actual es incorrecta";
        }
        if (!Player.esPasswordValido(nuevaPass)) {
            return "La nueva contraseña debe tener 5 caracteres";
        }
        playerActivo.setPassword(nuevaPass);
        return null; 
    }

    // Eliminar la cuenta activa
    public boolean eliminarCuentaActiva() {
        if (playerActivo != null) {
            players.remove(playerActivo);
            playerActivo = null;
            return true;
        }
        return false;
    }

    public ArrayList<Player> getOponentesDisponibles() {
        ArrayList<Player> oponentes = new ArrayList<>();
        for (Player p : players) {
            if (playerActivo != null && !p.getUser().equalsIgnoreCase(playerActivo.getUser())) {
                oponentes.add(p);
            }
        }
        return oponentes;
    }

    // Recursividad para el Ranking
    public ArrayList<Player> getRanking() {
        ArrayList<Player> listaOrdenada = new ArrayList<>(players);
        if (!listaOrdenada.isEmpty()) {
            ordenarRankingRecursivo(listaOrdenada, listaOrdenada.size());
        }
        return listaOrdenada;
    }

    private void ordenarRankingRecursivo(ArrayList<Player> lista, int n) {
        if (n == 1) {
            return;
        }

        for (int i = 0; i < n - 1; i++) {
            if (lista.get(i).getPuntos() < lista.get(i + 1).getPuntos()) {
                Player temp = lista.get(i);
                lista.set(i, lista.get(i + 1));
                lista.set(i + 1, temp);
            }
        }

        ordenarRankingRecursivo(lista, n - 1);
    }
}