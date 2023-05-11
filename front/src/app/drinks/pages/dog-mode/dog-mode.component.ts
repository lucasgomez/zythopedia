import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import {catchError, Observable, pluck, tap} from 'rxjs';
import { DescriptiveList } from '../../../shared/models/DescriptiveList';
import { Drink } from '../../../shared/models/Drink';
import { BoughtDrinkService } from '../../../shared/services/bought-drink.service';
import {FormBuilder, FormGroup, Validators} from "@angular/forms";

@Component({
    selector: 'dog-mode',
    templateUrl: './dog-mode.component.html',
    styleUrls: ['./dog-mode.component.css']
})
export class DogModeComponent implements OnInit {

    drinks$!: Observable<DescriptiveList<Drink>>;
    availabilities = ['SOON','AVAILABLE','OUT_OF_STOCK'];
    loginForm!: FormGroup;

    constructor(
        private readonly route: ActivatedRoute,
        private readonly boughtDrinkService: BoughtDrinkService,
        private readonly formBuilder: FormBuilder,
    ) {
    }

    ngOnInit(): void {
        this.loginForm = this.buildLoginForm();
        this.drinks$ = this.route.data.pipe(pluck('drinks'));
    }

    changeAvailability(drink: Drink, availability: string): void {
        if (this.loginForm.invalid) {
            alert('Il faut avoir remplir le user/password pour utiliser le dog mode');
            return;
        }

        this.boughtDrinkService.changeAvailability(drink.id, availability, this.loginForm.get('username')?.value, this.loginForm.get('password')?.value).pipe(
            tap(drink => alert(`Boisson ${drink.id} - ${drink.name} mise à jour en '${drink.availability}'`)),
            catchError(e => {
                alert(`Impossible d'exécuter. Vérifie ton user/pwd, mec!`)
                return e;
            }))
            .subscribe();
    }

    buildLoginForm(): FormGroup {
        return this.formBuilder.group({
            username: ['', Validators.required],
            password: ['', Validators.required]
        });
    }
}
