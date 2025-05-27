import {HttpClient, HttpHeaders} from '@angular/common/http';
import {Injectable} from '@angular/core';
import {Observable} from 'rxjs';
import {environment} from '../../../environments/environment';
import {Service} from "../models/Service";
import {LoginService} from "./login.service";

const API_URL = `${environment.BASE_URL}service`;

@Injectable({
    providedIn: 'root'
})
export class ServiceService {

    constructor(
        private readonly http: HttpClient,
        private readonly loginService: LoginService) {
    }

    findById$(serviceId: number): Observable<Service> {
        return this.http.get<Service>(`${API_URL}/${serviceId}`);
    }

    updateServices(boughtDrinkId: number, services: Service[]): Observable<void> {
        return this.http.put<void>(
            `${environment.BASE_URL}boughtdrink/${boughtDrinkId}/service`,
            services,
            this.loginService.getHttpOptions()
        );
    }
}
