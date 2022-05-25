import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { ButtonModule } from 'primeng/button';
import { CardModule } from 'primeng/card';
import { ChipModule } from 'primeng/chip';
import { DataViewModule } from 'primeng/dataview';
import { RippleModule } from 'primeng/ripple';
import { TableModule } from 'primeng/table';
import { DrinkResolver } from '../shared/resolvers/drink.resolver';
import { DrinksByColorResolver } from '../shared/resolvers/drinks-by-color.resolver';
import { DrinksByOriginResolver } from '../shared/resolvers/drinks-by-origin.resolver';
import { DrinksByProducerResolver } from '../shared/resolvers/drinks-by-producer.resolver';
import { DrinksByStyleResolver } from '../shared/resolvers/drinks-by-style.resolver';
import { DrinksResolver } from '../shared/resolvers/drinks.resolver';
import { SharedModule } from '../shared/shared.module';
import { StrengthRadarComponent } from './molecules/strength-radar/strength-radar.component';
import { BeamerDisplayComponent } from './pages/beamer-display/beamer-display.component';
import { DescriptiveDrinksListComponent } from './pages/descriptive-drinks-list/descriptive-drinks-list.component';
import { DrinkComponent } from './pages/drink/drink.component';

@NgModule({
    declarations: [
        DescriptiveDrinksListComponent,
        BeamerDisplayComponent,
        DrinkComponent,
        StrengthRadarComponent
    ],
    imports: [
        SharedModule,
        RouterModule.forChild([
            {
                path: '',
                component: DescriptiveDrinksListComponent,
                resolve: { drinks: DrinksResolver },
                data: { title: 'Toutes les boissons' }
            },
            {
                path: 'beamer',
                component: BeamerDisplayComponent,
                resolve: { drinks: DrinksResolver },
                data: { title: 'Liste de prix' }
            },
            {
                path: 'colors/:colorId',
                component: DescriptiveDrinksListComponent,
                resolve: { drinks: DrinksByColorResolver },
                data: { title: 'Boissons de couleur' }
            },
            {
                path: 'styles/:styleId',
                component: DescriptiveDrinksListComponent,
                resolve: { drinks: DrinksByStyleResolver },
                data: { title: 'Boissons de type' }
            },
            {
                path: 'producers/:producerId',
                component: DescriptiveDrinksListComponent,
                resolve: { drinks: DrinksByProducerResolver },
                data: { title: 'Boissons venant de' }
            },
            {
                path: 'origins/:originId',
                component: DescriptiveDrinksListComponent,
                resolve: { drinks: DrinksByOriginResolver },
                data: { title: 'Boissons provenant de' }
            },
            {
                path: ':drinkId',
                component: DrinkComponent,
                resolve: { drink: DrinkResolver }
            }
        ]),
        TableModule,
        ButtonModule,
        RippleModule,
        CardModule,
        ChipModule,
        DataViewModule
    ],
})
export class DrinksModule {
}
