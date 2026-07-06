import { TestBed } from '@angular/core/testing';
import { describe, it, expect, vi, beforeEach, afterEach } from 'vitest';
import { of } from 'rxjs';
import { BookListComponent } from './book-list.component';
import { BookService } from '../../services/book.service';
import { BookSummary } from '../../models/book-summary.model';

describe('BookListComponent', () => {
  const mockBooks: BookSummary[] = [
    { title: 'The Whispering Lighthouse', author: 'Corwin Ashgrove', difficulty: 'EASY', chapterCount: 8 },
    { title: 'The Clockwork Heist', author: 'Delphine Argent', difficulty: 'MEDIUM', chapterCount: 8 },
  ];

  let mockBookService: { getBooks: ReturnType<typeof vi.fn> };

  beforeEach(() => {
    vi.useFakeTimers();
    mockBookService = { getBooks: vi.fn().mockReturnValue(of(mockBooks)) };
  });

  afterEach(() => {
    vi.useRealTimers();
  });

  it('renders one card per book on initial load', async () => {
    await TestBed.configureTestingModule({
      imports: [BookListComponent],
      providers: [{ provide: BookService, useValue: mockBookService }],
    }).compileComponents();

    const fixture = TestBed.createComponent(BookListComponent);
    fixture.detectChanges();

    vi.advanceTimersByTime(300);
    fixture.detectChanges();

    const cards = fixture.nativeElement.querySelectorAll('.book-card');
    expect(cards.length).toBe(2);
    expect(cards[0].textContent).toContain('The Whispering Lighthouse');
    expect(mockBookService.getBooks).toHaveBeenCalledWith(undefined, undefined);
  });

  it('debounces search input before calling BookService', async () => {
    await TestBed.configureTestingModule({
      imports: [BookListComponent],
      providers: [{ provide: BookService, useValue: mockBookService }],
    }).compileComponents();

    const fixture = TestBed.createComponent(BookListComponent);
    fixture.detectChanges();
    vi.advanceTimersByTime(300);
    mockBookService.getBooks.mockClear();

    const component = fixture.componentInstance;
    component.onSearchChange('lig');
    vi.advanceTimersByTime(100);
    component.onSearchChange('light');
    vi.advanceTimersByTime(100);
    component.onSearchChange('lighthouse');

    expect(mockBookService.getBooks).not.toHaveBeenCalled();

    vi.advanceTimersByTime(300);

    expect(mockBookService.getBooks).toHaveBeenCalledTimes(1);
    expect(mockBookService.getBooks).toHaveBeenCalledWith('lighthouse', undefined);
  });

  it('calls BookService with the selected difficulty', async () => {
    await TestBed.configureTestingModule({
      imports: [BookListComponent],
      providers: [{ provide: BookService, useValue: mockBookService }],
    }).compileComponents();

    const fixture = TestBed.createComponent(BookListComponent);
    fixture.detectChanges();
    vi.advanceTimersByTime(300);
    mockBookService.getBooks.mockClear();

    const component = fixture.componentInstance;
    component.onDifficultyChange('EASY');
    vi.advanceTimersByTime(300);

    expect(mockBookService.getBooks).toHaveBeenCalledWith(undefined, 'EASY');
  });
});
