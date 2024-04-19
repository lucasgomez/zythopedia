import {HttpClient} from '@angular/common/http';
import {Injectable} from '@angular/core';
import {Observable} from 'rxjs';
import {environment} from '../../../environments/environment';
import {Color} from '../models/Color';

const API_URL = `${environment.BASE_URL}color`;

@Injectable({
    providedIn: 'root'
})
export class ColorService {

    constructor(private readonly http: HttpClient) {
    }

    findAll(): Observable<Color[]> {
        return this.http.get<Color[]>(`${API_URL}/current`);
    }

    findOne(id: number): Observable<Color> {
        return this.http.get<Color>(`${API_URL}/${id}`);
    }
}
