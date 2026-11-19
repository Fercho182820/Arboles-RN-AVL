package UNAM.ICO.balanceados;

import java.util.ArrayList;
import java.util.List;

/**
 * Árbol Rojo-Negro clásico (versión con centinela NIL, estilo CLRS).
 * Política de duplicados: NO se insertan (insertar devuelve false).
 * La altura se mide en nodos reales (árbol vacío = 0).
 */
public class ArbolRN<T extends Comparable<T>> {

    private final NodoRN<T> NIL = new NodoRN<>(null);   // centinela: todas las hojas lo apuntan
    private NodoRN<T> raiz;
    private int tamanio;
    private int rotaciones;
    private int recoloraciones;
    private final List<String> bitacora = new ArrayList<>();

    public ArbolRN() {
        NIL.rojo = false;                                // NIL siempre es negro
        NIL.izq = NIL.der = NIL.padre = NIL;
        raiz = NIL;
    }

    // ---------------------------------------------------------------- rotaciones
    private void rotarIzq(NodoRN<T> x) {
        NodoRN<T> y = x.der;
        x.der = y.izq;
        if (y.izq != NIL) y.izq.padre = x;
        y.padre = x.padre;
        if (x.padre == NIL)         raiz = y;
        else if (x == x.padre.izq)  x.padre.izq = y;
        else                        x.padre.der = y;
        y.izq = x;
        x.padre = y;
        rotaciones++;
    }

    private void rotarDer(NodoRN<T> x) {
        NodoRN<T> y = x.izq;
        x.izq = y.der;
        if (y.der != NIL) y.der.padre = x;
        y.padre = x.padre;
        if (x.padre == NIL)         raiz = y;
        else if (x == x.padre.der)  x.padre.der = y;
        else                        x.padre.izq = y;
        y.der = x;
        x.padre = y;
        rotaciones++;
    }

    // ---------------------------------------------------------------- inserción
    public boolean insertar(T valor) {
        NodoRN<T> padre = NIL;
        NodoRN<T> actual = raiz;
        while (actual != NIL) {                          // 1. bajar recordando al padre
            padre = actual;
            int c = valor.compareTo(actual.valor);
            if (c == 0) return false;                    // duplicado: se ignora
            actual = c < 0 ? actual.izq : actual.der;
        }
        NodoRN<T> z = new NodoRN<>(valor);               // 2. colgar el nodo rojo
        z.izq = z.der = NIL;
        z.padre = padre;
        if (padre == NIL)                       raiz = z;
        else if (valor.compareTo(padre.valor) < 0) padre.izq = z;
        else                                    padre.der = z;
        tamanio++;
        arreglarInsercion(z);                            // 3. corregir hacia arriba
        return true;
    }

    private void arreglarInsercion(NodoRN<T> z) {
        while (z.padre.rojo) {                           // dos rojos seguidos
            NodoRN<T> abuelo = z.padre.padre;
            if (z.padre == abuelo.izq) {
                NodoRN<T> tio = abuelo.der;
                if (tio.rojo) {                          // CASO 1: tío rojo
                    registrar("Inserción, caso 1 (tío rojo): recolorear padre " + z.padre.valor
                            + " y tío " + tio.valor + " a negro, abuelo " + abuelo.valor + " a rojo");
                    z.padre.rojo = false;
                    tio.rojo = false;
                    abuelo.rojo = true;
                    recoloraciones += 3;
                    z = abuelo;
                } else {
                    if (z == z.padre.der) {              // CASO 2: zig-zag
                        z = z.padre;
                        registrar("Inserción, caso 2 (zig-zag): rotación izquierda en " + z.valor);
                        rotarIzq(z);
                    }
                    registrar("Inserción, caso 3 (línea recta): recolorear y rotación derecha en "
                            + z.padre.padre.valor);
                    z.padre.rojo = false;
                    z.padre.padre.rojo = true;
                    recoloraciones += 2;
                    rotarDer(z.padre.padre);
                }
            } else {                                     // espejo
                NodoRN<T> tio = abuelo.izq;
                if (tio.rojo) {
                    registrar("Inserción, caso 1 (tío rojo): recolorear padre " + z.padre.valor
                            + " y tío " + tio.valor + " a negro, abuelo " + abuelo.valor + " a rojo");
                    z.padre.rojo = false;
                    tio.rojo = false;
                    abuelo.rojo = true;
                    recoloraciones += 3;
                    z = abuelo;
                } else {
                    if (z == z.padre.izq) {
                        z = z.padre;
                        registrar("Inserción, caso 2 (zig-zag): rotación derecha en " + z.valor);
                        rotarDer(z);
                    }
                    registrar("Inserción, caso 3 (línea recta): recolorear y rotación izquierda en "
                            + z.padre.padre.valor);
                    z.padre.rojo = false;
                    z.padre.padre.rojo = true;
                    recoloraciones += 2;
                    rotarIzq(z.padre.padre);
                }
            }
        }
        if (raiz.rojo) {
            raiz.rojo = false;                           // la raíz siempre negra
            recoloraciones++;
        }
    }

