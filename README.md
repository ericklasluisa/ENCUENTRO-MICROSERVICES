# ENCUENTRO Microservices

Repositorio de microservicios para gestión de eventos y pagos, construido con Spring Boot + Spring Cloud, API Gateway, Eureka, PostgreSQL y RabbitMQ. Desplegado en una instancia EC2 de Amazon Web Services (AWS)

## Integrantes

- Silvia Añasco
- Sheylee Enriquez
- Erick Lasluisa

## 🧭 Arquitectura breve

- Descubrimiento: Eureka (ms-eureka-server).
- Entrada única: API Gateway (ms-api-gateway).
- Servicios de dominio:
  - ms-authentication: autenticación/usuarios.
  - ms-eventos: eventos, boletos y orquestación de compra.
  - ms-pagos: solicitudes y aprobaciones de pagos.
  - ms-notificaciones: recepción/listado de notificaciones.
- Mensajería: RabbitMQ (colas: solicitud_pagos.cola, respuesta_pagos.cola, notificaciones.cola, etc.).

## ✨ Funcionalidad principal

- Autenticación: login/register/refresh; obtención de usuarios por ID(s).
- Eventos: CRUD de eventos, disponibilidad y asistentes; creación/compra de boletos.
- Pagos: creación de solicitud, aprobación/rechazo, consultas por organizador/asistente/evento.
- Notificaciones: listado básico de notificaciones recibidas.

## ▶️ Ejecución local con Docker Compose

1. Asegura Docker/Compose activos.
2. En la raíz del repo:
   - docker compose build
   - docker compose up -d
3. Verifica:
   - Gateway: http://localhost:8000/actuator/health
   - Eureka: http://localhost:8761
   - RabbitMQ UI (opcional): http://localhost:15672 (admin/admin)

Base de datos: el init `deploy/postgres-init.sql` crea auth_db, eventos_db, notificaciones_db, pagos_db automáticamente.
