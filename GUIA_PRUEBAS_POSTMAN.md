# Guía de Pruebas con Postman - Integración ms-eventos y ms-pagos

## 🎯 **Objetivo**

Probar el flujo completo de compra de boletos con pago por transferencia y aprobación manual del organizador.

## 🔧 **Preparación del Entorno**

### **1. Servicios Requeridos**

Asegúrate de que estén ejecutándose:

- ✅ **PostgreSQL** (puertos 5432)
- ✅ **RabbitMQ** (puerto 5672)
- ✅ **ms-eventos** (puerto 8081)
- ✅ **ms-pagos** (puerto 8082)

### **2. Comandos para Ejecutar Microservicios**

```bash
# Terminal 1 - ms-eventos
cd "d:\ESPE\SEPTIMO SEMESTRE\DISTRIBUIDAS\PROYECTO-ENCUENTRO\ENCUENTRO-MICROSERVICES\ms-eventos"
mvn spring-boot:run

# Terminal 2 - ms-pagos
cd "d:\ESPE\SEPTIMO SEMESTRE\DISTRIBUIDAS\PROYECTO-ENCUENTRO\ENCUENTRO-MICROSERVICES\ms-pagos"
mvn spring-boot:run
```

## 🔄 **Flujo de Pruebas Completo**

### **PASO 1: Verificar Estado de los Microservicios**

#### **Health Check ms-eventos**

```http
GET http://localhost:8081/eventos/health
```

**Respuesta esperada:** `200 OK`

#### **Health Check ms-pagos**

```http
GET http://localhost:8082/pagos/health
```

**Respuesta esperada:** `200 OK` con mensaje de solo transferencias

---

### **PASO 2: Preparar Datos de Prueba**

#### **2.1 Crear un Evento**

```http
POST http://localhost:8081/eventos
Content-Type: application/json

{
    "titulo": "Concierto de Rock",
    "descripcion": "Concierto increíble de rock",
    "fecha": "2025-08-15",
    "aforo": 100,
    "precioEntrada": 25.50,
    "estado": "ACTIVO",
    "categoria": "Música",
    "direccion": "Av. Principal 123",
    "ciudad": "Quito",
    "lugar": "Teatro Nacional",
    "idOrganizador": 1
}
```

**Respuesta esperada:** `201 CREATED` con el evento creado
**Importante:** Anotar el `idEvento` devuelto (ej: 1)

---

### **PASO 3: Flujo de Compra con Pago**

#### **3.1 Solicitar Compra de Boletos**

```http
POST http://localhost:8081/boletos/comprar
Content-Type: application/json

{
    "idAsistente": 2,
    "idEvento": 1,
    "cantidadBoletos": 3
}
```

**Respuesta esperada:** `200 OK`

```json
"Solicitud de compra enviada exitosamente. Monto total: $76.50. El organizador debe aprobar el pago por transferencia para confirmar los boletos."
```

#### **¿Qué sucede internamente?**

1. ms-eventos valida disponibilidad y calcula monto total
2. ms-eventos envía solicitud a `solicitud_pagos.cola`
3. ms-pagos recibe la solicitud y crea un pago PENDIENTE
4. ms-pagos notifica al organizador y al asistente

---

### **PASO 4: Verificar Pago Creado**

#### **4.1 Consultar Pagos Pendientes del Organizador**

```http
GET http://localhost:8082/pagos/organizador/1/pendientes
```

**Respuesta esperada:** `200 OK` con lista de pagos pendientes

```json
[
  {
    "idPago": 1,
    "idAsistente": 2,
    "idEvento": 1,
    "idOrganizador": 1,
    "cantidadBoletos": 3,
    "montoTotal": 76.5,
    "estadoPago": "PENDIENTE",
    "fechaCreacion": "2025-07-09T00:30:00",
    "fechaAprobacion": null
  }
]
```

**Importante:** Anotar el `idPago` (ej: 1)

#### **4.2 Consultar Pagos del Asistente**

```http
GET http://localhost:8082/pagos/asistente/2
```

**Respuesta esperada:** Lista con el pago en estado PENDIENTE

