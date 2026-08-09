/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Game;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Image;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.Timer;
import javax.swing.border.TitledBorder;

/**
 *
 * @author User
 */
public class RegisterPanel extends JPanel {

    private final Image fondo;
    private final JLabel lblMessage;

    public RegisterPanel(GameSystem brain, Runnable onBack) {
        this.fondo = new ImageIcon(getClass().getResource("/Images/MenuPrin.png")).getImage();

        setLayout(new GridBagLayout());

     
        JPanel formPanel = new JPanel(new GridLayout(7, 1, 10, 10));
        formPanel.setOpaque(false);

        Color fondoCajas = new Color(30, 30, 30);
        Color txtBlanc = new Color(230, 230, 230);
        Color bordeRojo = new Color(150, 40, 50);

        JLabel lblTitle = new JLabel("CREAR CUENTA", SwingConstants.CENTER);
        lblTitle.setForeground(txtBlanc);
        lblTitle.setFont(new Font("Georgia", Font.BOLD, 22));

        JTextField txtUser = new JTextField(15);
        txtUser.setBackground(fondoCajas);
        txtUser.setForeground(txtBlanc);
        txtUser.setCaretColor(Color.WHITE);
        txtUser.setFont(new Font("Georgia", Font.PLAIN, 14));
        txtUser.setBorder(createCustomBorder("Nuevo Usuario", bordeRojo, txtBlanc));

        JPasswordField txtPass = new JPasswordField(15);
        txtPass.setBackground(fondoCajas);
        txtPass.setForeground(txtBlanc);
        txtPass.setCaretColor(Color.WHITE);
        txtPass.setFont(new Font("Georgia", Font.PLAIN, 14));
        txtPass.setBorder(createCustomBorder("Contraseña", bordeRojo, txtBlanc));

        JPasswordField txtConfirmPass = new JPasswordField(15);
        txtConfirmPass.setBackground(fondoCajas);
        txtConfirmPass.setForeground(txtBlanc);
        txtConfirmPass.setCaretColor(Color.WHITE);
        txtConfirmPass.setFont(new Font("Georgia", Font.PLAIN, 14));
        txtConfirmPass.setBorder(createCustomBorder("Confirmar Contraseña", bordeRojo, txtBlanc));

        // Label para mostrar errores o confirmaciones directamente en la pantalla
        lblMessage = new JLabel(" ", SwingConstants.CENTER);
        lblMessage.setFont(new Font("Georgia", Font.BOLD, 12));

        JButton btnRegistrar = LogInMenu.createButton("Ingresar");
        JButton btnVolver = LogInMenu.createButton("Volver al Menú");

        btnVolver.addActionListener(e -> {
            txtUser.setText("");
            txtPass.setText("");
            txtConfirmPass.setText("");
            lblMessage.setText(" ");

            if (onBack != null) {
                onBack.run();
            }
        });

        btnRegistrar.addActionListener(e -> {
            String user = txtUser.getText();
            String pass = new String(txtPass.getPassword());
            String confirmPass = new String(txtConfirmPass.getPassword());

            if (!pass.equals(confirmPass)) {
                mostrarMensaje("Las contraseñas no coinciden.", new Color(255, 100, 100));
                return;
            }

            String resultado = brain.crearPlayer(user, pass);

            if (resultado != null) {
                mostrarMensaje(resultado, new Color(255, 100, 100));
            } else {
                mostrarMensaje("¡Cuenta creada con éxito!", new Color(100, 255, 100));
                txtUser.setText("");
                txtPass.setText("");
                txtConfirmPass.setText("");

                // Retardo breve para visualizar la confirmación antes de volver
                Timer timer = new Timer(800, evt -> {
                    lblMessage.setText(" ");
                    if (onBack != null) {
                        onBack.run();
                    }
                });
                timer.setRepeats(false);
                timer.start();
            }
        });

        formPanel.add(lblTitle);
        formPanel.add(txtUser);
        formPanel.add(txtPass);
        formPanel.add(txtConfirmPass);
        formPanel.add(lblMessage);
        formPanel.add(btnRegistrar);
        formPanel.add(btnVolver);

        add(formPanel);
    }

    private void mostrarMensaje(String texto, Color color) {
        lblMessage.setForeground(color);
        lblMessage.setText(texto);
    }

    private TitledBorder createCustomBorder(String title, Color borderColor, Color textColor) {
        return BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(borderColor, 2),
                title,
                TitledBorder.LEFT,
                TitledBorder.TOP,
                new Font("Georgia", Font.BOLD, 12),
                textColor
        );
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (fondo != null) {
            g.drawImage(fondo, 0, 0, getWidth(), getHeight(), this);
        }
    }
}