import {Injectable} from '@angular/core';
import {Service} from "../models/Service";

@Injectable({
    providedIn: 'root'
})
export class CartService {

    getBasketContent(): number[] {
        return JSON.parse(localStorage.getItem(SERVICE_ID_PARAM_NAME) || '[]').map((id: string) => +id);
    }

    addDrinkServiceToBasket(service: Service): void {
        let drinkServicesIds = JSON.parse(localStorage.getItem('drinkServicesIds') || '[]');
        drinkServicesIds.push(service.id);
        localStorage.setItem('drinkServicesIds', JSON.stringify(drinkServicesIds));
    }

    clearCart(): void {
        localStorage.removeItem(SERVICE_ID_PARAM_NAME);
    }

    setBasketContent(basketContent: number[]) {
        localStorage.setItem(SERVICE_ID_PARAM_NAME, JSON.stringify(basketContent));
    }
}

export const SERVICE_ID_PARAM_NAME = 'drinkServicesIds';
