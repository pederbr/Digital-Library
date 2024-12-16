import { api } from '../config/api';

/**
 * Fetches a list of books from the API.
 *
 * This asynchronous function sends a GET request to the `/books` endpoint
 * and retrieves the list of books. If successful, it returns the content
 * data from the response; if an error occurs, it logs the error and returns null.
 *
 * @async
 * @function getBooks
 * @returns {Promise<Array<Object>|null>} An array of book objects if successful, or null if an error occurs.
 */
export const getBooks = async () => {
  try {
    const response = await api.get('/books');
    console.log('Books fetched:', response.data.content);
    return response.data.content;
  } catch (error) {
    console.log('Error fetching books:', error);
    return null;
  }
};

/**
 * Fetches a specific book by ID from the API.
 *
 * This asynchronous function sends a GET request to the `/books/{id}` endpoint,
 * where `{id}` is the ID of the book to retrieve. If successful, it returns
 * the book data; if an error occurs, it logs the error and returns null.
 *
 * @async
 * @function getBook
 * @param {string} id - The ID of the book to fetch.
 * @returns {Promise<Object|null>} The book object if found, or null if an error occurs.
 */
export const getBook = async id => {
  try {
    const response = await api.get(`/books/${id}`);
    console.log('Book fetched:', response.data);
    return response.data;
  } catch (error) {
    console.log('Error fetching book:', error);
    return null;
  }
};

/**
 * Uploads a new book to the API.
 *
 * This asynchronous function sends a POST request to the `/books` endpoint, including
 * the book details and content file. It uses FormData to handle multipart form data,
 * allowing file upload alongside other book details. If successful, it returns the
 * response data; if an error occurs, it logs the error and returns null.
 *
 * @async
 * @function postBook
 * @param {string} title - The title of the book.
 * @param {string} author - The author of the book.
 * @param {string} year - The publication year of the book.
 * @param {string} genre - The genre of the book.
 * @param {string} isbn - The ISBN of the book.
 * @param {File} content - The file content of the book to be uploaded.
 * @returns {Promise<Object|null>} The created book object if successful, or null if an error occurs.
 */
export const postBook = async (title, author, year, genre, isbn, content) => {
  try {
    // Create FormData to handle file and other text data
    const formData = new FormData();
    formData.append('title', title);
    formData.append('author', author);
    formData.append('year', year);
    formData.append('genre', genre);
    formData.append('isbn', isbn);
    formData.append('content', content); // Append file here

    const response = await api.post('/books', formData, {
      headers: {
        'Content-Type': 'multipart/form-data',
      },
    });
    console.log('Book added:', response.data);
    return response.data;
  } catch (error) {
    console.error('Error adding book:', error);
    return null;
  }
};

/**
 * Deletes a book by ID from the API.
 *
 * This asynchronous function sends a DELETE request to the `/books/{id}` endpoint,
 * where `{id}` is the ID of the book to delete. If the deletion is successful,
 * it logs a success message to the console and returns `true`. If an error occurs
 * during the deletion request, it logs the error and returns `false`.
 *
 * @async
 * @function deleteBook
 * @param {string} id - The ID of the book to delete.
 * @returns {Promise<boolean>} A promise that resolves to `true` if the book is successfully deleted, or `false` if the deletion fails.
 */
export const deleteBook = async id => {
  try {
    await api.delete(`/books/${id}`);
    console.log(`Book with id ${id} deleted successfully`);
    return true;
  } catch (error) {
    console.log('Error deleting book:', error);
    return false;
  }
};
