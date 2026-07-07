import { Routes } from '@angular/router';
import { BookListComponent } from './components/book-list/book-list.component';
import { GamePlayComponent } from './components/game-play/game-play.component';

export const routes: Routes = [
  { path: '', component: BookListComponent },
  { path: 'play/:title', component: GamePlayComponent },
];
