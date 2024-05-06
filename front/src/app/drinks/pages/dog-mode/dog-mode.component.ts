import {Component, OnInit} from '@angular/core';
import {ActivatedRoute} from '@angular/router';
import {catchError, Observable, pluck, Subject, switchMap, tap} from 'rxjs';
import {DescriptiveList} from '../../../shared/models/DescriptiveList';
import {Drink, FullDrinkDto} from '../../../shared/models/Drink';
import {BoughtDrinkService, ClunkyCredentials} from '../../../shared/services/bought-drink.service';
import {FormBuilder, FormGroup, Validators} from "@angular/forms";
import {Availability} from "../../../shared/models/Availability";

@Component({
    selector: 'dog-mode',
    templateUrl: './dog-mode.component.html',
    styleUrls: ['./dog-mode.component.css']
})
export class DogModeComponent implements OnInit {

    drinks$!: Observable<DescriptiveList<Drink>>;
    drinkToEdit$ = new Subject<Drink>();
    detailedDrinkToEdit$!: Observable<FullDrinkDto>;
    loginForm!: FormGroup;
    editDrinkPopupVisible = false;

    constructor(
        private readonly route: ActivatedRoute,
        private readonly boughtDrinkService: BoughtDrinkService,
        private readonly formBuilder: FormBuilder,
    ) {
    }

    ngOnInit(): void {
        this.loginForm = this.buildLoginForm();
        this.drinks$ = this.route.data.pipe(pluck('drinks'));
        this.detailedDrinkToEdit$ = this.drinkToEdit$.pipe(
            switchMap(drink => this.boughtDrinkService.getFullDrink(drink.id, this.getCredentials()))
        );
    }

    changeAvailability(drink: Drink): void {
        if (this.loginForm.invalid) {
            alert('Il faut avoir remplir le user/password pour utiliser le dog mode');
            return;
        }

        this.boughtDrinkService.changeAvailability(drink.id, this.getNextAvailability(drink.availability), this.getCredentials()).pipe(
            tap(drink => alert(`Boisson ${drink.id} - ${drink.name} mise à jour en '${drink.availability}'`)),
            catchError(e => {
                alert(`Impossible d'exécuter. Vérifie ton user/pwd, mec!`)
                return e;
            }))
            .subscribe();
    }

    getNextAvailability(availability: Availability): Availability {
        switch (availability) {
            case "AVAILABLE":
                return 'OUT_OF_STOCK';
            case "SOON":
                return 'AVAILABLE';
            default:
                return 'SOON';
        }
    }

    buildLoginForm(): FormGroup {
        return this.formBuilder.group({
            username: ['', Validators.required],
            password: ['', Validators.required]
        });
    }

    editDrink(drink: Drink): void {
        if (this.loginForm.invalid) {
            alert('Il faut avoir remplir le user/password pour utiliser le dog mode');
            return;
        }

        this.drinkToEdit$.next(drink);
        this.editDrinkPopupVisible = true;
    }

    private getCredentials(): ClunkyCredentials {
        return {
            username: this.loginForm.get('username')?.value,
            password: this.loginForm.get('password')?.value
        };
    }

    onSaveDrink(drinkToSave: FullDrinkDto) {
        this.boughtDrinkService.updateDrink(drinkToSave.id, drinkToSave, this.getCredentials())
            .subscribe(updatedDrink => alert(`Boisson '${updatedDrink.name}' mise à jour`)
        );
    }
}
