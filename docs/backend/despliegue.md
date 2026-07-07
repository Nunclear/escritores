# Compilación y Despliegue

## Compilación

### Compilar el Proyecto

```bash
# Limpiar y compilar
mvn clean compile

# Compilar con ejecución de tests
mvn clean test

# Compilar y crear JAR (recomendado)
mvn clean package

# Compilar saltando tests
mvn clean package -DskipTests

# Compilar con output detallado
mvn clean package -X

# Compilar solo para generar JAR sin tests
mvn clean package -DskipTests -q
```

### Estructura del JAR

```
escritores-0.0.1-SNAPSHOT.jar
├── BOOT-INF/
│   ├── classes/
│   │   ├── com/nunclear/escritores/...
│   │   ├── application.properties
│   │   └── ...
│   ├── lib/
│   │   ├── spring-boot-4.0.6.jar
│   │   ├── spring-security-...jar
│   │   ├── mysql-connector-j-...jar
│   │   └── (todas las dependencias)
│   └── classpath.idx
├── META-INF/
│   ├── MANIFEST.MF
│   ├── spring.factories
│   └── ...
└── org/springframework/boot/loader/
    └── (clases del launcher)
```

### Verificar JAR Compilado

```bash
# Listar contenido del JAR
jar tf target/escritores-0.0.1-SNAPSHOT.jar | head -20

# Probar JAR
java -jar target/escritores-0.0.1-SNAPSHOT.jar

# Probar JAR con argumentos
java -jar target/escritores-0.0.1-SNAPSHOT.jar \
    --server.port=8081 \
    --spring.profiles.active=prod
```

## Despliegue en Producción

### Requisitos de Infraestructura

| Componente | Requerimiento | Notas |
|---|---|---|
| **Servidor** | Linux (preferiblemente Ubuntu 22.04) | Mínimo 2GB RAM |
| **Java** | JDK 21 o superior | Instalado en servidor |
| **Base de Datos** | MySQL 8.0+ | AWS RDS recomendado |
| **SSL/TLS** | Certificado válido | HTTPS obligatorio |
| **Reverse Proxy** | Nginx o Apache | Para HTTPS y load balancing |
| **Monitoreo** | Prometheus + Grafana | Recomendado |

### Opción 1: Despliegue en Servidor Linux

#### 1. Preparar Servidor

```bash
# Actualizar sistema
sudo apt update
sudo apt upgrade -y

# Instalar Java 21
sudo apt install openjdk-21-jdk -y

# Crear usuario para la aplicación
sudo useradd -m -s /bin/bash escritores

# Crear directorio de la aplicación
sudo mkdir -p /opt/escritores
sudo chown escritores:escritores /opt/escritores
```

#### 2. Transferir JAR

```bash
# Desde máquina local
scp target/escritores-0.0.1-SNAPSHOT.jar usuario@servidor.com:/opt/escritores/

# O usar rsync (más rápido para archivos grandes)
rsync -avz target/escritores-0.0.1-SNAPSHOT.jar usuario@servidor.com:/opt/escritores/
```

#### 3. Crear Variables de Entorno

```bash
# Editar /opt/escritores/.env
sudo vi /opt/escritores/.env

# Contenido (ejemplo):
```

```bash
export DB_URL=jdbc:mysql://tu-rds-host.amazonaws.com:3306/historias_db
export DB_USER=admin
export DB_PASSWORD=TuContraseñaSegura123!
export JWT_SECRET=TuClaveSecretaJWTDeMas32Caracteres123456
export JWT_ACCESS_EXPIRATION=3600
export JWT_REFRESH_EXPIRATION=604800
export SERVER_PORT=8080
export SPRING_PROFILES_ACTIVE=prod
```

#### 4. Crear Script de Inicio (Systemd)

```bash
# Crear archivo de servicio
sudo vi /etc/systemd/system/escritores.service
```

**Contenido** (`/etc/systemd/system/escritores.service`):

