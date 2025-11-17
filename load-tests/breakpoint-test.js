import http from 'k6/http';
import { sleep, check } from 'k6';

export let options = {
    stages: [
        { duration: "10s", target: 50 },
        { duration: "10s", target: 100 },
        { duration: "10s", target: 150 },
        { duration: "10s", target: 200 },
        { duration: "10s", target: 300 },
        { duration: "10s", target: 400 },
        { duration: "10s", target: 500 }
    ],
    thresholds: {
        http_req_duration: ["p(95)<500"],
        http_req_failed: ["rate<0.05"]
    }
};

export default function () {
    const res = http.get("http://localhost:35000/actuator/health");

    check(res, {
        "status is 200": (r) => r.status === 200,
    });

    sleep(1);
}
