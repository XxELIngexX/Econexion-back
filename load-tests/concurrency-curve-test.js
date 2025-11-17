import http from 'k6/http';
import { check } from 'k6';

export const options = {
    stages: [
        { duration: '15s', target: 20 },   // Warm-up
        { duration: '15s', target: 50 },   // Subida suave
        { duration: '15s', target: 100 },  // Carga media
        { duration: '20s', target: 200 },  // Pico alto
        { duration: '15s', target: 50 },   // Recuperación
        { duration: '10s', target: 0 },    // Apagado
    ],
    thresholds: {
        http_req_failed: ['rate<0.10'],   // aceptamos 10% de fallos
        http_req_duration: ['p(95)<500'], // 95% bajo 500 ms
    },
};

export default function () {
    const res = http.get('http://localhost:35000/api/health');

    check(res, {
        'status 200': (r) => r.status === 200,
    });
}
