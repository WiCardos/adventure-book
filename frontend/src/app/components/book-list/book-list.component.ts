import { Component, OnInit, signal } from '@angular/core';
import { BookService } from '../../services/book.service';
import { BookSummary } from '../../models/book-summary.model';

@Component({
  selector: 'app-book-list',
  standalone: true,
  templateUrl: './book-list.component.html',
  styleUrl: './book-list.component.css',
})
export class BookListComponent implements OnInit {
  books = signal<BookSummary[]>([]);

  constructor(private bookService: BookService) {}

  ngOnInit(): void {
    this.bookService.getBooks().subscribe((books) => this.books.set(books));
  }
}
