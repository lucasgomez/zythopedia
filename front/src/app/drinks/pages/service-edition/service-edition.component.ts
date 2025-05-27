import { Component, OnInit } from '@angular/core';
import { FormArray, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { BoughtDrinkService } from '../../../shared/services/bought-drink.service';
import { ServiceService } from '../../../shared/services/service.service';
import { DetailedDrink } from '../../../shared/models/Drink';
import { Service } from "../../../shared/models/Service";

@Component({
  selector: 'app-service-edition',
  templateUrl: './service-edition.component.html',
  styleUrls: ['./service-edition.component.css'],
})
export class ServiceEditionComponent implements OnInit {
  drink!: DetailedDrink;
  servicesForm!: FormGroup;
  boughtDrinkId!: number;
  isLoading = true;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private fb: FormBuilder,
    private boughtDrinkService: BoughtDrinkService,
    private serviceService: ServiceService
  ) {}

  ngOnInit(): void {
    // Récupérer le Paramètre ID à partir de l'URL
    this.boughtDrinkId = Number(this.route.snapshot.paramMap.get('id'));

    // Initialiser le formulaire
    this.initForm();

    // Charger les données
    this.loadDrinkData();
  }

  initForm(): void {
    this.servicesForm = this.fb.group({
      services: this.fb.array([]),
    });
  }

  // Crée un objet FormGroup pour chaque Service
  private buildServiceFormGroup(service: Service): FormGroup {
    return this.fb.group({
      id: [service.id],
      volumeInCl: [service.volumeInCl, [Validators.required, Validators.min(0)]],
      sellingPrice: [service.sellingPrice, [Validators.required, Validators.min(0)]],
    });
  }

  // Obtenir l'array des services
  get services(): FormArray {
    return this.servicesForm.get('services') as FormArray;
  }

  // Charger les données du Drink
  private loadDrinkData(): void {
    this.boughtDrinkService.getDrink(this.boughtDrinkId).subscribe({
      next: (drink: DetailedDrink) => {
        this.drink = drink;

        // Ajouter chaque service au formulaire
        drink.services.forEach((service) => {
          this.services.push(this.buildServiceFormGroup(service));
        });

        this.isLoading = false;
      },
      error: () => {
        alert('Impossible de charger les détails du drink.');
        this.router.navigate(['/']); // Retour à la page d'accueil.
      },
    });
  }

  // Ajouter un nouveau service
  addService(): void {
    const newService: Service = {
      id: 0, // ID temporaire pour un nouveau service
      boughtDrinkId: this.boughtDrinkId,
      drinkName: this.drink.name,
      producerName: this.drink.producer.name,
      volumeInCl: 0,
      sellingPrice: 0,
      availability: 'AVAILABLE',
    };
    this.services.push(this.buildServiceFormGroup(newService));
  }

  // Supprimer un service
  removeService(index: number): void {
    this.services.removeAt(index);
  }

  // Annuler et retourner en arrière
  cancel(): void {
    this.router.navigate(['/']); // Retour à la page précédente
  }

  // Sauvegarder les services
  saveServices(): void {
    if (this.servicesForm.valid) {
      this.serviceService
        .updateServices(this.boughtDrinkId, this.servicesForm.value.services, this.getCredentials())
        .subscribe({
          next: () => {
            alert('Services sauvegardés avec succès.');
            this.router.navigate(['/']); // Redirection après la sauvegarde
          },
          error: () => {
            alert('Erreur lors de l\'enregistrement des services.');
          },
        });
    } else {
      alert('Veuillez corriger les erreurs dans le formulaire.');
    }
  }

  private getCredentials() {
    //TODO use stored credentials
    return { username: 'barbar', password: 'motdepasse' };
  }
}