import {Injectable} from '@angular/core';
import {MatSnackBar, MatSnackBarRef} from '@angular/material/snack-bar';
import {SnackBarComponent} from './snack-bar.component';
import {MatSnackBarConfig} from '@angular/material/snack-bar/snack-bar-config';
import {take, tap} from 'rxjs/operators';

@Injectable({
    providedIn: 'root',
})
export class SnackBarService {
    constructor(private snackBar: MatSnackBar) {
    }

    createSnackBar(): SnackBarConfig {
        return new SnackBarConfig(this.snackBar);
    }

    displayError(error: Error): SnackBarConfig {
        return this.displayErrorMessage(JSON.stringify(error));
    }

    displayErrorMessage(message: string) {
        return new SnackBarConfig(this.snackBar).error(message).duration(7000).display();
    }

    displayInfoMessage(message: string): SnackBarConfig {
        return new SnackBarConfig(this.snackBar).info(message).duration(7000).display();
    }

}

export class SnackBarConfig {
    private timer: number | undefined;

    private matSnackBarRef: MatSnackBarRef<SnackBarComponent> | undefined;

    private message: string | undefined;

    private showProgressBar: boolean = false;

    config: MatSnackBarConfig = {
        duration: undefined, // ms
        verticalPosition: 'top', // 'top' | 'bottom'
        horizontalPosition: 'end', //'start' | 'center' | 'end' | 'left' | 'right'
        panelClass: ['white-snackbar'],
    };

    constructor(private matSnackBar: MatSnackBar) {
    }

    display() {
        this.matSnackBarRef = this.matSnackBar.openFromComponent(SnackBarComponent, this.config);
        this.matSnackBarRef.instance.setMessage(this.message);
        this.matSnackBarRef.instance.showProgress(this.showProgressBar);
        this.matSnackBarRef.afterDismissed().pipe(
            take(1),
            tap(() => {
                this.matSnackBarRef = undefined; // cleanup
            }),
        );
        return this;
    }

    destroy(timeout: number = 0): SnackBarConfig {
        // @ts-ignore
        this.timer = setTimeout(() => {
            this.destroySnackBar();
        }, timeout);
        if (!!this.matSnackBarRef && timeout > 0) {
            this.matSnackBarRef.instance.timeout(timeout);
        }
        return this;
    }

    duration(duration: number): SnackBarConfig {
        this.config.duration = duration;
        if (!!this.matSnackBarRef) {
            this.destroy(duration);
        }
        return this;
    }

    success(message?: string): SnackBarConfig {
        this.appendPanelClass('green-snackbar');
        this.setMessage(message);
        return this;
    }

    info(message?: string): SnackBarConfig {
        this.appendPanelClass('white-snackbar');
        this.setMessage(message);
        return this;
    }

    error(message?: string): SnackBarConfig {
        this.appendPanelClass('red-snackbar');
        this.setMessage(message);
        return this;
    }

    append(message: string): SnackBarConfig {
        this.message = message;
        if (!!this.matSnackBarRef) {
            this.matSnackBarRef.instance.addMessage(message);
        }
        return this;
    }

    showProgress(show: boolean) {
        this.showProgressBar = show;
        if (!!this.matSnackBarRef) {
            this.matSnackBarRef.instance.showProgress(show);
        }
        return this;
    }

    private setMessage(message?: string) {
        if (!message) {
            return;
        }
        this.message = message;
        if (!!this.matSnackBarRef) {
            this.matSnackBarRef.instance.setMessage(message);
        }
    }

    private appendPanelClass(newClass: string) {
        if (!Array.isArray(this.config.panelClass)) {
            this.config.panelClass = [];
        }
        if (this.config.panelClass.indexOf(newClass) === -1) {
            this.config.panelClass.push(newClass);
        }
    }

    private destroySnackBar() {
        clearTimeout(this.timer); // to make sure a leftover timer doesn't destroy the wrong snackBar
        if (this.matSnackBarRef !== undefined) {
            this.matSnackBarRef.dismiss();
            this.matSnackBarRef = undefined;
        }
        return this;
    }
}
