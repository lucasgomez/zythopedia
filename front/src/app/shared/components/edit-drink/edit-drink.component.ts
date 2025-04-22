import {Component, EventEmitter, Input, OnChanges, OnInit, Output, SimpleChanges} from '@angular/core';
import {map, Observable} from "rxjs";
import {FullDrinkDto} from "../../models/Drink";
import {FormControl, FormGroup, Validators} from "@angular/forms";
import {Producer} from "../../models/Producer";
import {Color} from "../../models/Color";
import {Style} from "../../models/Style";
import {ColorService} from "../../services/color.service";
import {ProducerService} from "../../services/producer.service";
import {StyleService} from "../../services/style.service";
import {SelectItem} from "primeng/api";

@Component({
    selector: 'edit-drink',
    templateUrl: './edit-drink.component.html',
    styleUrls: ['./edit-drink.component.css']
})
export class EditDrinkComponent implements OnInit, OnChanges {

    @Input() drink?: FullDrinkDto;
    @Output() onSave= new EventEmitter<FullDrinkDto>();

    colorList$!: Observable<SelectItem<Color>[]>;
    producerList$!: Observable<SelectItem<Producer>[]>;
    styleList$!: Observable<SelectItem<Style>[]>;
    drinkForm!: FormGroup;

    constructor(
        private readonly colorService: ColorService,
        private readonly producerService: ProducerService,
        private readonly styleService: StyleService,
    ) {
    }

    ngOnInit(): void {
        this.colorList$ = this.colorService.findAll().pipe(
            map(colors => colors.map(color => this.buildColorItem(color))));
        this.producerList$ = this.producerService.findAll().pipe(
            map(producers => producers.map(producer => this.buildProducerItem(producer))));
        this.styleList$ = this.styleService.findAll().pipe(
            map(styles => styles.map(style => this.buildStyleItem(style))));
    }

    ngOnChanges(changes: SimpleChanges): void {

        this.drinkForm = new FormGroup({
            id: new FormControl(this.drink?.id),
            name: new FormControl(this.drink?.name, Validators.required),
            description: new FormControl(this.drink?.description),
            availability: new FormControl(this.drink?.availability, Validators.required),
            producerId: new FormControl(this.drink?.producerId),
            styleId: new FormControl(this.drink?.styleId),
            colorId: new FormControl(this.drink?.colorId),
            sourness: new FormControl(this.drink?.sourness, [Validators.min(0), Validators.max(5)]),
            bitterness: new FormControl(this.drink?.bitterness, [Validators.min(0), Validators.max(5)]),
            sweetness: new FormControl(this.drink?.sweetness, [Validators.min(0), Validators.max(5)]),
            hoppiness: new FormControl(this.drink?.hoppiness, [Validators.min(0), Validators.max(5)]),
            abv: new FormControl(this.drink?.abv)
        });
    }

    onSubmit(): void {
        if (this.drinkForm.valid) {
            const editedDrink: FullDrinkDto = {
                ...this.drinkForm.value
            };
            this.onSave.emit(editedDrink);
        }
    }

    private buildColorItem(color: Color): SelectItem<Color> {
        return {
            label: color.name,
            value: color
        };
    }

    private buildProducerItem(producer: Producer): SelectItem<Producer> {
        return {
            label: producer.name,
            value: producer
        };
    }

    private buildStyleItem(style: Style): SelectItem<Style> {
        return {
            label: style.name,
            value: style
        };
    }
}