```ini
[Unit]
Description=Escritores Backend Application
After=network.target

[Service]
Type=simple
User=escritores
WorkingDirectory=/opt/escritores
EnvironmentFile=/opt/escritores/.env
ExecStart=/usr/bin/java -Xmx512m -Xms256m \
    -Dspring.datasource.url=${DB_URL} \
    -Dspring.datasource.username=${DB_USER} \
    -Dspring.datasource.password=${DB_PASSWORD} \
    -Dapp.jwt.secret=${JWT_SECRET} \
    -Dserver.port=${SERVER_PORT} \
    -Dspring.profiles.active=${SPRING_PROFILES_ACTIVE} \
    -jar /opt/escritores/escritores-0.0.1-SNAPSHOT.jar

Restart=always
RestartSec=10

[Install]
WantedBy=multi-user.target
```

#### 5. Habilitar y Iniciar Servicio

```bash
# Recargar systemd
sudo systemctl daemon-reload

# Habilitar al inicio
sudo systemctl enable escritores

# Iniciar servicio
sudo systemctl start escritores

# Verificar estado
sudo systemctl status escritores

# Ver logs
sudo journalctl -u escritores -f
```

### Opción 2: Despliegue con Docker

#### Crear Dockerfile

```dockerfile
# Multi-stage build para optimizar tamaño
FROM maven:3.9-eclipse-temurin-21 AS builder
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

FROM eclipse-temurin:21-jdk-jammy
WORKDIR /app

# Copiar JAR desde builder
COPY --from=builder /app/target/escritores-0.0.1-SNAPSHOT.jar app.jar

# Exponer puerto
EXPOSE 8080

# Healthcheck
HEALTHCHECK --interval=30s --timeout=3s --start-period=40s --retries=3 \
    CMD java -cp app.jar org.springframework.boot.loader.JarLauncher \
    || exit 1

# Ejecutar aplicación
ENTRYPOINT ["java", "-Xmx512m", "-Xms256m", "-jar", "app.jar"]
```

#### Crear docker-compose.yml

```yaml
version: '3.8'

services:
  db:
    image: mysql:8.0
    container_name: escritores_db
    environment:
      MYSQL_ROOT_PASSWORD: root123
      MYSQL_DATABASE: historias_db
      MYSQL_USER: admin
      MYSQL_PASSWORD: Nunclear55
    ports:
      - "3306:3306"
    volumes:
      - db_data:/var/lib/mysql
    healthcheck:
      test: ["CMD", "mysqladmin", "ping", "-h", "localhost"]
      interval: 10s
      timeout: 5s
      retries: 5

  app:
    build: .
    container_name: escritores_app
    ports:
      - "8080:8080"
    environment:
      SPRING_DATASOURCE_URL: jdbc:mysql://db:3306/historias_db
      SPRING_DATASOURCE_USERNAME: admin
      SPRING_DATASOURCE_PASSWORD: Nunclear55
      SPRING_PROFILES_ACTIVE: prod
      APP_JWT_SECRET: TuClaveSecretaJWTDeMas32Caracteres123456
    depends_on:
      db:
        condition: service_healthy
    volumes:
      - ./logs:/app/logs
    restart: unless-stopped

volumes:
  db_data:
```

#### Desplegar con Docker

```bash
# Construir imagen
docker-compose build

# Iniciar servicios
docker-compose up -d

# Ver logs
docker-compose logs -f app

# Detener
docker-compose down
```

### Opción 3: Despliegue en Cloud (AWS, Azure, GCP)

#### AWS Elastic Beanstalk

```bash
# Instalar EB CLI
pip install awsebcli

# Inicializar aplicación
eb init -p "Java 21 running on 64bit Amazon Linux 2" escritores

# Crear ambiente
eb create escritores-prod

# Desplegar
eb deploy

# Ver estado
eb status

# Ver logs
eb logs
```

#### AWS Fargate (Contenedores)

```bash
# Crear repositorio ECR
aws ecr create-repository --repository-name escritores

# Construir y push de imagen
docker build -t escritores:latest .
docker tag escritores:latest 123456789.dkr.ecr.us-east-1.amazonaws.com/escritores:latest
aws ecr get-login-password | docker login --username AWS --password-stdin 123456789.dkr.ecr.us-east-1.amazonaws.com
docker push 123456789.dkr.ecr.us-east-1.amazonaws.com/escritores:latest

# Crear tarea en ECS/Fargate
# (Usar AWS Console o CLI)
```

