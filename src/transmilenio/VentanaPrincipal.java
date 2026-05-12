package transmilenio;

import java.awt.*;
import javax.swing.*;

public class VentanaPrincipal extends JFrame {

    private final Grafo grafo;

    public VentanaPrincipal() {
        grafo = new Grafo();
        configurarVentana();
        construirUI();
    }

    private void configurarVentana() {
        setTitle("Grafo – Portales De TransMilenio · Bogotá D.C. | UMB");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 800);
        setMinimumSize(new Dimension(900, 600));
        setLocationRelativeTo(null);
        getContentPane().setBackground(new Color(12, 18, 32));
    }

    private void construirUI() {
        JTabbedPane tabs = new JTabbedPane(JTabbedPane.TOP);
        tabs.setBackground(new Color(12, 18, 32));
        tabs.setForeground(new Color(0, 210, 255));
        tabs.setFont(new Font("Segoe UI", Font.BOLD, 13));

        tabs.addTab("  Grafo Visual  ",          new PanelGrafo(grafo));
        tabs.addTab("  Matriz de Adyacencias  ",  new PanelMatriz(grafo));
        // Pestaña nueva con los algoritmos del punto 7
        tabs.addTab("  Prim y Floyd Warshall  ",   new PanelAlgoritmos(grafo));

        JLabel estado = new JLabel(
            "  Nodos: " + grafo.getN() + "   |   Aristas: " + grafo.getAristas().size()
            + "   |   Grafo no dirigido valorado – distancias en km (Google Maps)");
        estado.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        estado.setForeground(new Color(100, 140, 190));
        estado.setBackground(new Color(8, 14, 26));
        estado.setOpaque(true);
        estado.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(40, 70, 120)),
            BorderFactory.createEmptyBorder(5, 8, 5, 8)));

        add(tabs,   BorderLayout.CENTER);
        add(estado, BorderLayout.SOUTH);
    }
}
