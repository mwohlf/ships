import {Component} from '@angular/core';
import {BehaviorSubject} from 'rxjs';

@Component({
    selector: 'daimler-snack-bar',
    templateUrl: './snack-bar.component.html',
})
export class SnackBarComponent {
    private static maxMessageCount: number = 5;

    // interval timer handle, might be used to cancel
    private intervalId: number | undefined;

    private messagesSubject = new BehaviorSubject<string[]>([]);
    messages$ = this.messagesSubject.asObservable();

    // to use this event: PUT (uploadEvent)="raiseUploadEvent($event)" in the host component

    progressBar: boolean | undefined;
    progressValue: number = 0; // 0-100

    public addMessage(message: string): void {
        console.log('appending message: ', message);
        let nextMessages = [...this.messagesSubject.value, message];
        if (nextMessages.length > SnackBarComponent.maxMessageCount) {
            nextMessages = nextMessages.slice(nextMessages.length - SnackBarComponent.maxMessageCount, nextMessages.length);
        }
        this.messagesSubject.next(nextMessages);
    }

    setMessage(message?: string) {
        if (!!message) {
            this.messagesSubject.next([message]);
        } else {
            this.messagesSubject.next([]);
        }
    }

    showProgress(showProgress: boolean) {
        this.progressBar = showProgress;
    }

    // set the progress in msec
    timeout(timeout: number) {
        this.progressBar = true;
        this.progressValue = 0;
        this.intervalId = setInterval(() => {
            this.progressValue += (100 * 1000) / timeout;
            if (this.progressValue > 100) clearInterval(this.intervalId);
        }, 1000);
    }
}
