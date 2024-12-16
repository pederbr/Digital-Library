import React, { useEffect, useState } from 'react';
import '../../styles/UploadTab.css';
import { postBook } from '../../services/BookHandler';
import { getGenres } from '../../services/GenreHandler';

const UploadTab = () => {
  const [title, setTitle] = useState('');
  const [author, setAuthor] = useState('');
  const [year, setYear] = useState('');
  const [isbn, setIsbn] = useState('');
  const [content, setContent] = useState('');
  const [genre, setGenre] = useState('');
  const [genres, setGenres] = useState([]);
  const [filteredGenres, setFilteredGenres] = useState([]);
  const [errorMessage, setErrorMessage] = useState(''); // Error message for invalid genres
  const [errorUploading, setErrorUploading] = useState(false); // Error message for failed uploads
  const [isUploading, setIsUploading] = useState(false);

  useEffect(() => {
    /**
     * Fetches the list of genres from the server and updates the state with the retrieved content.
     * If an error occurs, it logs the error to the console.
     *
     * @async
     * @function fetchGenres
     * @returns {Promise<void>} A promise that resolves when the genres have been fetched and the state has been updated.
     */
    const fetchGenres = async () => {
      const genres = await getGenres();
      if (genres) {
        setGenres(genres);
        setFilteredGenres(genres);
      }
    };
    fetchGenres();
  }, []);

  /**
   * Handles changes to the genre input field.
   *
   * This function updates the `genre` state with the user's input, and then
   * filters the list of available genres based on the input. It performs a
   * case-insensitive search and sets the filtered results in `filteredGenres`
   * for displaying relevant suggestions to the user.
   *
   * @param {Event} e - The input change event.
   */
  const handleGenreChange = e => {
    const input = e.target.value;
    setGenre(input);

    const filtered = genres.filter(g =>
      g.toLowerCase().includes(input.toLowerCase())
    );
    setFilteredGenres(filtered);
  };

  /**
   * Handles file selection for uploading content.
   *
   * This function captures the selected file from the input field
   * and updates the `content` state with it.
   *
   * @param {Event} e - The file input change event.
   */
  const handleFileChange = e => {
    setContent(e.target.files[0]); // Set the uploaded file
  };

  /**
   * Handles the form submission to upload a new book.
   *
   * This function validates the form fields, including checking if the selected genre is valid.
   * If validation fails, an error message is displayed. If all fields are valid, the function
   * sends the book data (title, author, year, genre, ISBN, and content) to the server.
   * Upon a successful upload, an alert is shown, the form fields are reset, and the page reloads.
   * If the upload fails, an error state is set.
   *
   * @async
   * @param {Event} e - The form submission event.
   * @returns {Promise<void>} A promise that resolves when the upload process completes.
   */
  const handleUpload = async e => {
    e.preventDefault();
    if (!genres.includes(genre)) {
      setErrorMessage('Invalid genre selected');
      return;
    }
    setErrorUploading(false);
    setIsUploading(true);
    setErrorMessage('');

    if (title && author && year && isbn && genre && content) {
      const result = await postBook(title, author, year, genre, isbn, content);
      if (result) {
        alert('Book uploaded successfully!');
        window.location.reload();
        // Reset the form
        setTitle('');
        setAuthor('');
        setYear('');
        setIsbn('');
        setContent('');
        setGenre('');
      } else {
        setErrorUploading(true);
        setIsUploading(false);
      }
    }
    setIsUploading(false);
  };

  return (
    <div className="upload-tab">
      <p>{isUploading ? 'Uploading...' : ''}</p>
      <p style={{ color: 'red' }}>
        {errorUploading ? 'Error uploading book' : ''}
      </p>
      <form onSubmit={handleUpload} className="upload-form">
        <div className="form-group">
          <label htmlFor="title">Title:</label>
          <input
            type="text"
            id="title"
            value={title}
            onChange={e => setTitle(e.target.value)}
            required
          />
        </div>

        <div className="form-group">
          <label htmlFor="author">Author:</label>
          <input
            type="text"
            id="author"
            value={author}
            onChange={e => setAuthor(e.target.value)}
            required
          />
        </div>

        <div className="form-group">
          <label htmlFor="genre">Genre:</label>
          <input
            type="text"
            id="genre"
            value={genre}
            onChange={handleGenreChange}
            placeholder="Type to filter genres"
            list="genre-list"
          />
          <datalist id="genre-list">
            {filteredGenres.map(g => (
              <option key={g} value={g}>
                {g}
              </option>
            ))}
          </datalist>
          {errorMessage && (
            <div className="error-message" style={{ color: 'red' }}>
              {errorMessage}
            </div>
          )}
        </div>

        <div className="form-group">
          <label htmlFor="year">Year:</label>
          <input
            type="number"
            id="year"
            value={year}
            onChange={e => setYear(e.target.value)}
            required
            max={new Date().getFullYear()}
            min={0}
          />
        </div>

        <div className="form-group">
          <label htmlFor="isbn">ISBN (13 digits):</label>
          <input
            type="text"
            id="isbn"
            value={isbn}
            onChange={e => setIsbn(e.target.value)}
            required
            maxLength={13}
          />
        </div>

        <div className="form-group">
          <label htmlFor="contents">Content:</label>
          <input
            type="file"
            accept=".txt"
            id="contents"
            onChange={handleFileChange}
            required
          />
        </div>

        <button type="submit" className="upload-button">
          Upload
        </button>
      </form>
    </div>
  );
};

export default UploadTab;
