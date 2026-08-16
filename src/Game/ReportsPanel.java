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
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

public class ReportsPanel extends JPanel {

    private final Image fondo;
    private final DefaultTableModel rankingModel;
    private final DefaultTableModel historialModel;

    public ReportsPanel(GameSystem brain, Runnable onBack) {
        this.fondo = new ImageIcon(getClass().getResource("/Images/MainHall.png")).getImage();
        setLayout(new GridBagLayout());

        JPanel containerPanel = new JPanel();
        containerPanel.setLayout(new BoxLayout(containerPanel, BoxLayout.Y_AXIS));
        containerPanel.setOpaque(false);

        Color fondoCajas = new Color(25, 25, 25);
        Color txtBlanc = new Color(230, 230, 230);
        Color bordeRojo = new Color(150, 40, 50);

        JLabel lblTitle = new JLabel("REPORTES Y ESTADÍSTICAS", SwingConstants.CENTER);
        lblTitle.setForeground(txtBlanc);
        lblTitle.setFont(new Font("Georgia", Font.BOLD, 22));
        lblTitle.setAlignmentX(CENTER_ALIGNMENT);

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Georgia", Font.BOLD, 12));

        // TAB 1: RANKING
        String[] rankingCols = {"Posición", "Usuario", "Puntos"};
        rankingModel = createNonEditableModel(rankingCols);
        JTable rankingTable = createStyledTable(rankingModel, fondoCajas, txtBlanc, bordeRojo);
        JScrollPane scrollRanking = new JScrollPane(rankingTable);
        configureScrollPane(scrollRanking, fondoCajas, bordeRojo);

        // TAB 2: HISTORIAL DE PARTIDAS
        String[] historialCols = {"Fecha", "Ganador", "Perdedor", "Causa"};
        historialModel = createNonEditableModel(historialCols);
        JTable historialTable = createStyledTable(historialModel, fondoCajas, txtBlanc, bordeRojo);
        JScrollPane scrollHistorial = new JScrollPane(historialTable);
        configureScrollPane(scrollHistorial, fondoCajas, bordeRojo);

        tabbedPane.addTab("Ranking", scrollRanking);
        tabbedPane.addTab("Historial de Partidas", scrollHistorial);

        JButton btnVolver = LogInMenu.createButton("Volver");
        Dimension dimBtn = new Dimension(220, 40);
        btnVolver.setPreferredSize(dimBtn);
        btnVolver.setMaximumSize(dimBtn);
        btnVolver.setAlignmentX(CENTER_ALIGNMENT);

        btnVolver.addActionListener(e -> {
            if (onBack != null) {
                onBack.run();
            }
        });

        containerPanel.add(lblTitle);
        containerPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        containerPanel.add(tabbedPane);
        containerPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        containerPanel.add(btnVolver);

        add(containerPanel);
    }

    private DefaultTableModel createNonEditableModel(String[] columns) {
        return new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
    }

    private JTable createStyledTable(DefaultTableModel model, Color fondo, Color texto, Color seleccion) {
        JTable table = new JTable(model);
        table.setFont(new Font("Georgia", Font.PLAIN, 12));
        table.setForeground(texto);
        table.setBackground(fondo);
        table.setSelectionBackground(seleccion);
        table.setSelectionForeground(Color.WHITE);
        table.setRowHeight(26);

        table.getTableHeader().setFont(new Font("Georgia", Font.BOLD, 13));
        table.getTableHeader().setBackground(new Color(40, 15, 20));
        table.getTableHeader().setForeground(texto);
        table.getTableHeader().setReorderingAllowed(false);

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        centerRenderer.setBackground(fondo);
        centerRenderer.setForeground(texto);

        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }
        return table;
    }

    private void configureScrollPane(JScrollPane scrollPane, Color fondo, Color borde) {
        scrollPane.setPreferredSize(new Dimension(420, 220));
        scrollPane.setMaximumSize(new Dimension(420, 220));
        scrollPane.getViewport().setBackground(fondo);
        scrollPane.setBorder(BorderFactory.createLineBorder(borde, 2));
    }

    public void cargarRanking(GameSystem brain) {
        if (brain == null) return;

        rankingModel.setRowCount(0);
        ArrayList<Player> ranking = brain.getRanking();
        int pos = 1;
        if (ranking != null) {
            for (Player p : ranking) {
                if (p != null) {
                    rankingModel.addRow(new Object[]{pos + "°", p.getUser(), p.getPuntos()});
                    pos++;
                }
            }
        }

        historialModel.setRowCount(0);
        ArrayList<GameMatch> historial = brain.getHistorialPartidas();
        if (historial != null) {
            for (GameMatch m : historial) {
                if (m != null) {
                    historialModel.addRow(new Object[]{
                        m.getFechaFormateada(),
                        m.getGanador(),
                        m.getPerdedor(),
                        m.getCausa()
                    });
                }
            }
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