import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Producer } from '../models/Producer';

const API_URL = '/api/producer';

@Injectable({
    providedIn: 'root'
})
export class ProducerService {

    constructor(private readonly http: HttpClient) {
    }

    findAll(): Observable<Producer[]> {
        return this.http.get<Producer[]>(API_URL);
    }
}
