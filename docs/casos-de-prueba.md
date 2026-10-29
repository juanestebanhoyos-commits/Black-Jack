# Casos de prueba del 21 Blackjack

Los casos demuestran cada punto de la sección 10 del enunciado. Todos corren con:

```
mvnw test        # 5 pruebas: Juego21Test (3), ManoTest (1), MazoTest (1)
```

La primera tabla tiene **una fila por cada método de prueba**; los casos que agrupa se
verifican dentro de ese mismo método. Para reproducir jugadas concretas, `Juego21Test`
arma un **mazo de orden fijo** (`mazoFijo(Rango...)`), así que las cartas que recibe
cada mano no dependen del azar.

## Pruebas del modelo (mvnw test)

| Prueba | Datos y acción | Resultado esperado | Resultado obtenido | Estado |
| --- | --- | --- | --- | --- |
| Validar la apuesta (`validaLaApuestaDelJugador`) | Ingresar 10 y 1000 fichas (válidas), y luego 5, 15 y 1010 (mínima, no múltiplo de 10, mayor al saldo). | Aceptar las válidas y rechazar las inválidas explicando el motivo. | Se aceptaron 10 y 1000; 5, 15 y 1010 se rechazaron con el mensaje del modelo. | Cumple |
| Turnos y saldo (`losTurnosAjustanElSaldo`) | Jugador con 20 se planta y banca 3+4 pide el 10; jugador pide hasta pasar de 21 (30); banca con 15 pide y llega a 24. | La banca pide bajo 17 y se planta al llegar a 17; si el jugador se pasa, pierde al instante y la banca ni juega; si la banca se pasa, gana el jugador. | VICTORIA con banca en 17 y saldo 1.100; DERROTA con banca sin cartas y saldo 900; VICTORIA con banca en 24 y saldo 1.100. | Cumple |
| Jugadas especiales (`reconoceLasJugadasEspeciales`) | Jugador con Blackjack (As+Rey), doble Blackjack, pedir hasta 21, figura+4 contra banca de 20 y de 21, empate 20=20 y Blackjack de la banca. | El Blackjack del jugador paga 2,5; doble Blackjack empata y devuelve la apuesta; llegar a 21 cierra el turno y gana; veinte y medio gana contra 20 y pierde contra 21; el empate igual devuelve la apuesta; el Blackjack de la banca gana. | VICTORIA_BLACKJACK (1.150), EMPATE (1.000), VICTORIA 21 a 19, VICTORIA (1.100), DERROTA (900), EMPATE (1.000) y DERROTA (900). | Cumple |
| Mano: lista y ases (`agregaCartasYCalculaElPuntaje`) | Agregar Rey y después As en la misma mano; armar As+As, As+As+As y Rey+Rey+Cinco+As. | El As queda al frente con cantidad 2; el as vale 11 solo si cabe: 21, 12, 13 y 26. | El As fue la primera carta y se obtuvieron 21, 12, 13 y 26. | Cumple |
| Mazo completo (`reparteSinRepetirYReinicia`) | Repartir las 52 cartas y revisar; luego mezclar y repartir de nuevo. | 52 cartas distintas y mazo vacío; tras mezclar, otras 52. | 52 distintas (`tieneCartas()` falso) y 52 de nuevo tras mezclar. | Cumple |

## Verificación visual de la ventana

Los requisitos de la sección 7 del enunciado son estados de la interfaz y se verifican
manualmente en la ventana:

| Prueba | Datos y acción | Resultado esperado | Resultado obtenido | Estado |
| --- | --- | --- | --- | --- |
| «Pedir carta» y «Plantarse» según el turno | Iniciar una ronda desde el diálogo de apuesta y observar los tres botones. | «Pedir carta» y «Plantarse» solo en tu turno; «Nueva ronda» bloqueado durante la ronda y si el saldo es menor a 10. | Se bloquearon y habilitaron según la fase. | Cumple |
| Marcadores durante la ronda | Observar saldo y apuesta mientras el computador juega su secuencia. | El saldo solo se liquida al terminar la secuencia; la apuesta no cambia. | El saldo quedó congelado y se liquidó al final. | Cumple |
| Ronda no interrumpida | Intentar pulsar «Nueva ronda» a mitad de turno. | Nada cambia: el botón está bloqueado y el modelo ignora la llamada. | No se inició otra ronda. | Cumple |
| Reglas conservan la partida | Abrir «Ver reglas» y cerrarla a mitad de turno. | La partida sigue igual y se puede seguir jugando. | La partida continuó sin cambios. | Cumple |
| Cierre con confirmación | «Cerrar juego» y la ✕ de la ventana: cancelar y luego confirmar. | Preguntar antes de cerrar; cancelar deja todo igual y confirmar termina. | Se pidió confirmación; al cancelar no cambió nada. | Cumple |
