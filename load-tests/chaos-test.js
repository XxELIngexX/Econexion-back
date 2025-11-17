import http from 'k6/http';
import { sleep, check } from 'k6';

// Introduce caos en la latencia simulando un servidor inestable
function randomDelay() {
    const delays = [0, 100, 250, 500, 800, 1200, 2000]; // hasta 2s de retraso
    const index = Math.floor(Math.random() * delays.length);
    sleep(delays[index] / 1000);
}

export const options = {
    scenarios: {
        chaos: {
            executor: 'constant-vus',
            vus: 50,               // usuarios constantes bajo caos
            duration: '40s',
        },
    },
    thresholds: {
        http_req_failed: ['rate<0.20'], // aceptamos hasta 20% de fallos por caos
        http_req_duration: ['p(95)<2000'], // p95 debe ser menor a 2s
    },
};

export default function () {
    randomDelay(); // inyecta comportamiento impredecible

    const res = http.get('http://localhost:35000/api/health');

    check(res, {
        'status is 200 or 5xx acceptable in chaos': (r) =>
            r.status === 200 || r.status >= 500,
    });
}
