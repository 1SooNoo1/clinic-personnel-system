import { useNavigate, Link } from "react-router-dom";
import { useAuth } from "../hooks/useAuth";
import "../index.css"

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
    <header className="header">
      <h1>Клиника — Система персонала</h1>
      <div className="nav-links">
        <Link to="/vacancies" className="nav-link-blue">Вакансии</Link>

        {hasAnyRole("HR", "ADMIN") && (
          <Link to="/applications" className="nav-link-blue">Отклики</Link>
        )}

        {hasRole("CANDIDATE") && (
          <Link to="/my-applications" className="nav-link">Мои отклики</Link>
        )}

        {hasAnyRole("HR", "ADMIN") && (
          <Link to="/stats" className="nav-link-blue">Статистика</Link>
        )}

        {hasAnyRole("HR", "ADMIN") && (
          <Link to="/employees" className="nav-link">Сотрудники</Link>
        )}

        {hasAnyRole("HR", "ADMIN") && (
          <>
            <Link to="/departments" className="nav-link-blue">Отделения</Link>
            <Link to="/positions" className="nav-link-blue">Должности</Link>
          </>
        )}

        <Link to="/profile" className="user-name">{fullName}</Link>
        <button onClick={logout} className="logout-button">Выйти</button>
      </div>
    </header>
  );
}