import http from 'k6/http';
import { check } from 'k6';

const BASE_URL = "http://localhost:35000";
const ENDPOINT = "/api/users";
const URL = `${BASE_URL}${ENDPOINT}`;

export default function () {
    const res = http.get(URL);

    check(res, {
        "status is 200": (r) => r.status === 200,
    });
}
