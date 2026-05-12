package transmilenio;

import java.awt.*;
import java.util.List;
import javax.swing.*;
import javax.swing.table.*;

public class PanelMatriz extends JPanel {

    private static final Color TM_ROJO        = new Color(220,  30,  30);
    private static final Color TM_ROJO_OSCURO = new Color(140,  10,  10);
    private static final Color TM_GRIS_OSCURO = new Color( 28,  28,  28);
    private static final Color TM_GRIS_CELDA  = new Color( 38,  38,  38);
    private static final Color TM_GRIS_VALOR  = new Color( 55,  20,  20);
    private static final Color TM_BLANCO      = new Color(240, 240, 240);
    private static final Color TM_AMARILLO    = new Color(255, 210,  50);
    private static final Color TM_TEXTO_CERO  = new Color( 80,  60,  60);
    private static final Color TM_DIAGONAL    = new Color( 60,  10,  10);

    private final Grafo grafo;

    public PanelMatriz(Grafo grafo) {
        this.grafo = grafo;
        setLayout(new BorderLayout(10, 10));
        setBackground(TM_GRIS_OSCURO);
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        construirUI();
    }

    private void construirUI() {
        //Título
        JPanel bandaTitulo = new JPanel(new BorderLayout());
        bandaTitulo.setBackground(TM_ROJO);
        bandaTitulo.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));

        JLabel titulo = new JLabel("MATRIZ DE ADYACENCIAS", SwingConstants.CENTER);
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 19));
        titulo.setForeground(Color.WHITE);

        JLabel sub = new JLabel(
            "Portales De TransMilenio + UMB Bogotá  |  valores en km  |  0.00 = sin conexión directa",
            SwingConstants.CENTER);
        sub.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        sub.setForeground(new Color(255, 200, 200));

        bandaTitulo.add(titulo, BorderLayout.CENTER);
        bandaTitulo.add(sub,    BorderLayout.SOUTH);

        //Datos
        List<Nodo> nodos = grafo.getNodos();
        int n = nodos.size();
        double[][] mat = grafo.getMatrizAdyacencia();

        String[] encabezados = new String[n + 1];
        encabezados[0] = "Nodo";
        for (int i = 0; i < n; i++) {
            String nom = nodos.get(i).getNombre();
            encabezados[i + 1] = nom;
        }

        Object[][] datos = new Object[n][n + 1];
        for (int i = 0; i < n; i++) {
            String nom = nodos.get(i).getNombre();
            datos[i][0] = nom;
            for (int j = 0; j < n; j++) {
                datos[i][j + 1] = String.format("%.1f", mat[i][j]);
            }
        }

        //Tabla
        DefaultTableModel modelo = new DefaultTableModel(datos, encabezados) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        JTable tabla = new JTable(modelo);
        tabla.setBackground(TM_GRIS_CELDA);
        tabla.setForeground(TM_BLANCO);
        tabla.setFont(new Font("Consolas", Font.PLAIN, 11));
        tabla.setRowHeight(23);
        tabla.setGridColor(new Color(70, 20, 20));
        tabla.setShowGrid(true);
        tabla.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        tabla.setSelectionBackground(TM_ROJO);
        tabla.setSelectionForeground(Color.WHITE);

        TableCellRenderer render = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object val,
                    boolean sel, boolean foc, int row, int col) {
                super.getTableCellRendererComponent(t, val, sel, foc, row, col);
                setHorizontalAlignment(col == 0 ? LEFT : CENTER);
                if (sel) {
                    setBackground(TM_ROJO);
                    setForeground(Color.WHITE);
                    return this;
                }
                if (col == 0) {
                    // Columna de nombres
                    setBackground(new Color(50, 15, 15));
                    setForeground(new Color(255, 180, 180));
                    setFont(new Font("Segoe UI", Font.BOLD, 11));
                } else if (row == col - 1) {
                    // Diagonal principal
                    setBackground(TM_DIAGONAL);
                    setForeground(new Color(100, 40, 40));
                    setFont(new Font("Consolas", Font.PLAIN, 11));
                } else if ("0.0".equals(val)) {
                    setBackground(TM_GRIS_CELDA);
                    setForeground(TM_TEXTO_CERO);
                    setFont(new Font("Consolas", Font.PLAIN, 11));
                } else {
                    setBackground(TM_GRIS_VALOR);
                    setForeground(TM_AMARILLO);
                    setFont(new Font("Consolas", Font.BOLD, 11));
                }
                return this;
            }
        };

        JTableHeader header = tabla.getTableHeader();
        header.setBackground(TM_ROJO_OSCURO);
        header.setForeground(Color.BLACK);
        header.setFont(new Font("Segoe UI", Font.BOLD, 10));
        header.setReorderingAllowed(false);

        TableColumnModel cols = tabla.getColumnModel();
        cols.getColumn(0).setPreferredWidth(190);
        
        for (int c = 0; c <= n; c++) {
            cols.getColumn(c).setCellRenderer(render);
            
            if (c > 0) cols.getColumn(c).setPreferredWidth(82);
        }

        JScrollPane scroll = new JScrollPane(tabla);
        scroll.getViewport().setBackground(TM_GRIS_OSCURO);
        scroll.setBorder(BorderFactory.createLineBorder(TM_ROJO_OSCURO, 1));
        scroll.getVerticalScrollBar().setBackground(TM_GRIS_OSCURO);

        JTextArea textArea = new JTextArea(grafo.matrizAString());
        textArea.setFont(new Font("Consolas", Font.PLAIN, 10));
        textArea.setBackground(new Color(18, 5, 5));
        textArea.setForeground(new Color(255, 150, 150));
        textArea.setEditable(false);
        textArea.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));

        JScrollPane scrollTexto = new JScrollPane(textArea);
        scrollTexto.setPreferredSize(new Dimension(0, 300));
        scrollTexto.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(TM_ROJO_OSCURO),
            "Representación textual de la matriz",
            javax.swing.border.TitledBorder.LEFT,
            javax.swing.border.TitledBorder.TOP,
            new Font("Segoe UI", Font.PLAIN, 10),
            new Color(220, 100, 100)));

        add(bandaTitulo, BorderLayout.NORTH);
        add(scroll,      BorderLayout.CENTER);
        add(scrollTexto, BorderLayout.SOUTH);
    }
}