import { createRoot } from 'react-dom/client'
import App from './App.jsx'
import { BrowserRouter } from 'react-router-dom'
//StrictMode는 두번씩 출력으로 제거
createRoot(document.getElementById('root')).render(
  <>
  <BrowserRouter>
    <App />
  </BrowserRouter>
  </>
)
