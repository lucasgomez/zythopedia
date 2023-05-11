import {HttpClient, HttpHeaders} from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { DetailedDrink } from '../models/Drink';

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

    changeAvailability(drinkId: number, availability: string): Observable<DetailedDrink> {
        const httpOptions = {
            headers: new HttpHeaders({
                'Content-Type':  'application/json',
                'Authorization': 'Basic ' + btoa('brewmaster:motdepasse')
            })
        };

        return this.http.get<DetailedDrink>(`${API_URL}/${drinkId}/availability/${availability}`, httpOptions);
    }
}
