import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
    name: 'abv'
})
export class AbvPipe implements PipeTransform {

    transform(value: number | undefined): string {
        if (!value) return '';
        return `${value.toFixed(1)}%`
    }

}
