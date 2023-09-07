import { Pipe, PipeTransform } from '@angular/core';
import * as moment from 'moment';

@Pipe({
    name: 'momentDate',
})
export class MomentDatePipe implements PipeTransform {
    transform(value: unknown, ...args: unknown[]): unknown {
        const { year, month, day }: any = value;

        const modate = moment(new Date(year, month, day));
        if (!modate.isValid()) {
            return value;
        }

        return modate.fromNow();
    }
}
