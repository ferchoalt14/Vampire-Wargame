/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Game;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import javax.swing.*;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Image;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

/**
 *
 * @author User
 */
public class LoginPanel extends JPanel {
private final Image fondo;
    private final JLabel lblMessage;

    public LoginPanel(GameSystem brain, Runnable onBack, Runnable onLoginSucces) {
        this.fondo = new ImageIcon(getClass().getResource("/Images/MenuPrin.png")).getImage();

        setLayout(new GridBagLayout());

        
        JPanel formPanel = new JPanel(new GridLayout(6, 1, 10, 10));
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

        // Label integrado para mostrar los mensajes en pantalla
        lblMessage = new JLabel(" ", SwingConstants.CENTER);
        lblMessage.setFont(new Font("Georgia", Font.BOLD, 12));

        JButton btnIngresar = LogInMenu.createButton("Ingresar");
        JButton btnVolver = LogInMenu.createButton("Volver al Menú");

        btnVolver.addActionListener(e -> {
            txtUser.setText("");
            txtPass.setText("");
            lblMessage.setText(" ");
            if (onBack != null) {
                onBack.run();
            }
        });

        btnIngresar.addActionListener(e -> {
            String user = txtUser.getText().trim();
            String pass = new String(txtPass.getPassword());

            if (user.isEmpty() || pass.isEmpty()) {
                mostrarMensaje("Completa todos los campos.", new Color(255, 100, 100));
                return;
            }

            Player jugador = brain.buscarJugador(user);

            if (jugador == null) {
                mostrarMensaje("El usuario no existe.", new Color(255, 100, 100));
            } else if (!jugador.getPassword().equals(pass)) {
                mostrarMensaje("Contraseña incorrecta.", new Color(255, 100, 100));
            } else {
                brain.setJugadorActivo(jugador);
                mostrarMensaje("¡Bienvenido, " + jugador.getUser() + "!", new Color(100, 255, 100));

                txtUser.setText("");
                txtPass.setText("");

                // Retardo breve para que el usuario pueda ver el mensaje de bienvenida
                Timer timer = new Timer(700, evt -> {
                    lblMessage.setText(" ");
                    if (onLoginSucces != null) {
                        onLoginSucces.run();
                    }
                });
                timer.setRepeats(false);
                timer.start();
            }
        });

        formPanel.add(lblTitle);
        formPanel.add(txtUser);
        formPanel.add(txtPass);
        formPanel.add(lblMessage);
        formPanel.add(btnIngresar);
        formPanel.add(btnVolver);

        add(formPanel);
    }

    private void mostrarMensaje(String texto, Color color) {
        lblMessage.setForeground(color);
        lblMessage.setText(texto);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (fondo != null) {
            g.drawImage(fondo, 0, 0, getWidth(), getHeight(), this);
        }
    }
}