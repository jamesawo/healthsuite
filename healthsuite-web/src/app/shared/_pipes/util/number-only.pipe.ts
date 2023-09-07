import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
    name: 'numberOnly',
})
export class NumberOnlyPipe implements PipeTransform {
    transform(value: any, ...args: unknown[]): unknown {
        return null;
    }
}
