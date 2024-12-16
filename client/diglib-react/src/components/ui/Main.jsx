// LibraryView.js
import React from 'react';
import ViewTab from './ViewTab';
import ListTab from './ListTab';
import UploadTab from './UploadTab';
import '../../styles/App.css';
import '../../styles/Tabs.css';

const TABS = {
  LIST: 'ListTab',
  VIEW: 'ViewTab',
  UPLOAD: 'UploadTab',
};

const Main = ({
  selectedTab,
  books,
  selectedBook,
  errorMessage,
  isDeleting,
  loadingBooks,
  errorDeleting,
  onTabClick,
  onBookClick,
  onDelete,
}) => (
  <div>
    <h1>Digital Library</h1>
    {/* Tab Navigation */}
    <div className="tabs">
      <button
        className={`tab-button ${selectedTab === TABS.LIST ? 'active' : ''}`}
        onClick={() => onTabClick(TABS.LIST)}
      >
        Books
      </button>
      <button
        className={`tab-button ${selectedTab === TABS.VIEW ? 'active' : ''}`}
        onClick={() => onTabClick(TABS.VIEW)}
      >
        View Book
      </button>
      <button
        className={`tab-button ${selectedTab === TABS.UPLOAD ? 'active' : ''}`}
        onClick={() => onTabClick(TABS.UPLOAD)}
      >
        Upload Book
      </button>
    </div>

    {/* Tab Content */}
    <div className="tab-content">
      {isDeleting && <p>Deleting...</p>}
      {selectedTab === TABS.LIST && (
        <ListTab
          books={books}
          onBookClick={onBookClick}
          loadingBooks={loadingBooks}
        />
      )}
      {selectedTab === TABS.UPLOAD && <UploadTab />}
      {selectedTab === TABS.VIEW &&
        (selectedBook ? (
          <ViewTab
            book={selectedBook}
            onDelete={onDelete}
            errorDeleting={errorDeleting}
          />
        ) : (
          <div className="error-message">{errorMessage}</div>
        ))}
    </div>
  </div>
);

export default Main;
