package transmilenio;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * Clase principal del grafo de TransMilenio.
 * Contiene la estructura base + algoritmos:
 *   - Lista de adyacencia
 *   - BFS (recorrido por anchura)
 *   - DFS (recorrido por profundidad)
 *   - Prim (árbol de expansión mínima)
 *   - Floyd-Warshall (caminos mínimos entre todos los pares)
 */
public class Grafo {

    private final List<Nodo>   nodos;
    private final List<Arista> aristas;
    private final double[][]   matrizAdyacencia;
    private final int          n;

    // Constante para "sin conexión" en Floyd-Warshall
    public static final double INF = Double.MAX_VALUE / 2.0;

    public Grafo() {
        nodos   = new ArrayList<>();
        aristas = new ArrayList<>();
        inicializarNodos();
        n = nodos.size();
        matrizAdyacencia = new double[n][n];
        inicializarAristas();
    }

    // =========================================================
    //  INICIALIZACIÓN (sin cambios respecto al original)
    // =========================================================

    private void inicializarNodos() {
        nodos.add(new Nodo(0,  "UMB Bogotá",         4.6486, -74.0648));
        nodos.add(new Nodo(1,  "Portal Norte",        4.7589, -74.0900));
        nodos.add(new Nodo(2,  "Portal Sur",          4.5573, -74.1050));
        nodos.add(new Nodo(3,  "Portal de la 80",     4.7044, -74.1300));
        nodos.add(new Nodo(4,  "Portal Suba",         4.6800, -74.1100));
        nodos.add(new Nodo(5,  "Portal Américas",     4.5800, -74.1200));
        nodos.add(new Nodo(6,  "Portal El Tunal",     4.5500, -74.0900));
        nodos.add(new Nodo(7,  "Portal Usme",         4.5300, -74.0600));
        nodos.add(new Nodo(8,  "Portal 20 de Julio",  4.5900, -74.0500));
        nodos.add(new Nodo(9,  "Est. San Mateo",      4.5400, -74.1150));
        nodos.add(new Nodo(10, "Est. Banderas",       4.5900, -74.1200));
        nodos.add(new Nodo(11, "Est. Museo Nacional", 4.6300, -74.0700));
        nodos.add(new Nodo(12, "Portal El Dorado",    4.5700, -74.1400));
        nodos.add(new Nodo(13, "Est. Ricaurte",            4.6100, -74.0800));
        nodos.add(new Nodo(14, "Est. NQS - Calle 75",      4.6700, -74.1050));
    }

    private void inicializarAristas() {
        agregarArista(9,  2,   5.0);
        agregarArista(2,  13, 11.0);
        agregarArista(6,  13,  8.3);
        agregarArista(9,  12, 18.0);
        agregarArista(9,  1,  32.0);
        agregarArista(10, 13,  6.8);
        agregarArista(13, 8,   6.4);
        agregarArista(8,  11,  6.7);
        agregarArista(11, 13,  3.5);
        agregarArista(11, 0,   3.9);
        agregarArista(11, 7,  13.0);
        agregarArista(11, 12, 13.0);
        agregarArista(7,  1,  29.0);
        agregarArista(7,  4,  30.0);
        agregarArista(7,  12, 22.0);
        agregarArista(13, 12, 12.0);
        agregarArista(13, 3,  14.0);
        agregarArista(13, 4,  19.0);
        agregarArista(13, 14,  8.4);
        agregarArista(14, 3,   6.9);
        agregarArista(14, 4,  11.0);
        agregarArista(14, 1,  11.0);
        agregarArista(1,  3,  12.0);
        agregarArista(10, 5,   5.6);
    }

    public void agregarArista(int idOrigen, int idDestino, double distanciaKm) {
        if (matrizAdyacencia[idOrigen][idDestino] == 0) {
            Nodo origen  = nodos.get(idOrigen);
            Nodo destino = nodos.get(idDestino);
            aristas.add(new Arista(origen, destino, distanciaKm));
            matrizAdyacencia[idOrigen][idDestino] = distanciaKm;
            matrizAdyacencia[idDestino][idOrigen] = distanciaKm;
        }
    }

