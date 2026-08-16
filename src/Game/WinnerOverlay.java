/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Game;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagLayout;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

public class WinnerOverlay extends JPanel {

    public WinnerOverlay(String ganador, String causa, Runnable onContinuar) {
        setLayout(new GridBagLayout());
        setOpaque(false); // Permite ver el fondo o el tablero semitransparente

        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(new Color(20, 20, 20, 235));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(255, 215, 0), 3),
                BorderFactory.createEmptyBorder(20, 30, 20, 30)
        ));

        JLabel lblVictoria = new JLabel("¡VICTORIA!", SwingConstants.CENTER);
        lblVictoria.setFont(new Font("Georgia", Font.BOLD, 26));
        lblVictoria.setForeground(new Color(255, 215, 0));
        lblVictoria.setAlignmentX(CENTER_ALIGNMENT);

        JLabel lblGanador = new JLabel("Ganador: " + (ganador != null ? ganador : "Jugador"), SwingConstants.CENTER);
        lblGanador.setFont(new Font("Georgia", Font.BOLD, 18));
        lblGanador.setForeground(Color.WHITE);
        lblGanador.setAlignmentX(CENTER_ALIGNMENT);

        JLabel lblCausa = new JLabel(causa != null ? causa : "Partida Finalizada", SwingConstants.CENTER);
        lblCausa.setFont(new Font("Georgia", Font.ITALIC, 13));
        lblCausa.setForeground(new Color(200, 200, 200));
        lblCausa.setAlignmentX(CENTER_ALIGNMENT);

        JLabel lblPuntos = new JLabel("+3 Puntos otorgados", SwingConstants.CENTER);
        lblPuntos.setFont(new Font("Georgia", Font.BOLD, 14));
        lblPuntos.setForeground(new Color(100, 255, 100));
        lblPuntos.setAlignmentX(CENTER_ALIGNMENT);

        JButton btnContinuar = LogInMenu.createButton("Volver al Menú");
        Dimension dimBtn = new Dimension(200, 40);
        btnContinuar.setPreferredSize(dimBtn);
        btnContinuar.setMaximumSize(dimBtn);
        btnContinuar.setAlignmentX(CENTER_ALIGNMENT);

        btnContinuar.addActionListener(e -> {
            if (onContinuar != null) {
                onContinuar.run();
            }
        });

        card.add(lblVictoria);
        card.add(Box.createRigidArea(new Dimension(0, 10)));
        card.add(lblGanador);
        card.add(Box.createRigidArea(new Dimension(0, 5)));
        card.add(lblCausa);
        card.add(Box.createRigidArea(new Dimension(0, 10)));
        card.add(lblPuntos);
        card.add(Box.createRigidArea(new Dimension(0, 15)));
        card.add(btnContinuar);

        add(card);
    }
}