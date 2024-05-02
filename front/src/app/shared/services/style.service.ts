import {HttpClient} from '@angular/common/http';
import {Injectable} from '@angular/core';
import {Observable} from 'rxjs';
import {environment} from '../../../environments/environment';
import {Style} from '../models/Style';

@Injectable({
    providedIn: 'root'
})
export class StyleService {

    constructor(private readonly http: HttpClient) {
    }

    findAll(): Observable<Style[]> {
        return this.http.get<Style[]>(`${environment.BASE_URL}/edition/current/style`);
    }
}
