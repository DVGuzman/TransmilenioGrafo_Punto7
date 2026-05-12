package transmilenio;

//Representa una arista entre dos nodos con su peso en kilómetros.
public class Arista {

    private final Nodo origen;
    private final Nodo destino;
    private final double distanciaKm;

    public Arista(Nodo origen, Nodo destino, double distanciaKm) {
        this.origen = origen;
        this.destino = destino;
        this.distanciaKm = distanciaKm;
    }

    public Nodo getOrigen()         { return origen; }
    public Nodo getDestino()        { return destino; }
    public double getDistanciaKm()  { return distanciaKm; }

    @Override
    public String toString() {
        return origen.getNombre() + " <-> " + destino.getNombre()
                + " [" + distanciaKm + " km]";
    }
}