    // -------------------------------------------------------------- eliminación
    private void trasplantar(NodoRN<T> u, NodoRN<T> v) {
        if (u.padre == NIL)         raiz = v;
        else if (u == u.padre.izq)  u.padre.izq = v;
        else                        u.padre.der = v;
        v.padre = u.padre;                               // también funciona si v es NIL
    }

    private NodoRN<T> minimo(NodoRN<T> n) {
        while (n.izq != NIL) n = n.izq;
        return n;
    }

    public boolean eliminar(T valor) {
        NodoRN<T> z = buscarNodo(valor);
        if (z == NIL) return false;

        NodoRN<T> y = z;
        NodoRN<T> x;
        boolean colorOriginal = y.rojo;
        if (z.izq == NIL) {
            x = z.der;
            trasplantar(z, z.der);
        } else if (z.der == NIL) {
            x = z.izq;
            trasplantar(z, z.izq);
        } else {
            y = minimo(z.der);                           // sucesor inorden
            colorOriginal = y.rojo;
            x = y.der;
            if (y.padre == z) {
                x.padre = y;
            } else {
                trasplantar(y, y.der);
                y.der = z.der;
                y.der.padre = y;
            }
            trasplantar(z, y);
            y.izq = z.izq;
            y.izq.padre = y;
            y.rojo = z.rojo;
        }
        tamanio--;
        if (!colorOriginal) {
            registrar("Eliminación de " + valor + ": se quitó un nodo negro, se corrigen las propiedades");
            arreglarEliminacion(x);
        } else {
            registrar("Eliminación de " + valor + ": el nodo quitado era rojo, no hay que corregir");
        }
        NIL.padre = NIL;                                 // limpiar el rastro temporal en NIL
        return true;
    }

    private void arreglarEliminacion(NodoRN<T> x) {
        while (x != raiz && !x.rojo) {
            if (x == x.padre.izq) {
                NodoRN<T> w = x.padre.der;               // hermano
                if (w.rojo) {                            // CASO 1: hermano rojo
                    registrar("Eliminación, caso 1 (hermano rojo): recolorear y rotación izquierda en " + x.padre.valor);
                    w.rojo = false;
                    x.padre.rojo = true;
                    recoloraciones += 2;
                    rotarIzq(x.padre);
                    w = x.padre.der;
                }
                if (!w.izq.rojo && !w.der.rojo) {        // CASO 2: sobrinos negros
                    registrar("Eliminación, caso 2 (hermano negro, sobrinos negros): hermano " + w.valor + " a rojo");
                    w.rojo = true;
                    recoloraciones++;
                    x = x.padre;
                } else {
                    if (!w.der.rojo) {                   // CASO 3: sobrino lejano negro
                        registrar("Eliminación, caso 3: recolorear y rotación derecha en " + w.valor);
                        w.izq.rojo = false;
                        w.rojo = true;
                        recoloraciones += 2;
                        rotarDer(w);
                        w = x.padre.der;
                    }
                    registrar("Eliminación, caso 4: recolorear y rotación izquierda en " + x.padre.valor);
                    w.rojo = x.padre.rojo;
                    x.padre.rojo = false;
                    w.der.rojo = false;
                    recoloraciones += 3;
                    rotarIzq(x.padre);
                    x = raiz;
                }
            } else {                                     // espejo
                NodoRN<T> w = x.padre.izq;
                if (w.rojo) {
                    registrar("Eliminación, caso 1 (hermano rojo): recolorear y rotación derecha en " + x.padre.valor);
                    w.rojo = false;
                    x.padre.rojo = true;
                    recoloraciones += 2;
                    rotarDer(x.padre);
                    w = x.padre.izq;
                }
                if (!w.der.rojo && !w.izq.rojo) {
                    registrar("Eliminación, caso 2 (hermano negro, sobrinos negros): hermano " + w.valor + " a rojo");
                    w.rojo = true;
                    recoloraciones++;
                    x = x.padre;
                } else {
                    if (!w.izq.rojo) {
                        registrar("Eliminación, caso 3: recolorear y rotación izquierda en " + w.valor);
                        w.der.rojo = false;
                        w.rojo = true;
                        recoloraciones += 2;
                        rotarIzq(w);
                        w = x.padre.izq;
                    }
                    registrar("Eliminación, caso 4: recolorear y rotación derecha en " + x.padre.valor);
                    w.rojo = x.padre.rojo;
                    x.padre.rojo = false;
                    w.izq.rojo = false;
                    recoloraciones += 3;
                    rotarDer(x.padre);
                    x = raiz;
                }
            }
        }
        x.rojo = false;
    }

