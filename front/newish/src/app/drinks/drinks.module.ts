import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { TableModule } from 'primeng/table';
import { DrinksByColorResolver } from '../shared/resolvers/drinks-by-color.resolver';
import { DrinksByOriginResolver } from '../shared/resolvers/drinks-by-origin.resolver';
import { DrinksByProducerResolver } from '../shared/resolvers/drinks-by-producer.resolver';
import { DrinksByStyleResolver } from '../shared/resolvers/drinks-by-style.resolver';
import { SharedModule } from '../shared/shared.module';
import { DescriptiveDrinksListComponent } from './pages/descriptive-drinks-list/descriptive-drinks-list.component';

@NgModule({
    declarations: [
        DescriptiveDrinksListComponent
    ],
    imports: [
        SharedModule,
        RouterModule.forChild([
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
            }
        ]),
        TableModule
    ],
})
export class DrinksModule {
}
