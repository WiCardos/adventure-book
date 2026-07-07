
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { GameStartResult, SectionView } from '../models/section-view.model';

@Injectable({ providedIn: 'root' })
export class GameService {
  private readonly baseUrl = 'http://localhost:8080/games';

  constructor(private http: HttpClient) {}

  startGame(title: string): Observable<GameStartResult> {
    return this.http.post<GameStartResult>(this.baseUrl, { title });
  }

  makeChoice(sessionId: string, gotoId: number): Observable<SectionView> {
    return this.http.post<SectionView>(`${this.baseUrl}/${sessionId}/choices`, { gotoId });
  }
}
