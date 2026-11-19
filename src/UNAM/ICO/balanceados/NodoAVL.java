package UNAM.ICO.balanceados;

/** Nodo del árbol AVL. Además del valor y los hijos, guarda su altura. */
class NodoAVL<T extends Comparable<T>> {
    T valor;
    NodoAVL<T> izq;
    NodoAVL<T> der;
    int altura = 1;          // un nodo hoja mide 1 (se cuenta en nodos)

    NodoAVL(T valor) {
        this.valor = valor;
    }
}
