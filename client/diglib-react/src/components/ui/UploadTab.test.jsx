import React from 'react';
import { fireEvent, render, screen, waitFor } from '@testing-library/react';
import { act } from 'react';
import UploadTab from './UploadTab';
import { getGenres } from '../../services/GenreHandler';
import { postBook } from '../../services/BookHandler';

jest.mock('../../services/GenreHandler', () => ({
  getGenres: jest.fn(),
}));
jest.mock('../../services/BookHandler', () => ({
  postBook: jest.fn(),
}));

describe('UploadTab Component', () => {
  const originalAlert = window.alert;

  beforeEach(() => {
    getGenres.mockResolvedValue(['Genre One', 'Genre Two']);
    postBook.mockResolvedValue(true);

    window.alert = jest.fn();
  });

  afterEach(() => {
    window.alert = originalAlert;
  });

  test('renders upload form', async () => {
    await act(async () => {
      render(<UploadTab />);
    });
    expect(screen.getByText('Title:')).toBeInTheDocument();
    expect(screen.getByText('Author:')).toBeInTheDocument();
    expect(screen.getByText('Genre:')).toBeInTheDocument();
    expect(screen.getByText('Year:')).toBeInTheDocument();
    expect(screen.getByText('ISBN (13 digits):')).toBeInTheDocument();
    expect(screen.getByText('Content:')).toBeInTheDocument();
    expect(screen.getByText('Upload')).toBeInTheDocument();
  });

  test('renders genre list', async () => {
    await act(async () => {
      render(<UploadTab />);
    });
    expect(await screen.findByText('Genre One')).toBeInTheDocument();
    expect(screen.getByText('Genre Two')).toBeInTheDocument();
  });

  test('uploads book', async () => {
    await act(async () => {
      render(<UploadTab />);
    });
    window.alert = jest.fn();
    await screen.findByText('Genre One');
    await act(async () => {
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
    });

    //mock a file
    const file = new Blob(['Mocked file content'], { type: 'text/plain' });
    Object.defineProperty(file, 'name', { value: 'mockedFile.txt' });

    await act(async () => {
      fireEvent.change(screen.getByLabelText('Content:'), {
        target: { files: [file] },
      });
    });
    expect(screen.getByLabelText('Content:').files[0]).toBe(file);

    await act(async () => {
      fireEvent.click(screen.getByText('Upload'));
    });

    //need to test this
    await waitFor(() => expect(postBook).toHaveBeenCalledTimes(1));

    expect(screen.getByLabelText('Title:').value).toBe('');
    expect(screen.getByLabelText('Author:').value).toBe('');
    expect(screen.getByLabelText('Year:').value).toBe('');
    expect(screen.getByLabelText('ISBN (13 digits):').value).toBe('');
  });
  test('renders error message', async () => {
    await act(async () => {
      render(<UploadTab />);
    });
    expect(screen.queryByText('/Invalid genre selected/i')).toBeNull();

    await screen.findByText('Genre One');
    await act(async () => {
      fireEvent.change(screen.getByLabelText('Title:'), {
        target: { value: 'Book One' },
      });
      fireEvent.change(screen.getByLabelText('Author:'), {
        target: { value: 'Author One' },
      });
      fireEvent.change(screen.getByLabelText('Genre:'), {
        target: { value: 'Genre Three' },
      });
      fireEvent.change(screen.getByLabelText('Year:'), {
        target: { value: '2001' },
      });
      fireEvent.change(screen.getByLabelText('ISBN (13 digits):'), {
        target: { value: '1234567890123' },
      });
    });

    //mock a file
    const file = new Blob(['Mocked file content'], { type: 'text/plain' });
    Object.defineProperty(file, 'name', { value: 'mockedFile.txt' });

    await act(async () => {
      fireEvent.change(screen.getByLabelText('Content:'), {
        target: { files: [file] },
      });
    });

    expect(screen.getByLabelText('Content:').files[0]).toBe(file);

    await act(async () => {
      fireEvent.click(screen.getByText('Upload'));
    });

    await expect(postBook).toHaveBeenCalledTimes(0);
  });

  test('error getting genres', async () => {
    getGenres.mockResolvedValue(null);
    render(<UploadTab />);
    expect(screen.queryByText('Genre One')).toBeNull();
  });

  test('invalid input in upload form', async () => {
    render(<UploadTab />);
    await screen.findByText('Genre One');
    fireEvent.change(screen.getByLabelText('Title:'), {
      target: { value: '' },
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
  });

  test('error uploading book', async () => {
    render(<UploadTab />);
    postBook.mockResolvedValue(null);
    await screen.findByText('Genre One');
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
    fireEvent.click(screen.getByText('Upload'));
    await screen.findByText('Error uploading book');
  });
});
