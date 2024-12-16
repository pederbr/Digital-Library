import { readPage, readAllPages } from './ContentHandler';
import { api } from '../config/api';

jest.mock('../config/api');

describe('ContentHandler', () => {
  afterEach(() => {
    jest.clearAllMocks();
  });

  describe('readPage', () => {
    it('should fetch a specific page of content from a book', async () => {
      const mockResponse = {
        data: {
          content: 'Page content',
          totalPages: 10,
          readingTime: 5,
        },
      };
      api.get.mockResolvedValue(mockResponse);

      const bookID = 1;
      const page = 0;
      const pageSize = 10;

      const result = await readPage(bookID, page, pageSize);

      expect(api.get).toHaveBeenCalledWith(`/books/${bookID}/content`, {
        params: { page, pageSize },
      });
      expect(result).toEqual({
        content: 'Page content',
        totalPages: 10,
        readingTime: 5,
      });
    });

    it('should return null if there is an error fetching the page', async () => {
      api.get.mockRejectedValue(new Error('Network error'));

      const bookID = 1;
      const page = 0;
      const pageSize = 10;

      const result = await readPage(bookID, page, pageSize);

      expect(api.get).toHaveBeenCalledWith(`/books/${bookID}/content`, {
        params: { page, pageSize },
      });
      expect(result).toBeNull();
    });
  });

  describe('readAllPages', () => {
    it('should fetch all pages of content from a book', async () => {
      const mockFirstPageResponse = {
        data: {
          content: 'First page content',
          totalPages: 2,
          readingTime: 5,
        },
      };
      const mockSecondPageResponse = {
        data: {
          content: 'Second page content',
          totalPages: 2,
          readingTime: 5,
        },
      };
      api.get
        .mockResolvedValueOnce(mockFirstPageResponse)
        .mockResolvedValueOnce(mockSecondPageResponse);

      const bookID = 1;
      const pageSize = 10;

      const result = await readAllPages(bookID, pageSize);

      expect(api.get).toHaveBeenCalledWith(`/books/${bookID}/content`, {
        params: { page: 0, pageSize },
      });
      expect(api.get).toHaveBeenCalledWith(`/books/${bookID}/content`, {
        params: { page: 1, pageSize },
      });
      expect(result).toEqual([
        {
          content: 'First page content',
          totalPages: 2,
          readingTime: 5,
        },
        {
          content: 'Second page content',
          totalPages: 2,
          readingTime: 5,
        },
      ]);
    });

    it('should return an empty array if there is an error fetching the initial page', async () => {
      api.get.mockRejectedValue(new Error('Network error'));

      const bookID = 1;
      const pageSize = 10;

      const result = await readAllPages(bookID, pageSize);

      expect(api.get).toHaveBeenCalledWith(`/books/${bookID}/content`, {
        params: { page: 0, pageSize },
      });
      expect(result).toEqual([]);
    });
    it('should return an empty array if there is an error fetching a subsequent page', async () => {
      const mockFirstPageResponse = {
        data: {
          content: 'First page content',
          totalPages: 2,
          readingTime: 5,
        },
      };
      api.get
        .mockResolvedValueOnce(mockFirstPageResponse)
        .mockRejectedValueOnce(new Error('Network error'));

      const bookID = 1;
      const pageSize = 10;
      const result = await readAllPages(bookID, pageSize);

      expect(api.get).toHaveBeenCalledWith(`/books/${bookID}/content`, {
        params: { page: 0, pageSize },
      });
      expect(api.get).toHaveBeenCalledWith(`/books/${bookID}/content`, {
        params: { page: 1, pageSize },
      });
      expect(result).toEqual([]);
    });
  });
});
