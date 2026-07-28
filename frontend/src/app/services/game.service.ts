
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { GameStartResult, SectionView } from '../models/section-view.model';
import { SavedGame } from '../models/saved-game.model';
import { environment } from '../../environments/environment';

@Injectable({ providedIn: 'root' })
export class GameService {
  private readonly baseUrl = `${environment.apiUrl}`;

  constructor(private http: HttpClient) {}

  startGame(title: string): Observable<GameStartResult> {
    return this.http.post<GameStartResult>(`${this.baseUrl}/games`, { title });
  }

  makeChoice(sessionId: string, gotoId: number): Observable<SectionView> {
    return this.http.post<SectionView>(`${this.baseUrl}/games/${sessionId}/choices`, { gotoId });
  }

  checkSave(title: string): Observable<SavedGame> {
    return this.http.get<SavedGame>(`${this.baseUrl}/saves/${encodeURIComponent(title)}`);
  }

  resumeGame(title: string): Observable<GameStartResult> {
    return this.http.post<GameStartResult>(`${this.baseUrl}/games/resume`, { title });
  }

  saveGame(sessionId: string): Observable<void> {
    return this.http.post<void>(`${this.baseUrl}/games/${sessionId}/save`, {});
  }

  deleteSave(title: string): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/saves/${encodeURIComponent(title)}`);
  }
}
