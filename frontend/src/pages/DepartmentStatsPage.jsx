import { useEffect, useState } from "react";
import axios from "../api/axios";
import Header from "../components/Header";

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
      <main className="p-6">
        <h1 className="text-2xl font-bold mb-4">Статистика по отделению</h1>

        <div className="flex flex-wrap gap-4 mb-4">
          <select
            value={departmentId}
            onChange={(e) => setDepartmentId(e.target.value)}
            className="border p-2 max-w-xs w-full"
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
            className="border p-2 max-w-xs w-full"
          />
          <input
            type="date"
            value={toDate}
            onChange={(e) => setToDate(e.target.value)}
            className="border p-2 max-w-xs w-full"
          />

          <button
            onClick={fetchStats}
            className="bg-blue-600 text-white px-4 py-2 rounded"
          >
            Показать
          </button>
        </div>

        {error && <p className="text-red-600 mb-4">{error}</p>}

        {stats && (
          <div className="bg-white p-4 rounded shadow-md max-w-md">
            <h2 className="text-lg font-bold mb-2">{stats.departmentName}</h2>
            <table className="w-full text-sm border">
              <tbody>
                <tr><td className="border p-2">Сотрудников на начало</td><td className="border p-2">{stats.countAtStart}</td></tr>
                <tr><td className="border p-2">Принято за период</td><td className="border p-2">{stats.hiredDuringPeriod}</td></tr>
                <tr><td className="border p-2">Уволено за период</td><td className="border p-2">{stats.dismissedDuringPeriod}</td></tr>
                <tr><td className="border p-2">Сотрудников на конец</td><td className="border p-2">{stats.countAtEnd}</td></tr>
              </tbody>
            </table>
          </div>
        )}
      </main>
    </div>
  );
}
