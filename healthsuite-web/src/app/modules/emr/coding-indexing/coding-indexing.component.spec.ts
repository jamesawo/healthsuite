import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { CodingIndexingComponent } from './coding-indexing.component';

describe('CodingIndexingComponent', () => {
  let component: CodingIndexingComponent;
  let fixture: ComponentFixture<CodingIndexingComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ CodingIndexingComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(CodingIndexingComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
