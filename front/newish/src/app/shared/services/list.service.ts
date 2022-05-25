import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { DescriptiveList } from '../models/DescriptiveList';
import { Drink } from '../models/Drink';

const API_URL = '/api';

@Injectable({
    providedIn: 'root'
})
export class ListService {

    constructor(private readonly http: HttpClient) {
    }

    findByColor(colorId: number): Observable<DescriptiveList<Drink>> {
        return this.http.get<DescriptiveList<Drink>>(`${API_URL}/color/${colorId}/drink`);
    }

    findByOrigin(originId: number): Observable<DescriptiveList<Drink>> {
        return this.http.get<DescriptiveList<Drink>>(`${API_URL}/origin/${originId}/drink`);
    }

    findByProducer(producerId: number): Observable<DescriptiveList<Drink>> {
        return this.http.get<DescriptiveList<Drink>>(`${API_URL}/producer/${producerId}/drink`);
    }

    findByStyle(styleId: number): Observable<DescriptiveList<Drink>> {
        return this.http.get<DescriptiveList<Drink>>(`${API_URL}/style/${styleId}/drink`);
    }
}