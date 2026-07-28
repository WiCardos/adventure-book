import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting, HttpTestingController } from '@angular/common/http/testing';
import { describe, it, expect, afterEach } from 'vitest';
import { BookService } from './book.service';
import { BookSummary } from '../models/book-summary.model';
import { environment } from '../../environments/environment';

describe('BookService', () => {
  let service: BookService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [BookService, provideHttpClient(), provideHttpClientTesting()],
    });
    service = TestBed.inject(BookService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('fetches all books from GET /books', () => {
    const mockBooks: BookSummary[] = [
      { title: 'Test Book', author: 'Test Author', difficulty: 'EASY', chapterCount: 2 },
    ];

    service.getBooks().subscribe((books) => {
      expect(books).toEqual(mockBooks);
    });

    const req = httpMock.expectOne(`${environment.apiUrl}/books`);
    expect(req.request.method).toBe('GET');
    req.flush(mockBooks);
  });

  it('fetches books with search and difficulty query params', () => {
    const mockBooks: BookSummary[] = [
      { title: 'Lighthouse Tale', author: 'Corwin', difficulty: 'EASY', chapterCount: 8 },
    ];

    service.getBooks('lighthouse', 'EASY').subscribe((books) => {
      expect(books).toEqual(mockBooks);
    });

    const req = httpMock.expectOne(
      (r) => r.url === `${environment.apiUrl}/books` &&
        r.params.get('search') === 'lighthouse' &&
        r.params.get('difficulty') === 'EASY'
    );
    expect(req.request.method).toBe('GET');
    req.flush(mockBooks);
  });
});
