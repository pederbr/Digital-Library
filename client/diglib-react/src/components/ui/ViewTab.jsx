import { readAllPages, readPage } from '../../services/ContentHandler';
import { createBookFile } from '../../services/FileCreator';
import '../../styles/ViewTab.css';
import React, { useEffect, useState } from 'react';

const PAGE_SIZE = 1000;

const ViewTab = ({ book, onDelete, errorDeleting }) => {
  const [currentPage, setCurrentPage] = useState(1);
  const [totalPages, setTotalPages] = useState(0);
  const [validPage, setValidPage] = useState(true);
  const [inputPage, setInputPage] = useState('');
  const [pageContent, setPageContent] = useState('');
  const [readingTime, setReadingTime] = useState(0);
  const [isDownloading, setIsDownloading] = useState(false);
  const [isLoadingPage, setIsLoadingPage] = useState(false);
  const [errorLoadingPage, setErrorLoadingPage] = useState(false);
  const [errorDownloading, setErrorDownloading] = useState(false);

  useEffect(() => {
    /**
     * Fetches the initial content for the book when the component mounts.
     *
     * This asynchronous function retrieves the reading time and total pages
     * for the specified book from the server using the `readPage` function.
     * If the content is successfully fetched, it updates the state with
     * the reading time and total pages. If there's an error, it sets an
     * error message in the page content state.
     *
     * @async
     * @function fetchInitialContent
     * @returns {Promise<void>}
     */
    const fetchInitialContent = async () => {
      const content = await readPage(book.id, 0, PAGE_SIZE);
      if (content) {
        setReadingTime(content.readingTime);
        setTotalPages(content.totalPages);
        return;
      }
    };
    fetchInitialContent();
  }, [book.id]);

  useEffect(() => {
    /**
     * Fetches the content for the current page whenever the page number or book ID changes.
     *
     * This function retrieves the content for the specified page of the book using
     * the `readPage` function. While the page content is being fetched, it sets a loading state.
     * If the content is successfully fetched, it splits the content into paragraphs
     * and updates the `pageContent` state with the rendered paragraphs. If the content
     * cannot be fetched, an error state is set, and `pageContent` is cleared.
     *
     * @async
     * @function fetchPageContent
     * @param {number} pageNumber - The current page number to fetch.
     * @returns {Promise<void>} A promise that resolves when the page content is loaded or an error is encountered.
     */
    const fetchPageContent = async pageNumber => {
      setIsLoadingPage(true);
      const content = await readPage(book.id, pageNumber - 1, PAGE_SIZE);
      if (content) {
        const paragraphs = content.content
          .split('\n')
          .map((line, index) => <p key={index}>{line}</p>);
        setPageContent(paragraphs);
      } else {
        setPageContent('');
        setErrorLoadingPage(true);
        setIsLoadingPage(false);
      }
      setIsLoadingPage(false);
    };
    fetchPageContent(currentPage);
  }, [currentPage, book.id]);

  /**
   * Handles page changes for navigating through the book.
   *
   * This function calculates the new page number based on whether the
   * user is navigating to the next or previous page. It ensures that the
   * new page number is within valid bounds (0 to totalPages) and updates
   * the currentPage state. It also scrolls the window to the top smoothly.
   *
   * @param {boolean} nextPage - Indicates if the user is navigating to
   *                             the next page (true) or the previous page (false).
   */
  const handlePageChange = nextPage => {
    const newPage = nextPage ? currentPage + 1 : currentPage - 1;
    if (newPage >= 1 && newPage <= totalPages) {
      setCurrentPage(newPage);
      window.scrollTo({
        top: 0,
        behavior: 'smooth',
      });
    }
  };

  /**
   * Downloads the entire book content.
   *
   * This asynchronous function retrieves all pages of the book using the `readAllPages` function.
   * If the pages are successfully retrieved, it compiles the content into a book file using the
   * `createBookFile` function. If the retrieval fails (i.e., an empty page array is returned),
   * an error state is set to indicate a failed download.
   *
   * During the download process, the function manages a loading state (`isDownloading`) to
   * provide feedback to the user. Any errors encountered in the `readAllPages` function will
   * result in an error state being set.
   *
   * @async
   * @function downloadBook
   * @returns {Promise<void>} A promise that resolves once the download process completes.
   */
  const downloadBook = async () => {
    setIsDownloading(true);
    const pages = await readAllPages(book.id, PAGE_SIZE * 1000);
    if (pages.length === 0) {
      setErrorDownloading(true);
    } else {
      createBookFile(book.title, pages);
    }
    setIsDownloading(false);
  };

  /**
   * Handles the page change logic when the user presses the "Enter" key in the input field.
   *
   * This function performs the following actions:
   * 1. It listens for the "Enter" key press (`e.key === "Enter"`).
   * 2. If the key pressed is not "Enter", the function will reset any error state and return early.
   * 3. If the "Enter" key is pressed, it parses the input value as an integer and checks whether it is a valid page number.
   *    - A valid page number is one that is a positive integer within the range of 1 to `totalPages`.
   * 4. If the page number is valid, it updates:
   *    - `currentPage`: to the new valid page number.
   *    - `inputPage`: to reflect the valid page number in the input field.
   *    - `validPage`: to `true`, which clears any validation error message.
   * 5. If the page number is invalid (either not a number or outside the valid range), it sets `validPage` to `false` to trigger an error message.
   *
   * @param {Object} e - The event object triggered by the key press.
   * @param {string} e.key - The key pressed by the user (used to check if it's "Enter").
   * @param {string} e.target.value - The current value of the input field, which is parsed to an integer for the page number.
   * @returns {void} - This function does not return anything, but modifies the state based on the input and key press.
   */
  const changePage = e => {
    if (e.key !== 'Enter') {
      setValidPage(true);
      return;
    }

    const pageNumber = parseInt(e.target.value);
    // Check if the page number is valid
    if (!isNaN(pageNumber) && pageNumber >= 1 && pageNumber <= totalPages) {
      setCurrentPage(pageNumber); // Update current page
      setInputPage(pageNumber); // Update input value on Enter key
      setValidPage(true); // Ensure no error message
    } else {
      setValidPage(false); // Show error if page number is invalid
    }
  };

  /**
   * Handles the change in the input field as the user types.
   *
   * This function is triggered on every keystroke in the input field. It immediately updates
   * the input value and resets the error message state.
   *
   * @param {Object} e - The event object triggered by the input change.
   * @param {string} e.target.value - The current value of the input field, which is updated on every change.
   * @returns {void} - This function does not return anything, but modifies the state based on the input value.
   */
  const handleChange = e => {
    const value = e.target.value;
    setInputPage(value); // Update the input value immediately
    setValidPage(true); // Reset error message as the user types
  };

  return (
    <div className="view-tab">
      <div className="book-info">
        <h2>Title: {book.title}</h2>
        <p>Author: {book.author}</p>
        <p>Genre: {book.genre}</p>
        <p>Published Year: {book.year}</p>
        <p>ISBN: {book.isbn}</p>
        <p>
          Page {currentPage} of {totalPages}
        </p>
        <p>Reading time: {readingTime}</p>
        <input
          type="number"
          value={inputPage}
          onChange={handleChange}
          onKeyDown={changePage}
          min={1}
          max={totalPages}
          placeholder="Enter page number"
        />
        {!validPage && <p style={{ color: 'red' }}>Invalid page number</p>}
        <div className="download-button">
          <button onClick={downloadBook}>Download Book</button>
          {isDownloading && <p>Downloading...</p>}
          {errorDownloading && (
            <p style={{ color: 'red' }}>Error downloading book</p>
          )}
        </div>
        <div className="delete-button">
          <button onClick={() => onDelete(book.id)}>Delete Book</button>
          {errorDeleting && <p style={{ color: 'red' }}>Error deleting book</p>}
        </div>
      </div>
      <div className="book-content">
        <p>{isLoadingPage ? 'Loading...' : ''}</p>
        <p style={{ color: 'red' }}>
          {errorLoadingPage ? 'Error loading page' : ''}
        </p>
        <p>{pageContent}</p>
        <div className="page-navigation">
          <button
            className="previousPage-button"
            onClick={() => handlePageChange(false)}
            disabled={currentPage === 0}
          >
            Previous Page
          </button>
          <button
            className="nextPage-button"
            onClick={() => handlePageChange(true)}
            disabled={currentPage === totalPages}
          >
            Next Page
          </button>
        </div>
      </div>
    </div>
  );
};

export default ViewTab;
