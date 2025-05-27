import {Component, OnInit} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {catchError, Observable, pluck, tap} from 'rxjs';
import {DescriptiveList} from '../../../shared/models/DescriptiveList';
import {Drink} from '../../../shared/models/Drink';
import {BoughtDrinkService} from '../../../shared/services/bought-drink.service';
import {Availability} from "../../../shared/models/Availability";
import {ConfirmationService} from "primeng/api";

@Component({
    selector: 'dog-mode',
    templateUrl: './dog-mode.component.html',
    styleUrls: ['./dog-mode.component.css']
})
export class DogModeComponent implements OnInit {
    drinks$!: Observable<DescriptiveList<Drink>>;

    editDrinkPopupVisible = false;
    constructor(
        private readonly route: ActivatedRoute,
        private readonly boughtDrinkService: BoughtDrinkService,
        private readonly confirmationService: ConfirmationService,
        private readonly router: Router,
    ) {
    }

    ngOnInit(): void {
        this.drinks$ = this.route.data.pipe(pluck('drinks'));
    }

    nukeDemAll() {
        this.confirmationService.confirm({
            message: 'Voulez-vous vraiment mettre toutes les bières à "EPUISÉE" ?',
            header: '❌🍻 Panic mode! ❌🍻',
            icon: 'pi pi-exclamation-triangle',
            accept: () => setTimeout(() => {
                // Réaliser l'appel à l'API pour sauvegarder
                this.confirmationService.confirm({
                    message: 'Sûr, sûr, SÛR? Toutes?',
                    header: '😱 pANIC MODE! 😱',
                    icon: 'pi pi-exclamation-triangle',
                    accept: () => {
                        // Réaliser l'appel à l'API pour sauvegarder
                        this.boughtDrinkService
                            .nukeDemAll()
                            .subscribe(() => {
                                alert('Tout est réduit en cendres...');
                                this.router.navigate(['/']);
                            });
                    },
                });
            }, 1000),
        });
    }

    handleAvailabilityChange(drink: Drink, newAvailability: Availability): void {
        this.boughtDrinkService.changeAvailability(drink.id, newAvailability).pipe(
            tap(drink => alert(`Boisson ${drink.id} - ${drink.name} mise à jour en '${drink.availability}'`)),
            catchError(e => {
                alert(`Impossible d'exécuter. Vérifie ton user/pwd, mec!`)
                return e;
            }))
            .subscribe();
    }
}
