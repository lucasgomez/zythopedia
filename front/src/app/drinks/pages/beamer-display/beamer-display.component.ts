import {Component, OnDestroy, OnInit} from '@angular/core';
import {filter, map, Observable, Subject, switchMap, tap, timer} from 'rxjs';
import {Drink} from '../../../shared/models/Drink';
import {Service} from '../../../shared/models/Service';
import {HeaderDisplayService} from '../../../shared/services/header-display.service';
import {ListService} from '../../../shared/services/list.service';

@Component({
    selector: 'beamer-display',
    templateUrl: './beamer-display.component.html',
    styleUrls: ['./beamer-display.component.css']
})
export class BeamerDisplayComponent implements OnInit, OnDestroy {

    private readonly RELOAD_DELAY = 11000;
    private readonly MAX_PAGE_TILES = 24;
    private readonly TICKS_COUNT = 11;
    private readonly GLASS_ICON = "🍺";
    private readonly EMPTY_ICON = "💀";

    private readonly TICK_DELAY = this.RELOAD_DELAY / this.TICKS_COUNT;
    private readonly SHIFT_SIZE = this.MAX_PAGE_TILES / 6;

    drinks$!: Observable<Drink[]>;
    pageCounter = 0;
    maxPages = 0;
    tick$ = new Subject<number>();
    counter$!: Observable<string>;
    counterLeft$!: Observable<string>;
    counterRight$!: Observable<string>;
    title$ = new Subject<string>();

    constructor(
        private readonly listService: ListService,
        private readonly headerDisplayService: HeaderDisplayService,
    ) {
        this.counter$ = this.tick$.pipe(
            map(counter => this.buildCounter(counter)),
        );
        this.counterLeft$ = this.counter$.pipe(
            map(counter => counter?.slice(0, counter.length/2)),
        );
        this.counterRight$ = this.counter$.pipe(
            map(counter => counter?.slice(-counter.length/2)),
        );
    }

    ngOnInit(): void {
        this.headerDisplayService.changeHeaderDisplay(false);
        this.drinks$ = timer(1000, this.TICK_DELAY).pipe(
            map(count => count % (this.TICKS_COUNT*2) ),
            tap(tick => this.tick$.next(tick)),
            filter(tick => tick % this.TICKS_COUNT == 0),
            switchMap(() => this.listService.findAvailableBeers$()),
            tap(drinks => this.incrementPageCounter(drinks)),
            map(drinks => this.filterPage(drinks))
        );
    }

    ngOnDestroy() {
        this.headerDisplayService.changeHeaderDisplay(true);
    }

    incrementPageCounter(drinks: Drink[]): void {
        this.pageCounter++;
        this.maxPages = Math.ceil(drinks.length / this.SHIFT_SIZE);
        if (this.pageCounter >= this.maxPages) {
            this.pageCounter = 0;
        }
    }

    buildPriceDisplay(service: Service): string {
        return `${service.sellingPrice}.- (${service.volumeInCl}cl)`;
    }

    getSortedServices(drink: Drink): Service[] {
        return drink.services.sort((a, b) => a.sellingPrice - b.sellingPrice);
    }

    private filterPage(drinks: Drink[]): Drink[] {
        const firstItem = this.pageCounter * this.SHIFT_SIZE;
        const lastItem = firstItem + this.MAX_PAGE_TILES;
        if (lastItem > drinks.length) {
            this.title$.next(`${drinks.length - this.MAX_PAGE_TILES} - ${drinks.length}`)
            return drinks?.slice(-1 * this.MAX_PAGE_TILES);
        }
        this.title$.next(`${firstItem} - ${lastItem}`)
        return drinks?.slice(firstItem, lastItem);
    }

    private buildCounter(tick: number): string {
        const icons = tick < this.TICKS_COUNT ? [this.EMPTY_ICON, this.GLASS_ICON] : [this.GLASS_ICON, this.EMPTY_ICON];
        const leftCount = tick % this.TICKS_COUNT;
        const rightCount = this.TICKS_COUNT - leftCount - 1;
        return `${icons[0].repeat(leftCount)}${icons[1].repeat(rightCount)}`;
    }
}
