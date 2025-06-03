import { useNavigate, Link } from "react-router-dom";
import { useAuth } from "../hooks/useAuth";

export default function Header() {
  const { fullName, roles } = useAuth();
  const navigate = useNavigate();

  const logout = () => {
    localStorage.clear();
    navigate("/login");
  };

  const hasRole = (role) => roles.includes(role);
  const hasAnyRole = (...required) => required.some(r => roles.includes(r));

  return (
    <header className="bg-blue-600 text-white flex justify-between items-center px-6 py-3">
      <h1 className="text-lg font-semibold">Клиника — Система персонала</h1>
      <div className="flex items-center gap-4">
        <Link to="/vacancies" className="text-sm text-blue-500 hover:underline">
          Вакансии
        </Link>

        {hasAnyRole("HR", "ADMIN") && (
          <Link to="/applications" className="text-sm text-blue-500 hover:underline">
            Отклики
          </Link>
        )}

        {hasRole("CANDIDATE") && (
          <Link to="/my-applications" className="text-white hover:underline">
            Мои отклики
          </Link>
        )}

        {hasAnyRole("HR", "ADMIN") && (
          <Link to="/stats" className="text-sm text-blue-500 hover:underline">
            Статистика
          </Link>
        )}

        {hasAnyRole("HR", "ADMIN") && (
          <Link to="/employees" className="text-white hover:underline">
            Сотрудники
          </Link>
        )}

        {hasAnyRole("HR", "ADMIN") && (
          <>
            <Link to="/departments" className="text-sm text-blue-500 hover:underline">
              Отделения
            </Link>
            <Link to="/positions" className="text-sm text-blue-500 hover:underline">
              Должности
            </Link>
          </>
        )}

        <Link to="/profile" className="hover:underline">
          {fullName}
        </Link>
        <button
          onClick={logout}
          className="bg-white text-blue-600 px-3 py-1 rounded hover:bg-gray-100"
        >
          Выйти
        </button>
      </div>
    </header>
  );
}
