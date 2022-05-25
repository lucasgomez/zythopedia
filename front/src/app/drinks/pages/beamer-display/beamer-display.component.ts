import { Component, OnInit } from '@angular/core';
import { Observable } from 'rxjs';
import { Drink } from '../../../shared/models/Drink';
import { Service } from '../../../shared/models/Service';
import { ListService } from '../../../shared/services/list.service';

@Component({
    selector: 'beamer-display',
    templateUrl: './beamer-display.component.html',
    styleUrls: ['./beamer-display.component.css']
})
export class BeamerDisplayComponent implements OnInit {

    drinks$!: Observable<Drink[]>;

    constructor(
        private readonly listService: ListService
    ) {
    }

    ngOnInit(): void {
        this.drinks$ = this.listService.findAvailableTapBeers$();
    }

    buildPriceDisplay(service: Service): string {
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

}