    // =========================================================
    //  GETTERS (sin cambios)
    // =========================================================

    public List<Nodo>   getNodos()            { return nodos; }
    public List<Arista> getAristas()          { return aristas; }
    public double[][]   getMatrizAdyacencia() { return matrizAdyacencia; }
    public int          getN()                { return n; }

    // =========================================================
    //  REPRESENTACIÓN TEXTUAL DE MATRIZ (sin cambios)
    // =========================================================

    public String matrizAString() {
        StringBuilder sb = new StringBuilder();
        sb.append("MATRIZ DE ADYACENCIAS – Portales De TransMilenio + UMB Bogota\n");
        sb.append("Valores en kilometros | 0.00 = sin conexion directa\n\n");

        String[] abrev = new String[n];
        for (int i = 0; i < n; i++) {
            String nom = nodos.get(i).getNombre();
            abrev[i] = nom.length() > 8 ? nom.substring(0, 8) : nom;
        }

        sb.append(String.format("%-22s", ""));
        for (String a : abrev) sb.append(String.format("%9s", a));
        sb.append("\n").append("-".repeat(22 + n * 9)).append("\n");

        for (int i = 0; i < n; i++) {
            String etiqueta = nodos.get(i).getNombre();
            if (etiqueta.length() > 21) etiqueta = etiqueta.substring(0, 21);
            sb.append(String.format("%-22s", etiqueta));
            for (int j = 0; j < n; j++) {
                sb.append(String.format("%9.2f", matrizAdyacencia[i][j]));
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    // =========================================================
    //  1. LISTA DE ADYACENCIA
    // =========================================================
    /**
     * Construye y retorna la lista de adyacencia del grafo como texto.
     * Para cada nodo lista sus vecinos directos con el peso de la arista.
     */
    public String listaAdyacenciaAString() {
        StringBuilder sb = new StringBuilder();
        sb.append("LISTA DE ADYACENCIA – Grafo TransMilenio\n");
        sb.append("=========================================\n\n");

        for (int i = 0; i < n; i++) {
            sb.append(nodos.get(i).getNombre()).append(":\n");
            boolean tieneVecinos = false;
            for (int j = 0; j < n; j++) {
                if (matrizAdyacencia[i][j] > 0) {
                    sb.append("    -> ")
                      .append(nodos.get(j).getNombre())
                      .append(String.format(" (%.1f km)\n", matrizAdyacencia[i][j]));
                    tieneVecinos = true;
                }
            }
            if (!tieneVecinos) {
                sb.append("    (sin conexiones directas)\n");
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    // =========================================================
    //  2. BFS – RECORRIDO POR ANCHURA
    // =========================================================
    /**
     * Recorre el grafo en anchura (BFS) desde el nodo idInicio.
     * Usa una cola (Queue) para procesar nodos nivel por nivel.
     *
     * @param idInicio id del nodo de inicio (0..n-1)
     * @return texto con el recorrido BFS paso a paso
     */
    public String bfsAString(int idInicio) {
        boolean[] visitado = new boolean[n];
        List<Nodo> recorrido = new ArrayList<>();
        Queue<Integer> cola = new LinkedList<>();

        visitado[idInicio] = true;
        cola.add(idInicio);

        while (!cola.isEmpty()) {
            int actual = cola.poll();
            recorrido.add(nodos.get(actual));

            for (int j = 0; j < n; j++) {
                if (matrizAdyacencia[actual][j] > 0 && !visitado[j]) {
                    visitado[j] = true;
                    cola.add(j);
                }
            }
        }

        StringBuilder sb = new StringBuilder();
        sb.append("RECORRIDO BFS (Anchura) desde: ")
          .append(nodos.get(idInicio).getNombre()).append("\n");
        sb.append("=".repeat(50)).append("\n\n");
        for (int i = 0; i < recorrido.size(); i++) {
            sb.append(String.format("  Paso %2d: %s\n", i + 1, recorrido.get(i).getNombre()));
        }
        sb.append("\nTotal de nodos visitados: ").append(recorrido.size());
        return sb.toString();
    }

    // =========================================================
    //  3. DFS – RECORRIDO POR PROFUNDIDAD
    // =========================================================
    /**
     * Recorre el grafo en profundidad (DFS) desde el nodo idInicio.
     * Implementación recursiva: visita un vecino, luego sus vecinos, etc.
     *
     * @param idInicio id del nodo de inicio (0..n-1)
     * @return texto con el recorrido DFS paso a paso
     */
    public String dfsAString(int idInicio) {
        boolean[] visitado = new boolean[n];
        List<Nodo> recorrido = new ArrayList<>();

        dfsRecursivo(idInicio, visitado, recorrido);

        StringBuilder sb = new StringBuilder();
        sb.append("RECORRIDO DFS (Profundidad) desde: ")
          .append(nodos.get(idInicio).getNombre()).append("\n");
        sb.append("=".repeat(50)).append("\n\n");
        for (int i = 0; i < recorrido.size(); i++) {
            sb.append(String.format("  Paso %2d: %s\n", i + 1, recorrido.get(i).getNombre()));
        }
        sb.append("\nTotal de nodos visitados: ").append(recorrido.size());
        return sb.toString();
    }

    /** Auxiliar recursivo del DFS. */
    private void dfsRecursivo(int actual, boolean[] visitado, List<Nodo> recorrido) {
        visitado[actual] = true;
        recorrido.add(nodos.get(actual));

        for (int j = 0; j < n; j++) {
            if (matrizAdyacencia[actual][j] > 0 && !visitado[j]) {
                dfsRecursivo(j, visitado, recorrido);
            }
        }
    }

    // =========================================================
    //  4. ALGORITMO DE PRIM – ÁRBOL DE EXPANSIÓN MÍNIMA (MST)
    // =========================================================
    /**
     * Calcula el MST usando el algoritmo de Prim (versión con arreglos).
     *
     * Variables clave:
     *   - clave[v]  = peso mínimo de arista para añadir v al MST.
     *   - padre[v]  = nodo desde el que v fue añadido al MST.
     *   - enMST[v]  = true si v ya está en el MST.
     *
     * Ciclo principal: n-1 iteraciones, una por cada arista del MST.
     *
     * @return texto con las aristas seleccionadas y el peso total del MST
     */
    public String primAString() {
        double[]  clave  = new double[n];
        int[]     padre  = new int[n];
        boolean[] enMST  = new boolean[n];

        for (int i = 0; i < n; i++) {
            clave[i] = INF;
            padre[i] = -1;
        }
        clave[0] = 0.0; // Iniciar desde nodo 0 (UMB Bogotá)

        for (int iter = 0; iter < n - 1; iter++) {
            // 1. Elegir el nodo fuera del MST con menor clave
            int u = -1;
            double minClave = INF;
            for (int v = 0; v < n; v++) {
                if (!enMST[v] && clave[v] < minClave) {
                    minClave = clave[v];
                    u = v;
                }
            }
            if (u == -1) break; // Grafo desconectado

            enMST[u] = true;

            // 2. Actualizar claves de los vecinos de u
            for (int v = 0; v < n; v++) {
                double peso = matrizAdyacencia[u][v];
                if (peso > 0 && !enMST[v] && peso < clave[v]) {
                    clave[v] = peso;
                    padre[v] = u;
                }
            }
        }

        StringBuilder sb = new StringBuilder();
        sb.append("ÁRBOL DE EXPANSIÓN MÍNIMA – Algoritmo de Prim\n");
        sb.append("=".repeat(52)).append("\n\n");
        sb.append(String.format("  Nodo inicial: %s\n\n", nodos.get(0).getNombre()));

        double pesoTotal = 0.0;
        List<String> lineas = new ArrayList<>();
        for (int v = 1; v < n; v++) {
            if (padre[v] != -1) {
                double peso = matrizAdyacencia[padre[v]][v];
                pesoTotal += peso;
                lineas.add(String.format("  %-25s  ->  %-25s  =  %.1f km",
                        nodos.get(padre[v]).getNombre(),
                        nodos.get(v).getNombre(),
                        peso));
            }
        }
        Collections.sort(lineas);
        for (String l : lineas) sb.append(l).append("\n");

        sb.append("\n").append("-".repeat(52)).append("\n");
        sb.append(String.format("  Peso total del MST: %.1f km\n", pesoTotal));
        return sb.toString();
    }

    // =========================================================
    //  5. FLOYD-WARSHALL
    // =========================================================
    /**
     * Calcula las distancias mínimas entre todos los pares de nodos.
     *
     * Inicialización:
     *   dist[i][j] = peso de arista si hay conexión directa.
     *   dist[i][j] = INF si no hay conexión y i != j.
     *   dist[i][i] = 0.
     *
     * Triple ciclo: para cada nodo intermedio k, para cada par (i,j),
     *   si pasar por k mejora la distancia, se actualiza.
     *
     * @return texto con la matriz de distancias mínimas y ejemplos
     */
    public String floydWarshallAString() {
        // Inicializar dist[][]
        double[][] dist = new double[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (i == j) {
                    dist[i][j] = 0.0;
                } else if (matrizAdyacencia[i][j] > 0) {
                    dist[i][j] = matrizAdyacencia[i][j];
                } else {
                    dist[i][j] = INF;
                }
            }
        }

        // Aplicar Floyd-Warshall
        for (int k = 0; k < n; k++) {
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    if (dist[i][k] < INF && dist[k][j] < INF) {
                        if (dist[i][k] + dist[k][j] < dist[i][j]) {
                            dist[i][j] = dist[i][k] + dist[k][j];
                        }
                    }
                }
            }
        }

        // Construir salida
        StringBuilder sb = new StringBuilder();
        sb.append("FLOYD-WARSHALL – Distancias mínimas entre todos los pares\n");
        sb.append("=".repeat(60)).append("\n");
        sb.append("(INF = no existe camino entre esos nodos)\n\n");

        String[] abrev = new String[n];
        for (int i = 0; i < n; i++) {
            String nom = nodos.get(i).getNombre();
            abrev[i] = nom.length() > 7 ? nom.substring(0, 7) : nom;
        }
        sb.append(String.format("%-22s", ""));
        for (String a : abrev) sb.append(String.format("%9s", a));
        sb.append("\n").append("-".repeat(22 + n * 9)).append("\n");

        for (int i = 0; i < n; i++) {
            String etiqueta = nodos.get(i).getNombre();
            if (etiqueta.length() > 21) etiqueta = etiqueta.substring(0, 21);
            sb.append(String.format("%-22s", etiqueta));
            for (int j = 0; j < n; j++) {
                if (dist[i][j] >= INF) {
                    sb.append(String.format("%9s", "INF"));
                } else {
                    sb.append(String.format("%9.1f", dist[i][j]));
                }
            }
            sb.append("\n");
        }

        // Ejemplos de distancias mínimas
        sb.append("\n").append("=".repeat(60)).append("\n");
        sb.append("Ejemplos de distancias mínimas calculadas:\n\n");
        int[][] pares = {{0,1},{0,7},{1,5},{2,4},{6,14},{9,3}};
        for (int[] par : pares) {
            int a = par[0], b = par[1];
            String val = dist[a][b] >= INF ? "INF"
                         : String.format("%.1f km", dist[a][b]);
            sb.append(String.format("  %-25s  <->  %-25s  =  %s\n",
                    nodos.get(a).getNombre(),
                    nodos.get(b).getNombre(),
                    val));
        }
        return sb.toString();
    }
}
