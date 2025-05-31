import axios from 'axios';

const instance = axios.create({
  baseURL: 'http://localhost:8080/api', 
});

instance.interceptors.request.use(config => {
  const token = localStorage.getItem("token");

 
  if (
    token &&
    !config.url.includes("/auth/login") && 
    !config.url.includes("/auth/register") 
  ) {
    config.headers.Authorization = `Bearer ${token}`;
  }

  return config;
});


export default instance;
