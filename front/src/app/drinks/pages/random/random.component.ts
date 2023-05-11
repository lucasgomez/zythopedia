import {Component, OnInit} from '@angular/core';
import {Observable} from 'rxjs';
import {DetailedDrink} from '../../../shared/models/Drink';
import {ListService} from "../../../shared/services/list.service";

const COUNT = 2;

@Component({
    selector: 'app-random',
    templateUrl: './random.component.html',
    styleUrls: ['./random.component.css']
})
export class RandomComponent implements OnInit {

    drinks$!: Observable<DetailedDrink[]>;

    constructor(private readonly listService: ListService) {
    }

    ngOnInit(): void {
        this.drinks$ = this.listService.getRandom$(COUNT);
    }
}
