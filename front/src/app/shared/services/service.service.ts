import {HttpClient} from '@angular/common/http';
import {Injectable} from '@angular/core';
import {Observable} from 'rxjs';
import {environment} from '../../../environments/environment';
import {Service} from "../models/Service";

const API_URL = `${environment.BASE_URL}service`;

@Injectable({
    providedIn: 'root'
})
export class ServiceService {

    constructor(private readonly http: HttpClient) {
    }

    findById$(serviceId: number): Observable<Service> {
        return this.http.get<Service>(`${API_URL}/${serviceId}`);
    }
}
