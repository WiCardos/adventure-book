import { TestBed } from '@angular/core/testing';
import { describe, it, expect, vi } from 'vitest';
import { of } from 'rxjs';
import { BookListComponent } from './book-list.component';
import { BookService } from '../../services/book.service';
import { BookSummary } from '../../models/book-summary.model';

describe('BookListComponent', () => {
  it('renders one card per book returned by BookService', async () => {
    const mockBooks: BookSummary[] = [
      { title: 'The Whispering Lighthouse', author: 'Corwin Ashgrove', difficulty: 'EASY', chapterCount: 8 },
      { title: 'The Clockwork Heist', author: 'Delphine Argent', difficulty: 'MEDIUM', chapterCount: 8 },
    ];

    const mockBookService = { getBooks: vi.fn().mockReturnValue(of(mockBooks)) };

    await TestBed.configureTestingModule({
      imports: [BookListComponent],
      providers: [{ provide: BookService, useValue: mockBookService }],
    }).compileComponents();

    const fixture = TestBed.createComponent(BookListComponent);
    fixture.detectChanges();

    const cards = fixture.nativeElement.querySelectorAll('.book-card');
    expect(cards.length).toBe(2);
    expect(cards[0].textContent).toContain('The Whispering Lighthouse');
  });
});
