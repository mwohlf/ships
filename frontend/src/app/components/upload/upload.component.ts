import {Component} from '@angular/core';
import {FileSystemDirectoryEntry, FileSystemFileEntry, NgxFileDropEntry} from "ngx-file-drop";
import {DatabaseControllerService} from "../../../generated";
import {SnackBarService} from "../snack-bar/snack-bar.service";
import {BehaviorSubject} from "rxjs";

@Component({
    selector: 'app-upload',
    templateUrl: './upload.component.html',
    styleUrls: ['./upload.component.scss']
})
export class UploadComponent {

    public files: NgxFileDropEntry[] = [];

    private notes = new BehaviorSubject<string[]>([]);
    public notes$ = this.notes.asObservable();

    constructor(
        private databaseControllerService: DatabaseControllerService,
        private snackBarService: SnackBarService,
    ) {
    }

    public dropped(files: NgxFileDropEntry[]) {
        // setup initial counts
        let tableMap = new Map<string, number>();
        this.databaseControllerService.details().subscribe(
            next => {
                next.tableDetails?.forEach( table => {
                    if (table.name) {
                        tableMap.set(table.name, table.rowCount || 0);
                    }
                })
            }
        )

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
                        (uploadResponse: any) => {
                            console.debug("uploadResponse: ", uploadResponse);
                            this.snackBarService.displayInfoMessage("...finished uploading");
                            this.displayDelta(tableMap);
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

    private displayDelta(initialTableMap: Map<string, number>) {
        console.log("initialTableMap", initialTableMap);
        this.databaseControllerService.details().subscribe(
            nextTableMap => {
                let nextNotes: string[] = [];
                nextTableMap.tableDetails?.forEach( newTable => {
                    if (newTable.name && initialTableMap) {
                        let initialCount = initialTableMap.get(newTable.name);
                        let newCount = newTable.rowCount || 0;
                        console.log("initialCount", initialCount);
                        console.log("newCount", newCount);
                        let delta = newCount - initialCount!;
                        console.log("delta", delta);
                        nextNotes.push(" table " + newTable.name + ": +" + delta + " -> " + newCount);
                    }
                })
                this.notes.next(nextNotes);
            }
        )
    }
}
