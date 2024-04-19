import {Injectable} from '@angular/core';
import {ActivatedRouteSnapshot, Resolve} from '@angular/router';
import {Observable} from 'rxjs';
import {DescriptiveList} from '../models/DescriptiveList';
import {Drink} from '../models/Drink';
import {ListService} from '../services/list.service';

@Injectable({
    providedIn: 'root'
})
export class DrinksByStyleResolver implements Resolve<DescriptiveList<Drink>> {
    constructor(private readonly listService: ListService) {
    }

    resolve(route: ActivatedRouteSnapshot): Observable<DescriptiveList<Drink>> {
        return this.listService.findByStyle(route.params['styleId']);
    }
}
