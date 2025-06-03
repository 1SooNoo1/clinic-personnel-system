import { useEffect, useState } from "react";
import axios from "../api/axios";
import Header from "../components/Header";

export default function ApplicationsPage() {
  const [vacancies, setVacancies] = useState([]);
  const [selectedVacancyId, setSelectedVacancyId] = useState("");
  const [applications, setApplications] = useState([]);


  useEffect(() => {
    axios.get("/vacancies").then(res => setVacancies(res.data));
  }, []);

  useEffect(() => {
    if (selectedVacancyId) {
      axios.get(`/applications/vacancy/${selectedVacancyId}`)
        .then(res => setApplications(res.data))
        .catch(err => console.error("Ошибка загрузки откликов", err));
    }
  }, [selectedVacancyId]);

  const handleAccept = async (id) => {
    try {
      await axios.put(`/applications/${id}/accept`);
      const updated = await axios.get(`/applications/vacancy/${selectedVacancyId}`);
      setApplications(updated.data);
    } catch (err) {
      console.error("Ошибка при принятии кандидата", err);
    }
  };

  const handleReject = async (id) => {
    try {
      await axios.put(`/applications/${id}/reject`);
      const updated = await axios.get(`/applications/vacancy/${selectedVacancyId}`);
      setApplications(updated.data);
    } catch (err) {
      console.error("Ошибка при отклонении кандидата", err);
    }
  };

  return (
    <div>
      <Header />
      <main className="p-6">
        <h1 className="text-2xl font-bold mb-4">Отклики на вакансии</h1>

        <select
          value={selectedVacancyId}
          onChange={(e) => setSelectedVacancyId(e.target.value)}
          className="border p-2 mb-4"
        >
          <option value="">Выберите вакансию</option>
          {vacancies.map(v => (
            <option key={v.id} value={v.id}>
              {v.departmentName} — {v.positionTitle}
            </option>
          ))}
        </select>

        {applications.length === 0 && selectedVacancyId && (
          <p className="text-gray-500">Нет откликов на эту вакансию</p>
        )}

        {applications.length > 0 && (
          <table className="w-full border text-sm">
            <thead className="bg-gray-100">
              <tr>
                <th className="border p-2">Кандидат</th>
                <th className="border p-2">Телефон</th>
                <th className="border p-2">Сообщение</th>
                <th className="border p-2">Дата</th>
                <th className="border p-2">Статус</th>
                <th className="border p-2">Действия</th>
              </tr>
            </thead>
            <tbody>
              {applications.map(app => (
                <tr key={app.id} className="hover:bg-gray-50">
                  <td className="border p-2">{app.userFullName}</td>
                  <td className="border p-2">{app.userPhone}</td>
                  <td className="border p-2">{app.message || "—"}</td>
                  <td className="border p-2">{new Date(app.createdAt).toLocaleDateString()}</td>
                  <td className="border p-2">{app.status}</td>
                  <td className="border p-2 space-x-2">
                    {(app.status === "PENDING" || app.status === "NEW") && (
                      <>
                        <button
                          className="text-green-600 hover:underline"
                          onClick={() => handleAccept(app.id)}
                        >Принять</button>
                        <button
                          className="text-red-600 hover:underline"
                          onClick={() => handleReject(app.id)}
                        >Отклонить</button>
                      </>
                    )}
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        )}
      </main>
    </div>
  );
}
