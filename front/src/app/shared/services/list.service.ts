import {HttpClient} from '@angular/common/http';
import {Injectable} from '@angular/core';
import {Observable} from 'rxjs';
import {environment} from '../../../environments/environment';
import {DescriptiveList} from '../models/DescriptiveList';
import {DetailedDrink, Drink} from '../models/Drink';

const API_URL = `${environment.BASE_URL}`;

@Injectable({
    providedIn: 'root'
})
export class ListService {

    constructor(private readonly http: HttpClient) {
    }

    findByServiceType(serviceType: 'TAP' | 'BOTTLE'): Observable<DescriptiveList<Drink>> {
        return this.http.get<DescriptiveList<Drink>>(`${API_URL}service/${serviceType}/drink`);
    }

    findByColor(colorId: number): Observable<DescriptiveList<Drink>> {
        return this.http.get<DescriptiveList<Drink>>(`${API_URL}color/${colorId}/drink`);
    }

    findByOrigin(originId: number): Observable<DescriptiveList<Drink>> {
        return this.http.get<DescriptiveList<Drink>>(`${API_URL}origin/${originId}/drink`);
    }

    findByProducer(producerId: number): Observable<DescriptiveList<Drink>> {
        return this.http.get<DescriptiveList<Drink>>(`${API_URL}producer/${producerId}/drink`);
    }

    findByStyle(styleId: number): Observable<DescriptiveList<Drink>> {
        return this.http.get<DescriptiveList<Drink>>(`${API_URL}style/${styleId}/drink`);
    }

    findAvailableBeers$(serviceMethod: string): Observable<Drink[]> {
        return this.http.get<Drink[]>(`${API_URL}service/${serviceMethod}/beer/available`);
    }

    getRandom$(count: number) {
        return this.http.get<DetailedDrink[]>(`${API_URL}drink/random/${count}`);
    }
}
