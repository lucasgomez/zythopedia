import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { Observable, pluck } from 'rxjs';
import { DescriptiveList } from '../../../shared/models/DescriptiveList';
import { Drink } from '../../../shared/models/Drink';

@Component({
    selector: 'app-descriptive-drinks-list',
    templateUrl: './descriptive-drinks-list.component.html',
    styleUrls: ['./descriptive-drinks-list.component.css']
})
export class DescriptiveDrinksListComponent implements OnInit {

    title!: string;
    drinks$!: Observable<DescriptiveList<Drink>>;

    constructor(
        private readonly route: ActivatedRoute,
    ) {
    }

    ngOnInit(): void {
        this.title = this.route.snapshot.data['title'];
        this.drinks$ = this.route.data.pipe(pluck('drinks'));
    }

    buildPricesDisplay(drink: Drink): string {
        return drink.services.map(service => `${service.sellingPrice}.- (${service.volumeInCl}cl)`).join(" / ");
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

    buildData(drink: Drink): string {
        return `${drink.colorName} (${drink.abv.toFixed(1)}%)`;
    }
}
