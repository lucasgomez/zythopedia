import { Availability } from './Availability';

export interface Service {
    id: number;
    volume: number;
    sellingPrice: number;
    availability: Availability;
}