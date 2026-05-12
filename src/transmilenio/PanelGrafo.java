package transmilenio;

import java.awt.*;
import javax.swing.JPanel;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Area;

public class PanelGrafo extends JPanel {

    private static final int MARGEN     = 70;
    private static final int RADIO_NODO = 13;
    private static final int RADIO_UMB  = 15;

    //Paleta TransMilenio 
    private static final Color TM_ROJO        = new Color(210,  30,  30);
    private static final Color TM_ROJO_OSCURO = new Color(130,  10,  10);
    private static final Color TM_ROJO_CLARO  = new Color(255,  80,  80);
    private static final Color TM_VERDE       = new Color( 40, 180,  90);
    private static final Color TM_AMARILLO    = new Color(255, 210,  50);
    private static final Color TM_GRIS_OSCURO = new Color( 22,  22,  22);
    private static final Color TM_GRIS_MEDIO  = new Color( 40,  40,  40);
    private static final Color TM_BLANCO      = new Color(240, 240, 240);

    private final Grafo grafo;

    private double zoom = 1.0;
    private double offsetX = 0;
    private double offsetY = 0;

    private int lastMouseX;
    private int lastMouseY;

    private boolean arrastrando = false;
    private final double ZOOM_MIN = 0.5;
    private final double ZOOM_MAX = 2.5;
    // Posiciones relativas de cada nodo (porcentaje del área útil del panel)
    // Índice = ID del nodo
    private static final double[][] POS = {
        {0.42, 0.08},  // 0 UMB
        {0.14, 0.24},  // 1 Portal Norte
        {0.76, 0.70},  // 2 Portal Sur
        {0.26, 0.76},  // 3 Portal 80
        {0.12, 0.58},  // 4 Portal Suba
        {0.66, 0.78},  // 5 Portal Americas
        {0.86, 0.58},  // 6 Portal Tunal
        {0.90, 0.42},  // 7 Portal Usme
        {0.80, 0.18},  // 8 Portal 20 Julio
        {0.84, 0.86},  // 9 San Mateo
        {0.58, 0.74},  // 10 Banderas
        {0.48, 0.20},  // 11 Museo Nacional
        {0.46, 0.88},  // 12 Portal Dorado
        {0.56, 0.46},  // 13 Ricaurte
        {0.34, 0.44},  // 14 Calle 75
    };

public PanelGrafo(Grafo grafo) {

    this.grafo = grafo;

    setBackground(TM_GRIS_OSCURO);

    setPreferredSize(new Dimension(500, 300));

    // ZOOM
    addMouseWheelListener((MouseWheelEvent e) -> {
        if (e.getPreciseWheelRotation() < 0) {
            zoom += 0.05;
        } else {
            zoom -= 0.05;
        }
        
        if (zoom < ZOOM_MIN) {
            zoom = ZOOM_MIN;
        }
        
        if (zoom > ZOOM_MAX) {
            zoom = ZOOM_MAX;
        }
        
        repaint();
    });


    // Movimiento en la ventana
    MouseAdapter mouse = new MouseAdapter() {

        @Override
        public void mousePressed(MouseEvent e) {

            lastMouseX = e.getX();
            lastMouseY = e.getY();

            arrastrando = true;
        }

        @Override
        public void mouseReleased(MouseEvent e) {

            arrastrando = false;
        }

        @Override
        public void mouseDragged(MouseEvent e) {

            if (arrastrando) {

                int dx = e.getX() - lastMouseX;
                int dy = e.getY() - lastMouseY;

                offsetX += dx;
                offsetY += dy;

                lastMouseX = e.getX();
                lastMouseY = e.getY();

                repaint();
            }
        }
    };

    addMouseListener(mouse);

    addMouseMotionListener(mouse);
}


    private Color colorDeNodo(Nodo nodo) {
        if (nodo.getId() == 0) return TM_AMARILLO;
        if (nodo.getNombre().startsWith("Portal")) return TM_ROJO;
        return TM_VERDE; // Estaciones
    }

