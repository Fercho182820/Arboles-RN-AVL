package UNAM.ICO.balanceados;

import java.io.PrintWriter;
import java.util.*;

/**
 * Análisis experimental: mide altura, rotaciones y tiempos de inserción, búsqueda y eliminación
 * para AVL y Rojo-Negro con datos aleatorios y ascendentes. Genera resultados.csv.
 */
public class Benchmark {

    static final int[] TAMANIOS = {1_000, 5_000, 10_000, 50_000, 100_000};
    static final int REPETICIONES = 5;     // se reporta la mediana

    public static void main(String[] args) throws Exception {
        calentar();
        try (PrintWriter csv = new PrintWriter("resultados.csv", "UTF-8")) {
            csv.println("estructura,patron,n,altura,rotaciones,recoloraciones,"
                    + "insertar_ms,buscar_ms,eliminar_ms");
            System.out.printf("%-6s %-11s %8s %7s %9s %8s %8s %8s%n",
                    "Tipo", "Patrón", "n", "Altura", "Rotac.", "Ins(ms)", "Bus(ms)", "Eli(ms)");
            for (String patron : new String[]{"aleatorio", "ascendente"}) {
                for (int n : TAMANIOS) {
                    int[] datos = generar(patron, n);
                    medir("AVL", patron, n, datos, csv);
                    medir("RN", patron, n, datos, csv);
                }
            }
        }
        System.out.println("\nResultados guardados en resultados.csv");
    }

    static int[] generar(String patron, int n) {
        int[] d = new int[n];
        for (int i = 0; i < n; i++) d[i] = i;
        if (patron.equals("aleatorio")) {
            Random r = new Random(42);
            for (int i = n - 1; i > 0; i--) {
                int j = r.nextInt(i + 1);
                int t = d[i]; d[i] = d[j]; d[j] = t;
            }
        }
        return d;
    }

    static void medir(String tipo, String patron, int n, int[] datos, PrintWriter csv) {
        long[] ins = new long[REPETICIONES], bus = new long[REPETICIONES], eli = new long[REPETICIONES];
        int altura = 0, rot = 0, rec = 0;
        for (int rep = 0; rep < REPETICIONES; rep++) {
            if (tipo.equals("AVL")) {
                ArbolAVL<Integer> a = new ArbolAVL<>();
                long t0 = System.nanoTime();
                for (int v : datos) a.insertar(v);
                ins[rep] = System.nanoTime() - t0;
                altura = a.altura(); rot = a.getRotaciones();
                t0 = System.nanoTime();
                int hallados = 0;
                for (int v : datos) if (a.buscar(v).encontrado()) hallados++;
                bus[rep] = System.nanoTime() - t0;
                if (hallados != n) throw new IllegalStateException("búsqueda incorrecta");
                t0 = System.nanoTime();
                for (int v : datos) a.eliminar(v);
                eli[rep] = System.nanoTime() - t0;
            } else {
                ArbolRN<Integer> a = new ArbolRN<>();
                long t0 = System.nanoTime();
                for (int v : datos) a.insertar(v);
                ins[rep] = System.nanoTime() - t0;
                altura = a.altura(); rot = a.getRotaciones(); rec = a.getRecoloraciones();
                t0 = System.nanoTime();
                int hallados = 0;
                for (int v : datos) if (a.buscar(v).encontrado()) hallados++;
                bus[rep] = System.nanoTime() - t0;
                if (hallados != n) throw new IllegalStateException("búsqueda incorrecta");
                t0 = System.nanoTime();
                for (int v : datos) a.eliminar(v);
                eli[rep] = System.nanoTime() - t0;
            }
        }
        double i = mediana(ins), b = mediana(bus), e = mediana(eli);
        System.out.printf("%-6s %-11s %8d %7d %9d %8.2f %8.2f %8.2f%n", tipo, patron, n, altura, rot, i, b, e);
        csv.printf(Locale.US, "%s,%s,%d,%d,%d,%d,%.3f,%.3f,%.3f%n", tipo, patron, n, altura, rot, rec, i, b, e);
    }

    static double mediana(long[] nanos) {
        long[] c = nanos.clone();
        Arrays.sort(c);
        return c[c.length / 2] / 1_000_000.0;
    }

    /** Calentamiento de la JVM para que las primeras mediciones no salgan infladas. */
    static void calentar() {
        for (int k = 0; k < 5; k++) {
            ArbolAVL<Integer> a = new ArbolAVL<>();
            ArbolRN<Integer> r = new ArbolRN<>();
            for (int i = 0; i < 20_000; i++) { a.insertar(i * 7 % 20011); r.insertar(i * 7 % 20011); }
            for (int i = 0; i < 20_000; i++) { a.eliminar(i * 7 % 20011); r.eliminar(i * 7 % 20011); }
        }
    }
}
