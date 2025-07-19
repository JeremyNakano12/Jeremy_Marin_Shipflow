# Jeremy_Marin_Shipflow
# 📦 Shipflow - Sistema de Gestión de Envíos

Sistema de gestión de envíos de paquetes desarrollado con **Spring Boot** y **Kotlin** que permite registrar y gestionar el estado de paquetes, para la materia Arquitectura empresarial.

---

## 📋 **Descripción General**

Shipflow es una API REST que proporciona un sistema completo para la gestión de envíos de paquetes. El sistema permite:

- **Registro de envíos** con generación automática de tracking ID único
- **Gestión de estados** con validaciones de transición
- **Historial completo** de cambios de estado

### **Funcionalidades Principales:**

✅ **Registro de Envíos:**
- Tipos de paquete: `DOCUMENT`, `SMALL_BOX`, `FRAGILE`
- Validación de peso y descripción
- Ciudades origen y destino 
- Tracking ID único generado automáticamente
- Estado inicial `PENDING`
- Fecha estimada de entrega

✅ **Consulta de Envíos:**
- Listado completo de envíos
- Consulta individual por tracking ID
- Historial de cambios de estado

✅ **Gestión de Estados:**
- Estados: `PENDING`, `IN_TRANSIT`, `DELIVERED`, `ON_HOLD`, `CANCELLED`
- Validación de transiciones según las reglas especificadas
- Registro histórico de todos los cambios
- Comentarios opcionales en cada cambio

---

## ⚙️ **Requisitos Previos**

Antes de ejecutar el proyecto, asegúrate de tener instalado:

- **Java 21**
- **Docker** y **Docker Compose**
- **Git**

---

## 🚀 **Instalación y Ejecución**

### **1. Clonar el Repositorio**
```bash
git clone https://github.com/JeremyNakano12/Jeremy_Marin_Shipflow.git
cd Jeremy_Marin_Shipflow
```

### **2. Configurar Base de Datos**
```bash
# Levantar PostgreSQL con Docker
docker-compose up -d
```

### **3. Ejecutar la Aplicación**
```bash
./gradlew bootRun
```
---

## 📋 **API Endpoints**

**Base URL:** `http://localhost:8080/shipflow/api`

### **📦 Gestión de Paquetes**

#### **Crear Envío**
```http
POST /shipflow/api/packages

{
  "type": "DOCUMENT",
  "weight": 0.5,
  "description": "Contrato de compraventa",
  "cityFrom": "Quito",
  "cityTo": "Guayaquil"
}
```

#### **Listar Todos los Envíos**
```http
GET /shipflow/api/packages
```

#### **Consultar Envío por Tracking ID**
```http
GET /shipflow/api/packages/{trackingId}
```

#### **Consultar Historial Completo**
```http
GET /shipflow/api/packages/{trackingId}/history
```

#### **Actualizar Estado del Envío**
```http
PUT /shipflow/api/packages/{trackingId}/status

{
  "status": "IN_TRANSIT",
  "comment": "Paquete recogido en oficina central"
}
```

