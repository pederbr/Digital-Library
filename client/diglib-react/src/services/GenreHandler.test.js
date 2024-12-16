import { getGenres } from './GenreHandler';
import { api } from '../config/api';

jest.mock('../config/api');

describe('GenreHandler', () => {
  describe('getGenres', () => {
    it('should fetch genres successfully', async () => {
      const mockGenres = [{ name: 'Fiction' }, { name: 'Non-Fiction' }];
      api.get.mockResolvedValue({ data: mockGenres });

      const genres = await getGenres();

      expect(api.get).toHaveBeenCalledWith('/genres');
      expect(genres).toEqual(mockGenres);
    });

    it('should handle errors when fetching genres', async () => {
      const mockError = new Error('Network Error');
      api.get.mockRejectedValue(mockError);

      const genres = await getGenres();

      expect(api.get).toHaveBeenCalledWith('/genres');
      expect(genres).toBeNull();
    });
  });
});
