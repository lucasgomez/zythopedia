import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, Resolve } from '@angular/router';
import { combineLatest, map, Observable } from 'rxjs';
import { DescriptiveList } from '../models/DescriptiveList';
import { Drink } from '../models/Drink';
import { ListService } from '../services/list.service';

@Injectable({
    providedIn: 'root'
})
export class DrinksResolver implements Resolve<DescriptiveList<Drink>> {
    constructor(private readonly listService: ListService) {
    }

    resolve(route: ActivatedRouteSnapshot): Observable<DescriptiveList<Drink>> {
        return combineLatest([
            this.listService.findByServiceType('TAP'),
            this.listService.findByServiceType('BOTTLE'),
        ]).pipe(
            map(([t, b]) => [...new Set([...t.content, ...b.content])]),
            map(drinks => ({
                title: '',
                content: drinks
            }))
        )
    }
}
