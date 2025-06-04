import { useEffect, useState } from "react";
import axios from "../api/axios";
import Header from "../components/Header";
import '../index.css'; 

export default function PositionsPage() {
  const [positions, setPositions] = useState([]);
  const [title, setTitle] = useState("");
  const [editingId, setEditingId] = useState(null);
  const [error, setError] = useState("");

  const loadPositions = async () => {
    try {
      const res = await axios.get("/positions");
      setPositions(res.data);
    } catch (err) {
      console.error("Ошибка загрузки должностей", err);
    }
  };

  useEffect(() => {
    loadPositions();
  }, []);

  const handleSubmit = async () => {
    try {
      if (editingId) {
        await axios.put(`/positions/${editingId}`, { title });
      } else {
        await axios.post("/positions", { title });
      }
      setTitle("");
      setEditingId(null);
      await loadPositions();
    } catch (err) {
      setError("Ошибка при сохранении");
      console.error(err);
    }
  };

  const handleDelete = async (id) => {
    if (window.confirm("Удалить должность?")) {
      await axios.delete(`/positions/${id}`);
      await loadPositions();
    }
  };

  const startEdit = (pos) => {
    setTitle(pos.title);
    setEditingId(pos.id);
  };

  return (
    <div>
      <Header />
      <main className="positions-container">
        <h1 className="positions-title">Должности</h1>

        <div className="position-form-card">
          <h2 className="position-form-heading">
            {editingId ? "Редактировать" : "Новая"} должность
          </h2>
          <input
            type="text"
            value={title}
            onChange={(e) => setTitle(e.target.value)}
            placeholder="Название должности"
            className="position-input"
          />

          <div className="position-actions">
            <button
              onClick={handleSubmit}
              className="position-action-button position-save-button"
            >
              {editingId ? "Сохранить" : "Добавить"}
            </button>

            {editingId && (
              <button
                onClick={() => {
                  setEditingId(null);
                  setTitle("");
                }}
                className="position-action-button position-cancel-button"
              >
                Отмена
              </button>
            )}
          </div>

          {error && (
            <p className="position-error-message">{error}</p>
          )}
        </div>

        <table className="position-table">
          <thead>
            <tr>
              <th>Название</th>
              <th>Действия</th>
            </tr>
          </thead>
          <tbody>
            {positions.map((pos) => (
              <tr key={pos.id}>
                <td>{pos.title}</td>
                <td className="position-table-actions">
                  <button
                    onClick={() => startEdit(pos)}
                    className="position-table-button position-table-edit"
                  >
                    Редактировать
                  </button>
                  <button
                    onClick={() => handleDelete(pos.id)}
                    className="position-table-button position-table-delete"
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