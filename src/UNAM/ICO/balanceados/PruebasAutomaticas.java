package UNAM.ICO.balanceados;

import java.util.*;

/**
 * Pruebas automatizadas sin dependencias externas (se ejecuta con main).
 * Como "oráculo" se usa un HashSet + lista ordenada, nunca una estructura autobalanceada de Java.
 */
public class PruebasAutomaticas {

    static int pasadas = 0, fallidas = 0;

    static void comprobar(String nombre, boolean condicion) {
        if (condicion) { pasadas++; }
        else { fallidas++; System.out.println("FALLÓ: " + nombre); }
    }

    public static void main(String[] args) {
        pruebaSecuenciaOficial();
        pruebaDuplicados();
        pruebaBusqueda();
        pruebaAscendenteYAlturas();
        pruebaAleatoriaContraOraculo(1L);
        pruebaAleatoriaContraOraculo(2L);
        pruebaAleatoriaContraOraculo(3L);
        pruebaVaciarTodo();

        System.out.println("\nPruebas pasadas: " + pasadas + " | fallidas: " + fallidas);
        System.exit(fallidas == 0 ? 0 : 1);
    }

    static void pruebaSecuenciaOficial() {
        ArbolAVL<Integer> avl = new ArbolAVL<>();
        ArbolRN<Integer> rn = new ArbolRN<>();
        for (int v : Main.SECUENCIA) { avl.insertar(v); rn.insertar(v); }
        List<Integer> esperado = new ArrayList<>();
        for (int v : Main.SECUENCIA) esperado.add(v);
        Collections.sort(esperado);
        comprobar("AVL inorden ordenado", avl.inorden().equals(esperado));
        comprobar("RN inorden ordenado", rn.inorden().equals(esperado));
        comprobar("AVL propiedades", avl.verificar());
        comprobar("RN propiedades", rn.verificar());
        for (int v : Main.ELIMINAR) {
            comprobar("AVL elimina " + v, avl.eliminar(v));
            comprobar("RN elimina " + v, rn.eliminar(v));
            comprobar("AVL propiedades tras eliminar " + v, avl.verificar());
            comprobar("RN propiedades tras eliminar " + v, rn.verificar());
        }
        comprobar("AVL tamaño final", avl.tamanio() == 7);
        comprobar("RN tamaño final", rn.tamanio() == 7);
    }

    static void pruebaDuplicados() {
        ArbolAVL<Integer> avl = new ArbolAVL<>();
        ArbolRN<Integer> rn = new ArbolRN<>();
        comprobar("AVL primer insert", avl.insertar(5));
        comprobar("AVL duplicado rechazado", !avl.insertar(5));
        comprobar("RN primer insert", rn.insertar(5));
        comprobar("RN duplicado rechazado", !rn.insertar(5));
        comprobar("AVL tamaño tras duplicado", avl.tamanio() == 1);
        comprobar("RN tamaño tras duplicado", rn.tamanio() == 1);
        comprobar("AVL eliminar inexistente", !avl.eliminar(99));
        comprobar("RN eliminar inexistente", !rn.eliminar(99));
    }

    static void pruebaBusqueda() {
        ArbolAVL<Integer> avl = new ArbolAVL<>();
        ArbolRN<Integer> rn = new ArbolRN<>();
        for (int v : Main.SECUENCIA) { avl.insertar(v); rn.insertar(v); }
        for (int v : new int[]{5, 22, 35}) {
            comprobar("AVL encuentra " + v, avl.buscar(v).encontrado());
            comprobar("RN encuentra " + v, rn.buscar(v).encontrado());
        }
        comprobar("AVL no encuentra 100", !avl.buscar(100).encontrado());
        comprobar("RN no encuentra 100", !rn.buscar(100).encontrado());
        comprobar("AVL nodos visitados <= altura", avl.buscar(100).nodosVisitados() <= avl.altura());
        comprobar("RN nodos visitados <= altura", rn.buscar(100).nodosVisitados() <= rn.altura());
    }

    static void pruebaAscendenteYAlturas() {
        int n = 1000;
        ArbolAVL<Integer> avl = new ArbolAVL<>();
        ArbolRN<Integer> rn = new ArbolRN<>();
        for (int i = 1; i <= n; i++) { avl.insertar(i); rn.insertar(i); }
        comprobar("AVL ascendente válido", avl.verificar());
        comprobar("RN ascendente válido", rn.verificar());
        double log = Math.log(n + 2) / Math.log(2);
        comprobar("AVL altura <= 1.45 log2(n+2)", avl.altura() <= 1.45 * log);
        comprobar("RN altura <= 2 log2(n+1)", rn.altura() <= 2 * (Math.log(n + 1) / Math.log(2)));
    }

    /** Mezcla aleatoria de inserciones y eliminaciones, verificando propiedades tras CADA operación. */
    static void pruebaAleatoriaContraOraculo(long semilla) {
        Random rnd = new Random(semilla);
        ArbolAVL<Integer> avl = new ArbolAVL<>();
        ArbolRN<Integer> rn = new ArbolRN<>();
        Set<Integer> oraculo = new HashSet<>();
        boolean todoBien = true;
        for (int paso = 0; paso < 4000 && todoBien; paso++) {
            int v = rnd.nextInt(300);
            if (rnd.nextInt(100) < 55) {
                boolean nuevo = oraculo.add(v);
                todoBien &= avl.insertar(v) == nuevo;
                todoBien &= rn.insertar(v) == nuevo;
            } else {
                boolean existia = oraculo.remove(v);
                todoBien &= avl.eliminar(v) == existia;
                todoBien &= rn.eliminar(v) == existia;
            }
            todoBien &= avl.verificar() && rn.verificar();
            todoBien &= avl.tamanio() == oraculo.size() && rn.tamanio() == oraculo.size();
        }
        List<Integer> ordenado = new ArrayList<>(oraculo);
        Collections.sort(ordenado);
        comprobar("aleatoria semilla " + semilla + " (propiedades tras cada operación)", todoBien);
        comprobar("aleatoria semilla " + semilla + " AVL inorden", avl.inorden().equals(ordenado));
        comprobar("aleatoria semilla " + semilla + " RN inorden", rn.inorden().equals(ordenado));
    }

    static void pruebaVaciarTodo() {
        ArbolAVL<Integer> avl = new ArbolAVL<>();
        ArbolRN<Integer> rn = new ArbolRN<>();
        for (int i = 0; i < 200; i++) { avl.insertar(i); rn.insertar(i); }
        boolean ok = true;
        for (int i = 0; i < 200; i++) {
            ok &= avl.eliminar(i) && rn.eliminar(i) && avl.verificar() && rn.verificar();
        }
        comprobar("vaciar todo conserva propiedades", ok);
        comprobar("árboles vacíos", avl.estaVacio() && rn.estaVacio());
    }
}
