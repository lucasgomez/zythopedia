import {Component, Input, OnInit} from '@angular/core';
import {DetailedDrink} from '../../../shared/models/Drink';
import {FormBuilder, FormGroup} from "@angular/forms";
import {Producer} from "../../../shared/models/Producer";
import {Color} from "../../../shared/models/Color";
import {Style} from "../../../shared/models/Style";
import {Availability} from "../../../shared/models/Availability";
import {Service} from "../../../shared/models/Service";
import {SelectItem} from "primeng/api";
import {map, Observable} from "rxjs";

@Component({
    selector: 'drink-edition',
    templateUrl: './drink-edition.component.html',
    styleUrls: ['./drink-edition.component.css']
})
export class DrinkEditionComponent implements OnInit {

    @Input()
    drink: DetailedDrink;

    colorsItemList$!: Observable<SelectItem[]>;

    form!: FormGroup;

    constructor(
        private readonly formBuilder: FormBuilder,
        private readonly colorService: ColorService,) {
    }

    ngOnInit(): void {
        this.colorsItemList$ = this.buildColorsItemList();
        if (!!this.drink) {
            this.form = this.buildExistingDrinkForm(this.drink);
            return;
        }
        this.form = this.buildNewDrinkForm();
    }

    private buildNewDrinkForm(): FormGroup {
        return this.formBuilder.group({
            name: [existingDrink.name],
            description: [existingDrink.description],
            producerId: [existingDrink.producer.id],
            color: [existingDrink.color.id],
            style: [existingDrink.style.id],
            sourness: [existingDrink.sourness],
            bitterness: [existingDrink.bitterness],
            sweetness: [existingDrink.sweetness],
            hoppiness: [existingDrink.hoppiness],
            abv: [existingDrink.abv],
            availability: [existingDrink.availability],
        })
    }

    private buildExistingDrinkForm(existingDrink: DetailedDrink): FormGroup {
        return this.formBuilder.group({
            name: [existingDrink.name],
            description: [existingDrink.description],
            producerId: [existingDrink.producer.id],
            color: [existingDrink.color.id],
            style: [existingDrink.style.id],
            sourness: [existingDrink.sourness],
            bitterness: [existingDrink.bitterness],
            sweetness: [existingDrink.sweetness],
            hoppiness: [existingDrink.hoppiness],
            abv: [existingDrink.abv],
            availability: [existingDrink.availability],
        })
    }

    private buildColorsItemList() {
        return this.colorService.findAll().pipe(
            map(colors => colors.map(color => ({label: color.name, value: color.id})))
        );
    }
}
