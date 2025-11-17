import http from 'k6/http';
import { sleep, check } from 'k6';

export let options = {
    stages: [
        { duration: "10s", target: 0 },
        { duration: "10s", target: 50 },
        { duration: "10s", target: 200 },
        { duration: "10s", target: 500 },   // pico extremo
        { duration: "10s", target: 50 },    // bajamos
        { duration: "20s", target: 10 }     // fase de recuperación
    ],
    thresholds: {
        http_req_failed: ["rate<0.05"],
        http_req_duration: ["p(95)<500"],
    },
};

export default function () {
    const res = http.get("http://localhost:35000/actuator/health");

    check(res, {
        "status 200": (r) => r.status === 200,
    });

    sleep(1);
}
