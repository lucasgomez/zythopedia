import {Component, Input} from '@angular/core';
import {Drink} from '../../../shared/models/Drink';
import {Service} from '../../../shared/models/Service';

@Component({
    selector: 'beer-card',
    templateUrl: './beer-card.component.html',
    styleUrls: ['./beer-card.component.css']
})
export class BeerCardComponent {

    @Input()
    drink!: Drink;

    buildPriceDisplay(service: Service): string {
        return `${service.sellingPrice}.- (${service.volumeInCl}cl)`;
    }
}
