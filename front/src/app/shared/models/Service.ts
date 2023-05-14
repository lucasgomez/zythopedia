import { Availability } from './Availability';

export interface Service {
    id: number;
    boughtDrinkId: number;
    drinkName: string;
    producerName: string;
    volumeInCl: number;
    sellingPrice: number;
    availability: Availability;
}
