import React from 'react';
import '../../styles/ListTab.css';
import BookImage from '../../assets/standard.png';

const ListTab = ({ books, onBookClick, loadingBooks }) => {
  return (
    <div className="book-list-container">
      {loadingBooks && <p className="loading-message">Loading books...</p>}
      {books === null ? (
        <p style={{ color: 'red' }}>Error fetching books</p>
      ) : (
        <div className="book-list">
          {books.map(book => (
            <div
              className="book-item"
              key={book.isbn}
              onClick={() => onBookClick(book)} // Handle book click
            >
              <img src={BookImage} alt={book.title} className="book-image" />
              <div className="book-details">
                <h3 className="book-title">{book.title}</h3>
                <p className="book-author">by {book.author}</p>
                <p className="book-year">{book.year}</p>
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  );
};

export default ListTab;