    // ----------------------------------------------------------------- búsqueda
    private NodoRN<T> buscarNodo(T valor) {
        NodoRN<T> actual = raiz;
        while (actual != NIL) {
            int c = valor.compareTo(actual.valor);
            if (c == 0) return actual;
            actual = c < 0 ? actual.izq : actual.der;
        }
        return NIL;
    }

    public ResultadoBusqueda buscar(T valor) {
        NodoRN<T> actual = raiz;
        int visitados = 0;
        while (actual != NIL) {
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

    private void inorden(NodoRN<T> n, List<T> l) {
        if (n == NIL) return;
        inorden(n.izq, l); l.add(n.valor); inorden(n.der, l);
    }
    private void preorden(NodoRN<T> n, List<T> l) {
        if (n == NIL) return;
        l.add(n.valor); preorden(n.izq, l); preorden(n.der, l);
    }
    private void postorden(NodoRN<T> n, List<T> l) {
        if (n == NIL) return;
        postorden(n.izq, l); postorden(n.der, l); l.add(n.valor);
    }

    // ------------------------------------------------------------ altura y datos
    public int altura() { return altura(raiz); }

    private int altura(NodoRN<T> n) {
        return n == NIL ? 0 : 1 + Math.max(altura(n.izq), altura(n.der));
    }

    /** Altura negra: nodos negros desde la raíz hasta una hoja NIL (sin contar NIL). */
    public int alturaNegra() {
        int r = alturaNegra(raiz);
        return r < 0 ? -1 : r - 1;
    }

    public int tamanio()  { return tamanio; }
    public boolean estaVacio() { return raiz == NIL; }

    // ------------------------------------------------------ representación textual
    /** Dibuja el árbol girado 90°: hijo derecho arriba. (R)=rojo, (N)=negro. */
    public String mostrar() {
        if (raiz == NIL) return "(árbol vacío)\n";
        StringBuilder sb = new StringBuilder();
        dibujar(raiz, 0, sb);
        return sb.toString();
    }

    private void dibujar(NodoRN<T> n, int nivel, StringBuilder sb) {
        if (n == NIL) return;
        dibujar(n.der, nivel + 1, sb);
        sb.append("      ".repeat(nivel)).append(n.valor).append(n.rojo ? " (R)" : " (N)").append("\n");
        dibujar(n.izq, nivel + 1, sb);
    }

    // -------------------------------------------------------------- verificación
    public boolean verificarOrden() { return ordenado(raiz, null, null); }

    private boolean ordenado(NodoRN<T> n, T min, T max) {
        if (n == NIL) return true;
        if (min != null && n.valor.compareTo(min) <= 0) return false;
        if (max != null && n.valor.compareTo(max) >= 0) return false;
        return ordenado(n.izq, min, n.valor) && ordenado(n.der, n.valor, max);
    }

    /**
     * Comprueba las propiedades Rojo-Negro:
     * (1) cada nodo es rojo o negro (implícito), (2) la raíz es negra, (3) las hojas NIL son negras,
     * (4) un nodo rojo no tiene hijos rojos, (5) todos los caminos tienen igual cantidad de nodos negros.
     * También revisa que los punteros al padre sean coherentes.
     */
    public boolean verificarPropiedades() {
        if (NIL.rojo) return false;                       // propiedad 3
        if (raiz == NIL) return true;
        if (raiz.rojo) return false;                      // propiedad 2
        return alturaNegra(raiz) >= 0 && padresCoherentes(raiz);   // propiedades 4 y 5
    }

    private int alturaNegra(NodoRN<T> n) {                // -1 si hay violación
        if (n == NIL) return 1;
        if (n.rojo && (n.izq.rojo || n.der.rojo)) return -1;
        int l = alturaNegra(n.izq), r = alturaNegra(n.der);
        if (l < 0 || r < 0 || l != r) return -1;
        return l + (n.rojo ? 0 : 1);
    }

    private boolean padresCoherentes(NodoRN<T> n) {
        if (n == NIL) return true;
        if (n.izq != NIL && n.izq.padre != n) return false;
        if (n.der != NIL && n.der.padre != n) return false;
        return padresCoherentes(n.izq) && padresCoherentes(n.der);
    }

    public boolean verificar() { return verificarOrden() && verificarPropiedades(); }

    // ----------------------------------------------------- bitácora y estadísticas
    private void registrar(String mensaje) { bitacora.add(mensaje); }
    public List<String> getBitacora()      { return new ArrayList<>(bitacora); }
    public void limpiarBitacora()          { bitacora.clear(); }
    public int getRotaciones()             { return rotaciones; }
    public int getRecoloraciones()         { return recoloraciones; }
    public void reiniciarContadores()      { rotaciones = 0; recoloraciones = 0; bitacora.clear(); }
}
