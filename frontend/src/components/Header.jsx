import { useNavigate } from "react-router-dom";
import { useAuth } from "../hooks/useAuth";

export default function Header() {
  const { fullName } = useAuth();
  const navigate = useNavigate();

  const logout = () => {
    localStorage.clear();
    navigate("/login");
  };

  return (
    <header className="bg-blue-600 text-white flex justify-between items-center px-6 py-3">
      <h1 className="text-lg font-semibold">Клиника — Система персонала</h1>
      <div className="flex items-center gap-4">
        <span>{fullName}</span>
        <button onClick={logout} className="bg-white text-blue-600 px-3 py-1 rounded hover:bg-gray-100">
          Выйти
        </button>
      </div>
    </header>
  );
}
