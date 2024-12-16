import React, { useEffect, useState } from 'react';
import Main from './components/ui/Main';
import { getBooks, deleteBook } from './services/BookHandler';

const TABS = {
  LIST: 'ListTab',
  VIEW: 'ViewTab',
  UPLOAD: 'UploadTab',
};

const App = () => {
  const [books, setBooks] = useState([]);
  const [selectedTab, setSelectedTab] = useState(TABS.LIST);
  const [selectedBook, setSelectedBook] = useState(null);
  const [errorMessage, setErrorMessage] = useState('');
  const [isDeleting, setIsDeleting] = useState(false);
  const [loadingBooks, setLoadingBooks] = useState(true);
  const [errorDeleting, setErrorDeleting] = useState(false);

  useEffect(() => {
    /**
     * Fetches the list of books from the server and updates the state with the retrieved content.
     * Sets the loading state to false when the books have been fetched.
     *
     * @async
     * @function fetchBooks
     * @returns {Promise<void>} A promise that resolves when the books have been fetched and the state has been updated.
     */
    const fetchBooks = async () => {
      const content = await getBooks();
      setBooks(content);
      setLoadingBooks(false);
    };

    fetchBooks();
  }, []);

  /**
   * Handles the event when a book is clicked.
   * Sets the selected book and changes the selected tab to "ViewTab".
   *
   * @param {Object} book - The book object that was clicked.
   */
  const handleBookClick = book => {
    setSelectedBook(book);
    setSelectedTab(TABS.VIEW);
  };

  /**
   * Handles the tab click event.
   * Sets the selected tab and updates the error message if the "ViewTab" is selected without a selected book.
   *
   * @param {string} tab - The name of the tab that was clicked.
   * @returns {void}
   */
  const handleTabClick = tab => {
    setSelectedTab(tab);
    if (tab === TABS.VIEW && !selectedBook) {
      setErrorMessage('No books selected');
    } else {
      setErrorMessage('');
    }
  };

  /**
   * Handles the deletion of a book.
   *
   * This function sets the deleting state to true, attempts to delete the book
   * with the given ID by calling `deleteBook`. If successful, it updates the
   * state by removing the deleted book from the list, resets the selected book and
   * active tab, and finally sets the deleting state to false. If the deletion fails,
   * it sets an error state.
   *
   * @param {string} bookId - The ID of the book to be deleted.
   * @returns {Promise<void>} A promise that resolves when the book deletion process is complete.
   */
  const handleDeleteBook = async bookId => {
    setIsDeleting(true);
    const success = await deleteBook(bookId);
    if (success) {
      setBooks(prevBooks => prevBooks.filter(book => book.id !== bookId));
      setSelectedBook(null);
      setSelectedTab(TABS.LIST);
    } else {
      setErrorDeleting(true);
    }
    setIsDeleting(false);
  };

  return (
    <Main
      selectedTab={selectedTab}
      books={books}
      selectedBook={selectedBook}
      errorMessage={errorMessage}
      isDeleting={isDeleting}
      loadingBooks={loadingBooks}
      errorDeleting={errorDeleting}
      onTabClick={handleTabClick}
      onBookClick={handleBookClick}
      onDelete={handleDeleteBook}
    />
  );
};

export default App;