    //Asigna coordenadas de pantalla a cada nodo según POS
    private void calcularCoordenadas() {
        int anchoUtil = getWidth()  - 2 * MARGEN;
        int altoUtil  = getHeight() - 2 * MARGEN - 50;
        for (Nodo nodo : grafo.getNodos()) {
            int id = nodo.getId();
            nodo.setX(MARGEN + (int)(POS[id][0] * anchoUtil));
            nodo.setY(MARGEN + 50 + (int)(POS[id][1] * altoUtil));
        }
    }

@Override
protected void paintComponent(Graphics g) {

    super.paintComponent(g);

    Graphics2D g2 = (Graphics2D) g;

    // Calidad gráfica
    g2.setRenderingHint(
            RenderingHints.KEY_ANTIALIASING,
            RenderingHints.VALUE_ANTIALIAS_ON);

    g2.setRenderingHint(
            RenderingHints.KEY_TEXT_ANTIALIASING,
            RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

    g2.setRenderingHint(
            RenderingHints.KEY_RENDERING,
            RenderingHints.VALUE_RENDER_QUALITY);

    calcularCoordenadas();


    // ELEMENTOS FIJOS

    dibujarFondo(g2);

    dibujarTitulo(g2);

    dibujarLeyenda(g2);

    // Indicador zoom
    g2.setFont(new Font("Segoe UI", Font.BOLD, 11));

    g2.setColor(Color.WHITE);

    g2.drawString(
            "Zoom: " + (int)(zoom * 100) + "%",
            getWidth() - 130,
            65);


    Graphics2D gMapa = (Graphics2D) g2.create();

    // Área donde está la leyenda
    int leyendaX = getWidth() - 210;
    int leyendaY = getHeight() - 130;
    int leyendaW = 205;
    int leyendaH = 115;

     // Área visible total del grafo
    Area areaVisible = new Area(
            new Rectangle(
                    0,
                    55,
                    getWidth(),
                    getHeight() - 55));

    // Restar el área de la leyenda
    areaVisible.subtract(
            new Area(
                    new Rectangle(
                            leyendaX,
                            leyendaY,
                            leyendaW,
                            leyendaH)));

    // Aplicar clip
    gMapa.setClip(areaVisible);
        // Aplicar movimiento
        gMapa.translate(offsetX, offsetY);

        // Centro del zoom
        int centroX = getWidth() / 2;
        int centroY = getHeight() / 2;

        gMapa.translate(centroX, centroY);

        // Aplicar zoom
        gMapa.scale(zoom, zoom);

        gMapa.translate(-centroX, -centroY);

        // Dibujar grafo
        dibujarAristas(gMapa);

        dibujarNodos(gMapa);

        gMapa.dispose();

    }
    private void dibujarFondo(Graphics2D g2) {
        g2.setColor(TM_GRIS_OSCURO);
        g2.fillRect(0, 0, getWidth(), getHeight());
        g2.setColor(TM_GRIS_MEDIO);
        g2.setStroke(new BasicStroke(0.4f));
        for (int x = 0; x < getWidth();  x += 40) g2.drawLine(x, 0, x, getHeight());
        for (int y = 0; y < getHeight(); y += 40) g2.drawLine(0, y, getWidth(), y);
        g2.setColor(TM_ROJO_OSCURO);
        g2.setStroke(new BasicStroke(3f));
        g2.drawRect(4, 4, getWidth() - 8, getHeight() - 8);
    }

    private void dibujarTitulo(Graphics2D g2) {
        g2.setColor(TM_ROJO);
        g2.fillRect(0, 0, getWidth(), 48);
        g2.setColor(TM_ROJO_OSCURO);
        g2.setStroke(new BasicStroke(3f));
        g2.drawLine(0, 48, getWidth(), 48);

        g2.setFont(new Font("Segoe UI", Font.BOLD, 18));
        g2.setColor(Color.WHITE);
        String titulo = "PORTALES De TRANSMILENIO  –  BOGOTÁ D.C.";
        FontMetrics fm = g2.getFontMetrics();
        g2.drawString(titulo, (getWidth() - fm.stringWidth(titulo)) / 2, 30);

        g2.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        g2.setColor(new Color(255, 200, 200));
        String sub = "Grafo no dirigido  |  distancias en km (Google Maps)";
        FontMetrics fm2 = g2.getFontMetrics();
        g2.drawString(sub, (getWidth() - fm2.stringWidth(sub)) / 2, 44);
    }

    private void dibujarAristas(Graphics2D g2) {
        for (Arista arista : grafo.getAristas()) {
            Nodo o = arista.getOrigen();
            Nodo d = arista.getDestino();

            // Sombra
            g2.setStroke(new BasicStroke(5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2.setColor(new Color(0, 0, 0, 90));
            g2.drawLine(o.getX() + 2, o.getY() + 2, d.getX() + 2, d.getY() + 2);

            // Línea roja
            g2.setStroke(new BasicStroke(1.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2.setColor(TM_ROJO_CLARO);
            g2.drawLine(o.getX(), o.getY(), d.getX(), d.getY());

            // Etiqueta del peso
            int mx = (o.getX() + d.getX()) / 2;
            int my = (o.getY() + d.getY()) / 2;
            String peso = String.format("%.1f km", arista.getDistanciaKm());
            g2.setFont(new Font("Segoe UI", Font.BOLD, 9));
            FontMetrics fm = g2.getFontMetrics();
            int pw = fm.stringWidth(peso);

            g2.setColor(new Color(22, 22, 22, 215));
            g2.fillRoundRect(mx - pw/2 - 3, my - 9, pw + 6, 13, 4, 4);
            g2.setColor(TM_ROJO_OSCURO);
            g2.setStroke(new BasicStroke(0.7f));
            g2.drawRoundRect(mx - pw/2 - 3, my - 9, pw + 6, 13, 4, 4);
            g2.setColor(TM_AMARILLO);
            g2.drawString(peso, mx - pw/2, my + 1);
        }
    }

    private void dibujarNodos(Graphics2D g2) {
        for (Nodo nodo : grafo.getNodos()) {
            int x     = nodo.getX();
            int y     = nodo.getY();
            boolean esUMB = nodo.getId() == 0;
            int radio = esUMB ? RADIO_UMB : RADIO_NODO;
            Color color = colorDeNodo(nodo);

            // Sombra
            g2.setColor(new Color(0, 0, 0, 110));
            g2.fillOval(x - radio + 3, y - radio + 3, radio * 2, radio * 2);

            // Halo exterior
            g2.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 50));
            g2.fillOval(x - radio - 5, y - radio - 5, (radio + 5) * 2, (radio + 5) * 2);

            // Relleno oscuro interior
            int ri = color.getRed()   / 6;
            int gi = color.getGreen() / 6;
            int bi = color.getBlue()  / 6;
            g2.setColor(new Color(ri, gi, bi));
            g2.fillOval(x - radio, y - radio, radio * 2, radio * 2);

            // Borde del color
            g2.setStroke(new BasicStroke(2.8f));
            g2.setColor(color);
            g2.drawOval(x - radio, y - radio, radio * 2, radio * 2);

            // Símbolo interior
            String sym = esUMB ? "U" : "●";
            g2.setFont(new Font("Segoe UI", Font.BOLD, esUMB ? 11 : 8));
            g2.setColor(color);
            FontMetrics fms = g2.getFontMetrics();
            g2.drawString(sym, x - fms.stringWidth(sym)/2, y + fms.getAscent()/2 - 1);

            dibujarEtiquetaNodo(g2, nodo, color);
        }
    }

    private void dibujarEtiquetaNodo(Graphics2D g2, Nodo nodo, Color color) {
        int x     = nodo.getX();
        int y     = nodo.getY();
        String nombre = nodo.getNombre();

        g2.setFont(new Font("Segoe UI", Font.BOLD, 10));
        FontMetrics fm = g2.getFontMetrics();
        int tw = fm.stringWidth(nombre);

        int tx = x + RADIO_NODO + 5;
        int ty = y + 5;
        if (tx + tw > getWidth() - 10) tx = x - RADIO_NODO - tw - 5;
        if (ty + fm.getHeight() > getHeight() - 10) ty = y - RADIO_NODO - 5;

        g2.setColor(new Color(15, 5, 5, 220));
        g2.fillRoundRect(tx - 3, ty - fm.getAscent(), tw + 6, fm.getHeight() + 2, 5, 5);
        g2.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 140));
        g2.setStroke(new BasicStroke(0.8f));
        g2.drawRoundRect(tx - 3, ty - fm.getAscent(), tw + 6, fm.getHeight() + 2, 5, 5);
        g2.setColor(TM_BLANCO);
        g2.drawString(nombre, tx, ty);
    }

    private void dibujarLeyenda(Graphics2D g2) {
        int lx = getWidth() - 200;
        int ly = getHeight() - 110;

        g2.setColor(new Color(18, 5, 5, 235));
        g2.fillRoundRect(lx - 10, ly - 20, 195, 105, 10, 10);
        g2.setColor(TM_ROJO_OSCURO);
        g2.setStroke(new BasicStroke(1.5f));
        g2.drawRoundRect(lx - 10, ly - 20, 195, 105, 10, 10);

        g2.setFont(new Font("Segoe UI", Font.BOLD, 11));
        g2.setColor(TM_ROJO_CLARO);
        g2.drawString("CONVENCIONES", lx, ly - 4);

        g2.setFont(new Font("Segoe UI", Font.PLAIN, 10));

        // Rojo – Portales
        g2.setColor(TM_ROJO);
        g2.fillOval(lx, ly + 10, 12, 12);
        g2.setColor(TM_BLANCO);
        g2.drawString("Portales de TransMilenio", lx + 18, ly + 21);

        // Verde – Estaciones
        g2.setColor(TM_VERDE);
        g2.fillOval(lx, ly + 28, 12, 12);
        g2.setColor(TM_BLANCO);
        g2.drawString("Estaciones", lx + 18, ly + 39);

        // Amarillo – UMB
        g2.setColor(TM_AMARILLO);
        g2.fillOval(lx, ly + 46, 12, 12);
        g2.setColor(TM_BLANCO);
        g2.drawString("Universidad Manuela Beltrán", lx + 18, ly + 57);

        // Arista
        g2.setColor(TM_ROJO_CLARO);
        g2.setStroke(new BasicStroke(2f));
        g2.drawLine(lx, ly + 70, lx + 12, ly + 70);
        g2.setColor(TM_AMARILLO);
        g2.drawString("Distancia en km", lx + 18, ly + 74);
    }
}