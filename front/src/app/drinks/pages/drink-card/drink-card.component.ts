import {Component, Input} from '@angular/core';
import {DetailedDrink} from '../../../shared/models/Drink';

@Component({
    selector: 'drink-card',
    templateUrl: './drink-card.component.html',
    styleUrls: ['./drink-card.component.css']
})
export class DrinkCardComponent {

    @Input()
    drink!: DetailedDrink;

    constructor() {
    }
}
