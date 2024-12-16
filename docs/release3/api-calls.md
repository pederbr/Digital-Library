# API endpoints

>&#8203;    
>[Home](../../README.md)&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; 
[About](../../docs/README.md)&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
[Development-Tools](../../docs/release3/development-tools.md)&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
[API](../../docs/release3/api-calls.md)&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
[Workhabits](../../docs/release3/workflow.md)&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
[Tests](../../docs/release3/tests.md)&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
[JavaFX](../../client/diglib-javafx/README.md)&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
[React](../../client/diglib-react/README.md)&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
[Backend](../../backend/README.md)&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
[CI/CD](../../docs/release3/gitlab-ci.md)&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
[Google-Cloud](../../docs/release3/gcloud-setup.md)&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
[Cloudflare](../../docs/release3/cloudflare-setup.md)&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
[Use-cases](../../docs/release3/usercase.md)&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;     
>&#8203;    

### GET /books
Retrieves a list of books with optional filtering and sorting.

**Query Parameters:**
- `page` (optional): Page number (default: 0)
- `size` (optional): Number of items per page (default: 10)
- `sortBy` (optional): Field to sort by (default: "title")
- `sortDir` (optional): Sort direction ("asc" or "desc", default: "asc")
- `title` (optional): Filter books by title (case-insensitive partial match)
- `author` (optional): Filter books by author (case-insensitive partial match)
- `genre` (optional): Filter books by genre (case-insensitive partial match)

**Response:** Page object containing:
- List of Book objects
- Pagination metadata

### GET /books/{id}
Retrieves a specific book by its ID.

**Path Parameters:**
- `id`: Book ID

**Response:**
- 200: Book object
- 404: Not found if book doesn't exist

### GET /books/{id}/content
Retrieves the content of a specific book with pagination.

**Path Parameters:**
- `id`: Book ID

**Query Parameters:**
- `page` (optional): Page number (default: 0)
- `pageSize` (optional): Number of content items per page

**Response:**
- 200: PagedContent object
- 404: Not found if book doesn't exist
- 500: Internal server error if content cannot be read

### POST /books
Adds a new book to the library.

**Form Parameters:**
- `title`: Book title (required)
  - Must not be empty
  - Maximum length: 100 characters
- `author`: Book author (required)
  - Must not be empty
  - Maximum length: 100 characters
- `genre`: Book genre (required)
  - Must be a valid genre
- `isbn`: Book ISBN (required)
  - Must be exactly 13 digits
- `year`: Publication year (required)
  - Must be between 0 and 2025
- `content`: Book content file (MultipartFile)

**Response:**
- 200: Success message with book ID
- 400: Bad request if parameters are invalid
- 500: Internal server error if content cannot be stored

### DELETE /books/{id}
Deletes a book from the library.

**Path Parameters:**
- `id`: Book ID

**Response:**
- 200: Success message
- 404: Not found if book doesn't exist

## Genres Endpoint

### GET /genres
Retrieves the list of available genres.

**Response:**
- 200: List of genre strings
- 500: Internal server error if genres cannot be fetched
