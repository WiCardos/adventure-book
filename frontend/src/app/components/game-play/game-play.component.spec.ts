import { TestBed } from '@angular/core/testing';
import { describe, it, expect, vi } from 'vitest';
import { of, throwError } from 'rxjs';
import { ActivatedRoute, convertToParamMap } from '@angular/router';
import { GamePlayComponent } from './game-play.component';
import { GameService } from '../../services/game.service';
import { GameStartResult, SectionView } from '../../models/section-view.model';

describe('GamePlayComponent', () => {
  const mockStartResult: GameStartResult = {
    sessionId: 'abc-123',
    section: {
      text: 'Start of the adventure',
      options: [{ description: 'Go north', gotoId: 2 }],
      isEnding: false,
      health: 10,
      isDead: false
    },
  };

  function setup(title = 'Test Book') {
    const mockGameService = {
      startGame: vi.fn().mockReturnValue(of(mockStartResult)),
      makeChoice: vi.fn(),
      checkSave: vi.fn().mockReturnValue(throwError(() => new Error('404'))),
      resumeGame: vi.fn(),
      saveGame: vi.fn(),
      deleteSave: vi.fn().mockReturnValue(of(undefined)),
    };
    const mockRoute = {
      snapshot: { paramMap: convertToParamMap({ title }) },
    };

    TestBed.configureTestingModule({
      imports: [GamePlayComponent],
      providers: [
        { provide: GameService, useValue: mockGameService },
        { provide: ActivatedRoute, useValue: mockRoute },
      ],
    });

    const fixture = TestBed.createComponent(GamePlayComponent);
    fixture.detectChanges();

    return { fixture, mockGameService };
  }

  it('starts a game with the title from the route and renders the section', () => {
    const { fixture, mockGameService } = setup('Test Book');

    expect(mockGameService.startGame).toHaveBeenCalledWith('Test Book');

    const compiled = fixture.nativeElement as HTMLElement;
    expect(compiled.textContent).toContain('Start of the adventure');
    expect(compiled.querySelectorAll('.option-button').length).toBe(1);
  });

  it('advances to the next section when an option is chosen', () => {
    const { fixture, mockGameService } = setup();
    const nextSection: SectionView = { text: 'The end', options: [], isEnding: true, health: 10, isDead: false };
    mockGameService.makeChoice.mockReturnValue(of(nextSection));

    const component = fixture.componentInstance;
    component.choose(2);
    fixture.detectChanges();

    expect(mockGameService.makeChoice).toHaveBeenCalledWith('abc-123', 2);

    const compiled = fixture.nativeElement as HTMLElement;
    expect(compiled.textContent).toContain('The end');
    expect(compiled.textContent).toContain('The End.');
  });

  it('shows resume/start-over choice when a save exists', () => {
    const mockGameService = {
      startGame: vi.fn().mockReturnValue(of(mockStartResult)),
      makeChoice: vi.fn(),
      checkSave: vi.fn().mockReturnValue(of({ title: 'Test Book', sectionId: 2, health: 6 })),
      resumeGame: vi.fn(),
      saveGame: vi.fn(),
      deleteSave: vi.fn().mockReturnValue(of(undefined)),
    };
    const mockRoute = { snapshot: { paramMap: convertToParamMap({ title: 'Test Book' }) } };

    TestBed.configureTestingModule({
      imports: [GamePlayComponent],
      providers: [
        { provide: GameService, useValue: mockGameService },
        { provide: ActivatedRoute, useValue: mockRoute },
      ],
    });

    const fixture = TestBed.createComponent(GamePlayComponent);
    fixture.detectChanges();

    expect(mockGameService.startGame).not.toHaveBeenCalled();
    const compiled = fixture.nativeElement as HTMLElement;
    expect(compiled.textContent).toContain('Resume');
  });

  it('starts a fresh game directly when no save exists', () => {
    const { mockGameService } = setup();

    expect(mockGameService.startGame).toHaveBeenCalledWith('Test Book');
  });

  it('hides the save button on the very first section', () => {
    const { fixture } = setup();

    const compiled = fixture.nativeElement as HTMLElement;
    expect(compiled.querySelector('.save-button')).toBeNull();
  });
});
