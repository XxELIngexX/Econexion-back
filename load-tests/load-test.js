import http from 'k6/http';
import { sleep } from 'k6';

export let options = {
    vus: 10,           // usuarios simultáneos
    duration: '10s',   // duración
};

export default function () {
    http.get('http://localhost:35000/api/auth/health');   // cambia el endpoint si quieres
    sleep(1);
}
