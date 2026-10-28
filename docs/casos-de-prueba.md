# Casos de prueba del 21 Blackjack

Los casos demuestran cada punto de la sección 10 del enunciado. Todos corren con:

```
mvnw test        # 7 pruebas: Juego21Test (3), ManoTest (2), MazoTest (2)
```

Para reproducir jugadas concretas, `Juego21Test` arma un **mazo de orden fijo**
(`mazoFijo(Rango...)`) y así las cartas que recibe cada mano no dependen del azar.

| Requisito del enunciado (punto 10) | Prueba | Qué demuestra |
| --- | --- | --- |
| Validación de apuestas y saldo insuficiente | `Juego21Test::validaLaApuestaDelJugador` | `validarApuesta()`: 10 y 1.000 pasan; 5 (mínima), 15 (no múltiplo) y 1.010 (mayor al saldo) se rechazan con mensaje |
| Puntaje con uno o varios ases | `ManoTest::elPuntajeAjustaLosAses` | Rey+as vale 21 (as = 11); dos ases 12; tres ases 13; al no caber, el as baja a 1; si igual se pasa, vale 1 (26) |
| Reconocer Blackjack, 21 y Veinte y medio | `Juego21Test::reconoceLasJugadasEspeciales` | As+rey → `VICTORIA_BLACKJACK` y pagas 2,5 (saldo 1.150); doble Blackjack → `EMPATE` (saldo 1.000); 10+4+7 = 21 → veintiuno y victoria; reina+4 → veinte y medio solo avisa (perdió 14 a 20, sin pago especial); Blackjack de la banca (as+JOTA) gana a la mano normal sin Blackjack |
| Derrota por superar 21 | `Juego21Test::losTurnosAjustanElSaldo` | Con 10+10+10 = 30 pierde al instante: la banca queda sin cartas y el saldo 900 (apuesta perdida) |
| Empate | `Juego21Test::reconoceLasJugadasEspeciales` | Dobles Blackjack (20 = 20): la apuesta se devuelve y el saldo queda igual |
| Reglas del computador | `Juego21Test::losTurnosAjustanElSaldo` | La banca con 3+4 = 7 pide una carta y se planta al llegar a 17; si se pasa (Siete+Ocho+Nueve = 24) pierde; el computador no juega si el jugador ya perdió |
| Actualización correcta del saldo | `Juego21Test::losTurnosAjustanElSaldo` y `reconoceLasJugadasEspeciales` | Victoria 1.000→1.100; derrota 1.000→900; Blackjack 1.000→1.150; doble Blackjack y empate 1.000→1.000 |
| Cartas nunca repetidas y mazo completo cada ronda | `MazoTest::reparteLas52CartasSinRepetir` y `mezclarReiniciaElMazo` | Repartir todo entrega 52 cartas distintas y queda vacío; mezclar devuelve las 52 aunque ya se hayan repartido |

## Verificación de la vista (visual, en la ventana)

Los requisitos de la sección 7 del enunciado se verifican manualmente, porque
son estados de la interfaz y no del modelo:

| Requisito | Cómo se comprueba |
| --- | --- |
| Acciones habilitadas según el estado | «Pedir carta» y «Plantarse» nacen deshabilitados y solo funcionan durante el turno del jugador; «Iniciar ronda» se deshabilita durante la ronda y cuando el saldo queda menor a 10 |
| La apuesta no cambia durante la ronda | El campo se congela al repartir y se libera al terminar |
| No se inicia otra ronda a medio turno | «Iniciar ronda» bloqueado + `iniciarRonda()` del modelo ignora llamadas en `TURNO_JUGADOR` |
| «Ver reglas» conserva la partida | La ventana de reglas no bloquea ni cambia el estado; se puede seguir jugando con ella abierta |
| Cerrar con botón o ✕ pide confirmación | Ambos caminos muestran el mismo diálogo; cancelar no cambia nada del juego |
