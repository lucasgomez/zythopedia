import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Origin } from '../models/Origin';

const API_URL = '/api/origin';

@Injectable({
    providedIn: 'root'
})
export class OriginService {

    constructor(private readonly http: HttpClient) {
    }

    findAll(): Observable<Origin[]> {
        return this.http.get<Origin[]>(API_URL);
    }
}
