import { getBook, getBooks, postBook, deleteBook } from './BookHandler';
import { api } from '../config/api';

jest.mock('../config/api');

describe('BookHandler API calls', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  test('getBooks returns an array of books', async () => {
    const mockBooks = [
      {
        id: '123',
        title: 'Book One',
        author: 'Author One',
        genre: 'Genre One',
        year: '2001',
        isbn: '123456',
      },
      {
        id: '456',
        title: 'Book Two',
        author: 'Author Two',
        genre: 'Genre Two',
        year: '2002',
        isbn: '456789',
      },
    ];
    api.get.mockResolvedValue({ data: { content: mockBooks } });

    const books = await getBooks();
    expect(books).toEqual(mockBooks);
    expect(api.get).toHaveBeenCalledWith('/books');
  });

  test('getBooks returns null if an error occurs', async () => {
    api.get.mockRejectedValue(new Error('API is down'));

    const books = await getBooks();
    expect(books).toBeNull();
    expect(api.get).toHaveBeenCalledWith('/books');
  });

  test('getBook returns a specific book by ID', async () => {
    const mockBook = {
      id: '123',
      title: 'Book One',
      author: 'Author One',
      genre: 'Genre One',
      year: '2001',
      isbn: '123456',
    };
    api.get.mockResolvedValue({ data: mockBook });

    const book = await getBook('123');
    expect(book).toEqual(mockBook);
    expect(api.get).toHaveBeenCalledWith('/books/123');
  });

  test('getBook returns null if an error occurs', async () => {
    api.get.mockRejectedValue(new Error('API is down'));

    const book = await getBook('123');
    expect(book).toBeNull();
    expect(api.get).toHaveBeenCalledWith('/books/123');
  });

  test('postBook uploads a new book and returns the created book object', async () => {
    const mockBook = {
      id: '789',
      title: 'Book Three',
      author: 'Author Three',
      genre: 'Genre Three',
      year: '2003',
      isbn: '789012',
    };
    api.post.mockResolvedValue({ data: mockBook });

    const newBook = await postBook(
      'Book Three',
      'Author Three',
      '2003',
      'Genre Three',
      '789012',
      new File([], 'book.pdf')
    );
    expect(newBook).toEqual(mockBook);
    expect(api.post).toHaveBeenCalledWith('/books', expect.any(FormData), {
      headers: {
        'Content-Type': 'multipart/form-data',
      },
    });
  });

  test('postBook returns null if an error occurs', async () => {
    api.post.mockRejectedValue(new Error('API is down'));

    const newBook = await postBook(
      'Book Three',
      'Author Three',
      '2003',
      'Genre Three',
      '789012',
      new File([], 'book.pdf')
    );
    expect(newBook).toBeNull();
    expect(api.post).toHaveBeenCalledWith('/books', expect.any(FormData), {
      headers: {
        'Content-Type': 'multipart/form-data',
      },
    });
  });

  test('deleteBook deletes a book by ID', async () => {
    api.delete.mockResolvedValue({});

    const success = await deleteBook('123');
    expect(success).toEqual(true);
    expect(api.delete).toHaveBeenCalledWith('/books/123');
  });

  test('deleteBook returns false if an error occurs', async () => {
    api.delete.mockRejectedValue(new Error('API is down'));

    const success = await deleteBook('123');
    expect(success).toEqual(false);
    expect(api.delete).toHaveBeenCalledWith('/books/123');
  });
});
