import {CommonModule} from '@angular/common';
import {HttpClientModule} from '@angular/common/http';
import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {HomeComponent} from './home/home.component';

const routes: Routes = [
    { path: '', redirectTo: '/drinks', pathMatch: 'full' },
    {
        path: 'home',
        component: HomeComponent
    }
];

@NgModule({
    declarations: [
        HomeComponent
    ],
    imports: [
        CommonModule,
        HttpClientModule,
        RouterModule.forRoot(routes)
    ],
    providers: [],
    exports: [RouterModule]
})
export class AuthRoutingModule {
}
