import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { DescriptiveList } from '../models/DescriptiveList';
import { DetailedDrink, Drink } from '../models/Drink';

const API_URL = '/api/boughtdrink';

@Injectable({
    providedIn: 'root'
})
export class BoughtDrinkService {

    constructor(private readonly http: HttpClient) {
    }

    getDrink(drinkId: number): Observable<DetailedDrink> {
        return this.http.get<DetailedDrink>(`${API_URL}/${drinkId}/detail`);
    }
}
