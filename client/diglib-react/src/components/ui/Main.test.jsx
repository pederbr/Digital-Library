import React from 'react';
import { render, screen, fireEvent } from '@testing-library/react';
import Main from './Main';
const mockBooks = [
  { isbn: '123', title: 'Book One', author: 'Author One', year: '2001' },
  { isbn: '456', title: 'Book Two', author: 'Author Two', year: '2002' },
];
const mockValues = {
  selectedTab: 'ListTab',
  books: mockBooks,
  selectedBook: mockBooks[0],
  errorMessage: 'No book selected',
  isDeleting: false,
  loadingBooks: false,
  errorDeleting: null,
  onTabClick: jest.fn(),
  onBookClick: jest.fn(),
  onDelete: jest.fn(),
};

describe('Main Component', () => {
  test('renders without crashing', async () => {
    render(<Main {...mockValues} />);
    expect(screen.getByText('Digital Library')).toBeInTheDocument();
  });

  test('renders ListTab when selectedTab is LIST', () => {
    const handleTabClick = jest.fn();
    render(<Main {...mockValues} />);
    expect(handleTabClick).not.toHaveBeenCalledWith('ListTab');
  });

  test('renders ViewTab when selectedTab is VIEW and a book is selected', async () => {
    const handleTabClick = jest.fn();
    render(<Main {...mockValues} onTabClick={handleTabClick} />);
    fireEvent.click(screen.getByText('View Book'));
    expect(handleTabClick).toHaveBeenCalledWith('ViewTab');
  });

  test('renders error message when selectedTab is VIEW and no book is selected', () => {
    render(<Main {...mockValues} selectedTab="ViewTab" selectedBook={null} />);
    expect(screen.getByText('No book selected')).toBeInTheDocument();
  });

  test('renders UploadTab when selectedTab is UPLOAD', () => {
    const handleTabClick = jest.fn();
    render(<Main {...mockValues} onTabClick={handleTabClick} />);
    fireEvent.click(screen.getByText('Upload Book'));
    expect(handleTabClick).toHaveBeenCalledWith('UploadTab');
  });

  test('calls onTabClick when a tab button is clicked', () => {
    render(<Main {...mockValues} />);
    fireEvent.click(screen.getByText('Books'));
    expect(mockValues.onTabClick).toHaveBeenCalledWith('ListTab');
    fireEvent.click(screen.getByText('View Book'));
    expect(mockValues.onTabClick).toHaveBeenCalledWith('ViewTab');
    fireEvent.click(screen.getByText('Upload Book'));
    expect(mockValues.onTabClick).toHaveBeenCalledWith('UploadTab');
  });

  test('displays deleting message when isDeleting is true', () => {
    render(<Main {...mockValues} isDeleting={true} />);
    expect(screen.getByText('Deleting...')).toBeInTheDocument();
  });
});
