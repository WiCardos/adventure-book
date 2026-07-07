import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting, HttpTestingController } from '@angular/common/http/testing';
import { describe, it, expect, afterEach } from 'vitest';
import { GameService } from './game.service';
import { GameStartResult, SectionView } from '../models/section-view.model';

describe('GameService', () => {
  let service: GameService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [GameService, provideHttpClient(), provideHttpClientTesting()],
    });
    service = TestBed.inject(GameService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('starts a game with the given title', () => {
    const mockResult: GameStartResult = {
      sessionId: 'abc-123',
      section: { text: 'Start', options: [{ description: 'Go', gotoId: 2 }], isEnding: false },
    };

    service.startGame('Test Book').subscribe((result) => {
      expect(result).toEqual(mockResult);
    });

    const req = httpMock.expectOne('http://localhost:8080/games');
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual({ title: 'Test Book' });
    req.flush(mockResult);
  });

  it('makes a choice and returns the new section', () => {
    const mockSection: SectionView = { text: 'End', options: [], isEnding: true };

    service.makeChoice('abc-123', 2).subscribe((section) => {
      expect(section).toEqual(mockSection);
    });

    const req = httpMock.expectOne('http://localhost:8080/games/abc-123/choices');
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual({ gotoId: 2 });
    req.flush(mockSection);
  });
});
