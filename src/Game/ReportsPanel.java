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
import java.util.ArrayList;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

/**
 * 
 */
public class ReportsPanel extends JPanel {

    private final Image fondo;
    private final JTable rankingTable;
    private final DefaultTableModel tableModel;

    public ReportsPanel(GameSystem brain, Runnable onBack) {
        this.fondo = new ImageIcon(getClass().getResource("/Images/MainHall.png")).getImage();
        setLayout(new GridBagLayout());

        JPanel containerPanel = new JPanel();
        containerPanel.setLayout(new BoxLayout(containerPanel, BoxLayout.Y_AXIS));
        containerPanel.setOpaque(false);

        Color fondoCajas = new Color(25, 25, 25);
        Color txtBlanc = new Color(230, 230, 230);
        Color bordeRojo = new Color(150, 40, 50);

        // Titulo de la pantalla
        JLabel lblTitle = new JLabel("RANKING DE JUGADORES", SwingConstants.CENTER);
        lblTitle.setForeground(txtBlanc);
        lblTitle.setFont(new Font("Georgia", Font.BOLD, 22));
        lblTitle.setAlignmentX(CENTER_ALIGNMENT);

        // Modelo y Tabla
        String[] columnNames = {"Posición", "Usuario", "Puntos"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Hacer celdas no editables
            }
        };

        rankingTable = new JTable(tableModel);
        rankingTable.setFont(new Font("Georgia", Font.PLAIN, 13));
        rankingTable.setForeground(txtBlanc);
        rankingTable.setBackground(fondoCajas);
        rankingTable.setSelectionBackground(bordeRojo);
        rankingTable.setSelectionForeground(Color.WHITE);
        rankingTable.setRowHeight(28);

        // Estilo para el encabezado de la tabla
        rankingTable.getTableHeader().setFont(new Font("Georgia", Font.BOLD, 14));
        rankingTable.getTableHeader().setBackground(new Color(40, 15, 20));
        rankingTable.getTableHeader().setForeground(txtBlanc);
        rankingTable.getTableHeader().setReorderingAllowed(false);

        // Centrar texto en las celdas
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        centerRenderer.setBackground(fondoCajas);
        centerRenderer.setForeground(txtBlanc);
        for (int i = 0; i < rankingTable.getColumnCount(); i++) {
            rankingTable.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        // ScrollPane para la tabla 
        JScrollPane scrollPane = new JScrollPane(rankingTable);
        scrollPane.setPreferredSize(new Dimension(380, 220));
        scrollPane.setMaximumSize(new Dimension(380, 220));
        scrollPane.getViewport().setBackground(fondoCajas);
        scrollPane.setBorder(BorderFactory.createLineBorder(bordeRojo, 2));
        scrollPane.setAlignmentX(CENTER_ALIGNMENT);

        // Btn Volver
        JButton btnVolver = LogInMenu.createButton("Volver");
        Dimension dimBtn = new Dimension(220, 40);
        btnVolver.setPreferredSize(dimBtn);
        btnVolver.setMaximumSize(dimBtn);
        btnVolver.setMinimumSize(dimBtn);
        btnVolver.setAlignmentX(CENTER_ALIGNMENT);

        btnVolver.addActionListener(e -> {
            if (onBack != null) {
                onBack.run();
            }
        });

        // Ensamble de componentes
        containerPanel.add(lblTitle);
        containerPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        containerPanel.add(scrollPane);
        containerPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        containerPanel.add(btnVolver);

        add(containerPanel);
    }

    // Carga los datos usando el método recursivo de GameSystem
    public void cargarRanking(GameSystem brain) {
        tableModel.setRowCount(0); // Limpiar filas
        ArrayList<Player> ranking = brain.getRanking();

        int pos = 1;
        for (Player p : ranking) {
            tableModel.addRow(new Object[]{pos + "°", p.getUser(), p.getPuntos()});
            pos++;
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (fondo != null) {
            g.drawImage(fondo, 0, 0, getWidth(), getHeight(), this);
        }
    }
}