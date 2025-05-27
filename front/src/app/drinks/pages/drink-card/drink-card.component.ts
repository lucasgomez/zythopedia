import {Component, Input, OnInit} from '@angular/core';
import {DetailedDrink} from '../../../shared/models/Drink';
import {LoginService} from "../../../shared/services/login.service";

@Component({
    selector: 'drink-card',
    templateUrl: './drink-card.component.html',
    styleUrls: ['./drink-card.component.css']
})
export class DrinkCardComponent implements OnInit {

    @Input()
    drink!: DetailedDrink;
    isLoggedIn = false;

    constructor(private readonly loginService: LoginService) {
    }

    ngOnInit(): void {
        this.isLoggedIn = this.loginService.isLoggedIn();
    }

    showRadar(drink: DetailedDrink): boolean {
        return !!drink.bitterness || !!drink.hoppiness || !!drink.sourness || !!drink.sweetness;
    }
}
