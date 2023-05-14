import {Component, OnInit} from '@angular/core';
import {combineLatest, map, Observable} from 'rxjs';
import {ServiceService} from "../../../shared/services/service.service";
import {Service} from "../../../shared/models/Service";

const SERVICE_ID_PARAM_NAME = 'drinkServicesIds';

@Component({
    selector: 'app-cart',
    templateUrl: './cart.component.html',
    styleUrls: ['./cart.component.css']
})
export class CartComponent implements OnInit {

    private readonly DEPOSIT_PRICE = 2;
    services$! : Observable<(Service)[]>;

    constructor(private readonly serviceService: ServiceService) {
    }

    ngOnInit(): void {
        this.loadServices();
    }

    loadServices(): void {
        const drinkServicesIds = this.getBasketContent();
        const servicesArray$ = Array.from(new Set(drinkServicesIds))
            .map(id => this.serviceService.findById$(id));
        this.services$ = combineLatest(servicesArray$).pipe(
            map((services: Service[]) => drinkServicesIds
                .map(id => services.find(service => id === service.id))
                .filter(service => !!service)
                .map(service => service as Service))
            );

    }

    clearCart(): void {
        localStorage.removeItem(SERVICE_ID_PARAM_NAME);
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

    removeService(serviceId: number): void {
        const basketContent = this.getBasketContent();
        const index = basketContent.indexOf(serviceId);
        if (index > -1) {
            basketContent.splice(index, 1);
            localStorage.setItem(SERVICE_ID_PARAM_NAME, JSON.stringify(basketContent));
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
            link: `/drinks/${service.drinkId}`,
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

    private getBasketContent(): number[] {
        return JSON.parse(localStorage.getItem(SERVICE_ID_PARAM_NAME) || '[]').map((id: string) => +id);
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