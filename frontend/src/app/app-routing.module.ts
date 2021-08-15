import {NgModule} from '@angular/core';
import {Routes, RouterModule} from '@angular/router';
import {FetchComponent} from "./components/fetch/fetch.component";
import {UploadComponent} from "./components/upload/upload.component";
import {ViewComponent} from "./components/view/view.component";
import {StartComponent} from "./components/start/start.component";
import {ErrorComponent} from "./components/error/error.component";
import {DatabaseComponent} from "./components/database/database.component";


export function routerErrorHandler(error: Error): void {
    console.error('router error: ', JSON.stringify(error));
}


const routes: Routes = [
    {
        path: "upload",
        component: UploadComponent,
    },
    {
        path: "fetch",
        component: FetchComponent,
    },
    {
        path: "view",
        component: ViewComponent,
    },
    {
        path: "database",
        component: DatabaseComponent,
    },
    {
        path: "start",
        component: StartComponent,
    },
    {
        path: "error",
        component: ErrorComponent,
    },

    {path: '', redirectTo: 'start', pathMatch: 'full'},
    {path: '**', redirectTo: 'error'},
];

// for a new component:
//  frontend git:(master) ✗ ng g component components/database


@NgModule({
    imports: [RouterModule.forRoot(
        routes,
        {
            scrollPositionRestoration: 'enabled', // one of 'disabled', 'enabled', 'top'
            enableTracing: false, // log to console for debugging
            errorHandler: routerErrorHandler,
            paramsInheritanceStrategy: 'always',
            relativeLinkResolution: 'corrected', // one of 'legacy', 'corrected'
        })],
    exports: [RouterModule],
})
export class AppRoutingModule {
}
