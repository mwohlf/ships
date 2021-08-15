import {Component, OnInit} from '@angular/core';
import {AggregationControllerService} from "../../../generated";
import {Observable} from "rxjs";
import {map, tap} from 'rxjs/operators';

@Component({
    selector: 'app-view',
    templateUrl: './view.component.html',
    styleUrls: ['./view.component.scss']
})
export class ViewComponent implements OnInit {

    public gridData$: Observable<[][]> = new Observable<[][]>();

    constructor(
        private aggregationControllerService: AggregationControllerService
    ) {
    }

    ngOnInit(): void {
        console.log("on init called");
    }

    aggregationSpeed() {
        this.gridData$ = this.aggregationControllerService.aggregationSpeed().pipe(
            map(next => {
                return next as any
            }),
            tap(next => {
                console.log("returned", next)
            })
        )
    }

    aggregationTimeStamps() {
        this.gridData$ = this.aggregationControllerService.aggregationTimeStamps().pipe(
            map(next => {
                return next as any
            }),
            tap(next => {
                console.log("returned", next)
            })
        )
    }

    aggregationMiles() {
        this.gridData$ = this.aggregationControllerService.aggregationMiles().pipe(
            map(next => {
                return next as any
            }),
            tap(next => {
                console.log("returned", next)
            })
        )
    }
    aggregationEngines() {
        this.gridData$ = this.aggregationControllerService.aggregationEngines().pipe(
            map(next => {
                return next as any
            }),
            tap(next => {
                console.log("returned", next)
            })
        )
    }
}
