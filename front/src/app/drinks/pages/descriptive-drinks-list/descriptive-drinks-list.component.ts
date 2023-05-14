import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { Observable, pluck } from 'rxjs';
import { DescriptiveList } from '../../../shared/models/DescriptiveList';
import { Drink } from '../../../shared/models/Drink';
import {Service} from "../../../shared/models/Service";

@Component({
    selector: 'app-descriptive-drinks-list',
    templateUrl: './descriptive-drinks-list.component.html',
    styleUrls: ['./descriptive-drinks-list.component.css']
})
export class DescriptiveDrinksListComponent implements OnInit {

    title!: string;
    drinks$!: Observable<DescriptiveList<Drink>>;
    mobileDisplay!: boolean;

    constructor(
        private readonly route: ActivatedRoute,
    ) {
    }

    ngOnInit(): void {
        this.mobileDisplay = window.screen.width < 768;
        this.title = this.route.snapshot.data['title'];
        this.drinks$ = this.route.data.pipe(pluck('drinks'));
    }

    buildPricesDisplay(service: Service): string {
        return `${service.sellingPrice}.- (${service.volumeInCl}cl)`;
    }

    buildProducerUrl(drink: Drink): string {
        return `/drinks/producers/${drink.producerId}`;
    }

    buildColorUrl(drink: Drink): string {
        return `/drinks/colors/${drink.colorId}`;
    }

    buildStyleUrl(drink: Drink): string {
        return `/drinks/styles/${drink.styleId}`;
    }

    addDrinkServiceToBasket(service: Service): void {
        let drinkServicesIds = JSON.parse(localStorage.getItem('drinkServicesIds') || '[]');
        drinkServicesIds.push(service.id);
        localStorage.setItem('drinkServicesIds', JSON.stringify(drinkServicesIds));
    }

}
