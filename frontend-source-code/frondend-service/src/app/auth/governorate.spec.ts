import { TestBed } from '@angular/core/testing';

import { Governorate } from './governorate';

describe('Governorate', () => {
  let service: Governorate;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(Governorate);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
