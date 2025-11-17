import http from 'k6/http';
import { check } from 'k6';

export const options = {
    scenarios: {
        concurrency_burst: {
            executor: 'shared-iterations',
            vus: 300,          // 300 usuarios simultáneos EXACTOS
            iterations: 300,   // cada usuario hace 1 request al mismo tiempo
            maxDuration: '30s'
        }
    },
    thresholds: {
        http_req_failed: ['rate<0.10'],  // aceptamos hasta 10%
        http_req_duration: ['p(95)<500'], 
    }
};

export default function () {
    const res = http.get('http://localhost:35000/api/health');

    check(res, {
        'status 200': (r) => r.status === 200,
    });
}
