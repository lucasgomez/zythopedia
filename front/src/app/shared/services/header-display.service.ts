import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';
import { environment } from '../../../environments/environment';

const API_URL = `${environment.BASE_URL}/api/boughtdrink`;

@Injectable({
    providedIn: 'root'
})
export class HeaderDisplayService {

    private showHeader = new BehaviorSubject<boolean>(true);

    changeHeaderDisplay(show: boolean) {
        this.showHeader.next(show);
    }

    mustDisplayHeader(): Observable<boolean> {
        return this.showHeader.asObservable();
    }

}
