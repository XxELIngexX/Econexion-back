import http from 'k6/http';
import { sleep } from 'k6';

export let options = {
    stages: [
        { duration: '1s', target: 200 },  // Subida instantánea (pico)
        { duration: '15s', target: 200 }, // Mantener el pico
        { duration: '1s', target: 0 },    // Caída brusca
    ],
};

export default function () {
    http.get('http://localhost:35000/api/offers'); // cambia si quieres
    sleep(1);
}
