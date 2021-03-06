import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Color } from '../models/Color';

const API_URL = `${environment.BASE_URL}/api/color`;

@Injectable({
    providedIn: 'root'
})
export class ColorService {

    constructor(private readonly http: HttpClient) {
    }

    findAll(): Observable<Color[]> {
        return this.http.get<Color[]>(API_URL);
    }

    findOne(id: number): Observable<Color> {
        return this.http.get<Color>(`${API_URL}/${id}`);
    }
}
