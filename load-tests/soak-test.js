import http from 'k6/http';
import { sleep } from 'k6';

export let options = {
    vus: 20,            // usuarios simultáneos moderados
    duration: '5m',     // prueba prolongada de estabilidad
};

export default function () {
    http.get('http://localhost:35000/api/offers'); // ajusta si quieres
    sleep(1);
}
