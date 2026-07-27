/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Game;

import java.awt.Button;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 *
 * @author User
 */
public class LogInMenu extends JFrame {

    private CardLayout cardLayout;
    private JPanel cardsPanel;

    public LogInMenu() {

        this.setTitle("Vampire Wargame - Menú de Inicio");
        this.setSize(500, 500);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setResizable(false);
        this.setLocationRelativeTo(null);

        ImageIcon logo = new ImageIcon(getClass().getResource("/Images/logo.png"));
        this.setIconImage(logo.getImage());

        
        cardLayout = new CardLayout();
        cardsPanel = new JPanel(cardLayout);
        cardsPanel.setOpaque(false);

       //Creacion de paneles
        JPanel menuPanel = buildMenuButtonsPanel();
        LoginPanel loginPanel = new LoginPanel(() -> cardLayout.show(cardsPanel, "MENU"));
        RegisterPanel registerPanel = new RegisterPanel(() -> cardLayout.show(cardsPanel, "MENU")); 
   
        JPanel registerPlaceholder = new JPanel(); 
        registerPlaceholder.setOpaque(false);

        // 3. Registramos cada pantalla 
        cardsPanel.add(menuPanel, "MENU");
        cardsPanel.add(loginPanel, "LOGIN");
        cardsPanel.add(registerPanel, "REGISTER");

        // 4. Ponemos todo dentro del fondo
        BackgroundPanel mainBackground = new BackgroundPanel();
        mainBackground.setLayout(new GridBagLayout());
        mainBackground.add(cardsPanel);

        this.setContentPane(mainBackground);
        this.setVisible(true);
    }

    // Método para armar el panel de botones del menú principal
    private JPanel buildMenuButtonsPanel() {
        JPanel buttonPanel = new JPanel();
        buttonPanel.setOpaque(false);
        buttonPanel.setLayout(new GridLayout(3, 1, 0, 15));

        JButton btnIniciarSesion = createButton("Iniciar sesión");
        JButton btnCrearCuenta = createButton("Crear cuenta");
        JButton btnSalir = createButton("Salir");

        // Cambiar de pantalla al hacer clic
        btnIniciarSesion.addActionListener(e -> cardLayout.show(cardsPanel, "LOGIN"));
        btnCrearCuenta.addActionListener(e -> cardLayout.show(cardsPanel, "REGISTER"));
        btnSalir.addActionListener(e -> System.exit(0));

        buttonPanel.add(btnIniciarSesion);
        buttonPanel.add(btnCrearCuenta);
        buttonPanel.add(btnSalir);

        return buttonPanel;
    }

    public static JButton createButton(String text) {
        JButton button = new JButton(text);
        button.setPreferredSize(new Dimension(200, 45));
        
        Color colorNormal = new Color(40, 15, 20);
        Color colorHover = new Color(110, 20, 30);
        Color colorTexto = new Color(230, 220, 220);
        
        button.setFont(new Font("Georgia", Font.BOLD, 15));
        button.setBackground(colorNormal);
        button.setForeground(colorTexto);
        button.setFocusable(false);
        button.setBorder(BorderFactory.createLineBorder(new Color(150, 40, 50), 2));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(colorHover);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(colorNormal);
            }
        });

        return button;
    }

    private class BackgroundPanel extends JPanel {
        private final Image fondo;

        public BackgroundPanel() {
            ImageIcon image = new ImageIcon(getClass().getResource("/Images/MenuPrin.png"));
            this.fondo = image.getImage();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (fondo != null) {
                g.drawImage(fondo, 0, 0, getWidth(), getHeight(), this);
            }
        }
    }
}