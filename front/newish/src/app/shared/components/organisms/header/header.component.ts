import { Component, Inject, OnInit } from '@angular/core';
import { OKTA_AUTH, OktaAuthStateService } from '@okta/okta-angular';
import { AuthState, OktaAuth } from '@okta/okta-auth-js';
import { MenuItem } from 'primeng/api';
import { Menu } from 'primeng/menu';
import { combineLatest, map, Observable } from 'rxjs';
import { ColorService } from '../../../services/color.service';
import { OriginService } from '../../../services/origin.service';
import { ProducerService } from '../../../services/producer.service';
import { StyleService } from '../../../services/style.service';

@Component({
    selector: 'app-header',
    templateUrl: './header.component.html',
    styleUrls: ['./header.component.css']
})
export class HeaderComponent implements OnInit {

    items$!: Observable<MenuItem[]>;
    authState$!: Observable<AuthState>;

    constructor(
        @Inject(OKTA_AUTH) private readonly oktaAuth: OktaAuth,
        private readonly authService: OktaAuthStateService,
        private readonly producerService: ProducerService,
        private readonly originService: OriginService,
        private readonly styleService: StyleService,
        private readonly colorService: ColorService,
    ) {
    }

    ngOnInit(): void {
        this.authState$ = this.authService.authState$;
        this.items$ = combineLatest([
            this.producerService.findAll(),
            this.originService.findAll(),
            this.styleService.findAll(),
            this.colorService.findAll(),
        ]).pipe(
            map(([producers, origins, styles, colors]) => [
                { label: 'Toutes', icon: 'mdi mdi-format-list-text', routerLink: '/drinks' },
                {
                    label: 'Par couleur', icon: 'mdi mdi-palette',
                    items: colors.sort((a, b) => a.name.localeCompare(b.name)).map(c => ({
                        label: c.name, routerLink: `/drinks/colors/${c.id}`
                    }))
                },
                {
                    label: 'Par style', icon: 'mdi mdi-shape-outline',
                    items: styles.sort((a, b) => a.name.localeCompare(b.name)).map(s => ({
                        label: s.name, routerLink: `/drinks/styles/${s.id}`
                    }))
                },
                {
                    label: 'Par producteur', icon: 'mdi mdi-badge-account-outline',
                    items: producers.sort((a, b) => a.name.localeCompare(b.name)).map(p => ({
                        label: p.name, routerLink: `/drinks/producers/${p.id}`
                    }))
                },
                {
                    label: 'Par origine', icon: 'mdi mdi-map-marker-outline',
                    items: origins.sort((a, b) => a.name.localeCompare(b.name)).map(o => ({
                        label: `${o.flag} ${o.name}`, routerLink: `/drinks/origins/${o.id}`
                    }))
                },
            ])
        )
    }

    loginButton(authState: AuthState): void {
        if (authState.isAuthenticated) {
            this.oktaAuth.signOut().catch(console.error);
            return;
        }

        this.oktaAuth.signInWithRedirect().catch(console.error);
    }
}

