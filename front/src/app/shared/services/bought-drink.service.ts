import {HttpClient, HttpHeaders} from '@angular/common/http';
import {Injectable} from '@angular/core';
import {Observable} from 'rxjs';
import {environment} from '../../../environments/environment';
import {DetailedDrink, FullDrinkDto} from '../models/Drink';

const API_URL = `${environment.BASE_URL}boughtdrink`;

@Injectable({
    providedIn: 'root'
})
export class BoughtDrinkService {

    constructor(private readonly http: HttpClient) {
    }

    getDrink(drinkId: number): Observable<DetailedDrink> {
        return this.http.get<DetailedDrink>(`${API_URL}/${drinkId}/detail`);
    }

    changeAvailability(drinkId: number, availability: string, credentials: ClunkyCredentials): Observable<DetailedDrink> {
        return this.http.get<DetailedDrink>(
            `${API_URL}/${drinkId}/availability/${availability}`,
            BoughtDrinkService.getHttpOptions(credentials));
    }

    updateDrink(drinkId: number, updatedDrink: FullDrinkDto, credentials: ClunkyCredentials): Observable<DetailedDrink> {
        return this.http.put<DetailedDrink>(
                    `${API_URL}/${drinkId}`,
                    updatedDrink,
                    BoughtDrinkService.getHttpOptions(credentials));
    }

    getFullDrink(drinkId: number, credentials: ClunkyCredentials): Observable<FullDrinkDto> {
        return this.http.get<FullDrinkDto>(
            `${API_URL}/${drinkId}/full`,
            BoughtDrinkService.getHttpOptions(credentials));
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

export class ClunkyCredentials {
    username!: string;
    password!: string;
}
