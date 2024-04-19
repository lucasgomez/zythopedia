import {Injectable} from '@angular/core';
import {ActivatedRouteSnapshot, Resolve} from '@angular/router';
import {Observable} from 'rxjs';
import {DescriptiveList} from '../models/DescriptiveList';
import {Drink} from '../models/Drink';
import {ListService} from '../services/list.service';

@Injectable({
    providedIn: 'root'
})
export class DrinksByColorResolver implements Resolve<DescriptiveList<Drink>> {
    constructor(private readonly listService: ListService) {
    }

    resolve(route: ActivatedRouteSnapshot): Observable<DescriptiveList<Drink>> {
        return this.listService.findByColor(route.params['colorId']);
    }
}
