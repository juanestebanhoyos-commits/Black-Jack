# 21 Blackjack — Duelo contra el computador

Juego de escritorio en JavaFX donde retas al computador a una ronda de Blackjack
con fichas de apuesta. Constructor con Maven, en arquitectura Modelo–Vista–Controlador.

## Integrantes del equipo

JUAN ESTEBAN HOYOS ORTIZ — CÓDIGO: 2540054

DEINER MUÑOZ ARCE — CÓDIGO: 2257728

## Requisitos

* **JDK 27** (la versión con la que se compila el proyecto).
* Todo lo demás llega solo: JavaFX y JUnit se descargan con Maven, y el propio
  Maven viene incluido en el **wrapper** (`mvnw`). No hay que instalar nada.

## Instrucciones de ejecución

Correr las pruebas del modelo (5 pruebas):

```
.\mvnw.cmd test
```

Abrir la ventana del juego:

```
.\mvnw.cmd javafx:run
```

En **IntelliJ IDEA** también sirve: abrir el proyecto, esperar que cargue el Maven
y ejecutar la clase `com.example.blackjack.BlackjackApp` (botón ▶ sobre su método `start`).
