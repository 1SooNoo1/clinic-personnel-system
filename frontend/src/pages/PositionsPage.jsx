import { useEffect, useState } from "react";
import axios from "../api/axios";
import Header from "../components/Header";

export default function PositionsPage() {
  const [positions, setPositions] = useState([]);
  const [title, setTitle] = useState("");
  const [editingId, setEditingId] = useState(null);
  const [error, setError] = useState("");

  const loadPositions = async () => {
    const res = await axios.get("/positions");
    setPositions(res.data);
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
      <main className="max-w-2xl mx-auto p-6">
        <h1 className="text-2xl font-bold mb-4">Должности</h1>

        <div className="bg-white shadow p-4 rounded mb-6">
          <h2 className="font-semibold mb-2">{editingId ? "Редактировать" : "Новая"} должность</h2>
          <input
            type="text"
            value={title}
            onChange={(e) => setTitle(e.target.value)}
            placeholder="Название должности"
            className="border p-2 w-full mb-2"
          />
          <div className="flex gap-2">
            <button
              onClick={handleSubmit}
              className="bg-blue-600 text-white px-4 py-2 rounded"
            >
              {editingId ? "Сохранить" : "Добавить"}
            </button>
            {editingId && (
              <button
                onClick={() => {
                  setEditingId(null);
                  setTitle("");
                }}
                className="bg-gray-400 text-white px-4 py-2 rounded"
              >
                Отмена
              </button>
            )}
          </div>
          {error && <p className="text-red-600 mt-2">{error}</p>}
        </div>

        <table className="w-full border text-sm">
          <thead className="bg-gray-100">
            <tr>
              <th className="border p-2">Название</th>
              <th className="border p-2 w-32">Действия</th>
            </tr>
          </thead>
          <tbody>
            {positions.map((pos) => (
              <tr key={pos.id} className="hover:bg-gray-50">
                <td className="border p-2">{pos.title}</td>
                <td className="border p-2 flex gap-2">
                  <button
                    onClick={() => startEdit(pos)}
                    className="text-blue-600 hover:underline"
                  >
                    Редактировать
                  </button>
                  <button
                    onClick={() => handleDelete(pos.id)}
                    className="text-red-600 hover:underline"
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
