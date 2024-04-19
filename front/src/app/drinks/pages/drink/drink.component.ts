import {Component, OnInit} from '@angular/core';
import {ActivatedRoute} from '@angular/router';
import {Observable, pluck} from 'rxjs';
import {DetailedDrink} from '../../../shared/models/Drink';

@Component({
    selector: 'app-drink',
    templateUrl: './drink.component.html',
    styleUrls: ['./drink.component.css']
})
export class DrinkComponent implements OnInit {

    drink$!: Observable<DetailedDrink>;

    constructor(private readonly route: ActivatedRoute) {
    }

    ngOnInit(): void {
        this.drink$ = this.route.data.pipe(pluck('drink'));
    }
}
