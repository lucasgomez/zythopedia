import {Component, OnInit} from '@angular/core';
import {BehaviorSubject, combineLatest, map, Observable, switchMap} from 'rxjs';
import {ServiceService} from "../../../shared/services/service.service";
import {Service} from "../../../shared/models/Service";
import {CartService} from "../../../shared/services/cart.service";

@Component({
    selector: 'app-cart',
    templateUrl: './cart.component.html',
    styleUrls: ['./cart.component.css']
})
export class CartComponent implements OnInit {

    private readonly DEPOSIT_PRICE = 2;
    services$!: Observable<(Service)[]>;
    refreshServices$ = new BehaviorSubject<void>(undefined);

    constructor(
        private readonly serviceService: ServiceService,
        private readonly cartService: CartService) {
    }

    ngOnInit(): void {
        this.loadServices();
    }

    loadServices(): void {
        const servicesArray$ = Array.from(new Set(this.cartService.getBasketContent()))
            .map(id => this.serviceService.findById$(id));
        const serviceArray$ = combineLatest(servicesArray$).pipe();
        this.services$ = this.refreshServices$.pipe(
            switchMap(() => serviceArray$),
            map(services => services.filter(service => service.availability === 'AVAILABLE')),
            map((services: Service[]) => this.cartService.getBasketContent()
                .map(id => services.find(service => id === service.id))
                .filter(service => !!service)
                .map(service => service as Service)
            )
        );

    }

    buildTableData(services: (Service)[]): DrinkRow[] {
        const tableData = services
            .sort((a, b) => this.sort(a, b))
            .map(service => this.buildDrinkRow(service));

        tableData.push(this.buildOtherRow('Sous-total', this.calculateSubtotal(services), 'subtotal'));
        tableData.push(this.buildOtherRow(`Consignes x${services.length}`, this.calculateDeposit(services), 'subtotal'));
        tableData.push(this.buildOtherRow('Total', this.calculateTotal(services), 'total'));

        return tableData;
    }

    addService(serviceId: number): void {
        const content = this.cartService.getBasketContent();
        content.push(serviceId);
        this.cartService.setBasketContent(content);
        this.refreshServices$.next();
    }

    removeService(serviceId: number): void {
        const basketContent = this.cartService.getBasketContent();
        const index = basketContent.indexOf(serviceId);
        if (index > -1) {
            basketContent.splice(index, 1);
            this.cartService.setBasketContent(basketContent);
            this.refreshServices$.next();
        }
    }

    private sort(a: Service, b: Service): number {
        if (a.drinkName > b.drinkName) {
            return 1;
        }
        if (b.drinkName > a.drinkName) {
            return -1;
        }
        if (a.volumeInCl > b.volumeInCl) {
            return 1;
        }
        if (b.volumeInCl > a.volumeInCl) {
            return -1;
        }
        return 0;
    }

    private buildOtherRow(label: string, price: number, styleClass: string): DrinkRow {
        return {
            label: label,
            price: `${price}.-`,
            styleClass: styleClass
        } as DrinkRow;
    }

    private buildDrinkRow(service: Service): DrinkRow {
        return {
            id: service.id,
            link: `/drinks/${service.boughtDrinkId}`,
            label: `${service.producerName} - ${service?.drinkName}`,
            volume: `${service.volumeInCl}cl.`,
            price: `${service.sellingPrice}.-`,
        } as DrinkRow;
    }

    calculateTotal(services: (Service)[]): number {
        return this.calculateDeposit(services) + this.calculateSubtotal(services);
    }

    calculateSubtotal(services: (Service)[]): number {
        return services.map(service => service?.sellingPrice || 0).reduce((a, b) => a + b, 0);
    }

    calculateDeposit(services: (Service)[]): number {
        return services.length * this.DEPOSIT_PRICE;
    }

    hasBasketContent(): boolean {
        return this.cartService.getBasketContent().length > 0;
    }

    clearCart(): void {
        this.cartService.clearCart();
    }
}

interface DrinkRow {
    id?: number;
    link?: string;
    label: string;
    volume?: string;
    price: string;
    styleClass?: string;
}