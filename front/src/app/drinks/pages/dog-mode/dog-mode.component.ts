import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { Observable, pluck, tap } from 'rxjs';
import { DescriptiveList } from '../../../shared/models/DescriptiveList';
import { Drink } from '../../../shared/models/Drink';
import { BoughtDrinkService } from '../../../shared/services/bought-drink.service';

@Component({
    selector: 'dog-mode',
    templateUrl: './dog-mode.component.html',
    styleUrls: ['./dog-mode.component.css']
})
export class DogModeComponent implements OnInit {

    drinks$!: Observable<DescriptiveList<Drink>>;
    availabilities = ['SOON','AVAILABLE','OUT_OF_STOCK'];

    constructor(
        private readonly route: ActivatedRoute,
        private readonly boughtDrinkService: BoughtDrinkService,
    ) {
    }

    ngOnInit(): void {
        this.drinks$ = this.route.data.pipe(pluck('drinks'));
    }

    changeAvailability(drink: Drink, availability: string) {
        this.boughtDrinkService.changeAvailability(drink.id, availability).pipe(
            tap(drink => alert(`Boisson ${drink.id} - ${drink.name} mise à jour en '${drink.availability}'`)))
            .subscribe();
    }
}
