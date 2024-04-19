import {Pipe, PipeTransform} from '@angular/core';
import {Availability} from '../models/Availability';

@Pipe({
    name: 'availabilityLabel'
})
export class AvailabilityLabelPipe implements PipeTransform {

    transform(value: Availability | undefined): string {
        switch (value) {
            case 'SOON':
                return 'Prochainement disponible';
            case 'AVAILABLE':
                return 'En stock';
            case 'OUT_OF_STOCK':
                return 'Terminée';
            default:
                return 'Chais pas...';
        }
    }
}
