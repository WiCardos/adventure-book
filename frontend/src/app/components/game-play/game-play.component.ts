import { Component, OnInit, signal } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { GameService } from '../../services/game.service';
import { SectionView } from '../../models/section-view.model';

@Component({
  selector: 'app-game-play',
  standalone: true,
  templateUrl: './game-play.component.html',
  styleUrl: './game-play.component.css',
})
export class GamePlayComponent implements OnInit {
  section = signal<SectionView | null>(null);
  private sessionId = '';

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
    this.gameService.startGame(title).subscribe((result) => {
      this.sessionId = result.sessionId;
      this.section.set(result.section);
    });
  }

  choose(gotoId: number): void {
    this.gameService.makeChoice(this.sessionId, gotoId).subscribe((section) => {
      this.section.set(section);
    });
  }

  backToLibrary(): void {
    this.router.navigate(['/']);
  }
}
