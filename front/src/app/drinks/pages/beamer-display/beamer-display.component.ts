import { Component, OnInit } from '@angular/core';
import { map, Observable, switchMap, tap, timer } from 'rxjs';
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
    private showTap = true;

    constructor(
        private readonly listService: ListService
    ) {
    }

    private readonly RELOAD_DELAY = 20000;

    ngOnInit(): void {
        this.drinks$ = timer(1000, this.RELOAD_DELAY).pipe(
            tap(() => this.showTap = !this.showTap),
            map(() => this.showTap ? 'tap' : 'bottle'),
            switchMap(service => this.listService.findAvailableTapBeers$(service))
        );
    }

    buildPriceDisplay(service: Service): string {
        return `${service.sellingPrice}.- (${service.volumeInCl}cl)`;
    }

}
