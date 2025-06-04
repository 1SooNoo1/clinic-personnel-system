import { useEffect, useState } from "react";
import axios from "../api/axios";
import Header from "../components/Header";
import '../index.css'; 

export default function DepartmentsPage() {
  const [departments, setDepartments] = useState([]);
  const [name, setName] = useState("");
  const [editingId, setEditingId] = useState(null);
  const [error, setError] = useState("");

  const loadDepartments = async () => {
    try {
      const res = await axios.get("/departments");
      setDepartments(res.data);
    } catch (err) {
      console.error("Ошибка загрузки отделений", err);
    }
  };

  useEffect(() => {
    loadDepartments();
  }, []);

  const handleSubmit = async () => {
    try {
      if (editingId) {
        await axios.put(`/departments/${editingId}`, { name });
      } else {
        await axios.post("/departments", { name });
      }
      setName("");
      setEditingId(null);
      await loadDepartments();
    } catch (err) {
      setError("Ошибка при сохранении");
      console.error(err);
    }
  };

  const handleDelete = async (id) => {
    if (window.confirm("Удалить отделение?")) {
      await axios.delete(`/departments/${id}`);
      await loadDepartments();
    }
  };

  const startEdit = (dept) => {
    setName(dept.name);
    setEditingId(dept.id);
  };

  return (
    <div>
      <Header />
      <main className="departments-container">
        <h1 className="departments-title">Отделения</h1>

        <div className="department-form-card">
          <h2 className="department-form-heading">
            {editingId ? "Редактировать" : "Новое"} отделение
          </h2>
          <input
            type="text"
            value={name}
            onChange={(e) => setName(e.target.value)}
            placeholder="Название отделения"
            className="department-input"
          />

          <div className="department-actions">
            <button
              onClick={handleSubmit}
              className="department-action-button department-save-button"
            >
              {editingId ? "Сохранить" : "Добавить"}
            </button>

            {editingId && (
              <button
                onClick={() => {
                  setEditingId(null);
                  setName("");
                }}
                className="department-action-button department-cancel-button"
              >
                Отмена
              </button>
            )}
          </div>

          {error && (
            <p className="department-error-message">{error}</p>
          )}
        </div>

        <table className="department-table">
          <thead>
            <tr>
              <th>Название</th>
              <th>Действия</th>
            </tr>
          </thead>
          <tbody>
            {departments.map((dept) => (
              <tr key={dept.id}>
                <td>{dept.name}</td>
                <td className="department-table-actions">
                  <button
                    onClick={() => startEdit(dept)}
                    className="department-table-button"
                  >
                    Редактировать
                  </button>
                  <button
                    onClick={() => handleDelete(dept.id)}
                    className="department-table-button department-table-delete"
                  >
                    Удалить
                  </button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </main>
    </div>
  );
}