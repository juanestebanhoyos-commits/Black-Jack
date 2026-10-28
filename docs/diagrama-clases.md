# Diagrama de clases UML del 21 Blackjack

El proyecto sigue el patrón **Modelo–Vista–Controlador**:

* **Modelo** (`com.example.blackjack.modelo`): cartas, mazo, manos y las reglas del juego.
* **Vista** (`vista/blackjack.fxml`): describe la mesa con JavaFX (zonas, puntajes, botones).
* **Controlador** (`controlador/BlackjackControlador`): atiende los eventos de la vista,
  pide operaciones al modelo y repinta la mesa con las respuestas.

El diagrama se dibuja en IntelliJ o en <https://mermaid.live>:

```mermaid
classDiagram
    direction LR

    class BlackjackApp {
        +start(Stage)
    }
    class BlackjackControlador {
        -juego : Juego21
        +iniciarRonda()
        +pedirCarta()
        +plantarse()
        +verReglas()
        +cerrarJuego()
        +cierreConfirmado() boolean
        -refrescar()
        -dibujarMano(zona, mano)
        -vistaDeCarta(carta) ImageView
    }
    class Juego21 {
        +SALDO_INICIAL = 1000
        +APUESTA_MINIMA = 10
        -mazo : Mazo
        -manoJugador : Mano
        -manoComputador : Mano
        -saldo : int
        -apuesta : int
        -estado : Estado
        -resultado : Resultado
        +iniciarRonda(apuestaPedida)
        +pedirCarta()
        +plantarse()
        +validarApuesta(apuestaPedida) String
        +getSaldo() int
        +getApuesta() int
        +getPuntajeJugador() int
        +getPuntajeComputador() int
        +jugadorTieneVeintiuno() boolean
        +jugadorTieneVeinteYMedio() boolean
        -turnoDeLaBanca()
        -resolver() Resultado
        -terminarRonda(resultado)
    }
    class Estado {
        <<enumeration>>
        SIN_RONDA
        TURNO_JUGADOR
        RONDA_TERMINADA
    }
    class Resultado {
        <<enumeration>>
        VICTORIA
        VICTORIA_BLACKJACK
        DERROTA
        EMPATE
    }
    class Mazo {
        -cartas : Carta[52]
        -repartidas : int
        +repartirCarta() Carta
        +mezclar()
        +tieneCartas() boolean
    }
    class Mano {
        -inicio : NodoCarta
        -cantidad : int
        +agregarCarta(carta)
        +getPrimera() NodoCarta
        +getPuntaje() int
        +esBlackjack() boolean
        +esVeinteYMedio() boolean
    }
    class NodoCarta {
        -carta : Carta
        -siguiente : NodoCarta
        +getCarta() Carta
        +getSiguiente() NodoCarta
    }
    class Carta {
        -simbolo : Simbolo
        -rango : Rango
        +getValorBase() int
    }
    class Simbolo {
        <<enumeration>>
        PICAS
        CORAZONES
        DIAMANTES
        TREBOLES
    }
    class Rango {
        <<enumeration>>
        AS, DOS, TRES, CUATRO, CINCO
        SEIS, SIETE, OCHO, NUEVE, DIEZ
        JOTA, REINA, REY
        -valorBase : int
        +getValorBase() int
        +esFigura() boolean
    }

    BlackjackApp --> BlackjackControlador : carga y consulta el cierre
    BlackjackControlador --> Juego21 : pide operaciones
    Juego21 *-- "2" Mano : jugador y computador
    Juego21 --> Mazo : baraja por ronda
    Juego21 --> Estado
    Juego21 --> Resultado
    Mano o-- NodoCarta : lista enlazada
    NodoCarta --> Carta
    Carta --> Simbolo
    Carta --> Rango
```

Detalles de diseño que valen la pena señalar:

* `Mazo` guarda las 52 cartas en un **arreglo**; `Mano` es una **lista enlazada** hecha en clase
  (`NodoCarta`), tal como pide el enunciado.
* `Juego21` administra el saldo, la apuesta vigente, el turno y el resultado: la vista solo muestra
  y el controlador solo traduce.
* Los constructores `Juego21(Mazo)` y `Mazo(Carta[])` son de acceso de paquete porque solo
  las pruebas los usan para fijar el orden de las cartas.
