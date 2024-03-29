import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Style } from '../models/Style';

const API_URL = `${environment.BASE_URL}style`;

@Injectable({
    providedIn: 'root'
})
export class StyleService {

    constructor(private readonly http: HttpClient) {
    }

    findAll(): Observable<Style[]> {
        return this.http.get<Style[]>(`${API_URL}/current`);
    }
}
