/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Game;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.TitledBorder;

/**
 *
 * @author User
 */
public class RegisterPanel extends JPanel {

    public RegisterPanel(Runnable onBack) {
        setOpaque(false);
        setLayout(new GridBagLayout());

        JPanel formPanel = new JPanel(new GridLayout(6, 1, 12, 12));
        formPanel.setOpaque(false);

        Color fondoCajas = new Color(30, 30, 30);
        Color txtBlanc = new Color(230, 230, 230);
        Color bordeRojo = new Color(150, 40, 50);

        JLabel lblTitle = new JLabel("CREAR CUENTA", SwingConstants.CENTER);
        lblTitle.setForeground(txtBlanc);
        lblTitle.setFont(new Font("Georgia", Font.BOLD, 22));

        //usuario
        JTextField txtUser = new JTextField(15);
        txtUser.setBackground(fondoCajas);
        txtUser.setForeground(txtBlanc);
        txtUser.setCaretColor(Color.WHITE);
        txtUser.setFont(new Font("Georgia", Font.PLAIN, 14));
        txtUser.setBorder(createCustomBorder("Nuevo Usuario", bordeRojo, txtBlanc));

        //contrasena
        JPasswordField txtPass = new JPasswordField(15);
        txtPass.setBackground(fondoCajas);
        txtPass.setForeground(txtBlanc);
        txtPass.setCaretColor(Color.WHITE);
        txtPass.setFont(new Font("Georgia", Font.PLAIN, 14));
        txtPass.setBorder(createCustomBorder("Contraseña", bordeRojo, txtBlanc));
        //confirmarla
        JPasswordField txtConfirmPass = new JPasswordField(15);
        txtConfirmPass.setBackground(fondoCajas);
        txtConfirmPass.setForeground(txtBlanc);
        txtConfirmPass.setCaretColor(Color.WHITE);
        txtConfirmPass.setFont(new Font("Georgia", Font.PLAIN, 14));
        txtConfirmPass.setBorder(createCustomBorder("Confirmar Contraseña", bordeRojo, txtBlanc));

        //botones
        JButton btnRegistrar = LogInMenu.createButton("Ingresar");
        JButton btnVolver = LogInMenu.createButton("Volver al Menú");

        btnVolver.addActionListener(e -> onBack.run());

        formPanel.add(lblTitle);
        formPanel.add(txtUser);
        formPanel.add(txtPass);
        formPanel.add(txtConfirmPass);
        formPanel.add(btnRegistrar);
        formPanel.add(btnVolver);

        add(formPanel);
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
}
