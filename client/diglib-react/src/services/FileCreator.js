/**
 * Creates a downloadable text file from an array of book pages.
 *
 * This function takes a book title and an array of page objects, converts the page content
 * into a single string, and creates a downloadable
 * text file with the specified book title. A Blob is used to create the file content,
 * which is then downloaded by simulating a click on a dynamically created link element.
 *
 * @param {string} bookTitle - The title of the book, used as the filename for the downloaded file.
 * @param {Array<Object>} pages - An array of page objects, each containing a `content` property
 *                                representing the text content of each page.
 */
export const createBookFile = (bookTitle, pages) => {
  try {
    // Create a single string from the array of content pages
    const contentString = pages.map(page => page.content).join('');

    // Create a blob for the text content
    const blob = new Blob([contentString], { type: 'text/plain' });
    const link = document.createElement('a');
    link.download = `${bookTitle}.txt`;
    link.href = window.URL.createObjectURL(blob);
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
  } catch (error) {
    console.error('Error creating book file:', error);
  }
};
