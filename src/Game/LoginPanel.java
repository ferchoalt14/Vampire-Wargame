/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Game;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import javax.swing.*;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.MouseEvent;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;
import java.awt.event.MouseAdapter;

/**
 *
 * @author User
 */
public class LoginPanel extends JPanel {

    public LoginPanel(GameSystem brain, Runnable onBack) {
        setOpaque(false);
        setLayout(new GridBagLayout());

        JPanel formPanel = new JPanel(new GridLayout(5, 1, 10, 10));
        formPanel.setOpaque(false);

        Color fondoCajas = new Color(30, 30, 30);
        Color txtBlanc = new Color(230, 230, 230);
        Color bordeRojo = new Color(150, 40, 50);

        JLabel lblTitle = new JLabel("INICIAR SESION", SwingConstants.CENTER);
        lblTitle.setForeground(txtBlanc);
        lblTitle.setFont(new Font("Georgia", Font.BOLD, 18));

        JTextField txtUser = new JTextField(15);
        txtUser.setBackground(fondoCajas);
        txtUser.setForeground(txtBlanc);
        txtUser.setCaretColor(Color.WHITE);
        txtUser.setFont(new Font("Georgia", Font.PLAIN, 14));

        txtUser.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(bordeRojo, 2),
                "Usuario",
                TitledBorder.LEFT,
                TitledBorder.TOP,
                new Font("Georgia", Font.BOLD, 12),
                txtBlanc
        ));

        // 3. Caja de texto para la Contraseña
        JPasswordField txtPass = new JPasswordField(15);
        txtPass.setBackground(fondoCajas);
        txtPass.setForeground(txtBlanc);
        txtPass.setCaretColor(Color.WHITE);
        txtPass.setFont(new Font("Georgia", Font.PLAIN, 14));

        txtPass.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(bordeRojo, 2),
                "Contraseña",
                TitledBorder.LEFT,
                TitledBorder.TOP,
                new Font("Georgia", Font.BOLD, 12),
                txtBlanc
        ));

        JButton btnIngresar = LogInMenu.createButton("Ingresar");
        JButton btnVolver = LogInMenu.createButton("Volver al Menú");

        btnVolver.addActionListener(e -> {

            txtUser.setText("");
            txtPass.setText("");

            if (onBack != null) {
                onBack.run();
            }
        });

        formPanel.add(lblTitle);
        formPanel.add(txtUser);
        formPanel.add(txtPass);
        formPanel.add(btnIngresar);
        formPanel.add(btnVolver);

        add(formPanel);

        btnIngresar.addActionListener(e -> {
            String user = txtUser.getText();
            String pass = new String(txtPass.getPassword());

            Player jugador = brain.buscarJugador(user);

            if (jugador == null) {
                JOptionPane.showMessageDialog(this, "El usuario no existe.", "Error", JOptionPane.ERROR_MESSAGE);
            } else if (!jugador.getPassword().equals(pass)) {
                JOptionPane.showMessageDialog(this, "Contraseña incorrecta.", "Error", JOptionPane.ERROR_MESSAGE);
            } else {
                // Login correcto
                brain.setJugadorActivo(jugador);
                JOptionPane.showMessageDialog(this, "¡Bienvenido de nuevo, " + jugador.getUser() + "!", "Sesión Iniciada", JOptionPane.INFORMATION_MESSAGE);
                // Aquí podrías redirigir al menú principal del juego
            }
        });

        btnVolver.addActionListener(e -> {
            if (onBack != null) {
                onBack.run();
            }
        });

        formPanel.add(lblTitle);
        formPanel.add(txtUser);
        formPanel.add(txtPass);
        formPanel.add(btnIngresar);
        formPanel.add(btnVolver);
    }

}
