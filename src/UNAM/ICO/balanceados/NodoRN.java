package UNAM.ICO.balanceados;

/** Nodo del árbol Rojo-Negro: valor, color, hijos y puntero al padre. */
class NodoRN<T extends Comparable<T>> {
    T valor;
    boolean rojo = true;     // todo nodo nuevo nace rojo
    NodoRN<T> izq;
    NodoRN<T> der;
    NodoRN<T> padre;

    NodoRN(T valor) {
        this.valor = valor;
    }
}
