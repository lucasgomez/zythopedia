import {HttpClient, HttpHeaders} from '@angular/common/http';
import {Injectable} from '@angular/core';
import {Observable} from 'rxjs';
import {environment} from '../../../environments/environment';
import {DetailedDrink, FullDrinkDto} from '../models/Drink';
import {LoginService} from "./login.service";

const API_URL = `${environment.BASE_URL}boughtdrink`;

@Injectable({
    providedIn: 'root'
})
export class BoughtDrinkService {

    constructor(
        private readonly http: HttpClient,
        private readonly loginService: LoginService) {
    }

    getDrink(drinkId: number): Observable<DetailedDrink> {
        return this.http.get<DetailedDrink>(`${API_URL}/${drinkId}/detail`);
    }

    changeAvailability(drinkId: number, availability: string): Observable<DetailedDrink> {
        return this.http.get<DetailedDrink>(
            `${API_URL}/${drinkId}/availability/${availability}`,
            this.loginService.getHttpOptions());
    }

    updateDrink(drinkId: number, updatedDrink: FullDrinkDto): Observable<DetailedDrink> {
        return this.http.put<DetailedDrink>(
                    `${API_URL}/${drinkId}`,
                    updatedDrink,
            this.loginService.getHttpOptions());
    }

    getFullDrink(drinkId: number): Observable<FullDrinkDto> {
        return this.http.get<FullDrinkDto>(
            `${API_URL}/${drinkId}/full`,
            this.loginService.getHttpOptions());
    }

    nukeDemAll(): Observable<void> {
        return this.http.post<void>(`${API_URL}/panicmode`, null, this.loginService.getHttpOptions());
    }
}
