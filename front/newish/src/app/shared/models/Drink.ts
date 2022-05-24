import { Availability } from './Availability';
import { Service } from './Service';

export interface Drink {
    id: number;
    name: string;
    producerId: number;
    producerName: string;
    colorId: number;
    colorName: string;
    styleId: number;
    styleName: string;
    abv: number;
    availability: Availability;
    services: Service[];
}
