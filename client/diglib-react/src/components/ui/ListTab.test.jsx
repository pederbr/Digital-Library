import React from 'react';
import { render, screen, fireEvent } from '@testing-library/react';
import { act } from 'react';
import ListTab from './ListTab';

const mockBooks = [
  { isbn: '123', title: 'Book One', author: 'Author One', year: '2001' },
  { isbn: '456', title: 'Book Two', author: 'Author Two', year: '2002' },
];

describe('ListTab Component', () => {
  test("renders 'No books in database' when books is null", async () => {
    await act(async () => {
      render(
        <ListTab books={null} onBookClick={jest.fn()} loadingBooks={false} />
      );
    });
    expect(screen.getByText('Error fetching books')).toBeInTheDocument();
  });

  test('renders a list of books', async () => {
    await act(async () => {
      render(<ListTab books={mockBooks} onBookClick={jest.fn()} />);
    });
    expect(screen.getByText('Book One')).toBeInTheDocument();
    expect(screen.getByText('Book Two')).toBeInTheDocument();
  });

  test('calls onBookClick when a book is clicked', async () => {
    const handleBookClick = jest.fn();
    await act(async () => {
      render(
        <ListTab
          books={mockBooks}
          onBookClick={handleBookClick}
          loadingBooks={false}
        />
      );
    });
    await act(async () => {
      fireEvent.click(screen.getByText('Book One'));
    });
    expect(handleBookClick).toHaveBeenCalledWith(mockBooks[0]);
  });

  test('renders book details correctly', async () => {
    await act(async () => {
      render(
        <ListTab
          books={mockBooks}
          onBookClick={jest.fn()}
          loadingBooks={false}
        />
      );
    });
    expect(screen.getByText('Book One')).toBeInTheDocument();
    expect(screen.getByText('by Author One')).toBeInTheDocument();
    expect(screen.getByText('2001')).toBeInTheDocument();
  });

  test('renders loading message when loadingBooks is true', async () => {
    await act(async () => {
      render(
        <ListTab books={null} onBookClick={jest.fn()} loadingBooks={true} />
      );
    });
    expect(screen.getByText('Loading books...')).toBeInTheDocument();
  });
});
