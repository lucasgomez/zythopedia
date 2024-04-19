import {Pipe, PipeTransform} from '@angular/core';
import {Availability} from '../models/Availability';

@Pipe({
    name: 'availabilityIcon'
})
export class AvailabilityIconPipe implements PipeTransform {

    transform(value: Availability | undefined): string {
        switch (value) {
            case 'SOON':
                return '🕛';
            case 'AVAILABLE':
                return '🍻';
            case 'OUT_OF_STOCK':
                return '❌';
            default:
                return '❔';
        }
    }
}
