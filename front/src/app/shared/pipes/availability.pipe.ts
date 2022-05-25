import { Pipe, PipeTransform } from '@angular/core';
import { Availability } from '../models/Availability';

@Pipe({
    name: 'availability'
})
export class AvailabilityPipe implements PipeTransform {

    transform(value: Availability | undefined): string {
        switch (value) {
            case 'SOON':
                return 'Bientôt disponible';
            case 'AVAILABLE':
                return 'Disponible';
            case 'OUT_OF_STOCK':
                return 'Épuisé';
            default:
                return 'Inconnu';
        }
    }
}