---

### **PASO 5: Proceso de Aprobación/Rechazo**

#### **5.1 APROBAR el Pago (Organizador)**

```http
POST http://localhost:8082/pagos/aprobar
Content-Type: application/json

{
    "idPago": 1,
    "accion": "APROBAR"
}
```

**Respuesta esperada:** `200 OK`

```json
{
  "exitoso": true,
  "mensaje": "Pago aprobado exitosamente",
  "estadoPago": "APROBADO",
  "idPago": 1,
  "idAsistente": 2,
  "idEvento": 1,
  "cantidadBoletos": 3,
  "montoTotal": 76.5
}
```

#### **¿Qué sucede internamente?**

1. ms-pagos actualiza el pago a APROBADO
2. ms-pagos envía respuesta a `respuesta_pagos.cola`
3. ms-eventos recibe la confirmación
4. ms-eventos crea automáticamente los 3 boletos para el asistente
5. Se envían notificaciones al asistente

#### **5.2 RECHAZAR el Pago (Alternativa)**

```http
POST http://localhost:8082/pagos/aprobar
Content-Type: application/json

{
    "idPago": 1,
    "accion": "RECHAZAR"
}
```

---

### **PASO 6: Verificar Boletos Creados**

#### **6.1 Consultar Disponibilidad del Evento**

```http
GET http://localhost:8081/eventos/1/disponibilidad
```

**Respuesta esperada:**

- Si se aprobó: `97` (100 - 3 boletos vendidos)
- Si se rechazó: `100` (sin cambios)

#### **6.2 Verificar Estado Final del Pago**

```http
GET http://localhost:8082/pagos/asistente/2
```

**Respuesta esperada:** Pago con estado APROBADO y fechaAprobacion

---

## 🧪 **Escenarios de Prueba Adicionales**

### **Escenario 1: Compra sin Disponibilidad**

```http
POST http://localhost:8081/boletos/comprar
Content-Type: application/json

{
    "idAsistente": 3,
    "idEvento": 1,
    "cantidadBoletos": 150
}
```

**Respuesta esperada:** `400 BAD REQUEST` - "Solo hay X boletos disponibles"

### **Escenario 2: Múltiples Pagos Pendientes**

Repetir PASO 3 con diferentes `idAsistente` y luego consultar:

```http
GET http://localhost:8082/pagos/organizador/1/pendientes
```

### **Escenario 3: Consultar Pagos por Evento**

```http
GET http://localhost:8082/pagos/evento/1
```

---

## 🔍 **Validaciones Importantes**

### **✅ Verificar que todo funciona:**

1. **Colas RabbitMQ activas:** `solicitud_pagos.cola` y `respuesta_pagos.cola`
2. **Estados de pago correctos:** PENDIENTE → APROBADO/RECHAZADO
3. **Boletos creados solo si se aprueba el pago**
4. **Disponibilidad del evento actualizada correctamente**
5. **Notificaciones enviadas (verificar logs)**

### **❌ Validar casos de error:**

1. Datos faltantes en solicitudes
2. IDs inexistentes
3. Cantidades inválidas
4. Pagos ya procesados

---

## 📊 **Monitoreo durante las Pruebas**

### **Logs a revisar:**

```bash
# ms-eventos logs
tail -f logs/ms-eventos.log

# ms-pagos logs
tail -f logs/ms-pagos.log
```

### **RabbitMQ Management (opcional):**

- URL: http://localhost:15672
- Usuario: admin / admin
- Verificar mensajes en colas

---

## 🎯 **Resultado Esperado del Flujo Completo**

**ÉXITO cuando:**

1. ✅ Solicitud de compra se envía correctamente
2. ✅ Pago se crea en estado PENDIENTE
3. ✅ Organizador puede aprobar/rechazar
4. ✅ Si se aprueba: boletos se crean automáticamente
5. ✅ Disponibilidad del evento se actualiza
6. ✅ Todas las consultas devuelven datos coherentes

**Este flujo demuestra la integración completa y asíncrona entre ambos microservicios! 🚀**
