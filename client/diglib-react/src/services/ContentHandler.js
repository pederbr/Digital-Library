import { api } from '../config/api';
/**
 * Reads a specific page of content from a book.
 *
 * @param {number} bookID - The ID of the book.
 * @param {number} page - The page number to read.
 * @param {number} pageSize - The size of the page.
 * @returns {Promise<Object>} A promise that resolves to the page content.
 */
export const readPage = async (bookID, page, pageSize) => {
  try {
    const response = await api.get(`/books/${bookID}/content`, {
      params: { page, pageSize },
    });
    console.log('Page fetched:', response.data);
    return {
      content: response.data.content,
      totalPages: response.data.totalPages,
      readingTime: response.data.readingTime,
    };
  } catch (error) {
    console.error('Error fetching page:', error);
    return null;
  }
};

/**
 * Reads all pages of content from a book.
 *
 * @param {number} bookID - The ID of the book.
 * @param {number} pageSize - The size of each page.
 * @returns {Promise<Object[]>} A promise that resolves to all pages of content.
 */
export const readAllPages = async (bookID, pageSize) => {
  const firstPage = await readPage(bookID, 0, pageSize);
  if (!firstPage) {
    console.error('Error fetching initial page.');
    return [];
  }

  const totalPages = firstPage.totalPages;
  const pages = [firstPage];

  for (let i = 1; i < totalPages; i++) {
    const page = await readPage(bookID, i, pageSize);
    if (page) pages.push(page);
    else {
      console.error('Error fetching page', i);
      return [];
    }
  }
  return pages;
};
