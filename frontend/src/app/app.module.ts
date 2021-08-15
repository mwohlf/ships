import {BrowserModule} from '@angular/platform-browser';
import {NgModule} from '@angular/core';

import {AppRoutingModule} from './app-routing.module';

import {AppComponent} from './app.component';
import {BrowserAnimationsModule} from '@angular/platform-browser/animations';
import {FlexModule} from '@angular/flex-layout';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {HttpClientModule} from '@angular/common/http';
import {LayoutModule} from '@angular/cdk/layout';
import {MatButtonModule} from '@angular/material/button';
import {MatCardModule} from '@angular/material/card';
import {MatFormFieldModule} from '@angular/material/form-field';
import {MatIconModule} from '@angular/material/icon';
import {MatInputModule} from '@angular/material/input';
import {MatListModule} from '@angular/material/list';
import {MatMenuModule} from '@angular/material/menu';
import {MatPaginatorModule} from '@angular/material/paginator';
import {MatProgressBarModule} from '@angular/material/progress-bar';
import {MatProgressSpinnerModule} from '@angular/material/progress-spinner';
import {MatSidenavModule} from '@angular/material/sidenav';
import {MatSnackBarModule} from '@angular/material/snack-bar';
import {MatTableModule} from '@angular/material/table';
import {MatToolbarModule} from '@angular/material/toolbar';
import {MatSortModule} from '@angular/material/sort';
import {ApiModule, Configuration} from '../generated';
import {ScrollingModule} from '@angular/cdk/scrolling';
import {MatAutocompleteModule} from '@angular/material/autocomplete';
import {MatTooltipModule} from '@angular/material/tooltip';
import { UploadComponent } from './components/upload/upload.component';
import { FetchComponent } from './components/fetch/fetch.component';
import { ViewComponent } from './components/view/view.component';
import { StartComponent } from './components/start/start.component';
import { ErrorComponent } from './components/error/error.component';
import { DatabaseComponent } from './components/database/database.component';
import { NgxFileDropModule } from 'ngx-file-drop';

@NgModule({
    declarations: [
        AppComponent,
        UploadComponent,
        FetchComponent,
        ViewComponent,
        StartComponent,
        ErrorComponent,
        DatabaseComponent
    ],
    imports: [
        ApiModule.forRoot(() => new Configuration({basePath: ''})),
        AppRoutingModule,
        BrowserAnimationsModule,
        BrowserModule,
        FlexModule,
        FormsModule,
        HttpClientModule,
        LayoutModule,
        MatAutocompleteModule,
        MatButtonModule,
        MatCardModule,
        MatFormFieldModule,
        MatIconModule,
        MatInputModule,
        MatListModule,
        MatMenuModule,
        MatPaginatorModule,
        MatProgressBarModule,
        MatProgressSpinnerModule,
        MatSidenavModule,
        MatSnackBarModule,
        MatSortModule,
        MatTableModule,
        MatToolbarModule,
        MatToolbarModule,
        MatTooltipModule,
        ReactiveFormsModule,
        ScrollingModule,
        NgxFileDropModule
    ],
    providers: [],
    bootstrap: [AppComponent]
})
export class AppModule {
}
