import http from 'k6/http';
import { check, sleep } from 'k6';

export const options = {
    scenarios: {
        latency_test: {
            executor: 'constant-vus',
            vus: 30,              // 30 usuarios simultáneos con mala red
            duration: '30s',      // dura 30 segundos
        },
    },
    thresholds: {
        http_req_duration: ['p(95)<2000'], // toleramos latencias altas
        http_req_failed: ['rate<0.20'],    // aceptamos hasta 20% fallos
    },
};

// Generador de latencia simulada (como red 3G/4G mala)
function randomLatency() {
    // entre 80ms y 1500ms con jitter aleatorio
    const base = Math.floor(Math.random() * (1500 - 80 + 1)) + 80;
    const jitter = Math.floor(Math.random() * 200); // jitter extra
    return base + jitter;
}

export default function () {
    // Dormimos entre 80ms y 1700ms simulando mala red
    sleep(randomLatency() / 1000);

    const url = 'http://localhost:35000/api/health';

    const res = http.get(url, {
        timeout: '3s', // requests pueden expirar con mala red
    });

    check(res, {
        'status 200 or acceptable': (r) =>
            r.status === 200 || r.status >= 500,
    });

    // Pequeña espera adicional como "lag residual"
    sleep(0.2);
}
