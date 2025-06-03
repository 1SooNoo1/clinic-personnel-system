import { useEffect, useState } from "react";
import axios from "../api/axios";
import Header from "../components/Header";
import { useAuth } from "../hooks/useAuth";

export default function MyApplicationsPage() {
  const [applications, setApplications] = useState([]);
  const [vacancies, setVacancies] = useState([]);
  const [selectedVacancyId, setSelectedVacancyId] = useState("");
  const [message, setMessage] = useState("");
  const [error, setError] = useState("");
  const { roles } = useAuth();

  const load = async () => {
    const [apps, vacs] = await Promise.all([
      axios.get("/applications/my"),
      axios.get("/vacancies/open")
    ]);
    setApplications(apps.data);
    setVacancies(vacs.data);
  };

  useEffect(() => {
    load();
  }, []);

  const handleApply = async () => {
    if (!selectedVacancyId) {
      setError("Выберите вакансию");
      return;
    }
    try {
      await axios.post("/applications/apply", null, {
        params: {
          vacancyId: selectedVacancyId,
          message: message
        }
      });
      setSelectedVacancyId("");
      setMessage("");
      setError("");
      await load();
    } catch (err) {
      console.error("Ошибка при отклике", err);
      setError("Ошибка: вы уже откликались на эту вакансию или произошла другая ошибка");
    }
  };


  return (
    <div>
      <Header />
      <main className="p-6 max-w-3xl mx-auto">
        <h1 className="text-2xl font-bold mb-4">Мои отклики</h1>

        {roles.includes("CANDIDATE") && (
          <div className="mb-6 border p-4 rounded">
            <h2 className="font-semibold mb-2">Отклик на вакансию</h2>
            <select
              value={selectedVacancyId}
              onChange={(e) => setSelectedVacancyId(e.target.value)}
              className="w-full border p-2 mb-2"
            >
              <option value="">Выберите вакансию</option>
              {vacancies.map(v => (
                <option key={v.id} value={v.id}>
                  {v.positionTitle} в {v.departmentName}
                </option>
              ))}
            </select>
            <textarea
              value={message}
              onChange={(e) => setMessage(e.target.value)}
              placeholder="Сообщение"
              className="w-full border p-2 mb-2"
            />
            <button
              onClick={handleApply}
              className="bg-blue-600 text-white px-4 py-2 rounded"
            >
              Откликнуться
            </button>
            {error && <p className="text-red-600 mt-2">{error}</p>}
          </div>
        )}

        <h2 className="text-xl font-semibold mb-2">История откликов</h2>
        <table className="w-full border text-sm">
          <thead className="bg-gray-100">
            <tr>
              <th className="border p-2">Должность</th>
              <th className="border p-2">Отделение</th>
              <th className="border p-2">Сообщение</th>
              <th className="border p-2">Дата</th>
            </tr>
          </thead>
          <tbody>
            {applications.map(app => (
              <tr key={app.id} className="hover:bg-gray-50">
                <td className="border p-2">{app.positionTitle}</td>
                <td className="border p-2">{app.departmentName}</td>
                <td className="border p-2">{app.message || "—"}</td>
                <td className="border p-2">{new Date(app.createdAt).toLocaleString()}</td>
              </tr>
            ))}
          </tbody>
        </table>
      </main>
    </div>
  );
}
