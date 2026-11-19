package UNAM.ICO.balanceados;

import java.util.ArrayList;
import java.util.List;

/**
 * Árbol AVL implementado desde cero.
 * Política de duplicados: NO se insertan (insertar devuelve false).
 * La altura se mide en nodos (árbol vacío = 0, un solo nodo = 1).
 */
public class ArbolAVL<T extends Comparable<T>> {

    private NodoAVL<T> raiz;
    private int tamanio;
    private boolean modificado;                       // bandera interna de insertar/eliminar
    private int rotaciones;                           // contador de rotaciones simples
    private final List<String> bitacora = new ArrayList<>();

    // ---------------------------------------------------------------- inserción
    public boolean insertar(T valor) {
        modificado = false;
        raiz = insertar(raiz, valor);
        if (modificado) tamanio++;
        return modificado;
    }

    private NodoAVL<T> insertar(NodoAVL<T> n, T valor) {
        if (n == null) {
            modificado = true;
            return new NodoAVL<>(valor);
        }
        int c = valor.compareTo(n.valor);
        if (c < 0)      n.izq = insertar(n.izq, valor);
        else if (c > 0) n.der = insertar(n.der, valor);
        else            return n;                    // duplicado: se ignora
        return balancear(n);                         // se ejecuta al regresar de la recursión
    }

    // -------------------------------------------------------------- eliminación
    public boolean eliminar(T valor) {
        modificado = false;
        raiz = eliminar(raiz, valor);
        if (modificado) tamanio--;
        return modificado;
    }

    private NodoAVL<T> eliminar(NodoAVL<T> n, T valor) {
        if (n == null) return null;
        int c = valor.compareTo(n.valor);
        if (c < 0)      n.izq = eliminar(n.izq, valor);
        else if (c > 0) n.der = eliminar(n.der, valor);
        else {
            modificado = true;
            if (n.izq == null) return n.der;         // 0 o 1 hijo
            if (n.der == null) return n.izq;
            NodoAVL<T> sucesor = minimo(n.der);      // 2 hijos: se usa el sucesor inorden
            n.valor = sucesor.valor;
            n.der = eliminar(n.der, sucesor.valor);
        }
        return balancear(n);
    }

    private NodoAVL<T> minimo(NodoAVL<T> n) {
        while (n.izq != null) n = n.izq;
        return n;
    }

    // ----------------------------------------------------------------- balanceo
    private int altura(NodoAVL<T> n) { return n == null ? 0 : n.altura; }

    private void actualizar(NodoAVL<T> n) {
        n.altura = 1 + Math.max(altura(n.izq), altura(n.der));
    }

    private int factor(NodoAVL<T> n) {               // altura izq - altura der
        return n == null ? 0 : altura(n.izq) - altura(n.der);
    }

    private NodoAVL<T> balancear(NodoAVL<T> n) {
        actualizar(n);
        int fb = factor(n);
        if (fb > 1) {                                // pesado a la izquierda
            if (factor(n.izq) < 0) {
                registrar("Rotación doble izquierda-derecha (LR) en el nodo " + n.valor);
                n.izq = rotarIzq(n.izq);
            } else {
                registrar("Rotación simple derecha (LL) en el nodo " + n.valor);
            }
            return rotarDer(n);
        }
        if (fb < -1) {                               // pesado a la derecha
            if (factor(n.der) > 0) {
                registrar("Rotación doble derecha-izquierda (RL) en el nodo " + n.valor);
                n.der = rotarDer(n.der);
            } else {
                registrar("Rotación simple izquierda (RR) en el nodo " + n.valor);
            }
            return rotarIzq(n);
        }
        return n;
    }

    private NodoAVL<T> rotarDer(NodoAVL<T> y) {
        NodoAVL<T> x = y.izq;
        y.izq = x.der;
        x.der = y;
        actualizar(y);
        actualizar(x);
        rotaciones++;
        return x;
    }

    private NodoAVL<T> rotarIzq(NodoAVL<T> x) {
        NodoAVL<T> y = x.der;
        x.der = y.izq;
        y.izq = x;
        actualizar(x);
        actualizar(y);
        rotaciones++;
        return y;
    }

