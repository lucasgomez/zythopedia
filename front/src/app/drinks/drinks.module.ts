import {NgModule} from '@angular/core';
import {RouterModule} from '@angular/router';
import {ButtonModule} from 'primeng/button';
import {CardModule} from 'primeng/card';
import {ChipModule} from 'primeng/chip';
import {DataViewModule} from 'primeng/dataview';
import {RippleModule} from 'primeng/ripple';
import {TableModule} from 'primeng/table';
import {TagModule} from 'primeng/tag';
import {DrinkResolver} from '../shared/resolvers/drink.resolver';
import {DrinksByColorResolver} from '../shared/resolvers/drinks-by-color.resolver';
import {DrinksByOriginResolver} from '../shared/resolvers/drinks-by-origin.resolver';
import {DrinksByProducerResolver} from '../shared/resolvers/drinks-by-producer.resolver';
import {DrinksByServiceResolver} from '../shared/resolvers/drinks-by-service.resolver';
import {DrinksByStyleResolver} from '../shared/resolvers/drinks-by-style.resolver';
import {DrinksResolver} from '../shared/resolvers/drinks.resolver';
import {SharedModule} from '../shared/shared.module';
import {StrengthRadarComponent} from './molecules/strength-radar/strength-radar.component';
import {BeamerDisplayComponent} from './pages/beamer-display/beamer-display.component';
import {DescriptiveDrinksListComponent} from './pages/descriptive-drinks-list/descriptive-drinks-list.component';
import {DogModeComponent} from './pages/dog-mode/dog-mode.component';
import {DrinkComponent} from './pages/drink/drink.component';
import {DrinkCardComponent} from "./pages/drink-card/drink-card.component";
import {RandomComponent} from "./pages/random/random.component";
import {BadgeModule} from "primeng/badge";
import {CartComponent} from "./pages/cart/cart.component";
import {ToastModule} from "primeng/toast";
import {DropdownModule} from "primeng/dropdown";
import {ToggleButtonModule} from "primeng/togglebutton";
import {DialogModule} from "primeng/dialog";
import { DrinkEditionComponent } from './pages/drink-edition/drink-edition.component';
import {ConfirmationService} from "primeng/api";
import { CommonModule } from '@angular/common';
import {InputTextModule} from "primeng/inputtext";
import {InputTextareaModule} from "primeng/inputtextarea";
import {ConfirmDialogModule} from "primeng/confirmdialog";
import { ServiceEditionComponent } from './pages/service-edition/service-edition.component';
import { LoginComponent } from './pages/login/login.component';

@NgModule({
    declarations: [
        DescriptiveDrinksListComponent,
        BeamerDisplayComponent,
        DogModeComponent,
        DrinkComponent,
        DrinkCardComponent,
        RandomComponent,
        CartComponent,
        StrengthRadarComponent,
        DrinkEditionComponent,
        ServiceEditionComponent,
        LoginComponent,
    ],
    imports: [
        SharedModule,
        ConfirmDialogModule,
        RouterModule.forChild([
            {
                path: '',
                component: DescriptiveDrinksListComponent,
                resolve: {drinks: DrinksResolver},
                data: {title: 'Toutes les boissons'}
            },
            {
                path: 'random',
                component: RandomComponent
            },
            {
                path: 'beamer',
                component: BeamerDisplayComponent,
                data: {title: 'Liste de prix'}
            },
            {
                path: 'dogmode',
                component: DogModeComponent,
                resolve: {drinks: DrinksResolver},
                data: {title: 'doG Mode'}
            },
            {
                path: 'drink-edition/:id',
                component: DrinkEditionComponent
            },
            {
                path: 'service-edition/:id',
                component: ServiceEditionComponent
            },
            {
                path: 'colors/:colorId',
                component: DescriptiveDrinksListComponent,
                resolve: {drinks: DrinksByColorResolver},
                data: {title: 'Boissons de couleur'}
            },
            {
                path: 'services/:service',
                component: DescriptiveDrinksListComponent,
                resolve: {drinks: DrinksByServiceResolver},
                data: {title: 'Boisson servie en'}
            },
            {
                path: 'styles/:styleId',
                component: DescriptiveDrinksListComponent,
                resolve: {drinks: DrinksByStyleResolver},
                data: {title: 'Boissons de type'}
            },
            {
                path: 'producers/:producerId',
                component: DescriptiveDrinksListComponent,
                resolve: {drinks: DrinksByProducerResolver},
                data: {title: 'Boissons venant de'}
            },
            {
                path: 'origins/:originId',
                component: DescriptiveDrinksListComponent,
                resolve: {drinks: DrinksByOriginResolver},
                data: {title: 'Boissons provenant de'}
            },
            {
                path: 'cart',
                component: CartComponent
            },
            {
                path: 'login',
                component: LoginComponent
            },
            {
                path: ':drinkId',
                component: DrinkComponent,
                resolve: {drink: DrinkResolver}
            }
        ]),
        TableModule,
        ButtonModule,
        RippleModule,
        CardModule,
        ChipModule,
        TagModule,
        DataViewModule,
        BadgeModule,
        ToastModule,
        InputTextModule,
        InputTextareaModule,
        DropdownModule,
        ToggleButtonModule,
        DialogModule,
        CommonModule,
    ],
    providers: [ConfirmationService],
})
export class DrinksModule {
}