import {Component, OnInit} from '@angular/core';
import {FormControl, FormGroup, Validators} from "@angular/forms";
import {FetchControllerService, FetchRequest} from "../../../generated";
import {SnackBarService} from "../snack-bar/snack-bar.service";

@Component({
    selector: 'app-fetch',
    templateUrl: './fetch.component.html',
    styleUrls: ['./fetch.component.scss']
})
export class FetchComponent {

    public formGroup: FormGroup = new FormGroup({
        apiKey: new FormControl('', Validators.required),
        mmsi: new FormControl('', Validators.required),
        fromDate: new FormControl('', [Validators.required]),
        toDate: new FormControl('', [Validators.required]),
    });

    constructor(
        private fetchControllerService: FetchControllerService,
        private snackBarService: SnackBarService,

    ) {
    }


    fetchContent() {
        const formModel = this.formGroup.value;
        let snackbar = this.snackBarService.displayInfoMessage("fetching content...");
        console.log("formModel.fromDate:", formModel.fromDate);
        console.log("formModel.fromDate:", formModel.toDate);
        let fetchRequest: FetchRequest = {
            apiKey: formModel.apiKey.trim(),
            mmsi: formModel.mmsi.trim(),
            fromDate: formModel.fromDate.toISOString(),
            toDate: formModel.toDate.toISOString(),
        }
        console.log("fetchRequest:", fetchRequest);
        this.fetchControllerService.fetchContent(fetchRequest).subscribe(
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
        )
    }
}
