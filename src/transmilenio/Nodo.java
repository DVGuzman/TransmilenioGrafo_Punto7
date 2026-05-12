package transmilenio;

//Representa un nodo del grafo: Portal, Estación o Universidad.
public class Nodo {

    private final int id;
    private final String nombre;
    private final double latitud;
    private final double longitud;
    private int x;
    private int y;

    public Nodo(int id, String nombre, double latitud, double longitud) {
        this.id = id;
        this.nombre = nombre;
        this.latitud = latitud;
        this.longitud = longitud;
    }

    public int getId()          { return id; }
    public String getNombre()   { return nombre; }
    public double getLatitud()  { return latitud; }
    public double getLongitud() { return longitud; }
    public int getX()           { return x; }
    public void setX(int x)     { this.x = x; }
    public int getY()           { return y; }
    public void setY(int y)     { this.y = y; }

    @Override
    public String toString() { return nombre; }
}