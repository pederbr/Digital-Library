import { createBookFile } from './FileCreator';

describe('createBookFile', () => {
  beforeEach(() => {
    // Mock the createElement and appendChild methods
    document.createElement = jest.fn().mockReturnValue({
      click: jest.fn(),
      setAttribute: jest.fn(),
      href: '',
      download: '',
    });
    document.body.appendChild = jest.fn();
    document.body.removeChild = jest.fn();
    window.URL.createObjectURL = jest.fn().mockReturnValue('blob:url');
  });

  afterEach(() => {
    jest.resetAllMocks();
  });

  it('should create a downloadable file with the correct content and filename', () => {
    const bookTitle = 'TestBook';
    const pages = [
      { content: 'Page 1 content' },
      { content: 'Page 2 content' },
    ];

    createBookFile(bookTitle, pages);

    const link = document.createElement.mock.results[0].value;

    expect(link.download).toBe('TestBook.txt');
    expect(link.href).toBe('blob:url');
    expect(document.body.appendChild).toHaveBeenCalledWith(link);
    expect(link.click).toHaveBeenCalled();
    expect(document.body.removeChild).toHaveBeenCalledWith(link);
  });

  it('should handle empty pages array', () => {
    const bookTitle = 'EmptyBook';
    const pages = [];

    createBookFile(bookTitle, pages);

    const link = document.createElement.mock.results[0].value;

    expect(link.download).toBe('EmptyBook.txt');
    expect(link.href).toBe('blob:url');
    expect(document.body.appendChild).toHaveBeenCalledWith(link);
    expect(link.click).toHaveBeenCalled();
    expect(document.body.removeChild).toHaveBeenCalledWith(link);
  });

  it('should handle errors gracefully', () => {
    const bookTitle = 'ErrorBook';
    const pages = [
      { content: 'Page 1 content' },
      { content: 'Page 2 content' },
    ];

    // Force an error by making createElement throw
    document.createElement = jest.fn(() => {
      throw new Error('Test error');
    });

    console.error = jest.fn();

    createBookFile(bookTitle, pages);

    expect(console.error).toHaveBeenCalledWith(
      'Error creating book file:',
      expect.any(Error)
    );
  });
});
