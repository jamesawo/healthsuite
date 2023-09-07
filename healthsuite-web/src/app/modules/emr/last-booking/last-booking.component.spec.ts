import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { LastBookingComponent } from './last-booking.component';

describe('LastBookingComponent', () => {
  let component: LastBookingComponent;
  let fixture: ComponentFixture<LastBookingComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ LastBookingComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(LastBookingComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
