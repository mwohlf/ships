import {Component, OnInit} from '@angular/core';
import {DatabaseControllerService} from "../../../generated";
import {SnackBarService} from "../snack-bar/snack-bar.service";

@Component({
    selector: 'app-database',
    templateUrl: './database.component.html',
    styleUrls: ['./database.component.scss']
})
export class DatabaseComponent implements OnInit {

    constructor(
        private databaseControllerService: DatabaseControllerService,
        private snackBarService: SnackBarService,
    ) {
    }

    ngOnInit(): void {
    }

    dropDatabaseContent() {
        let snackbar = this.snackBarService.createSnackBar().info("dropping database content, please wait...").display();
        this.databaseControllerService.deleteDatabase("all").subscribe(
            (next) => {
                console.log(next);
                snackbar.destroy();
            },
            (error: Error) => {
                console.error(error);
                this.snackBarService.displayErrorMessage("something went wrong on the server side: " + JSON.stringify(error));
            },
            () => {
                console.log("complete");
                snackbar.destroy();
            },
        );
    }

}
