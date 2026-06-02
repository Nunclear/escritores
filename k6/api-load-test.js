import http from 'k6/http';
import { check, sleep } from 'k6';

/*
 * Script de pruebas de carga para la API de Escritores.
 *
 * Este script utiliza k6, una herramienta de pruebas de rendimiento de
 * código abierto desarrollada por Grafana Labs, que permite definir
 * escenarios de carga en JavaScript y ejecutarlos desde la línea de
 * comandos.  Las pruebas de carga ayudan a validar el comportamiento
 * de la API bajo distintos niveles de concurrencia y a identificar
 * cuellos de botella antes de desplegar en producción.  Tal como se
 * describe en la guía de inicio rápido de k6, los scripts se escriben
 * en JavaScript y se ejecutan con el comando `k6 run archivo.js`【91227551196018†L124-L144】.
 *
 * Este ejemplo utiliza la opción `stages` para incrementar
 * progresivamente la cantidad de usuarios virtuales (VU) y realiza
 * peticiones a varios endpoints públicos de la API.  Puedes ajustar
 * las etapas, el número de usuarios o la duración según tus
 * necesidades.  Para ejecutar este script, asegúrate de tener k6
 * instalado localmente o utiliza la imagen oficial de Docker.
 *
 * Uso típico:
 *
 *   # Instalación local (véase la documentación de k6)
 *   k6 run k6/api-load-test.js
 *
 *   # Utilizando Docker (sin necesidad de instalar k6 en el host)
 *   docker run --rm -i grafana/k6 run - < k6/api-load-test.js
 *
 * Puedes parametrizar la URL base de la API mediante la variable de
 * entorno BASE_URL:
 *   BASE_URL=http://localhost:8080 k6 run k6/api-load-test.js
 */

export const options = {
  // Definición de etapas de carga: incrementa gradualmente
  // el número de usuarios virtuales y luego vuelve a cero.
  stages: [
    { duration: '10s', target: 5 },  // 5 VUs durante 10 segundos
    { duration: '20s', target: 15 }, // 15 VUs durante 20 segundos
    { duration: '10s', target: 0 },  // Reducción a 0 VUs en 10 segundos
  ],
};

// Utiliza la variable de entorno BASE_URL si está definida; de lo
// contrario, recurre al valor por defecto.  Esto facilita cambiar el
// host y puerto en distintos entornos sin modificar el código.
const BASE_URL = __ENV.BASE_URL || 'http://localhost:8080';

export default function () {
  // Endpoint principal de historias (público)
  const resStories = http.get(`${BASE_URL}/stories`);
  check(resStories, {
    'GET /stories debe devolver 200': (r) => r.status === 200,
  });

  // Endpoint de documentación OpenAPI (público)
  const resDocs = http.get(`${BASE_URL}/v3/api-docs`);
  check(resDocs, {
    'GET /v3/api-docs debe devolver 200': (r) => r.status === 200,
  });

  // Endpoint de métricas (puede devolver 200 o 404 según si hay datos)
  const resMetrics = http.get(`${BASE_URL}/metrics/stories/top-viewed`);
  check(resMetrics, {
    'GET /metrics/stories/top-viewed status < 500': (r) => r.status < 500,
  });

  // Intento de login; el resultado puede variar según la existencia de
  // usuarios en la base de datos.  Se consideran válidos los códigos
  // 200 (login correcto) o 400/401 (credenciales incorrectas o falta de
  // datos).  Ajusta los datos de payload según tus usuarios de prueba.
  const loginPayload = JSON.stringify({
    loginName: 'testuser',
    password: 'password123',
  });
  const loginParams = { headers: { 'Content-Type': 'application/json' } };
  const resLogin = http.post(`${BASE_URL}/auth/login`, loginPayload, loginParams);
  check(resLogin, {
    'POST /auth/login status aceptable': (r) => [200, 400, 401].includes(r.status),
  });

  // Pausa entre iteraciones para simular tiempo de procesamiento del usuario
  sleep(1);
}