import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import axios from "../api/axios";
import Header from "../components/Header";
import { useAuth } from "../hooks/useAuth";
import '../index.css'; 

export default function VacanciesPage() {
  const [vacancies, setVacancies] = useState([]);
  const [departments, setDepartments] = useState([]);
  const [positions, setPositions] = useState([]);
  const [search, setSearch] = useState("");
  const [showModal, setShowModal] = useState(false);
  const [editId, setEditId] = useState(null);

  const [form, setForm] = useState({
    departmentId: "",
    positionId: "",
    description: "",
    isOpen: true,
  });

  const { roles } = useAuth();

  const hasAnyRole = (...required) => required.some(r => roles.includes(r));

  const load = async () => {
    try {
      const [vacs, depts, poss] = await Promise.all([
        axios.get("/vacancies/open"),
        axios.get("/departments"),
        axios.get("/positions"),
      ]);
      setVacancies(vacs.data);
      setDepartments(depts.data);
      setPositions(poss.data);
    } catch (error) {
      console.error("Ошибка при загрузке данных", error);
    }
  };

  useEffect(() => {
    load();
  }, []);

  const filtered = vacancies.filter((v) =>
    v.description.toLowerCase().includes(search.toLowerCase()) ||
    v.departmentName.toLowerCase().includes(search.toLowerCase()) ||
    v.positionTitle.toLowerCase().includes(search.toLowerCase())
  );

  const handleSubmit = async () => {
    try {
      if (editId) {
        await axios.put(`/vacancies/${editId}`, form);
      } else {
        await axios.post("/vacancies", form);
      }
      await load();
      setShowModal(false);
      setEditId(null);
      setForm({
        departmentId: "",
        positionId: "",
        description: "",
        isOpen: true,
      });
    } catch (err) {
      console.error("Ошибка при сохранении вакансии", err);
    }
  };

  return (
    <div>
      <Header />
      <main className="vacancies-container">
        <h1 className="vacancies-title">Открытые вакансии</h1>

        <input
          type="text"
          placeholder="Поиск по описанию, отделению или должности"
          value={search}
          onChange={(e) => setSearch(e.target.value)}
          className="search-input"
        />

        {(hasAnyRole("HR", "ADMIN")) && (
          <button
            className="add-button"
            onClick={() => setShowModal(true)}
          >
            Добавить вакансию
          </button>
        )}

        <table className="vacancies-table">
          <thead>
            <tr>
              <th>Отделение</th>
              <th>Должность</th>
              <th>Описание</th>
              <th>Статус</th>
              {(hasAnyRole("HR", "ADMIN")) && <th>Действия</th>}
            </tr>
          </thead>
          <tbody>
            {filtered.map((v) => (
              <tr key={v.id}>
                <td>{v.departmentName}</td>
                <td>{v.positionTitle}</td>
                <td>{v.description}</td>
                <td>{v.isOpen ? "Открыта" : "Закрыта"}</td>
                {(hasAnyRole("HR", "ADMIN")) && (
                  <td className="actions">
                    <button
                      className="action-button edit-button"
                      onClick={() => {
                        setEditId(v.id);
                        setForm({
                          departmentId: v.departmentId,
                          positionId: v.positionId,
                          description: v.description,
                          isOpen: v.isOpen,
                        });
                        setShowModal(true);
                      }}
                    >
                      Редактировать
                    </button>

                    <button
                      className="action-button delete-button"
                      onClick={async () => {
                        if (window.confirm("Вы уверены, что хотите удалить эту вакансию?")) {
                          await axios.delete(`/vacancies/${v.id}`);
                          await load();
                        }
                      }}
                    >
                      Удалить
                    </button>

                    <Link to={`/vacancy/${v.id}/analysis`}>
                      <button className="action-button analyze-button">Анализ</button>
                    </Link>
                  </td>
                )}
              </tr>
            ))}
          </tbody>
        </table>

        {filtered.length === 0 && (
          <p className="no-results">Вакансии не найдены</p>
        )}
      </main>

      {showModal && (
        <div className="modal-overlay">
          <div className="modal-content">
            <h2 className="modal-title">
              {editId ? "Редактировать" : "Новая"} вакансия
            </h2>

            <select
              value={form.departmentId}
              onChange={(e) => setForm({ ...form, departmentId: e.target.value })}
              className="modal-select"
            >
              <option value="">Выберите отделение</option>
              {departments.map((d) => (
                <option key={d.id} value={d.id}>{d.name}</option>
              ))}
            </select>

            <select
              value={form.positionId}
              onChange={(e) => setForm({ ...form, positionId: e.target.value })}
              className="modal-select"
            >
              <option value="">Выберите должность</option>
              {positions.map((p) => (
                <option key={p.id} value={p.id}>{p.title}</option>
              ))}
            </select>

            <textarea
              placeholder="Описание вакансии"
              value={form.description}
              onChange={(e) => setForm({ ...form, description: e.target.value })}
              className="modal-textarea"
              rows="4"
            />

            <label className="checkbox-label">
              <input
                type="checkbox"
                checked={form.isOpen}
                onChange={(e) => setForm({ ...form, isOpen: e.target.checked })}
              />
              Вакансия открыта
            </label>

            <div className="modal-actions">
              <button
                onClick={() => {
                  setShowModal(false);
                  setEditId(null);
                }}
                className="modal-cancel"
              >
                Отмена
              </button>
              <button
                onClick={handleSubmit}
                className="modal-save"
              >
                Сохранить
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}