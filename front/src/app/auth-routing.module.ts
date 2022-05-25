import { CommonModule } from '@angular/common';
import { HTTP_INTERCEPTORS, HttpClientModule } from '@angular/common/http';
import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { OKTA_CONFIG, OktaAuthModule, OktaCallbackComponent } from '@okta/okta-angular';
import { OktaAuth } from '@okta/okta-auth-js';
import { HomeComponent } from './home/home.component';
import { AuthInterceptor } from './shared/okta/auth.interceptor';

const oktaConfig = {
    issuer: 'https://dev-78204857.okta.com/oauth2/default',
    clientId: '0oa50tq24w3EY7cXD5d7',
    redirectUri: '/callback',
    scopes: ['openid', 'profile']
};

const oktaAuth = new OktaAuth(oktaConfig);

const routes: Routes = [
    { path: '', redirectTo: '/drinks', pathMatch: 'full' },
    {
        path: 'home',
        component: HomeComponent
    },
    {
        path: 'callback',
        component: OktaCallbackComponent
    }
];

@NgModule({
    declarations: [
        HomeComponent
    ],
    imports: [
        CommonModule,
        HttpClientModule,
        OktaAuthModule,
        RouterModule.forRoot(routes)
    ],
    providers: [
        { provide: OKTA_CONFIG, useValue: { oktaAuth } },
        { provide: HTTP_INTERCEPTORS, useClass: AuthInterceptor, multi: true }
    ],
    exports: [RouterModule]
})
export class AuthRoutingModule {
}
