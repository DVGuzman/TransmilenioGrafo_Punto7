package transmilenio;

import java.awt.*;
import java.awt.event.*;
import java.util.List;
import javax.swing.*;

/**
 * Panel que expone los algoritmos del punto 7:
 *   - Lista de adyacencia
 *   - BFS  (recorrido por anchura)
 *   - DFS  (recorrido por profundidad)
 *   - Prim (árbol de expansión mínima)
 *   - Floyd-Warshall (caminos mínimos)
 *
 * El diseño visual (colores, fuentes, estilos) sigue exactamente
 * la paleta del proyecto original (TM rojo / gris oscuro).
 * NO se modifica ningún panel ni clase existente.
 */
public class PanelAlgoritmos extends JPanel {

    // ── Paleta igual que PanelMatriz ─────────────────────────
    private static final Color TM_ROJO        = new Color(220,  30,  30);
    private static final Color TM_ROJO_OSCURO = new Color(140,  10,  10);
    private static final Color TM_GRIS_OSCURO = new Color( 28,  28,  28);
    private static final Color TM_GRIS_CELDA  = new Color( 38,  38,  38);
    private static final Color TM_BLANCO      = new Color(240, 240, 240);
    private static final Color TM_AMARILLO    = new Color(255, 210,  50);

    private final Grafo     grafo;
    private JTextArea       areaResultado;
    private JComboBox<String> comboNodoInicio;

    public PanelAlgoritmos(Grafo grafo) {
        this.grafo = grafo;
        setLayout(new BorderLayout(8, 8));
        setBackground(TM_GRIS_OSCURO);
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        construirUI();
    }

    private void construirUI() {

        // ── BANDA DE TÍTULO ───────────────────────────────────
        JPanel bandaTitulo = new JPanel(new BorderLayout());
        bandaTitulo.setBackground(TM_ROJO);
        bandaTitulo.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));

        JLabel titulo = new JLabel("ALGORITMOS DE GRAFOS", SwingConstants.CENTER);
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 19));
        titulo.setForeground(Color.WHITE);

        JLabel sub = new JLabel(
            "Lista de Adyacencia · BFS · DFS · Prim · Floyd-Warshall",
            SwingConstants.CENTER);
        sub.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        sub.setForeground(new Color(255, 200, 200));

        bandaTitulo.add(titulo, BorderLayout.CENTER);
        bandaTitulo.add(sub,    BorderLayout.SOUTH);

        // ── PANEL DE BOTONES ──────────────────────────────────
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 8));
        panelBotones.setBackground(new Color(20, 20, 20));
        panelBotones.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, TM_ROJO_OSCURO));

        // Selector de nodo inicial (para BFS y DFS)
        JLabel lblNodo = new JLabel("Nodo inicio (BFS/DFS):");
        lblNodo.setForeground(new Color(200, 160, 160));
        lblNodo.setFont(new Font("Segoe UI", Font.PLAIN, 11));

        List<Nodo> nodos = grafo.getNodos();
        String[] nombresNodos = new String[nodos.size()];
        for (int i = 0; i < nodos.size(); i++) {
            nombresNodos[i] = nodos.get(i).getId() + " – " + nodos.get(i).getNombre();
        }
        comboNodoInicio = new JComboBox<>(nombresNodos);
        comboNodoInicio.setBackground(TM_GRIS_CELDA);
        comboNodoInicio.setForeground(TM_AMARILLO);
        comboNodoInicio.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        comboNodoInicio.setPreferredSize(new Dimension(220, 26));

        panelBotones.add(lblNodo);
        panelBotones.add(comboNodoInicio);
        panelBotones.add(Box.createHorizontalStrut(20));

        // Botones
        String[] etiquetas = {
            "Lista de Adyacencia", "BFS (Anchura)", "DFS (Profundidad)",
            "Prim (MST)", "Floyd-Warshall"
        };
        for (String etiqueta : etiquetas) {
            JButton btn = crearBoton(etiqueta);
            final String et = etiqueta;
            btn.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    ejecutar(et);
                }
            });
            panelBotones.add(btn);
        }

        // ── ÁREA DE RESULTADO ─────────────────────────────────
        areaResultado = new JTextArea();
        areaResultado.setFont(new Font("Consolas", Font.PLAIN, 11));
        areaResultado.setBackground(new Color(18, 5, 5));
        areaResultado.setForeground(new Color(255, 150, 150));
        areaResultado.setEditable(false);
        areaResultado.setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 12));
        areaResultado.setText("Seleccione un algoritmo para ver los resultados.");

        JScrollPane scroll = new JScrollPane(areaResultado);
        scroll.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(TM_ROJO_OSCURO),
            "Resultado",
            javax.swing.border.TitledBorder.LEFT,
            javax.swing.border.TitledBorder.TOP,
            new Font("Segoe UI", Font.PLAIN, 10),
            new Color(220, 100, 100)));
        scroll.getViewport().setBackground(new Color(18, 5, 5));

        // ── ENSAMBLAR ─────────────────────────────────────────
        add(bandaTitulo,  BorderLayout.NORTH);
        add(panelBotones, BorderLayout.CENTER);
        add(scroll,       BorderLayout.SOUTH);

        // El área de resultado debe ocupar la mayor parte
        scroll.setPreferredSize(new Dimension(0, 480));
    }

    /** Crea un botón con el estilo visual del proyecto. */
    private JButton crearBoton(String texto) {
        JButton btn = new JButton(texto);
        btn.setBackground(TM_ROJO_OSCURO);
        btn.setForeground(Color.BLACK);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 11));
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(TM_ROJO, 1),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        // Efecto hover
        btn.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) {
                btn.setBackground(TM_ROJO);
            }
            @Override public void mouseExited(MouseEvent e) {
                btn.setBackground(TM_ROJO_OSCURO);
            }
        });
        return btn;
    }

    /** Llama al método correspondiente del grafo y muestra el resultado. */
    private void ejecutar(String algoritmo) {
        int idNodo = comboNodoInicio.getSelectedIndex();
        String resultado;

        switch (algoritmo) {
            case "Lista de Adyacencia":
                resultado = grafo.listaAdyacenciaAString();
                break;
            case "BFS (Anchura)":
                resultado = grafo.bfsAString(idNodo);
                break;
            case "DFS (Profundidad)":
                resultado = grafo.dfsAString(idNodo);
                break;
            case "Prim (MST)":
                resultado = grafo.primAString();
                break;
            case "Floyd-Warshall":
                resultado = grafo.floydWarshallAString();
                break;
            default:
                resultado = "Algoritmo no reconocido.";
        }

        areaResultado.setText(resultado);
        areaResultado.setCaretPosition(0); // Scroll al inicio
    }
}
