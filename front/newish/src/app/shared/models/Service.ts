import { Availability } from './Availability';

export interface Service {
    id: number;
    volumeInCl: number;
    sellingPrice: number;
    availability: Availability;
}
