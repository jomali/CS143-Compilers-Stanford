# CS143-COMPILERS-STANFORD

## Introducción

**Cool**, acrónimo de **Classroom Object-Oriented Language**, es un pequeño lenguaje de programación diseñado para ser utilizado en el curso de pregrado sobre compiladores en la Universidad de Stanford. Aunque es lo suficientemente pequeño como para poder ser implementado a lo largo de un único curso, incluye muchas de las características de otros lenguajes de programación modernos como objetos, tipado estático y manejo automático de memoria. Genera código para el simulador MIPS.

Más información:

* [The Cool Reference Manual](https://lagunita.stanford.edu/assets/courseware/v1/27e1a38f1161e61d91c25a4b1805489b/c4x/Engineering/Compilers/asset/cool_manual.pdf)
* [A Tour of the Cool Support Code](https://lagunita.stanford.edu/assets/courseware/v1/115f9c1f48cffa3192f23dc37c3a4eee/c4x/Engineering/Compilers/asset/cool-tour.pdf)


## Relación de tareas

#### PA1: Ejemplos de programación en COOL

Se ha desarrollado el siguiente conjunto de programas de prueba en COOL.

* **p1_1.cl**: Programa "Hola Mundo".
* **p1_1b.cl**: Programa "Hola Mundo" alternativo.
* **p1_2.cl**: Programa con un bucle `while`.
* **p1_3.cl**: Programa para tomar datos de teclado.
* **p1_4.cl**: Ejemplo de uso de varias clases distintas, herencia, self y SELF_TYPE.
* **p1_5.cl**: Programa con un bucle `if`.
* **p1_6.cl**: Programa con operaciones aritméticas.

Completado.


#### P2: Análisis léxico

El **análisis léxico** es la primera fase de un compilador. Un analizador léxico, también llamado *scanner*, se encarga de dividir el código fuente en una serie de *tokens* y transmitir los datos al *parser* cuando éste lo necesita. Para desarrollar este apartado se utiliza una herramienta generadora de analizadores léxicos **Flex/JLex**. Flex genera, a partir de un fichero de reglas, la implementación en C++ de un autámata finito que reconoce las expresiones regulares espeficadas en el fichero de reglas. JLex hace lo propio para Java.

Pendiente.

#### P3: Parsing

Pendiente.

#### P4: Análisis semántico

Pendiente.

#### P5: Generación de código

Pendiente.
