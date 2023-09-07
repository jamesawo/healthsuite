import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
    name: 'fileSize',
})
export class FileSizePipe implements PipeTransform {
    transform(value: any, ...args: unknown[]): unknown {
        return Math.round(value / 1024) + ' KB';
    }
}
