import {CommonModule} from '@angular/common';
import {NgModule} from '@angular/core';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {ButtonModule} from 'primeng/button';
import {InputTextModule} from 'primeng/inputtext';
import {MegaMenuModule} from 'primeng/megamenu';
import {MenubarModule} from 'primeng/menubar';
import {RippleModule} from 'primeng/ripple';
import {StyleClassModule} from 'primeng/styleclass';
import {HeaderComponent} from './components/organisms/header/header.component';
import {AbvPipe} from './pipes/abv.pipe';
import {AvailabilityColorPipe} from './pipes/availability-color.pipe';
import {AvailabilityIconPipe} from './pipes/availabilityIcon.pipe';
import {AvailabilityLabelPipe} from "./pipes/availability-label.pipe";
import {BeerCardComponent} from "./components/beer-card/beer-card.component";
import {ChipModule} from "primeng/chip";
import { AvailibilityBadgeComponent } from './components/availibility-badge/availibility-badge.component';
import {BadgeModule} from "primeng/badge";
import { EditDrinkComponent } from './components/edit-drink/edit-drink.component';
import {DropdownModule} from "primeng/dropdown";

@NgModule({
    imports: [
        CommonModule,
        FormsModule,
        ReactiveFormsModule,
        RippleModule,
        StyleClassModule,
        MenubarModule,
        InputTextModule,
        ButtonModule,
        MegaMenuModule,
        ChipModule,
        BadgeModule,
        DropdownModule
    ],
    declarations: [
        HeaderComponent,
        BeerCardComponent,
        AbvPipe,
        AvailabilityIconPipe,
        AvailabilityLabelPipe,
        AvailabilityColorPipe,
        AvailibilityBadgeComponent,
        EditDrinkComponent,
    ],
    exports: [
        CommonModule,
        FormsModule,
        ReactiveFormsModule,
        HeaderComponent,
        BeerCardComponent,
        AbvPipe,
        AvailabilityIconPipe,
        AvailabilityLabelPipe,
        AvailabilityColorPipe,
        AvailibilityBadgeComponent,
        EditDrinkComponent,
    ]
})
export class SharedModule {
}
