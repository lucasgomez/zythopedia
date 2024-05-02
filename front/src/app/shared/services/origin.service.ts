import {HttpClient} from '@angular/common/http';
import {Injectable} from '@angular/core';
import {Observable} from 'rxjs';
import {environment} from '../../../environments/environment';
import {Origin} from '../models/Origin';

@Injectable({
    providedIn: 'root'
})
export class OriginService {

    constructor(private readonly http: HttpClient) {
    }

    findAll(): Observable<Origin[]> {
        return this.http.get<Origin[]>(`${environment.BASE_URL}/edition/current/origin`);
    }
}
