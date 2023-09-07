import { Component, OnInit } from '@angular/core';
import { CommonService } from '@app/shared/_services/common/common.service';
import { ActivatedRoute } from '@angular/router';

@Component({
    selector: 'app-under-construction',
    templateUrl: './under-construction.component.html',
    styleUrls: ['./under-construction.component.css'],
})
export class UnderConstructionComponent implements OnInit {
    imageUrl = '';

    constructor(
        private commonService: CommonService,
        private activatedRoute: ActivatedRoute
    ) {}

    ngOnInit(): void {
        this.imageUrl = this.commonService.getImage();
    }
}
