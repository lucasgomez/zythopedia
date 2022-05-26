import { Component, OnDestroy, OnInit } from '@angular/core';
import { map, Observable, switchMap, tap, timer } from 'rxjs';
import { Drink } from '../../../shared/models/Drink';
import { Service } from '../../../shared/models/Service';
import { HeaderDisplayService } from '../../../shared/services/header-display.service';
import { ListService } from '../../../shared/services/list.service';

@Component({
    selector: 'beamer-display',
    templateUrl: './beamer-display.component.html',
    styleUrls: ['./beamer-display.component.css']
})
export class BeamerDisplayComponent implements OnInit, OnDestroy {

    drinks$!: Observable<Drink[]>;
    pageCounter = 0;
    maxPages = 0;
    private showTap = true;

    constructor(
        private readonly listService: ListService,
        private readonly headerDisplayService: HeaderDisplayService,
    ) {
    }

    private readonly RELOAD_DELAY = 20000;
    private readonly MAX_PAGE_TILES = 20;

    ngOnInit(): void {
        this.headerDisplayService.changeHeaderDisplay(false);
        this.drinks$ = timer(1000, this.RELOAD_DELAY).pipe(
            tap(() => this.showTap = !this.showTap),
            map(() => this.showTap ? 'tap' : 'bottle'),
            switchMap(service => this.listService.findAvailableTapBeers$(service)),
            tap(drinks => this.incrementPageCounter(drinks)),
            map(drinks => this.filterForBottles(drinks))
        );
    }

    ngOnDestroy() {
        this.headerDisplayService.changeHeaderDisplay(true);
    }

    incrementPageCounter(drinks: Drink[]): void {
        if (!this.showTap) {
            this.pageCounter++;
            this.maxPages = Math.ceil(drinks.length / this.MAX_PAGE_TILES);
            if (this.pageCounter >= this.maxPages) {
                this.pageCounter = 0;
            }
        }
    }

    buildPriceDisplay(service: Service): string {
        return `${service.sellingPrice}.- (${service.volumeInCl}cl)`;
    }

    getTitle(): string {
        return this.showTap ? 'Pressions' : `Bouteilles ${this.pageCounter+1}/${this.maxPages}`;
    }

    private filterForBottles(drinks: Drink[]): Drink[] {
        if (this.showTap) {
            return drinks;
        }
        const firstItem = this.pageCounter * this.MAX_PAGE_TILES;
        const lastItem = (this.pageCounter + 1) * this.MAX_PAGE_TILES;
        if (lastItem > drinks.length) {
            return drinks.slice(-1 * this.MAX_PAGE_TILES);
        }
        return drinks.slice(firstItem, lastItem);
    }
}
