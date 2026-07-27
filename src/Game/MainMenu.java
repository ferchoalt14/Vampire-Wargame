/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Game;

import java.awt.Graphics;
import java.awt.Image;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 *
 * @author User
 */
public class MainMenu extends JFrame{

    public MainMenu() {
        
        this.setTitle("Vampire Wargame- menu de inicio");
        this.setSize(420, 420);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setResizable(false);
        this.setLocationRelativeTo(null);
       
        ImageIcon logo= new ImageIcon(getClass().getResource("/Images/logo.png"));
        this.setIconImage(logo.getImage());
        
        BackgroundPanel backGrnd = new BackgroundPanel();
        this.setContentPane(backGrnd);
        
         this.setVisible(true);
    }
    
    private class BackgroundPanel extends JPanel {
        private final Image fondo;

        public BackgroundPanel() {
            //
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
