import {Component, Input, OnInit} from '@angular/core';
import {DetailedDrink} from '../../../shared/models/Drink';
import {Service} from '../../../shared/models/Service';
import {Observable} from "rxjs";
import {BoughtDrinkService} from "../../services/bought-drink.service";
import {CartService} from "../../services/cart.service";

@Component({
    selector: 'beer-card',
    templateUrl: './beer-card.component.html',
    styleUrls: ['./beer-card.component.css']
})
export class BeerCardComponent implements OnInit {

    @Input()
    drinkId!: number;

    drink$!: Observable<DetailedDrink>;

    constructor(
        private readonly boughtDrinkService: BoughtDrinkService,
        private readonly cartService: CartService
    ) {
    }

    ngOnInit(): void {
        this.drink$ = this.boughtDrinkService.getDrink(this.drinkId);
    }

    buildPriceDisplay(service: Service): string {
        return `${service.sellingPrice}.- (${service.volumeInCl}cl)`;
    }

    addDrinkServiceToBasket(service: Service): void {
        this.cartService.addDrinkServiceToBasket(service);
    }

    buildTooltip(drink: DetailedDrink): string {
        if (drink.availability == 'AVAILABLE') {
            return 'Ajouter au panier';
        }
        if (drink.availability == 'SOON') {
            return 'Prochainement disponible';
        }
        return 'Epuisée';
    }
}