## Configuración para Producción

### Archivo de Configuración `application-prod.properties`

```properties
# Base de datos
spring.datasource.url=${SPRING_DATASOURCE_URL}
spring.datasource.username=${SPRING_DATASOURCE_USERNAME}
spring.datasource.password=${SPRING_DATASOURCE_PASSWORD}
spring.jpa.hibernate.ddl-auto=validate
spring.jpa.show-sql=false
spring.jpa.properties.hibernate.format_sql=false

# JWT
app.jwt.secret=${APP_JWT_SECRET}
app.jwt.access-expiration-seconds=3600
app.jwt.refresh-expiration-seconds=604800

# Logs
logging.level.root=WARN
logging.level.com.nunclear.escritores=INFO
logging.file.name=/opt/escritores/logs/app.log
logging.file.max-size=10MB
logging.file.max-history=30

# Server
server.port=8080
server.servlet.context-path=/api
server.compression.enabled=true
server.compression.min-response-size=1024

# Actuator para monitoreo
management.endpoints.web.exposure.include=health,metrics,info
management.endpoint.health.show-details=when-authorized
```

### Nginx como Reverse Proxy

```nginx
upstream escritores {
    server localhost:8080;
}

server {
    listen 80;
    server_name api.ejemplo.com;
    
    # Redirigir HTTP a HTTPS
    return 301 https://$server_name$request_uri;
}

server {
    listen 443 ssl http2;
    server_name api.ejemplo.com;
    
    # Certificados SSL
    ssl_certificate /etc/nginx/certs/cert.pem;
    ssl_certificate_key /etc/nginx/certs/key.pem;
    
    # Configuración SSL
    ssl_protocols TLSv1.2 TLSv1.3;
    ssl_ciphers HIGH:!aNULL:!MD5;
    ssl_prefer_server_ciphers on;
    
    # Compression
    gzip on;
    gzip_types text/plain application/json;
    gzip_min_length 1000;
    
    location /api {
        proxy_pass http://escritores;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
        
        # Timeouts
        proxy_connect_timeout 60s;
        proxy_send_timeout 60s;
        proxy_read_timeout 60s;
    }
}
```

## Monitoreo en Producción

### Health Check

```bash
# Verificar salud de la aplicación
curl -i https://api.ejemplo.com/actuator/health
```

**Response esperada**:
```json
{
  "status": "UP",
  "components": {
    "db": {
      "status": "UP"
    },
    "diskSpace": {
      "status": "UP"
    }
  }
}
```

### Métricas

```bash
# Ver métricas
curl https://api.ejemplo.com/actuator/metrics

# Métrica específica
curl https://api.ejemplo.com/actuator/metrics/jvm.memory.used
```

### Logs

```bash
# Seguir logs en tiempo real
tail -f /opt/escritores/logs/app.log

# Buscar errores
grep ERROR /opt/escritores/logs/app.log

# Últimas 100 líneas
tail -100 /opt/escritores/logs/app.log
```

## Rollback

```bash
# Si la nueva versión falla, rollback:
sudo systemctl stop escritores

# Restaurar versión anterior
cp /opt/escritores/backups/escritores-0.0.0-SNAPSHOT.jar /opt/escritores/app.jar

# Reiniciar
sudo systemctl start escritores

# Verificar
sudo systemctl status escritores
```

## Backup

```bash
# Backup de base de datos
mysqldump -h tu-rds-host.amazonaws.com -u admin -p historias_db > backup_$(date +%Y%m%d_%H%M%S).sql

# Restore de backup
mysql -h tu-rds-host.amazonaws.com -u admin -p historias_db < backup_20260707_103000.sql
```

## Checklist de Despliegue

- [ ] JAR compilado y testeado
- [ ] Variables de entorno configuradas
- [ ] Base de datos migrada y verificada
- [ ] Certificados SSL en lugar
- [ ] Nginx configurado como reverse proxy
- [ ] Firewall configurado (puerto 443, 80)
- [ ] Logs habilitados y rotados
- [ ] Monitoreo configurado
- [ ] Backup schedule creado
- [ ] Plan de rollback documentado
- [ ] Notificaciones de alertas configuradas
- [ ] Documentación de runbooks actualizada

