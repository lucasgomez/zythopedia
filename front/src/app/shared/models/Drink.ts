import {Availability} from './Availability';
import {Color} from './Color';
import {Producer} from './Producer';
import {Service} from './Service';
import {Style} from './Style';
import {ServiceMethod} from "./ServiceMethod";

export interface Drink {
    id: number;
    name: string;
    producerId: number;
    producerName: string;
    originFlag: string;
    originShortName: string;
    colorId: number;
    colorName: string;
    styleId: number;
    styleName: string;
    abv: number;
    availability: Availability;
    location: string;
    services: Service[];
}

export interface DetailedDrink {
    id: number;
    name: string;
    description: string;
    producer: Producer;
    color: Color;
    style: Style;
    sourness: number;
    bitterness: number;
    sweetness: number;
    hoppiness: number;
    abv: number;
    availability: Availability;
    location: string;
    services: Service[];
}

export interface FullDrinkDto {
    id: number;
    drinkId: number;
    name: string;
    description: string;
    abv: number;
    producerId: number;
    colorId: number;
    styleId: number;
    sourness: number;
    bitterness: number;
    sweetness: number;
    hoppiness: number;
    buyingPrice: number;
    location: string;
    serviceMethod: ServiceMethod;
    volumeInCl: number;
    availability: Availability;
}