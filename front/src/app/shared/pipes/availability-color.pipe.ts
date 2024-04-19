import {Pipe, PipeTransform} from '@angular/core';
import {Availability} from '../models/Availability';

@Pipe({
    name: 'availabilityColor'
})
export class AvailabilityColorPipe implements PipeTransform {

    transform(value: Availability | undefined): string {
        switch (value) {
            case 'SOON':
                return 'warning';
            case 'AVAILABLE':
                return 'success';
            case 'OUT_OF_STOCK':
                return 'danger';
            default:
                return 'info';
        }
    }
}
