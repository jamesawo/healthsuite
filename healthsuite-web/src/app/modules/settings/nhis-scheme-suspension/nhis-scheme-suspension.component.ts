import { Component, OnInit } from '@angular/core';
import { CommonService } from '@app/shared/_services/common/common.service';

@Component({
    selector: 'app-nhis-scheme-suspension',
    templateUrl: './nhis-scheme-suspension.component.html',
    styleUrls: ['./nhis-scheme-suspension.component.css'],
})
export class NhisSchemeSuspensionComponent implements OnInit {
    constructor(private commonService: CommonService) {}

    ngOnInit(): void {}
}
