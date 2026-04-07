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






### 1. ¿Cómo está estructurado tu bitmap ISO 8583? ¿Cómo determinas si un campo está presente?
El bitmap se maneja como una cadena Hexadecimal de 16 caracteres (64 bits) al igual que el bitmap secundario. 
- **Estructura**: El primer bit del Bitmap Primario indica la presencia de un **Bitmap Secundario** (campos 65-128). 
- **Detección**: Se utiliza una conversión bit a bit. Cada carácter hexadecimal se convierte a su valor decimal y se aplican máscaras de bits (`& 8`, `& 4`, `& 2`, `& 1`) para verificar si las posiciones 1, 2, 3 o 4 de ese cuarteto están activas. El parser recorre todo el bitmap y genera una lista de campos activos antes de proceder a la lectura de datos.

### 2. ¿Qué estrategia usaste para el parsing XML de las reglas de producto? ¿JAXB, StAX, DOM? ¿Por qué?
Se utilizó **Jackson XML Dataformat** (basado internamente en **StAX** - Streaming API for XML).
- **Razón**: StAX es un modelo de "pull" que permite leer el XML de forma secuencial y eficiente en memoria. Jackson XML simplifica esto permitiendo el mapeo directo a POJOs (`ProductRulesConfig`) mediante anotaciones, integrándose perfectamente con el ecosistema de Spring Boot y ofreciendo un rendimiento superior a DOM (que carga todo el árbol en memoria) para archivos de configuración que se cargan una sola vez al inicio (`@PostConstruct`).

### 3. ¿Cómo manejas la concurrencia en el cliente TCP? ¿Una conexión por request o pool de conexiones?
Actualmente se maneja **Una conexión por request (Short-lived connections)**.
- **Flujo**: Se abre un `java.net.Socket`, se envía la trama, se recibe la respuesta y se cierra la conexión en el bloque `try-with-resources`.
- **Justificación**: Para propósitos de este Gateway y considerando que el Core es un Mock, esto evita problemas de estado o tramas encoladas. En un entorno de producción de alta transaccionalidad, se migraría a un **Pool de Conexiones** (usando librerías como Apache Commons Pool o Netty) para reutilizar sockets existentes y reducir la latencia del "TCP Handshake".

### 4. ¿Qué harías diferente si el CORE usara IBM MQ en lugar de TCP directo?
La arquitectura cambiaría de un modelo Síncrono (Request/Response inmediato) a un modelo **Asíncrono basado en Colas**:
1. El Gateway colocaría el mensaje ISO en una **Queue de Envío** (Request Queue) y liberaría el hilo de ejecución rápidamente.
2. Implementaríamos un **CorrelationID** (usando el STAN o el campo DE-11) para identificar la respuesta.
3. Un Listener asíncrono escucharía la **Queue de Respuesta** (Response Queue).
4. Usaríamos un mecanismo de sincronización (como un `CompletableFuture` o `DeferredResult`) para entregar la respuesta al cliente REST una vez que el mensaje llegue por la cola de respuesta.

### 5. ¿Cómo extenderías el Gateway para soportar un nuevo producto bancario sin modificar código Java?
El Gateway está diseñado para ser **Data-Driven**. Para añadir un producto (ej. "Pago de Impuestos"):
1. Solo se requiere añadir una nueva entrada `<product>` en el archivo `product-rules.xml`.
2. Se definen sus campos obligatorios, Processing Code único, montos mínimos/máximos y horarios.
3. El `ProductRuleValidator` cargará automáticamente esta nueva regla en el `ruleCache` al iniciar, permitiendo que el Gateway valide el nuevo producto de inmediato sin necesidad de recompilar el código Java.

---

## 🚀 Guía de Ejecución

### Requisitos
- Java 25 (o compatible con la configuración del `pom.xml`)
- Maven 3.x

### Paso 1: Levantar el Mock Core (Servidor TCP)
Antes de iniciar el Gateway, necesitamos que el "Banco" esté escuchando:
```bash
# Ejecutar directamente la clase Mock
mvn exec:java -Dexec.mainClass="core.MockCoreTcpServer"
```

### Paso 2: Iniciar el Gateway (Spring Boot)
En una nueva terminal, levanta la aplicación principal:
```bash
mvn spring-boot:run
```

### Paso 3: Ejecutar Tests de Concurrencia (Tarea 4)
Para validar que los algoritmos de bloqueo (Optimistic y Pessimistic) funcionan bajo estrés:
```bash
mvn test -Dtest=AccountLedgerTest
```

---

## 🧪 Notas de Prueba (Postman) - Adicional se encuentra el archivo Json del postman en la raiz del proyecto.
- **POST /transfer**: Envía un JSON con PAN par para aprobación exitosa.
- **POST /reversal**: Realiza la anulación de una traza previa.
- **GET /{trace}/status**: Consulta el estado de una traza usando el MTI 0800.
