import {HttpClient, HttpHeaders} from '@angular/common/http';
import {Injectable} from '@angular/core';
import {Observable} from 'rxjs';
import {environment} from '../../../environments/environment';
import {Service} from "../models/Service";
import {BoughtDrinkService, ClunkyCredentials} from "./bought-drink.service";

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

    updateServices(boughtDrinkId: number, services: Service[], credentials: ClunkyCredentials): Observable<void> {
        return this.http.put<void>(
            `${environment.BASE_URL}boughtdrink/${boughtDrinkId}/service`,
            services,
            ServiceService.getHttpOptions(credentials)
        );
    }

    private static getHttpOptions(credentials: ClunkyCredentials): {headers: HttpHeaders} {
        return {
            headers: new HttpHeaders({
                'Content-Type': 'application/json',
                'Authorization': `Basic ${btoa(`${credentials.username}:${credentials.password}`)}`
            })
        };
    }
}
