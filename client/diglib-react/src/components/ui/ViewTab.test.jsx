import React from 'react';
import { render, screen, fireEvent } from '@testing-library/react';
import { act } from 'react';
import ViewTab from './ViewTab';
import { readAllPages, readPage } from '../../services/ContentHandler';
import { createBookFile } from '../../services/FileCreator';

// Mock the imported functions
jest.mock('../../services/ContentHandler', () => ({
  readAllPages: jest.fn(),
  readPage: jest.fn(),
}));
jest.mock('../../services/FileCreator', () => ({
  createBookFile: jest.fn(),
}));
window.scrollTo = jest.fn();

const mockBook = {
  id: 1,
  title: 'Mock Book Title',
  author: 'Mock Author',
  genre: 'Mock Genre',
  year: 2022,
  isbn: '1234567890',
};

describe('ViewTab', () => {
  beforeEach(() => {
    readPage.mockResolvedValue({
      readingTime: 120,
      totalPages: 10,
      content: 'Page content\nLine 2\nLine 3',
    });
    readAllPages.mockResolvedValue(['Page 1 content', 'Page 2 content']);
  });

  test('renders book details', async () => {
    await act(async () => {
      render(
        <ViewTab book={mockBook} onDelete={jest.fn()} errorDeleting={false} />
      );
    });
    expect(screen.getByText(`Title: ${mockBook.title}`)).toBeInTheDocument();
    expect(screen.getByText(`Author: ${mockBook.author}`)).toBeInTheDocument();
    expect(screen.getByText(`Genre: ${mockBook.genre}`)).toBeInTheDocument();
    expect(
      screen.getByText(`Published Year: ${mockBook.year}`)
    ).toBeInTheDocument();
    expect(screen.getByText(`ISBN: ${mockBook.isbn}`)).toBeInTheDocument();
  });

  test('fetches and displays initial page content', async () => {
    await act(async () => {
      render(
        <ViewTab book={mockBook} onDelete={jest.fn()} errorDeleting={false} />
      );
    });

    expect(readPage).toHaveBeenCalledWith(mockBook.id, 0, 1000);
    expect(screen.getByText('Page 1 of 10')).toBeInTheDocument();
    expect(screen.getByText('Reading time: 120')).toBeInTheDocument();
  });

  test('handles page navigation', async () => {
    await act(async () => {
      render(
        <ViewTab book={mockBook} onDelete={jest.fn()} errorDeleting={false} />
      );
    });

    await screen.findByText(/Page 1 of 10/i);

    await act(async () => {
      fireEvent.click(screen.getByText(/Next Page/i));
    });
    await screen.findByText(/Page 2 of 10/i);

    await act(async () => {
      fireEvent.click(screen.getByText(/Previous Page/i));
    });
    await screen.findByText(/Page 1 of 10/i);

    //test out of range
    await act(async () => {
      fireEvent.click(screen.getByText(/Previous Page/i));
    });
    await screen.findByText(/Page 1 of 10/i);

    for (let i = 0; i < 20; i++) {
      await act(async () => {
        fireEvent.click(screen.getByText(/Next Page/i));
      });
    }
    await screen.findByText(/Page 10 of 10/i);
  });

  test('change page with input', async () => {
    await act(async () => {
      render(
        <ViewTab book={mockBook} onDelete={jest.fn()} errorDeleting={false} />
      );
    });
    // Set up initial totalPages
    await screen.findByText('Page 1 of 10');

    // Change input value and trigger 'Enter' keydown event
    const inputField = screen.getByPlaceholderText('Enter page number');
    fireEvent.change(inputField, { target: { value: '4' } });
    await act(async () => {
      fireEvent.keyDown(inputField, { key: 'Enter', code: 'Enter' });
    });
    // Check if the page has been updated to page 4
    await screen.findByText('Page 4 of 10');
  });

  test('displays error for invalid page input', async () => {
    await act(async () => {
      render(
        <ViewTab book={mockBook} onDelete={jest.fn()} errorDeleting={false} />
      );
    });
    fireEvent.change(screen.getByPlaceholderText('Enter page number'), {
      target: { value: '100' },
    });
    await act(async () => {
      fireEvent.keyDown(screen.getByPlaceholderText('Enter page number'), {
        key: 'Enter',
        code: 'Enter',
      });
    });
    expect(screen.getByText('Invalid page number')).toBeInTheDocument();
  });

  test('does not change page on non-Enter key press', async () => {
    await act(async () => {
      render(
        <ViewTab book={mockBook} onDelete={jest.fn()} errorDeleting={false} />
      );
    });

    // Confirm the initial state
    await screen.findByText('Page 1 of 10');

    // Get the input field and change its value to a valid number
    const inputField = screen.getByPlaceholderText('Enter page number');
    fireEvent.change(inputField, { target: { value: '4' } });

    // Simulate a non-Enter key press, such as the 'Escape' key
    fireEvent.keyDown(inputField, { key: 'Escape', code: 'Escape' });

    // Assert that the page did not change since Enter was not pressed
    expect(screen.getByText('Page 1 of 10')).toBeInTheDocument(); // Page should still be 1
  });

  test('renders error when failing to fetch page content', async () => {
    readPage.mockResolvedValue(null);
    await act(async () => {
      render(
        <ViewTab book={mockBook} onDelete={jest.fn()} errorDeleting={false} />
      );
    });
    await screen.findByText('Error loading page');
  });

  test('downloads book content', async () => {
    await act(async () => {
      render(
        <ViewTab book={mockBook} onDelete={jest.fn()} errorDeleting={false} />
      );
    });
    await act(async () => {
      fireEvent.click(screen.getByText('Download Book'));
    });

    expect(readAllPages).toHaveBeenCalledWith(mockBook.id, 1000000);
    expect(createBookFile).toHaveBeenCalledWith(mockBook.title, [
      'Page 1 content',
      'Page 2 content',
    ]);
  });

  test('displays error message when download fails', async () => {
    readAllPages.mockResolvedValue([]);
    await act(async () => {
      render(
        <ViewTab book={mockBook} onDelete={jest.fn()} errorDeleting={false} />
      );
    });
    await act(async () => {
      fireEvent.click(screen.getByText('Download Book'));
    });

    expect(screen.getByText('Error downloading book')).toBeInTheDocument();
  });

  test('calls onDelete when delete button is clicked', async () => {
    const onDelete = jest.fn();
    await act(async () => {
      render(
        <ViewTab book={mockBook} onDelete={onDelete} errorDeleting={false} />
      );
    });
    fireEvent.click(screen.getByText('Delete Book'));

    expect(onDelete).toHaveBeenCalledWith(mockBook.id);
  });

  test('displays error when delete fails', async () => {
    await act(async () => {
      render(
        <ViewTab book={mockBook} onDelete={jest.fn()} errorDeleting={true} />
      );
    });

    expect(screen.getByText('Error deleting book')).toBeInTheDocument();
  });
});
