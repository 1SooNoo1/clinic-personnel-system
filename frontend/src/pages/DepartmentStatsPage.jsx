import { useEffect, useState } from "react";
import axios from "../api/axios";
import Header from "../components/Header";
import '../index.css'; 

export default function DepartmentStatsPage() {
  const [departments, setDepartments] = useState([]);
  const [departmentId, setDepartmentId] = useState("");
  const [fromDate, setFromDate] = useState("");
  const [toDate, setToDate] = useState("");
  const [stats, setStats] = useState(null);
  const [error, setError] = useState(null);

  useEffect(() => {
    axios.get("/departments")
      .then(res => setDepartments(res.data))
      .catch(() => setError("Ошибка загрузки отделений"));
  }, []);

  const fetchStats = async () => {
    setStats(null);
    setError(null);
    if (!departmentId || !fromDate || !toDate) {
      setError("Пожалуйста, заполните все поля");
      return;
    }

    try {
      const res = await axios.get("/statistics/department", {
        params: {
          departmentId,
          from: fromDate,
          to: toDate
        }
      });
      setStats(res.data);
    } catch (err) {
      setError("Ошибка получения статистики");
    }
  };

  return (
    <div>
      <Header />
      <main className="stats-container">
        <h1 className="stats-title">Статистика по отделению</h1>

        <div className="stats-controls">
          <select
            value={departmentId}
            onChange={(e) => setDepartmentId(e.target.value)}
            className="stats-select"
          >
            <option value="">Выберите отделение</option>
            {departments.map(d => (
              <option key={d.id} value={d.id}>{d.name}</option>
            ))}
          </select>

          <input
            type="date"
            value={fromDate}
            onChange={(e) => setFromDate(e.target.value)}
            className="stats-date-input"
          />

          <input
            type="date"
            value={toDate}
            onChange={(e) => setToDate(e.target.value)}
            className="stats-date-input"
          />

          <button onClick={fetchStats} className="stats-button">
            Показать
          </button>
        </div>

        {error && <p className="stats-error">{error}</p>}

        {stats && (
          <div className="stats-card">
            <h2>{stats.departmentName}</h2>
            <table className="stats-table">
              <tbody>
                <tr>
                  <td>Сотрудников на начало</td>
                  <td>{stats.countAtStart}</td>
                </tr>
                <tr>
                  <td>Принято за период</td>
                  <td>{stats.hiredDuringPeriod}</td>
                </tr>
                <tr>
                  <td>Уволено за период</td>
                  <td>{stats.dismissedDuringPeriod}</td>
                </tr>
                <tr>
                  <td>Сотрудников на конец</td>
                  <td>{stats.countAtEnd}</td>
                </tr>
              </tbody>
            </table>
          </div>
        )}
      </main>
    </div>
  );
}