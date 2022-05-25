import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { Observable, pluck } from 'rxjs';
import { DescriptiveList } from '../../../shared/models/DescriptiveList';
import { Drink } from '../../../shared/models/Drink';

@Component({
    selector: 'app-descriptive-drinks-list',
    templateUrl: './descriptive-drinks-list.component.html',
    styleUrls: ['./descriptive-drinks-list.component.css']
})
export class DescriptiveDrinksListComponent implements OnInit {

    title!: string;
    drinks$!: Observable<DescriptiveList<Drink>>;

    constructor(
        private readonly route: ActivatedRoute,
    ) {
    }

    ngOnInit(): void {
        this.title = this.route.snapshot.data['title'];
        this.drinks$ = this.route.data.pipe(pluck('drinks'));
    }

}
