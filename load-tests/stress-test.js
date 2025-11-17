import http from 'k6/http';
import { sleep } from 'k6';

export let options = {
    stages: [
        { duration: '10s', target: 20 },   // sube a 20 usuarios
        { duration: '10s', target: 50 },   // sube a 50 usuarios
        { duration: '10s', target: 100 },  // llega a 100
        { duration: '10s', target: 0 },    // baja a 0
    ]
};

export default function () {
    http.get('http://localhost:35000/api/offers');
    sleep(1);
}
