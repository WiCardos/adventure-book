import { Component, OnInit, signal } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { GameService } from '../../services/game.service';
import { SectionView } from '../../models/section-view.model';
import { SavedGame } from '../../models/saved-game.model';

@Component({
  selector: 'app-game-play',
  standalone: true,
  templateUrl: './game-play.component.html',
  styleUrl: './game-play.component.css',
})
export class GamePlayComponent implements OnInit {
  section = signal<SectionView | null>(null);
  savedGame = signal<SavedGame | null>(null);
  checkingForSave = signal(true);
  justSaved = signal(false);
  isAtBeginning = signal(false);
  private sessionId = '';
  private title = '';

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private gameService: GameService
  ) {}

  ngOnInit(): void {
    const title = this.route.snapshot.paramMap.get('title');
    if (!title) {
      this.router.navigate(['/']);
      return;
    }
    this.title = title;

    this.gameService.checkSave(title).subscribe({
      next: (save) => {
        this.savedGame.set(save);
        this.checkingForSave.set(false);
      },
      error: () => {
        // no save exists (404) — go straight into a fresh game
        this.checkingForSave.set(false);
        this.startFresh();
      },
    });
  }

  resumeSavedGame(): void {
    this.gameService.resumeGame(this.title).subscribe((result) => {
      this.sessionId = result.sessionId;
      this.section.set(result.section);
      this.savedGame.set(null);
      this.isAtBeginning.set(false);
    });
  }

  startFresh(): void {
    this.gameService.startGame(this.title).subscribe((result) => {
      this.sessionId = result.sessionId;
      this.section.set(result.section);
      this.savedGame.set(null);
      this.isAtBeginning.set(true);
    });
  }
  choose(gotoId: number): void {
    this.gameService.makeChoice(this.sessionId, gotoId).subscribe((section) => {
      this.section.set(section);
      this.isAtBeginning.set(false);
    });
  }

  saveProgress(): void {
    this.gameService.saveGame(this.sessionId).subscribe(() => {
      this.justSaved.set(true);
      setTimeout(() => this.justSaved.set(false), 1500);
    });
  }

  backToLibrary(): void {
    this.router.navigate(['/']);
  }

  startOver(): void {
    this.gameService.deleteSave(this.title).subscribe(() => {
      this.savedGame.set(null);
      this.startFresh();
    });
  }
}
