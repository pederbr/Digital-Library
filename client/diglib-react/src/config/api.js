import axios from 'axios';

const API_URL = 'https://api.diglib.no';

// Create axios instance with default config
export const api = axios.create({
  baseURL: API_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});
