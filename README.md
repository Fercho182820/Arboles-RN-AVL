# Árboles AVL y Rojo-Negro (UNAM · ICO · Estructura de datos)

Implementación desde cero, en Java, de un árbol AVL y un árbol Rojo-Negro (versión clásica con centinela NIL),
sin usar bibliotecas de árboles autobalanceados.

## Estructura
```
src/UNAM/ICO/balanceados/
  NodoAVL.java / ArbolAVL.java     Árbol AVL (altura por nodo, rotaciones LL, RR, LR, RL)
  NodoRN.java  / ArbolRN.java      Árbol Rojo-Negro (color, padre, NIL; casos 1-3 de inserción y 1-4 de eliminación)
  ResultadoBusqueda.java           Resultado de buscar: encontrado + nodos visitados
  Main.java                        Pruebas 1 a 4 de la actividad
  PruebasAutomaticas.java          Pruebas automatizadas (sin dependencias)
  Benchmark.java                   Análisis experimental, genera resultados.csv
```

## Operaciones (ambos árboles)
insertar, eliminar, buscar, inorden, preorden, postorden, altura, tamanio, mostrar (texto girado 90°),
verificar (orden BST + propiedades propias de cada árbol), bitácora de rotaciones y recoloraciones.

## Decisiones de diseño
- **Genéricos:** `ArbolAVL<T extends Comparable<T>>` y `ArbolRN<T extends Comparable<T>>`.
- **Duplicados:** no se insertan; `insertar` devuelve `false`.
- **Altura:** se mide en nodos (vacío = 0, un nodo = 1).
- **Eliminación con dos hijos:** se reemplaza por el sucesor inorden.
- **Independientes:** cada árbol tiene su propio nodo y no comparten clase base.

## Cómo ejecutar
Con IntelliJ: abre el proyecto, ejecuta la clase deseada (`Main`, `PruebasAutomaticas` o `Benchmark`).

Con terminal (JDK 17 o superior), desde la raíz del proyecto:
```
javac -encoding UTF-8 -d out src/UNAM/ICO/balanceados/*.java
java -cp out UNAM.ICO.balanceados.Main
java -cp out UNAM.ICO.balanceados.PruebasAutomaticas
java -cp out UNAM.ICO.balanceados.Benchmark
```
Si ves símbolos raros en la consola, ejecuta con `-Dfile.encoding=UTF-8`.

## Pruebas
`PruebasAutomaticas` comprueba secuencia oficial, duplicados, búsqueda, inserción ascendente (peor caso de un BST),
y miles de inserciones/eliminaciones aleatorias verificando las propiedades tras cada operación, contra un
`HashSet` usado solo como referencia.
