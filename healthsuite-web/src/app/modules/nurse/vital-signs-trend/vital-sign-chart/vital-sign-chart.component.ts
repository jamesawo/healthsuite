import { Component, Input, OnInit } from '@angular/core';
import { ChartDataSets, ChartType } from 'chart.js';
import { Color, Label } from 'ng2-charts';

@Component({
    selector: 'app-vital-sign-chart',
    templateUrl: './vital-sign-chart.component.html',
    styleUrls: ['./vital-sign-chart.component.css'],
})
export class VitalSignChartComponent implements OnInit {
    constructor() {}

    @Input('dataList')
    public lineChartData: ChartDataSets[];

    @Input('labels')
    lineChartLabels: Label[];

    lineChartOptions = {
        responsive: true,
        maintainAspectRatio: false,
        elements: {
            line: {
                tension: 0,
            },
        },
    };

    lineChartColors: Color[] = [
        {
            borderColor: 'black',
            backgroundColor: 'rgba(255,255,0,0.28)',
        },
    ];
    lineChartLegend = true;
    lineChartPlugins = [];
    lineChartType: ChartType = 'line';

    ngOnInit(): void {
        this.randomColor();
    }

    random(value: number) {
        return Math.floor(Math.random() * value);
    }

    randomColor() {
        this.lineChartColors[0].backgroundColor =
            'rgb(' + this.random(255) + ',' + this.random(255) + ', ' + this.random(255) + ')';
    }
}
