import { api } from '../config/api';

export const getGenres = async () => {
  try {
    const genres = await api.get('/genres');
    console.log('Genres fetched:', genres.data);
    return genres.data;
  } catch (error) {
    console.error('Error fetching genres:', error);
    return null;
  }
};
