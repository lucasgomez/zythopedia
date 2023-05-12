import {Component, OnInit} from '@angular/core';
import {MenuItem} from 'primeng/api';
import {combineLatest, map, Observable} from 'rxjs';
import {ColorService} from '../../../services/color.service';
import {HeaderDisplayService} from '../../../services/header-display.service';
import {OriginService} from '../../../services/origin.service';
import {ProducerService} from '../../../services/producer.service';
import {StyleService} from '../../../services/style.service';

@Component({
    selector: 'app-header',
    templateUrl: './header.component.html',
    styleUrls: ['./header.component.css']
})
export class HeaderComponent implements OnInit {

    items$!: Observable<MenuItem[]>;
    serviceMethods = [
        {label: 'Pressions', value: 'TAP'},
        {label: 'Bouteilles', value: 'BOTTLE'}];
    displayHeader$!: Observable<boolean>;

    constructor(
        private readonly producerService: ProducerService,
        private readonly originService: OriginService,
        private readonly styleService: StyleService,
        private readonly colorService: ColorService,
        private readonly headerDisplayService: HeaderDisplayService,
    ) {
    }

    ngOnInit(): void {
        this.displayHeader$ = this.headerDisplayService.mustDisplayHeader();
        this.items$ = combineLatest([
            this.producerService.findAll(),
            this.originService.findAll(),
            this.styleService.findAll(),
            this.colorService.findAll(),
        ]).pipe(
            map(([producers, origins, styles, colors]) => [
                { label: 'Toutes', icon: 'mdi mdi-format-list-text', routerLink: '/drinks' },
                { label: 'L\'Oracle', icon: 'mdi mdi-head-question', routerLink: '/drinks/random' },
                {
                    label: 'Par service', icon: 'mdi mdi-chemical-weapon',
                    items: this.serviceMethods.map(serviceMethod => ({
                        label: serviceMethod.label, routerLink: `/drinks/services/${serviceMethod.value}`
                    }))
                },
                {
                    label: 'Par couleur', icon: 'mdi mdi-palette',
                    items: colors.sort((a, b) => a.name.localeCompare(b.name)).map(c => ({
                        label: `${c.name} (${c.currentEditionAvailableCount}/${c.currentEditionCount})`,
                        routerLink: `/drinks/colors/${c.id}`
                    }))
                },
                {
                    label: 'Par style', icon: 'mdi mdi-shape-outline',
                    items: styles.sort((a, b) => a.name.localeCompare(b.name)).map(style => ({
                        label: `${style.name} (${style.currentEditionAvailableCount}/${style.currentEditionCount})`,
                        routerLink: `/drinks/styles/${style.id}`
                    }))
                },
                {
                    label: 'Par producteur', icon: 'mdi mdi-badge-account-outline',
                    items: producers.sort((a, b) => a.name.localeCompare(b.name)).map(producer => ({
                        label: `${producer.name} (${producer.currentEditionAvailableCount}/${producer.currentEditionCount})`,
                        routerLink: `/drinks/producers/${producer.id}`
                    }))
                },
                {
                    label: 'Par origine', icon: 'mdi mdi-map-marker-outline',
                    items: origins.sort((a, b) => a.name.localeCompare(b.name)).map(origin => ({
                        label: `${origin.flag} ${origin.name} (${origin.currentEditionAvailableCount}/${origin.currentEditionCount})`,
                        routerLink: `/drinks/origins/${origin.id}`
                    }))
                },
            ])
        )
    }
}

