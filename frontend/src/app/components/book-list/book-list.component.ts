import { Component, OnInit, signal } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { Subject } from 'rxjs';
import { debounceTime, distinctUntilChanged, switchMap } from 'rxjs/operators';
import { BookService } from '../../services/book.service';
import { BookSummary } from '../../models/book-summary.model';
import { Router } from '@angular/router';

@Component({
  selector: 'app-book-list',
  standalone: true,
  templateUrl: './book-list.component.html',
  styleUrl: './book-list.component.css',
})
export class BookListComponent implements OnInit {
  searchTerm = signal('');
  selectedDifficulty = signal<string | null>(null);

  private filters$ = new Subject<{ search: string; difficulty: string | null }>();

  books = toSignal(
    this.filters$.pipe(
      debounceTime(300),
      distinctUntilChanged((a, b) => a.search === b.search && a.difficulty === b.difficulty),
      switchMap(({ search, difficulty }) =>
        this.bookService.getBooks(search || undefined, difficulty || undefined)
      )
    ),
    { initialValue: [] }
  );

  constructor(private bookService: BookService, private router: Router) {}

  ngOnInit(): void {
    this.emitCurrentFilters();
  }

  onSearchChange(value: string): void {
    this.searchTerm.set(value);
    this.emitCurrentFilters();
  }

  onDifficultyChange(difficulty: string | null): void {
    this.selectedDifficulty.set(difficulty);
    this.emitCurrentFilters();
  }

  private emitCurrentFilters(): void {
    this.filters$.next({ search: this.searchTerm(), difficulty: this.selectedDifficulty() });
  }

  beginQuest(title: string): void {
    this.router.navigate(['/play', title]);
  }
}
