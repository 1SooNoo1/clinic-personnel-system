import { useEffect, useState } from "react";
import axios from "../api/axios";
import Header from "../components/Header";
import '../index.css'; 

export default function ApplicationsPage() {
  const [vacancies, setVacancies] = useState([]);
  const [selectedVacancyId, setSelectedVacancyId] = useState("");
  const [applications, setApplications] = useState([]);

  useEffect(() => {
    axios.get("/vacancies").then(res => setVacancies(res.data));
  }, []);

  useEffect(() => {
    if (selectedVacancyId) {
      axios
        .get(`/applications/vacancy/${selectedVacancyId}`)
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
      <main className="applications-container">
        <h1 className="applications-title">Отклики на вакансии</h1>

        <select
          value={selectedVacancyId}
          onChange={(e) => setSelectedVacancyId(e.target.value)}
          className="vacancy-select"
        >
          <option value="">Выберите вакансию</option>
          {vacancies.map(v => (
            <option key={v.id} value={v.id}>
              {v.departmentName} — {v.positionTitle}
            </option>
          ))}
        </select>

        {applications.length === 0 && selectedVacancyId && (
          <p className="no-applications">Нет откликов на эту вакансию</p>
        )}

        {applications.length > 0 && (
          <table className="applications-table">
            <thead>
              <tr>
                <th>Кандидат</th>
                <th>Телефон</th>
                <th>Сообщение</th>
                <th>Дата</th>
                <th>Статус</th>
                <th>Действия</th>
              </tr>
            </thead>
            <tbody>
              {applications.map(app => (
                <tr key={app.id}>
                  <td>{app.userFullName}</td>
                  <td>{app.userPhone}</td>
                  <td>{app.message || "—"}</td>
                  <td>{new Date(app.createdAt).toLocaleDateString()}</td>
                  <td>
                    <span
                      className={`status-tag ${
                        app.status === "NEW"
                          ? "status-new"
                          : app.status === "PENDING"
                          ? "status-pending"
                          : app.status === "ACCEPTED"
                          ? "status-accepted"
                          : "status-rejected"
                      }`}
                    >
                      {app.status === "NEW"
                        ? "Новый"
                        : app.status === "PENDING"
                        ? "В процессе"
                        : app.status === "ACCEPTED"
                        ? "Принят"
                        : "Отклонен"}
                    </span>
                  </td>
                  <td>
                    {(app.status === "PENDING" || app.status === "NEW") && (
                      <div className="action-buttons">
                        <button
                          className="action-button accept-button"
                          onClick={() => handleAccept(app.id)}
                        >
                          Принять
                        </button>
                        <button
                          className="action-button reject-button"
                          onClick={() => handleReject(app.id)}
                        >
                          Отклонить
                        </button>
                      </div>
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