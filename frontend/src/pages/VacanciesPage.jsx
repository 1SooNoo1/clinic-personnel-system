import { useEffect, useState } from "react";
import axios from "../api/axios";
import Header from "../components/Header";
import { useAuth } from "../hooks/useAuth";

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
      <main className="p-6">
        <h1 className="text-2xl font-bold mb-4">Открытые вакансии</h1>

        <input
          type="text"
          placeholder="Поиск по описанию, отделению или должности"
          value={search}
          onChange={(e) => setSearch(e.target.value)}
          className="border p-2 mb-4 w-full max-w-xl"
        />

        {(roles.includes("HR") || roles.includes("ADMIN")) && (
          <button
            className="mb-4 bg-green-600 text-white px-4 py-2 rounded"
            onClick={() => setShowModal(true)}
          >
            Добавить вакансию
          </button>
        )}

        <table className="w-full border text-sm">
          <thead className="bg-gray-100">
            <tr>
              <th className="border p-2">Отделение</th>
              <th className="border p-2">Должность</th>
              <th className="border p-2">Описание</th>
              <th className="border p-2">Статус</th>
              {(roles.includes("HR") || roles.includes("ADMIN")) && (
                <th className="border p-2">Действия</th>
              )}
            </tr>
          </thead>
          <tbody>
            {filtered.map((v) => (
              <tr key={v.id} className="hover:bg-gray-50">
                <td className="border p-2">{v.departmentName}</td>
                <td className="border p-2">{v.positionTitle}</td>
                <td className="border p-2">{v.description}</td>
                <td className="border p-2">
                  {v.isOpen ? "Открыта" : "Закрыта"}
                </td>
                {(roles.includes("HR") || roles.includes("ADMIN")) && (
                  <td className="border p-2 space-x-2">
                    <button
                      className="text-blue-600 hover:underline"
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
                      className="text-red-600 hover:underline"
                      onClick={async () => {
                        await axios.delete(`/vacancies/${v.id}`);
                        await load();
                      }}
                    >
                      Удалить
                    </button>
                  </td>
                )}
              </tr>
            ))}
          </tbody>
        </table>

        {filtered.length === 0 && (
          <p className="mt-4 text-gray-500">Вакансии не найдены</p>
        )}
      </main>

      {showModal && (
        <div className="fixed inset-0 bg-black bg-opacity-40 flex justify-center items-center z-50">
          <div className="bg-white p-6 rounded w-[400px]">
            <h2 className="text-lg font-bold mb-4">
              {editId ? "Редактировать" : "Новая"} вакансия
            </h2>

            <select
              value={form.departmentId}
              onChange={(e) =>
                setForm({ ...form, departmentId: e.target.value })
              }
              className="w-full border p-2 mb-3"
            >
              <option value="">Выберите отделение</option>
              {departments.map((d) => (
                <option key={d.id} value={d.id}>
                  {d.name}
                </option>
              ))}
            </select>

            <select
              value={form.positionId}
              onChange={(e) =>
                setForm({ ...form, positionId: e.target.value })
              }
              className="w-full border p-2 mb-3"
            >
              <option value="">Выберите должность</option>
              {positions.map((p) => (
                <option key={p.id} value={p.id}>
                  {p.title}
                </option>
              ))}
            </select>

            <textarea
              placeholder="Описание вакансии"
              value={form.description}
              onChange={(e) =>
                setForm({ ...form, description: e.target.value })
              }
              className="w-full border p-2 mb-3"
            />

            <label className="flex items-center gap-2 mb-3">
              <input
                type="checkbox"
                checked={form.isOpen}
                onChange={(e) =>
                  setForm({ ...form, isOpen: e.target.checked })
                }
              />{" "}
              Вакансия открыта
            </label>

            <div className="flex justify-between">
              <button
                onClick={() => {
                  setShowModal(false);
                  setEditId(null);
                }}
                className="text-gray-600 hover:underline"
              >
                Отмена
              </button>
              <button
                onClick={handleSubmit}
                className="bg-blue-600 text-white px-4 py-2 rounded"
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
