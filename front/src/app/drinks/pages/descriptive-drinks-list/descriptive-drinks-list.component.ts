import {AfterViewInit, Component, OnInit, ViewChild} from '@angular/core';
import {ActivatedRoute} from '@angular/router';
import {Observable, pluck} from 'rxjs';
import {DescriptiveList} from '../../../shared/models/DescriptiveList';
import {Drink} from '../../../shared/models/Drink';
import {Service} from "../../../shared/models/Service";
import {CartService} from "../../../shared/services/cart.service";
import {MessageService} from "primeng/api";
import {Table} from "primeng/table";

@Component({
    selector: 'app-descriptive-drinks-list',
    templateUrl: './descriptive-drinks-list.component.html',
    styleUrls: ['./descriptive-drinks-list.component.css'],
    providers: [MessageService]
})
export class DescriptiveDrinksListComponent implements OnInit, AfterViewInit {

    title!: string;
    drinks$!: Observable<DescriptiveList<Drink>>;
    mobileDisplay!: boolean;
    showOnlyAvailable = true;
    @ViewChild('drinksTable')
    drinksTable!: Table;
    @ViewChild('globalFilter')
    globalFilter!: any;

    constructor(
        private readonly route: ActivatedRoute,
        private readonly cartService: CartService,
        private readonly messageService: MessageService,
    ) {
    }

    ngOnInit(): void {
        this.mobileDisplay = window.screen.width < 768;
        this.title = this.route.snapshot.data['title'];
        this.drinks$ = this.route.data.pipe(pluck('drinks'));
    }

    ngAfterViewInit() {
        this.onAvailabilityToggle();
    }

    buildPricesDisplay(service: Service): string {
        return `${service.sellingPrice ?? '?'}.- (${service.volumeInCl}cl)`;
    }

    buildProducerUrl(drink: Drink): string {
        return `/drinks/producers/${drink.producerId}`;
    }

    buildColorUrl(drink: Drink): string {
        return `/drinks/colors/${drink.colorId}`;
    }

    buildStyleUrl(drink: Drink): string {
        return `/drinks/styles/${drink.styleId}`;
    }

    addDrinkServiceToBasket(service: Service): void {
        this.cartService.addDrinkServiceToBasket(service);
        this.messageService.add({severity: 'success', detail: `${service.drinkName} ajouté au panier`});
    }

    onAvailabilityToggle(): void {
        if (this.drinksTable === undefined) {
            return;
        }
        if (this.showOnlyAvailable) {
            this.drinksTable.filter('AVAILABLE', 'availability', 'equals');
            return;
        }
        this.drinksTable.filter('', 'availability', 'equals');
    }

    clearGlobalFilter(): void {
        this.drinksTable.filterGlobal('', 'contains');
        this.globalFilter.nativeElement.value = '';
    }
}
