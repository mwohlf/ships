import {BrowserModule} from '@angular/platform-browser';
import {NgModule} from '@angular/core';

import {AppRoutingModule} from './app-routing.module';
import {AppComponent} from './app.component';
import {ApiModule, Configuration} from "../generated";

@NgModule({
    declarations: [
        AppComponent
    ],
    imports: [
        ApiModule.forRoot(() => new Configuration({basePath: '/'})),
        BrowserModule,
        AppRoutingModule
    ],
    providers: [],
    bootstrap: [AppComponent]
})
export class AppModule {
}
