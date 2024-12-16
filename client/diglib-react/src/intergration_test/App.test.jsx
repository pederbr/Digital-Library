import React from 'react';
import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import App from '../App';
import * as BookHandler from '../services/BookHandler';
import { getGenres } from '../services/GenreHandler';

// Mock the BookHandler API calls
jest.mock('../services/BookHandler', () => ({
  getBooks: jest.fn(),
  deleteBook: jest.fn(),
  postBook: jest.fn(),
}));

jest.mock('../services/ContentHandler', () => ({
  readAllPages: jest.fn(),
  readPage: jest.fn(),
}));

jest.mock('../services/GenreHandler', () => ({
  getGenres: jest.fn(),
}));

describe('App Integration Tests', () => {
  beforeEach(() => {
    // Reset mocks before each test
    BookHandler.getBooks.mockReset();
    BookHandler.deleteBook.mockReset();
  });

  test('loads and displays book list', async () => {
    const books = [
      { id: 1, title: 'Book One', author: 'Author One', year: '2020' },
      { id: 2, title: 'Book Two', author: 'Author Two', year: '2021' },
    ];

    BookHandler.getBooks.mockResolvedValueOnce(books);

    render(<App />);

    // Check if loading message appears first
    expect(screen.getByText(/loading books/i)).toBeInTheDocument();

    // Wait for books to load and check their presence
    await waitFor(() =>
      expect(screen.getByText('Book One')).toBeInTheDocument()
    );
    expect(screen.getByText('Book Two')).toBeInTheDocument();
  });

  test('switches to view tab and displays error if no book is selected', () => {
    render(<App />);

    // Click on the "View Book" tab
    fireEvent.click(screen.getByText(/view book/i));

    // Expect an error message for unselected book
    expect(screen.getByText(/no books selected/i)).toBeInTheDocument();
  });

  test('selects a book and displays its details on view tab', async () => {
    const books = [
      {
        id: 1,
        title: 'Book One',
        author: 'Author One',
        year: '2020',
        isbn: '123',
      },
      {
        id: 2,
        title: 'Book Two',
        author: 'Author Two',
        year: '2021',
        isbn: '456',
      },
    ];
    BookHandler.getBooks.mockResolvedValueOnce(books);

    render(<App />);

    await waitFor(() =>
      expect(screen.getByText('Book One')).toBeInTheDocument()
    );

    // Select the book by clicking on it
    fireEvent.click(screen.getByText('Book One'));

    // Check if the selected book details are displayed
    expect(screen.getByText(/book one/i)).toBeInTheDocument();
    expect(screen.getByText(/author one/i)).toBeInTheDocument();
    expect(screen.getByText(/2020/i)).toBeInTheDocument();
    expect(screen.getByText(/isbn: 123/i)).toBeInTheDocument();
    expect(screen.queryByText('Book Two')).not.toBeInTheDocument();

    //test that book content is still displayed when switching tabs
    fireEvent.click(screen.getByText(/books/i));
    fireEvent.click(screen.getByText(/view book/i));
    expect(screen.getByText(/book one/i)).toBeInTheDocument();
    expect(screen.getByText(/author one/i)).toBeInTheDocument();
    expect(screen.getByText(/2020/i)).toBeInTheDocument();
    expect(screen.getByText(/isbn: 123/i)).toBeInTheDocument();
    expect(screen.queryByText('Book Two')).not.toBeInTheDocument();
  });

  test('deletes a book and updates the list', async () => {
    const books = [
      { id: 1, title: 'Book One', author: 'Author One', year: '2020' },
      { id: 2, title: 'Book Two', author: 'Author Two', year: '2021' },
    ];
    BookHandler.getBooks.mockResolvedValueOnce(books);
    BookHandler.deleteBook.mockResolvedValueOnce(true);

    render(<App />);

    await waitFor(() =>
      expect(screen.getByText('Book One')).toBeInTheDocument()
    );

    // Select a book and delete it
    fireEvent.click(screen.getByText('Book One'));
    fireEvent.click(screen.getByText(/delete book/i));

    await waitFor(() => {
      // Book should no longer be in the list
      expect(screen.queryByText('Book One')).not.toBeInTheDocument();
      expect(screen.getByText('Book Two')).toBeInTheDocument();
    });
  });

  test('displays error message if book deletion fails and book is still in database', async () => {
    const books = [
      { id: 1, title: 'Book One', author: 'Author One', year: '2020' },
    ];
    BookHandler.getBooks.mockResolvedValueOnce(books);
    BookHandler.deleteBook.mockResolvedValueOnce(false);

    render(<App />);

    await waitFor(() =>
      expect(screen.getByText('Book One')).toBeInTheDocument()
    );

    fireEvent.click(screen.getByText('Book One'));
    fireEvent.click(screen.getByText(/delete/i));

    await waitFor(() => {
      // Expect error message on failed deletion
      expect(screen.getByText(/error deleting book/i)).toBeInTheDocument();
    });

    fireEvent.click(screen.getByText(/books/i));
    expect(screen.getByText('Book One')).toBeInTheDocument();
  });

  test('navigate to upload tab and upload book', async () => {
    getGenres.mockResolvedValue(['Genre One', 'Genre Two']);
    BookHandler.postBook.mockResolvedValue(true);
    render(<App />);

    window.alert = jest.fn();

    fireEvent.click(screen.getByText('Upload Book'));
    await waitFor(() =>
      expect(screen.getByText('Genre One')).toBeInTheDocument()
    );
    fireEvent.change(screen.getByLabelText('Title:'), {
      target: { value: 'Book One' },
    });
    fireEvent.change(screen.getByLabelText('Author:'), {
      target: { value: 'Author One' },
    });
    fireEvent.change(screen.getByLabelText('Genre:'), {
      target: { value: 'Genre One' },
    });
    fireEvent.change(screen.getByLabelText('Year:'), {
      target: { value: '2001' },
    });
    fireEvent.change(screen.getByLabelText('ISBN (13 digits):'), {
      target: { value: '1234567890123' },
    });

    //mock a file
    const file = new Blob(['Mocked file content'], { type: 'text/plain' });
    Object.defineProperty(file, 'name', { value: 'mockedFile.txt' });

    fireEvent.change(screen.getByLabelText('Content:'), {
      target: { files: [file] },
    });

    expect(screen.getByLabelText('Content:').files[0]).toBe(file);

    fireEvent.click(screen.getByText('Upload'));

    //need to test this
    await waitFor(() => expect(BookHandler.postBook).toHaveBeenCalledTimes(1));
  });
});
