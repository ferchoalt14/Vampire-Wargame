/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Game;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridBagLayout;
import java.awt.Image;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.SwingConstants;
import javax.swing.Timer;
import javax.swing.border.TitledBorder;

public class AccountPanel extends JPanel {

    private final Image fondo;
    private final JLabel lblUserVal;
    private final JLabel lblPuntosVal;
    private final JLabel lblFechaVal; // Nuevo Label para la Fecha de Ingreso
    private final JLabel lblMessage;

    public AccountPanel(GameSystem brain, Runnable onBack, Runnable onAccountDeleted) {
        this.fondo = new ImageIcon(getClass().getResource("/Images/MainHall.png")).getImage();
        setLayout(new GridBagLayout());

        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setOpaque(false);

        Color fondoCajas = new Color(30, 30, 30);
        Color txtBlanc = new Color(230, 230, 230);
        Color bordeRojo = new Color(150, 40, 50);

        Dimension elemSize = new Dimension(220, 40);

        JLabel lblTitle = new JLabel("MI CUENTA", SwingConstants.CENTER);
        lblTitle.setForeground(txtBlanc);
        lblTitle.setFont(new Font("Georgia", Font.BOLD, 22));
        lblTitle.setAlignmentX(CENTER_ALIGNMENT);

        lblUserVal = new JLabel("Usuario: -", SwingConstants.CENTER);
        lblUserVal.setForeground(txtBlanc);
        lblUserVal.setFont(new Font("Georgia", Font.BOLD, 15));
        lblUserVal.setAlignmentX(CENTER_ALIGNMENT);

        lblPuntosVal = new JLabel("Puntos: 0", SwingConstants.CENTER);
        lblPuntosVal.setForeground(new Color(180, 180, 180));
        lblPuntosVal.setFont(new Font("Georgia", Font.PLAIN, 13));
        lblPuntosVal.setAlignmentX(CENTER_ALIGNMENT);

        // Label para desplegar la fecha formateada
        lblFechaVal = new JLabel("Ingreso: -", SwingConstants.CENTER);
        lblFechaVal.setForeground(new Color(180, 180, 180));
        lblFechaVal.setFont(new Font("Georgia", Font.ITALIC, 11));
        lblFechaVal.setAlignmentX(CENTER_ALIGNMENT);

        JPasswordField txtPassActual = new JPasswordField(15);
        txtPassActual.setBackground(fondoCajas);
        txtPassActual.setForeground(txtBlanc);
        txtPassActual.setCaretColor(Color.WHITE);
        setUniformSize(txtPassActual, elemSize);
        txtPassActual.setBorder(createCustomBorder("Contraseña Actual", bordeRojo, txtBlanc));

        JPasswordField txtPassNueva = new JPasswordField(15);
        txtPassNueva.setBackground(fondoCajas);
        txtPassNueva.setForeground(txtBlanc);
        txtPassNueva.setCaretColor(Color.WHITE);
        setUniformSize(txtPassNueva, elemSize);
        txtPassNueva.setBorder(createCustomBorder("Nueva Contraseña (5 chars)", bordeRojo, txtBlanc));

        lblMessage = new JLabel(" ", SwingConstants.CENTER);
        lblMessage.setFont(new Font("Georgia", Font.BOLD, 12));
        lblMessage.setAlignmentX(CENTER_ALIGNMENT);

        JButton btnCambiarPass = LogInMenu.createButton("Cambiar Contraseña");
        JButton btnEliminarCuenta = LogInMenu.createButton("Eliminar Cuenta");
        JButton btnVolver = LogInMenu.createButton("Volver");

        setUniformSize(btnCambiarPass, elemSize);
        setUniformSize(btnEliminarCuenta, elemSize);
        setUniformSize(btnVolver, elemSize);

        btnCambiarPass.addActionListener(e -> {
            String act = new String(txtPassActual.getPassword());
            String nue = new String(txtPassNueva.getPassword());

            String res = brain.cambiarPassword(act, nue);
            if (res != null) {
                mostrarMensaje(res, new Color(255, 100, 100));
            } else {
                mostrarMensaje("Contraseña actualizada.", new Color(100, 255, 100));
                txtPassActual.setText("");
                txtPassNueva.setText("");
            }
        });

        btnEliminarCuenta.addActionListener(e -> {
            if (brain.eliminarCuentaActiva()) {
                mostrarMensaje("Cuenta eliminada.", new Color(255, 100, 100));
                Timer t = new Timer(800, evt -> {
                    if (onAccountDeleted != null) onAccountDeleted.run();
                });
                t.setRepeats(false);
                t.start();
            }
        });

        btnVolver.addActionListener(e -> {
            txtPassActual.setText("");
            txtPassNueva.setText("");
            lblMessage.setText(" ");
            if (onBack != null) onBack.run();
        });

        formPanel.add(lblTitle);
        formPanel.add(Box.createRigidArea(new Dimension(0, 6)));
        formPanel.add(lblUserVal);
        formPanel.add(lblPuntosVal);
        formPanel.add(lblFechaVal); // Añadido a la interfaz
        formPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        formPanel.add(txtPassActual);
        formPanel.add(Box.createRigidArea(new Dimension(0, 6)));
        formPanel.add(txtPassNueva);
        formPanel.add(Box.createRigidArea(new Dimension(0, 6)));
        formPanel.add(lblMessage);
        formPanel.add(Box.createRigidArea(new Dimension(0, 6)));
        formPanel.add(btnCambiarPass);
        formPanel.add(Box.createRigidArea(new Dimension(0, 6)));
        formPanel.add(btnEliminarCuenta);
        formPanel.add(Box.createRigidArea(new Dimension(0, 6)));
        formPanel.add(btnVolver);

        add(formPanel);
    }

    private void setUniformSize(javax.swing.JComponent component, Dimension dim) {
        component.setPreferredSize(dim);
        component.setMaximumSize(dim);
        component.setMinimumSize(dim);
        component.setAlignmentX(CENTER_ALIGNMENT);
    }

    public void actualizarDatos(GameSystem brain) {
        if (brain.getJugadorActivo() != null) {
            lblUserVal.setText("Usuario: " + brain.getJugadorActivo().getUser());
            lblPuntosVal.setText("Puntos: " + brain.getJugadorActivo().getPuntos());
            lblFechaVal.setText("Ingreso: " + brain.getJugadorActivo().getFechaIngresoFormateada());
        }
        lblMessage.setText(" ");
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
                new Font("Georgia", Font.BOLD, 10),
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