# Digital-Gateway

## 4.5 Concurrencia - Respuestas de Diseño

### Q1: En el contexto de transferencias bancarias, ¿cuándo preferirías Optimistic sobre Pessimistic? ¿Y al revés?
**Respuesta:**

- **Optimistic Locking**: Lo usaria en casos de poco volumen de tranferencia en corto tiempo, como retiro de efectivo y a la ves el cobro de suscripciones o entra de plata mediante una tranferencia y esto suele ser inusual que llegen al mismo milisegundo y de ser el caso no llega a encolarse mucho tiempo hasta finalizar la transaccion y asi tambien usando el optimista se llega a tener un ahorro de RAM y CPU.


- **Pessimistic Locking**: Lo utilizaria cuando sea una cuenta muy recurente donde llegan a tener alto flujo de transacciones por ejemplo las cuentas de recaudacion, hay miles de personas que tienes que realizar una tranferencia a una misma cuenta y son estas las entidades que tienes miles de transaferencias simultaneas en un lapso menoas a 100ms, y usar el Optimistic en este caso llegaria a un bloqueo en hilos ya que al momento de realizar la validacion no va a llegar a realizar ya que siempre estaria en constante cambio.

### Q2: Si el saldo estuviera en base de datos relacional (Oracle/PostgreSQL), ¿cómo trasladarías esta lógica? ¿Cuáles son las implicancias?
**Respuesta:**

Claro en el sentido del optimistic se manejaria a un nivel sensillo de una tabla con los campos basico mas el saldo y version y de la misma forma al momento de realiza la actualizacion de saldo se realizaria update saldo = saldo +/- amount, version = version + 1 where accountNumber = '14XXXXXX10' and version = 1, aqui la implicancia seria aqui la mplicancias seria mas controlable ya que es mas sensillo de controlar ya que al recibir el mensaje "0 Rows Affected" se controlaria rapdio y java comenzaria un nuevo intento para completar la transferencia 

Ahora en el sentido del Pessimistic se llegaria a usar el comando **FOR UPDATE** ya que esto estaria bloqueando la fila para que no sea utilizada por otra peticion y poniendolo en espera hasta que la primera peticion que bloqueo la linea llegue a realizar el commit y libere la fila. ahora aqui la implicancia seria el uso de conexion abiertas esperando que se terminde de realizar la peticion y llenando el pool de conexion y genrando una saturacion.


### Q3: Cuando el Gateway escala a múltiples instancias/pods Kubernetes, el Map en memoria ya no es compartido. ¿Qué problema nuevo aparece y cómo lo resolverías sin introducir un SPOF (Single Point of Failure)?
**Respuesta:**

Uno de los principales problemas seria que no esten sincronizados con los ultimos cambios, es decir el usuario A tiene un saldo de 1000 pero retira 800 pero en esta operacion es enviado al POD_1 y se realiza el retiro exitoso, pero el usuario A vuelve a realizar la misma operacion sabiendo que no tiene el saldo pero en esta ocacion es enviada al POD_2 donde aun figura que su saldo es 1000 entonces al momento de realizar el retiro no encuentra un restriccion y logra realizar un segundo retiro superando su saldo disponible de su cuenta.

para resolver esto en lugar de usar un Map dentro de la JVM, usas un Almacén de Datos Externo y Distribuido Redis Clúster