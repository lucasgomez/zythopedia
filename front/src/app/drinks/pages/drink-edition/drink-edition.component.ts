import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import {ConfirmationService, SelectItem} from 'primeng/api'; // Confirmation dialog
import {map, Observable} from 'rxjs';
import {BoughtDrinkService} from "../../../shared/services/bought-drink.service";
import {ColorService} from "../../../shared/services/color.service";
import {ProducerService} from "../../../shared/services/producer.service";
import {StyleService} from "../../../shared/services/style.service";
import {FullDrinkDto} from "../../../shared/models/Drink";
import {Availability} from "../../../shared/models/Availability";

@Component({
  selector: 'app-drink-edition',
  templateUrl: './drink-edition.component.html',
  styleUrls: ['./drink-edition.component.css'],
})
export class DrinkEditionComponent implements OnInit {
  drinkForm!: FormGroup;
  drinkId!: number;
  isLoading = true;
  availabilitiesList: Availability[] = ['SOON', 'AVAILABLE', 'OUT_OF_STOCK'];
  colorList$!: Observable<SelectItem[]>;
  producerList$!: Observable<SelectItem[]>;
  styleList$!: Observable<SelectItem[]>;

  constructor(
    private fb: FormBuilder,
    private route: ActivatedRoute,
    private router: Router,
    private boughtDrinkService: BoughtDrinkService,
    private colorService: ColorService,
    private producerService: ProducerService,
    private stylerService: StyleService,
    private confirmationService: ConfirmationService // Dialog box service
  ) {}

  ngOnInit(): void {
    // Récupération de l'ID de l'URL
    this.drinkId = Number(this.route.snapshot.paramMap.get('id'));

    // Initialiser le formulaire
    this.initForm();

    // Charger les listes (Color, Producer, Style)
    this.loadDropdownValues();

    // Charger les données du composant
    this.loadDrinkData();
  }

  private loadDropdownValues(): void {
    this.colorList$ = this.colorService.findAll().pipe(map(colors => this.buildSelectItems(colors)));
    this.producerList$ = this.producerService.findAll().pipe(map(producers => this.buildSelectItems(producers)));
    this.styleList$ = this.stylerService.findAll().pipe(map(styles => this.buildSelectItems(styles)));
  }

  private buildSelectItems<T extends { id: any; name: string }>(items: T[]): SelectItem<T>[] {
    return items
        .map(item => this.buildSelectItem(item))
        .sort((a, b) => (a.label ?? '').localeCompare(b.label ?? ''));
  }

  private buildSelectItem<T extends { id: any; name: string }>(item: T): SelectItem<T> {
    return {
      label: item.name,
      value: item.id
    };
  }

  private loadDrinkData(): void {
    this.boughtDrinkService.getFullDrink(this.drinkId, this.getCredentials()).subscribe({
      next: (drink: FullDrinkDto) => {
        this.drinkForm.patchValue(drink);
        this.isLoading = false;
      },
      error: () => {
        alert('Impossible de charger les données du drink.');
        this.router.navigate(['/']); // Retour à une page principale en cas d'échec
      },
    });
  }

  private initForm(): void {
    this.drinkForm = this.fb.group({
      id: [null],
      name: ['', [Validators.required]],
      description: [''],
      location: [''],
      colorId: [null, [Validators.required]],
      producerId: [null, [Validators.required]],
      styleId: [null, [Validators.required]],
      abv: [0, [Validators.min(0), Validators.max(100)]],
      sourness: [0, [Validators.min(0), Validators.max(5)]],
      bitterness: [0, [Validators.min(0), Validators.max(5)]],
      sweetness: [0, [Validators.min(0), Validators.max(5)]],
      hoppiness: [0, [Validators.min(0), Validators.max(5)]],
      availability: ['AVAILABLE'], // par défaut
      buyingPrice: [0],
      serviceMethod: [''],
      volumeInCl: [0],
    });
  }

  saveDrink(): void {
    this.confirmationService.confirm({
      message: 'Voulez-vous vraiment sauvegarder cette modification ?',
      header: 'Confirmation',
      icon: 'pi pi-exclamation-triangle',
      accept: () => {
        // Réaliser l'appel à l'API pour sauvegarder
        this.boughtDrinkService
          .updateDrink(this.drinkId, this.drinkForm.value, this.getCredentials())
          .subscribe(() => {
            alert('Drink sauvegardé avec succès.');
            this.router.navigate(['/drinks', this.drinkId]); // Rediriger après la sauvegarde
          });
      },
    });
  }

  resetForm(): void {
    this.confirmationService.confirm({
      message: 'Voulez-vous vraiment réinitialiser le formulaire ?',
      header: 'Confirmation',
      icon: 'pi pi-exclamation-triangle',
      accept: () => {
        this.loadDrinkData(); // Réinitialise à partir des données chargées
      },
    });
  }

  cancel(): void {
    this.confirmationService.confirm({
      message: 'Voulez-vous annuler cette édition ?',
      header: 'Confirmation',
      icon: 'pi pi-exclamation-triangle',
      accept: () => {
        this.router.navigate(['/']); // Retourner à la page précédente
      },
    });
  }

  private getCredentials() {
    //TODO use stored credentials
    return { username: 'barbar', password: 'motdepasse' };
  }
}