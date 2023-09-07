import { Injectable } from '@angular/core';
import { concat, Observable, of, Subject, throwError } from 'rxjs';
import {
    catchError,
    debounceTime,
    distinctUntilChanged,
    filter,
    map,
    switchMap,
    tap,
} from 'rxjs/operators';
import { HttpClient, HttpParams } from '@angular/common/http';

@Injectable({
    providedIn: 'root',
})
export class SharedSearchService {
    constructor(private http: HttpClient) {}

    loadData(
        url: string,
        minLength: number,
        searchInput$: Subject<string>,
        loading$: Subject<boolean>,
        extraUrlParams: string = null
    ): Observable<any> {
        return concat(
            of([]), // default items
            searchInput$.pipe(
                filter((res) => {
                    return res !== null && res.length >= minLength;
                }),
                distinctUntilChanged(),
                debounceTime(800),
                tap(() => loading$.next(true)),
                switchMap((term) => {
                    return this.searchEndPoint(term, url, extraUrlParams).pipe(
                        catchError(() => of([])), // empty list on error
                        tap(() => loading$.next(false))
                    );
                })
            )
        );
    }

    searchEndPoint(
        term: string = null,
        url: string,
        extraUrlParams: string = null
    ): Observable<any> {
        let urlConstruct = `${url}?search=${term}`;
        if (extraUrlParams) {
            urlConstruct = urlConstruct + extraUrlParams;
        }
        return this.http.get<any>(urlConstruct).pipe(
            map((resp) => {
                if (resp.Error) {
                    throwError(resp.Error);
                } else {
                    return resp;
                }
            })
        );
    }
}
