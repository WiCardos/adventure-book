import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting, HttpTestingController } from '@angular/common/http/testing';
import { describe, it, expect, afterEach } from 'vitest';
import { GameService } from './game.service';
import { GameStartResult, SectionView } from '../models/section-view.model';
import { SavedGame } from '../models/saved-game.model';
import { environment } from '../../environments/environment';

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
      section: { text: 'Start', options: [{ description: 'Go', gotoId: 2 }], isEnding: false, health: 10, isDead: false },
    };

    service.startGame('Test Book').subscribe((result) => {
      expect(result).toEqual(mockResult);
    });

    const req = httpMock.expectOne(`${environment.apiUrl}/games`);
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual({ title: 'Test Book' });
    req.flush(mockResult);
  });

  it('makes a choice and returns the new section', () => {
    const mockSection: SectionView = { text: 'End', options: [], isEnding: true, health: 10, isDead: false };

    service.makeChoice('abc-123', 2).subscribe((section) => {
      expect(section).toEqual(mockSection);
    });

    const req = httpMock.expectOne(`${environment.apiUrl}/games/abc-123/choices`);
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual({ gotoId: 2 });
    req.flush(mockSection);
  });

  it('checks for an existing save', () => {
    const mockSave: SavedGame = { title: 'Test Book', sectionId: 2, health: 6 };

    service.checkSave('Test Book').subscribe((save) => {
      expect(save).toEqual(mockSave);
    });

    const req = httpMock.expectOne(`${environment.apiUrl}/saves/Test%20Book`);
    expect(req.request.method).toBe('GET');
    req.flush(mockSave);
  });

  it('resumes a game with the given title', () => {
    const mockResult: GameStartResult = {
      sessionId: 'abc-123',
      section: { text: 'Middle', options: [], isEnding: false, health: 6, isDead: false },
    };

    service.resumeGame('Test Book').subscribe((result) => {
      expect(result).toEqual(mockResult);
    });

    const req = httpMock.expectOne(`${environment.apiUrl}/games/resume`);
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual({ title: 'Test Book' });
    req.flush(mockResult);
  });

  it('saves the current game', () => {
    service.saveGame('abc-123').subscribe();

    const req = httpMock.expectOne(`${environment.apiUrl}/games/abc-123/save`);
    expect(req.request.method).toBe('POST');
    req.flush(null);
  });

  it('deletes an existing save', () => {
    service.deleteSave('Test Book').subscribe();

    const req = httpMock.expectOne(`${environment.apiUrl}/saves/Test%20Book`);
    expect(req.request.method).toBe('DELETE');
    req.flush(null);
  });
});
