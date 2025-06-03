import { useEffect, useState } from "react";
import axios from "../api/axios";
import Header from "../components/Header";

export default function DepartmentsPage() {
  const [departments, setDepartments] = useState([]);
  const [name, setName] = useState("");
  const [editingId, setEditingId] = useState(null);
  const [error, setError] = useState("");

  const loadDepartments = async () => {
    const res = await axios.get("/departments");
    setDepartments(res.data);
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
      <main className="max-w-2xl mx-auto p-6">
        <h1 className="text-2xl font-bold mb-4">Отделения</h1>

        <div className="bg-white shadow p-4 rounded mb-6">
          <h2 className="font-semibold mb-2">{editingId ? "Редактировать" : "Новое"} отделение</h2>
          <input
            type="text"
            value={name}
            onChange={(e) => setName(e.target.value)}
            placeholder="Название отделения"
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
                  setName("");
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
            {departments.map((dept) => (
              <tr key={dept.id} className="hover:bg-gray-50">
                <td className="border p-2">{dept.name}</td>
                <td className="border p-2 flex gap-2">
                  <button
                    onClick={() => startEdit(dept)}
                    className="text-blue-600 hover:underline"
                  >
                    Редактировать
                  </button>
                  <button
                    onClick={() => handleDelete(dept.id)}
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