    // ----------------------------------------------------------------- búsqueda
    public ResultadoBusqueda buscar(T valor) {
        NodoAVL<T> actual = raiz;
        int visitados = 0;
        while (actual != null) {
            visitados++;
            int c = valor.compareTo(actual.valor);
            if (c == 0) return new ResultadoBusqueda(true, visitados);
            actual = c < 0 ? actual.izq : actual.der;
        }
        return new ResultadoBusqueda(false, visitados);
    }

    // ---------------------------------------------------------------- recorridos
    public List<T> inorden()   { List<T> l = new ArrayList<>(); inorden(raiz, l);   return l; }
    public List<T> preorden()  { List<T> l = new ArrayList<>(); preorden(raiz, l);  return l; }
    public List<T> postorden() { List<T> l = new ArrayList<>(); postorden(raiz, l); return l; }

    private void inorden(NodoAVL<T> n, List<T> l) {
        if (n == null) return;
        inorden(n.izq, l); l.add(n.valor); inorden(n.der, l);
    }
    private void preorden(NodoAVL<T> n, List<T> l) {
        if (n == null) return;
        l.add(n.valor); preorden(n.izq, l); preorden(n.der, l);
    }
    private void postorden(NodoAVL<T> n, List<T> l) {
        if (n == null) return;
        postorden(n.izq, l); postorden(n.der, l); l.add(n.valor);
    }

    // ------------------------------------------------------------ altura y datos
    public int altura()   { return altura(raiz); }
    public int tamanio()  { return tamanio; }
    public boolean estaVacio() { return raiz == null; }

    // ------------------------------------------------------ representación textual
    /** Dibuja el árbol girado 90°: el hijo derecho arriba, el izquierdo abajo. */
    public String mostrar() {
        if (raiz == null) return "(árbol vacío)\n";
        StringBuilder sb = new StringBuilder();
        dibujar(raiz, 0, sb);
        return sb.toString();
    }

    private void dibujar(NodoAVL<T> n, int nivel, StringBuilder sb) {
        if (n == null) return;
        dibujar(n.der, nivel + 1, sb);
        sb.append("      ".repeat(nivel)).append(n.valor)
          .append("  [h=").append(n.altura).append(", fb=").append(factor(n)).append("]\n");
        dibujar(n.izq, nivel + 1, sb);
    }

    // -------------------------------------------------------------- verificación
    /** Comprueba que se cumple el orden de un árbol binario de búsqueda. */
    public boolean verificarOrden() { return ordenado(raiz, null, null); }

    private boolean ordenado(NodoAVL<T> n, T min, T max) {
        if (n == null) return true;
        if (min != null && n.valor.compareTo(min) <= 0) return false;
        if (max != null && n.valor.compareTo(max) >= 0) return false;
        return ordenado(n.izq, min, n.valor) && ordenado(n.der, n.valor, max);
    }

    /** Comprueba que cada nodo tenga factor de equilibrio en {-1,0,1} y altura bien guardada. */
    public boolean verificarBalance() { return alturaReal(raiz) >= 0; }

    private int alturaReal(NodoAVL<T> n) {           // -1 si hay violación
        if (n == null) return 0;
        int l = alturaReal(n.izq), r = alturaReal(n.der);
        if (l < 0 || r < 0 || Math.abs(l - r) > 1) return -1;
        if (n.altura != 1 + Math.max(l, r)) return -1;
        return 1 + Math.max(l, r);
    }

    public boolean verificar() { return verificarOrden() && verificarBalance(); }

    // ----------------------------------------------------- bitácora y estadísticas
    private void registrar(String mensaje) { bitacora.add(mensaje); }
    public List<String> getBitacora()      { return new ArrayList<>(bitacora); }
    public void limpiarBitacora()          { bitacora.clear(); }
    public int getRotaciones()             { return rotaciones; }
    public void reiniciarContadores()      { rotaciones = 0; bitacora.clear(); }
}
