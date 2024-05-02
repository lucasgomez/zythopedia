import {HttpClient} from '@angular/common/http';
import {Injectable} from '@angular/core';
import {Observable} from 'rxjs';
import {environment} from '../../../environments/environment';
import {Color} from '../models/Color';

@Injectable({
    providedIn: 'root'
})
export class ColorService {

    constructor(private readonly http: HttpClient) {
    }

    findAll(): Observable<Color[]> {
        return this.http.get<Color[]>(`${environment.BASE_URL}/edition/current/color`);
    }
}
