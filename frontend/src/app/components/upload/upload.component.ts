import {Component} from '@angular/core';
import {FileSystemDirectoryEntry, FileSystemFileEntry, NgxFileDropEntry} from "ngx-file-drop";
import {DatabaseControllerService} from "../../../generated";
import {SnackBarService} from "../snack-bar/snack-bar.service";

@Component({
    selector: 'app-upload',
    templateUrl: './upload.component.html',
    styleUrls: ['./upload.component.scss']
})
export class UploadComponent {

    public files: NgxFileDropEntry[] = [];

    constructor(
        private databaseControllerService: DatabaseControllerService,
        private snackBarService: SnackBarService,
    ) {
    }

    public dropped(files: NgxFileDropEntry[]) {
        this.files = files;
        this.snackBarService.displayInfoMessage("uploading content, please wait...");
        for (const droppedFile of files) {
            console.debug("droppedFile: ", droppedFile);
            if (droppedFile.fileEntry.isFile && droppedFile.fileEntry.name) {
                const fileEntry = droppedFile.fileEntry as FileSystemFileEntry;
                fileEntry.file((file: File) => {
                    // Here you can access the real file
                    console.log(fileEntry.name, file);
                    this.databaseControllerService.uploadDatabaseContent(file).subscribe(
                        (uploadResponse) => {
                            console.debug("uploadResponse: ", uploadResponse);
                            this.snackBarService.displayInfoMessage("...finished uploading");
                        }
                    );
                });
            } else {
                // It was a directory (empty directories are added, otherwise only files)
                const fileEntry = droppedFile.fileEntry as FileSystemDirectoryEntry;
                console.log(droppedFile.relativePath, fileEntry);
            }
        }
    }

    public fileOver(event: any) {
        console.log(event);
    }

    public fileLeave(event: any) {
        console.log(event);
    }

}
