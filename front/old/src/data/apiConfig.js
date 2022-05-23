let backendHost;

  //backendHost = 'http://drinks.elveteek.ch';
  backendHost = process.env.REACT_APP_BACKEND_HOST || 'http://localhost:8080';

export const API_ROOT = `${backendHost}`;
