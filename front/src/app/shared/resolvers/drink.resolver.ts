import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, Resolve } from '@angular/router';
import { Observable } from 'rxjs';
import { DetailedDrink } from '../models/Drink';
import { BoughtDrinkService } from '../services/bought-drink.service';

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
