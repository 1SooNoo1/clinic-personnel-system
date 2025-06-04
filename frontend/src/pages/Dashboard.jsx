import Header from "../components/Header";
import { useAuth } from "../hooks/useAuth";
import '../index.css'; 


export default function Dashboard() {
  const { roles } = useAuth();

  return (
    <div>
      <Header />
      <main className="dashboard-container">
        <div className="dashboard-header">
          <h2 className="dashboard-title">Добро пожаловать!</h2>
          <h3 className="dashboard-subtitle">Клиника имени доктора Крюкова</h3>
          <p className="dashboard-info">
            Ваша роль: <strong>{roles.join(", ")}</strong>
          </p>
        </div>

        <div className="card-grid">
          <div className="card">
            <h4 className="card-title">Вакансии</h4>
            <p className="card-description">
              Просмотрите список доступных вакансий или добавьте новую.
            </p>
          </div>

          <div className="card">
            <h4 className="card-title">Сотрудники</h4>
            <p className="card-description">
              Управление сотрудниками вашей организации.
            </p>
          </div>

          <div className="card">
            <h4 className="card-title">Отклики</h4>
            <p className="card-description">
              Отслеживайте и управляйте откликами кандидатов.
            </p>
          </div>

          <div className="card">
            <h4 className="card-title">Статистика</h4>
            <p className="card-description">
              Получайте аналитику по вакансиям и найму.
            </p>
          </div>
        </div>
      </main>
    </div>
  );
}