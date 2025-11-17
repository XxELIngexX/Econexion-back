import http from 'k6/http';
import { check, sleep } from 'k6';

const BASE_URL = "http://localhost:35000";
const ENDPOINT = "/api/users";
const URL = `${BASE_URL}${ENDPOINT}`;

export let options = {
    scenarios: {
        smoke: {
            executor: "constant-vus",
            vus: 1,
            duration: "10s",
        },
        load: {
            executor: "ramping-vus",
            stages: [
                { duration: "10s", target: 50 },
                { duration: "10s", target: 100 },
                { duration: "10s", target: 0 },
            ],
            startTime: "10s",
        },
        spike: {
            executor: "ramping-vus",
            stages: [
                { duration: "5s", target: 200 },
                { duration: "5s", target: 0 },
            ],
            startTime: "40s",
        },
        endurance: {
            executor: "constant-vus",
            vus: 20,
            duration: "60s",
            startTime: "50s",
        },
        latency: {
            executor: "constant-vus",
            vus: 10,
            duration: "30s",
            startTime: "110s",
        },
        chaos: {
            executor: "constant-vus",
            vus: 30,
            duration: "30s",
            startTime: "140s",
        },
        concurrency_curve: {
            executor: "ramping-vus",
            stages: [
                { duration: "20s", target: 50 },
                { duration: "20s", target: 150 },
                { duration: "20s", target: 0 },
            ],
            startTime: "170s",
        }
    },
};

export default function () {
    const res = http.get(URL);

    check(res, {
        "status is 200": (r) => r.status === 200,
    });

    sleep(1);
}
