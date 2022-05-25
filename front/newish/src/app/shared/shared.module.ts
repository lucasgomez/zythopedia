import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { ButtonModule } from 'primeng/button';
import { InputTextModule } from 'primeng/inputtext';
import { MegaMenuModule } from 'primeng/megamenu';
import { MenubarModule } from 'primeng/menubar';
import { RippleModule } from 'primeng/ripple';
import { StyleClassModule } from 'primeng/styleclass';
import { HeaderComponent } from './components/organisms/header/header.component';
import { AbvPipe } from './pipes/abv.pipe';
import { AvailabilityPipe } from './pipes/availability.pipe';

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
        MegaMenuModule
    ],
    declarations: [
        HeaderComponent,
        AbvPipe,
        AvailabilityPipe,
    ],
    exports: [
        CommonModule,
        FormsModule,
        ReactiveFormsModule,
        HeaderComponent,
        AbvPipe,
        AvailabilityPipe,
    ]
})
export class SharedModule {
}
