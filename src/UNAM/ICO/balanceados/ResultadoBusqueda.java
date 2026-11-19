package UNAM.ICO.balanceados;

/** Resultado de una búsqueda: si se encontró y cuántos nodos se visitaron. */
public record ResultadoBusqueda(boolean encontrado, int nodosVisitados) { }
