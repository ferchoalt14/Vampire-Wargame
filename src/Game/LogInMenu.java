/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Game;

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
    private GameSystem brain;

    public LogInMenu() {

        this.setTitle("Vampire Wargame - Menú de Inicio");
        
        // Configuración para pantalla grande e inicio maximizado
        this.setSize(1280, 720);
        this.setMinimumSize(new Dimension(1024, 680));
        this.setExtendedState(JFrame.MAXIMIZED_BOTH); // Maximiza la ventana al iniciar
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setResizable(true);
        this.setLocationRelativeTo(null);

        this.brain = new GameSystem();
        ImageIcon logo = new ImageIcon(getClass().getResource("/Images/logo.png"));
        this.setIconImage(logo.getImage());

        cardLayout = new CardLayout();
        cardsPanel = new JPanel(cardLayout);

        // 1. Creación del panel de botones inicial
        JPanel menuPanel = buildMenuButtonsPanel();

        // Declaramos la variable para usarla en las lambdas
        final MainMenu[] mainMenuRef = new MainMenu[1];

        AccountPanel accountPanel = new AccountPanel(
                brain,
                () -> {
                    if (mainMenuRef[0] != null) {
                        mainMenuRef[0].resetStatus();
                    }
                    this.setTitle("Vampire Wargame - Menú Principal");
                    cardLayout.show(cardsPanel, "MAIN_MENU");
                },
                () -> {
                    this.setTitle("Vampire Wargame - Menú de Inicio");
                    cardLayout.show(cardsPanel, "MENU");
                }
        );

        // Instancia del nuevo panel de reportes
        ReportsPanel reportsPanel = new ReportsPanel(
                brain,
                () -> {
                    this.setTitle("Vampire Wargame - Menú Principal");
                    cardLayout.show(cardsPanel, "MAIN_MENU");
                }
        );

        // Instancia del panel del tablero
        BoardPanel boardPanel = new BoardPanel(
                brain,
                () -> {
                    this.setTitle("Vampire Wargame - Menú Principal");
                    cardLayout.show(cardsPanel, "MAIN_MENU");
                }
        );

        // MainMenu adaptado con sus callbacks
        MainMenu mainMenuPanel = new MainMenu(
                brain,
                () -> {
                    this.setTitle("Vampire Wargame - Menú de Inicio");
                    cardLayout.show(cardsPanel, "MENU");
                },
                () -> {
                    accountPanel.actualizarDatos(brain);
                    this.setTitle("Vampire Wargame - Mi Cuenta");
                    cardLayout.show(cardsPanel, "ACCOUNT");
                },
                () -> {
                    reportsPanel.cargarRanking(brain);
                    this.setTitle("Vampire Wargame - Ranking");
                    cardLayout.show(cardsPanel, "REPORTS");
                },
                (oponente) -> {
                    // Acción que se ejecuta al presionar "Comenzar Batalla"
                    boardPanel.iniciarNuevaPartida(oponente);
                    this.setTitle("Vampire Wargame"); // Título limpio al estar en partida
                    cardLayout.show(cardsPanel, "BOARD");
                    cardsPanel.revalidate();
                    cardsPanel.repaint();
                }
        );

        mainMenuRef[0] = mainMenuPanel;

        LoginPanel loginPanel = new LoginPanel(
                brain,
                () -> {
                    this.setTitle("Vampire Wargame - Menú de Inicio");
                    cardLayout.show(cardsPanel, "MENU");
                },
                () -> {
                    mainMenuPanel.resetStatus();
                    this.setTitle("Vampire Wargame - Menú Principal");
                    cardLayout.show(cardsPanel, "MAIN_MENU");
                }
        );

        RegisterPanel registerPanel = new RegisterPanel(brain, () -> {
            this.setTitle("Vampire Wargame - Menú de Inicio");
            cardLayout.show(cardsPanel, "MENU");
        });

        // 2. Agregar los paneles al CardLayout
        cardsPanel.add(menuPanel, "MENU");
        cardsPanel.add(loginPanel, "LOGIN");
        cardsPanel.add(registerPanel, "REGISTER");
        cardsPanel.add(mainMenuPanel, "MAIN_MENU");
        cardsPanel.add(accountPanel, "ACCOUNT");
        cardsPanel.add(reportsPanel, "REPORTS");
        cardsPanel.add(boardPanel, "BOARD");

        this.setContentPane(cardsPanel);
        this.setVisible(true);
    }

    private JPanel buildMenuButtonsPanel() {
        JPanel buttonPanel = new JPanel(new GridBagLayout()) {
            private final Image fondo = new ImageIcon(getClass().getResource("/Images/MenuPrin.png")).getImage();

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (fondo != null) {
                    g.drawImage(fondo, 0, 0, getWidth(), getHeight(), this);
                }
            }
        };

        JPanel innerBox = new JPanel(new GridLayout(3, 1, 0, 45));
        innerBox.setOpaque(false);

        JButton btnIniciarSesion = createButton("Iniciar sesión");
        JButton btnCrearCuenta = createButton("Crear cuenta");
        JButton btnSalir = createButton("Salir");

        btnIniciarSesion.addActionListener(e -> {
            this.setTitle("Vampire Wargame - Iniciar Sesión");
            cardLayout.show(cardsPanel, "LOGIN");
        });
        btnCrearCuenta.addActionListener(e -> {
            this.setTitle("Vampire Wargame - Registrarse");
            cardLayout.show(cardsPanel, "REGISTER");
        });
        btnSalir.addActionListener(e -> System.exit(0));

        innerBox.add(btnIniciarSesion);
        innerBox.add(btnCrearCuenta);
        innerBox.add(btnSalir);

        buttonPanel.add(innerBox);

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
}