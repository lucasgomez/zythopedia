import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, Resolve } from '@angular/router';
import { combineLatest, map, Observable } from 'rxjs';
import { DescriptiveList } from '../models/DescriptiveList';
import { DetailedDrink, Drink } from '../models/Drink';
import { BoughtDrinkService } from '../services/bought-drink.service';
import { ListService } from '../services/list.service';

@Injectable({
    providedIn: 'root'
})
export class DrinkResolver implements Resolve<DetailedDrink> {
    constructor(private readonly boughtDrinkService: BoughtDrinkService) {
    }

    resolve(route: ActivatedRouteSnapshot): Observable<DetailedDrink> {
        return this.boughtDrinkService.getDrink(route.params['drinkId']);
    }
}
