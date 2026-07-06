import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { BookSummary } from '../models/book-summary.model';
import { HttpClient, HttpParams } from '@angular/common/http';

@Injectable({ providedIn: 'root' })
export class BookService {
  private readonly baseUrl = 'http://localhost:8080/books';

  constructor(private http: HttpClient) {}

  getBooks(search?: string, difficulty?: string): Observable<BookSummary[]> {
    let params = new HttpParams();
    if (search) {
      params = params.set('search', search);
    }
    if (difficulty) {
      params = params.set('difficulty', difficulty);
    }
    return this.http.get<BookSummary[]>(this.baseUrl, { params });
  }
}
