from locust import HttpUser, task, between
import random

class MyUser(HttpUser):
  host = "http://localhost:8000/api/v1"

  wait_time = between(0.5, 1.5)

  @task
  def crear_organizadores(self):
    payload = {
      "nombre": f"Usuario{random.randint(1, 1000)}",
      "apellido": f"Apellido{random.randint(1, 1000)}",
      "correo": f"organizador{random.randint(1, 10000)}@test.com",
      "contrasena": f"password{random.randint(100, 999)}",
      "fechaNacimiento": f"{random.randint(1980, 2000)}-{random.randint(1, 12):02d}-{random.randint(1, 28):02d}",
      "rol": "ORGANIZADOR",
      "empresa": f"Empresa{random.randint(1, 100)}",
      "ruc": f"{random.randint(1000000000, 9999999999)}001"
    }
    
    self.client.post("/auth/register", json=payload)

  @task
  def crear_asistentes(self):
    payload = {
      "nombre": f"Usuario{random.randint(1, 1000)}",
      "apellido": f"Apellido{random.randint(1, 1000)}",
      "correo": f"organizador{random.randint(1, 10000)}@test.com",
      "contrasena": f"password{random.randint(100, 999)}",
      "fechaNacimiento": f"{random.randint(1980, 2000)}-{random.randint(1, 12):02d}-{random.randint(1, 28):02d}",
      "rol": "ASISTENTE",
    }
    
    self.client.post("/auth/register", json=payload)
    
  @task
  def crear_eventos(self):
    payload = {
      "titulo": f"Evento{random.randint(1, 1000)}",
      "descripcion": f"Descripción del evento {random.randint(1, 100)}",
      "fecha": f"{random.randint(2024, 2026)}-{random.randint(1, 12):02d}-{random.randint(1, 28):02d}",
      "aforo": random.randint(50, 5000),
      "precioEntrada": round(random.uniform(10.0, 100.0), 2),
      "estado": "ACTIVO",
      "categoria": random.choice(["MUSICA", "DEPORTE", "CONFERENCIA", "TEATRO", "DANZA"]),
      "direccion": f"Dirección {random.randint(1, 100)}",
      "ciudad": f"Ciudad{random.randint(1, 20)}",
      "lugar": f"Lugar{random.randint(1, 50)}",
      "idOrganizador": random.randint(1, 100)
    }
    
    self.client.post("/eventos", json=payload)
    
  @task
  def comprar_boletos(self):
    payload = {
      "idAsistente": random.randint(1, 100),
      "idEvento": random.randint(1, 1000),
      "cantidadBoletos": random.randint(1, 10)
    }
    
    self.client.post("/boletos/comprar", json=payload)