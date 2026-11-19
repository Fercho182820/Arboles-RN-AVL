package UNAM.ICO.balanceados;

import java.util.List;

/** Programa principal: ejecuta las Pruebas 1 a 4 de la actividad para AVL y Rojo-Negro. */
public class Main {

    static final int[] SECUENCIA = {30, 20, 10, 25, 40, 50, 22, 5, 35, 45};
    static final int[] ELIMINAR  = {10, 40, 30};
    static final int[] BUSCAR    = {5, 22, 35, 100};

    public static void main(String[] args) {
        ArbolAVL<Integer> avl = new ArbolAVL<>();
        ArbolRN<Integer> rn = new ArbolRN<>();

        titulo("PRUEBA 1: INSERCIÓN  " + java.util.Arrays.toString(SECUENCIA));
        for (int v : SECUENCIA) {
            avl.limpiarBitacora();
            rn.limpiarBitacora();
            avl.insertar(v);
            rn.insertar(v);
            System.out.println("Insertar " + v);
            imprimirBitacora("  AVL", avl.getBitacora());
            imprimirBitacora("  R-N", rn.getBitacora());
        }
        resumen(avl, rn);

        titulo("PRUEBA 2: ELIMINACIÓN  " + java.util.Arrays.toString(ELIMINAR));
        for (int v : ELIMINAR) {
            avl.limpiarBitacora();
            rn.limpiarBitacora();
            avl.eliminar(v);
            rn.eliminar(v);
            System.out.println("\n>>> Eliminar " + v);
            imprimirBitacora("  AVL", avl.getBitacora());
            imprimirBitacora("  R-N", rn.getBitacora());
            resumen(avl, rn);
        }

        titulo("PRUEBA 3: BÚSQUEDA  " + java.util.Arrays.toString(BUSCAR));
        ArbolAVL<Integer> avl3 = new ArbolAVL<>();
        ArbolRN<Integer> rn3 = new ArbolRN<>();
        for (int v : SECUENCIA) { avl3.insertar(v); rn3.insertar(v); }
        System.out.println("(se usa el árbol de la Prueba 1, antes de eliminar)");
        System.out.printf("%-8s %-22s %-22s%n", "Valor", "AVL (¿hallado?, nodos)", "R-N (¿hallado?, nodos)");
        for (int v : BUSCAR) {
            ResultadoBusqueda a = avl3.buscar(v);
            ResultadoBusqueda r = rn3.buscar(v);
            System.out.printf("%-8d %-22s %-22s%n", v,
                    (a.encontrado() ? "sí" : "no") + ", " + a.nodosVisitados(),
                    (r.encontrado() ? "sí" : "no") + ", " + r.nodosVisitados());
        }

        titulo("PRUEBA 4: VERIFICACIÓN DE PROPIEDADES (árboles tras la Prueba 1)");
        System.out.println("AVL: orden BST = " + avl3.verificarOrden()
                + " | factor de equilibrio en [-1,1] = " + avl3.verificarBalance());
        System.out.println("R-N: orden BST = " + rn3.verificarOrden()
                + " | propiedades de color y altura negra = " + rn3.verificarPropiedades());
        System.out.println("(Para pruebas más fuertes, ejecuta PruebasAutomaticas.)");
    }

    static void resumen(ArbolAVL<Integer> avl, ArbolRN<Integer> rn) {
        System.out.println("\n--- AVL ---");
        System.out.print(avl.mostrar());
        System.out.println("Inorden:   " + avl.inorden());
        System.out.println("Preorden:  " + avl.preorden());
        System.out.println("Altura: " + avl.altura() + " | Rotaciones acumuladas: " + avl.getRotaciones()
                + " | ¿Cumple propiedades? " + avl.verificar());
        System.out.println("\n--- ROJO-NEGRO ---");
        System.out.print(rn.mostrar());
        System.out.println("Inorden:   " + rn.inorden());
        System.out.println("Preorden:  " + rn.preorden());
        System.out.println("Altura: " + rn.altura() + " | Altura negra: " + rn.alturaNegra()
                + " | Rotaciones: " + rn.getRotaciones() + " | Recoloraciones: " + rn.getRecoloraciones()
                + " | ¿Cumple propiedades? " + rn.verificar());
    }

    static void imprimirBitacora(String etiqueta, List<String> b) {
        if (b.isEmpty()) {
            System.out.println(etiqueta + ": sin rotaciones ni recoloraciones");
        } else {
            for (String s : b) System.out.println(etiqueta + ": " + s);
        }
    }

    static void titulo(String t) {
        System.out.println("\n==================================================");
        System.out.println(t);
        System.out.println("==================================================");
    }
}
