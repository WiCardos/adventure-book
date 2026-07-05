import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { BookSummary } from '../models/book-summary.model';

@Injectable({ providedIn: 'root' })
export class BookService {
  private readonly baseUrl = 'http://localhost:8080/books';

  constructor(private http: HttpClient) {}

  getBooks(): Observable<BookSummary[]> {
    return this.http.get<BookSummary[]>(this.baseUrl);
  }
}
