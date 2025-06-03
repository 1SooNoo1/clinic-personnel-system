import { useEffect, useState } from "react";
import axios from "../api/axios";
import Header from "../components/Header";
import { useAuth } from "../hooks/useAuth";

export default function EmployeesPage() {
  const [employees, setEmployees] = useState([]);
  const [search, setSearch] = useState("");
  const { roles } = useAuth();

  const [selectedDepartment, setSelectedDepartment] = useState("");
  const [selectedPosition, setSelectedPosition] = useState("");

  const [showModal, setShowModal] = useState(false);
  const [departments, setDepartments] = useState([]);
  const [positions, setPositions] = useState([]);

  const [dismissId, setDismissId] = useState(null);
  const [dismissDate, setDismissDate] = useState("");

  const [transferId, setTransferId] = useState(null);
  const [transferData, setTransferData] = useState({ departmentId: "", positionId: "" });

  const [editId, setEditId] = useState(null);
  const [editData, setEditData] = useState({
    fullName: "",
    email: "",
    phone: "",
    birthDate: "",
    employmentDate: "",
    departmentId: "",
    positionId: ""
  });  

  const [formData, setFormData] = useState({
    fullName: "",
    email: "",
    phone: "",
    birthDate: "",
    employmentDate: "",
    departmentId: "",
    positionId: ""
  });

  useEffect(() => {
    axios.get("/employees").then(res => setEmployees(res.data));
    axios.get("/departments").then(res => setDepartments(res.data));
    axios.get("/positions").then(res => setPositions(res.data));
  }, []);

  const filtered = employees
    .filter(emp =>
      emp.fullName.toLowerCase().includes(search.toLowerCase()) ||
      emp.email.toLowerCase().includes(search.toLowerCase())
    )
    .filter(emp =>
      (!selectedDepartment || emp.department?.id === parseInt(selectedDepartment)) &&
      (!selectedPosition || emp.position?.id === parseInt(selectedPosition))
    );

  return (
    <div>
      <Header />
      <main className="p-6">
        <h1 className="text-2xl font-bold mb-4">Сотрудники</h1>

        <div className="flex flex-wrap gap-4 mb-4">
          <input
            type="text"
            placeholder="Поиск по имени или email"
            value={search}
            onChange={(e) => setSearch(e.target.value)}
            className="border p-2 w-full max-w-xs"
          />

          <select
            value={selectedDepartment}
            onChange={(e) => setSelectedDepartment(e.target.value)}
            className="border p-2 w-full max-w-xs"
          >
            <option value="">Все отделения</option>
            {departments.map(dept => (
              <option key={dept.id} value={dept.id}>{dept.name}</option>
            ))}
          </select>

          <select
            value={selectedPosition}
            onChange={(e) => setSelectedPosition(e.target.value)}
            className="border p-2 w-full max-w-xs"
          >
            <option value="">Все должности</option>
            {positions.map(pos => (
              <option key={pos.id} value={pos.id}>{pos.title}</option>
            ))}
          </select>

          {(selectedDepartment || selectedPosition) && (
            <button
              onClick={() => {
                setSelectedDepartment("");
                setSelectedPosition("");
              }}
              className="text-blue-600 hover:underline"
            >
              Сбросить фильтры
            </button>
          )}
        </div>

        {roles.includes("HR") || roles.includes("ADMIN") ? (
          <button
            className="mb-4 bg-green-600 text-white px-4 py-2 rounded"
            onClick={() => setShowModal(true)}
          >
            Добавить сотрудника
          </button>
        ) : null}

        <table className="w-full border-collapse border text-sm">
          <thead className="bg-gray-100">
            <tr>
              <th className="border p-2">ФИО</th>
              <th className="border p-2">Email</th>
              <th className="border p-2">Телефон</th>
              <th className="border p-2">Дата рождения</th>
              <th className="border p-2">Дата приёма</th>
              <th className="border p-2">Отделение</th>
              <th className="border p-2">Должность</th>
              <th className="border p-2">Дата увольнения</th>
              <th className="border p-2">Активен</th>
              <th className="border p-2">Действия</th>
            </tr>
          </thead>
          <tbody>
            {filtered.map(emp => (
              <tr key={emp.id} className="hover:bg-gray-50">
                <td className="border p-2">{emp.fullName}</td>
                <td className="border p-2">{emp.email}</td>
                <td className="border p-2">{emp.phone}</td>
                <td className="border p-2">{emp.birthDate || "—"}</td>
                <td className="border p-2">{emp.employmentDate || "—"}</td>
                <td className="border p-2">{emp.department?.name || "—"}</td>
                <td className="border p-2">{emp.position?.title || "—"}</td>
                <td className="border p-2">{emp.dismissalDate || "—"}</td>
                <td className="border p-2">
                  {emp.active ? <span className="text-green-600 font-semibold">Да</span> : <span className="text-red-500 font-semibold">Нет</span>}
                </td>
                <td className="border p-2 text-center space-y-1">
                  {emp.active && (roles.includes("HR") || roles.includes("ADMIN")) && (
                    <>
                      <button
                        className="text-red-600 hover:underline block"
                        onClick={() => setDismissId(emp.id)}
                      >
                        Уволить
                      </button>
                      <button
                        className="text-blue-600 hover:underline block"
                        onClick={() => {
                          setTransferId(emp.id);
                          setTransferData({ departmentId: emp.department?.id || "", positionId: emp.position?.id || "" });
                        }}
                      >
                        Перевести
                      </button>
                      <button
                        className="text-orange-600 hover:underline block"
                        onClick={() => {
                            setEditId(emp.id);
                            setEditData({
                            fullName: emp.fullName,
                            email: emp.email,
                            phone: emp.phone,
                            birthDate: emp.birthDate,
                            employmentDate: emp.employmentDate,
                            departmentId: emp.department?.id || "",
                            positionId: emp.position?.id || ""
                            });
                        }}
                        >
                        Редактировать
                    </button>

                    </>
                  )}
                </td>
              </tr>
            ))}
          </tbody>
        </table>

        {filtered.length === 0 && <p className="mt-4 text-gray-500">Ничего не найдено</p>}
      </main>

      {/* Modal: Add Employee */}
      {showModal && (
        <div className="fixed inset-0 bg-black bg-opacity-40 flex justify-center items-center z-50">
          <div className="bg-white p-6 rounded w-[400px]">
            <h2 className="text-lg font-bold mb-4">Новый сотрудник</h2>

            <input type="text" placeholder="ФИО" value={formData.fullName}
              onChange={(e) => setFormData({ ...formData, fullName: e.target.value })}
              className="w-full border p-2 mb-3" required />

            <input type="email" placeholder="Email" value={formData.email}
              onChange={(e) => setFormData({ ...formData, email: e.target.value })}
              className="w-full border p-2 mb-3" required />

            <input type="text" placeholder="Телефон" value={formData.phone}
              onChange={(e) => setFormData({ ...formData, phone: e.target.value })}
              className="w-full border p-2 mb-3" required />

            <input type="date" value={formData.birthDate}
              onChange={(e) => setFormData({ ...formData, birthDate: e.target.value })}
              className="w-full border p-2 mb-3" required />

            <input type="date" value={formData.employmentDate}
              onChange={(e) => setFormData({ ...formData, employmentDate: e.target.value })}
              className="w-full border p-2 mb-3" required />

            <select value={formData.departmentId}
              onChange={(e) => setFormData({ ...formData, departmentId: e.target.value })}
              className="w-full border p-2 mb-3" required>
              <option value="">Выберите отделение</option>
              {departments.map(dept => <option key={dept.id} value={dept.id}>{dept.name}</option>)}
            </select>

            <select value={formData.positionId}
              onChange={(e) => setFormData({ ...formData, positionId: e.target.value })}
              className="w-full border p-2 mb-3" required>
              <option value="">Выберите должность</option>
              {positions.map(pos => <option key={pos.id} value={pos.id}>{pos.title}</option>)}
            </select>

            <div className="flex justify-between">
              <button onClick={() => setShowModal(false)} className="text-gray-600 hover:underline">Отмена</button>
              <button
                onClick={async () => {
                  try {
                    await axios.post("/employees", formData);
                    const res = await axios.get("/employees");
                    setEmployees(res.data);
                    setShowModal(false);
                  } catch (err) {
                    console.error("Ошибка при создании сотрудника", err);
                  }
                }}
                className="bg-blue-600 text-white px-4 py-2 rounded"
              >
                Сохранить
              </button>
            </div>
          </div>
        </div>
      )}

      {/* Modal: Dismiss */}
      {dismissId && (
        <div className="fixed inset-0 bg-black bg-opacity-40 flex justify-center items-center z-50">
          <div className="bg-white p-6 rounded w-[400px]">
            <h2 className="text-lg font-bold mb-4">Уволить сотрудника</h2>
            <label className="block mb-2">Дата увольнения</label>
            <input type="date" value={dismissDate}
              onChange={(e) => setDismissDate(e.target.value)}
              className="w-full border p-2 mb-4" required />

            <div className="flex justify-between">
              <button onClick={() => { setDismissId(null); setDismissDate(""); }} className="text-gray-600 hover:underline">Отмена</button>
              <button
                onClick={async () => {
                  try {
                    await axios.put(`/employees/${dismissId}/dismiss`, null, {
                      params: { dismissalDate: dismissDate }
                    });
                    const res = await axios.get("/employees");
                    setEmployees(res.data);
                    setDismissId(null);
                    setDismissDate("");
                  } catch (err) {
                    console.error("Ошибка при увольнении", err);
                  }
                }}
                className="bg-red-600 text-white px-4 py-2 rounded"
              >
                Подтвердить
              </button>
            </div>
          </div>
        </div>
      )}

      {/* Modal: Transfer */}
      {transferId && (
        <div className="fixed inset-0 bg-black bg-opacity-40 flex justify-center items-center z-50">
          <div className="bg-white p-6 rounded w-[400px]">
            <h2 className="text-lg font-bold mb-4">Перевести сотрудника</h2>

            <select value={transferData.departmentId}
              onChange={(e) => setTransferData({ ...transferData, departmentId: e.target.value })}
              className="w-full border p-2 mb-3" required>
              <option value="">Выберите новое отделение</option>
              {departments.map(dept => <option key={dept.id} value={dept.id}>{dept.name}</option>)}
            </select>

            <select value={transferData.positionId}
              onChange={(e) => setTransferData({ ...transferData, positionId: e.target.value })}
              className="w-full border p-2 mb-3" required>
              <option value="">Выберите новую должность</option>
              {positions.map(pos => <option key={pos.id} value={pos.id}>{pos.title}</option>)}
            </select>

            <div className="flex justify-between">
              <button onClick={() => setTransferId(null)} className="text-gray-600 hover:underline">Отмена</button>
              <button
                onClick={async () => {
                  try {
                    await axios.put(`/employees/${transferId}/transfer`, null, {
                      params: {
                        departmentId: transferData.departmentId,
                        positionId: transferData.positionId
                      }
                    });
                    const res = await axios.get("/employees");
                    setEmployees(res.data);
                    setTransferId(null);
                  } catch (err) {
                    console.error("Ошибка при переводе сотрудника", err);
                  }
                }}
                className="bg-blue-600 text-white px-4 py-2 rounded"
              >
                Перевести
              </button>
            </div>
          </div>
        </div>
      )}

      {editId && (
        <div className="fixed inset-0 bg-black bg-opacity-40 flex justify-center items-center z-50">
            <div className="bg-white p-6 rounded w-[400px]">
            <h2 className="text-lg font-bold mb-4">Редактировать сотрудника</h2>

            <input type="text" placeholder="ФИО" value={editData.fullName}
                onChange={(e) => setEditData({ ...editData, fullName: e.target.value })}
                className="w-full border p-2 mb-3" required />

            <input type="email" placeholder="Email" value={editData.email}
                onChange={(e) => setEditData({ ...editData, email: e.target.value })}
                className="w-full border p-2 mb-3" required />

            <input type="text" placeholder="Телефон" value={editData.phone}
                onChange={(e) => setEditData({ ...editData, phone: e.target.value })}
                className="w-full border p-2 mb-3" required />

            <input type="date" value={editData.birthDate}
                onChange={(e) => setEditData({ ...editData, birthDate: e.target.value })}
                className="w-full border p-2 mb-3" required />

            <input type="date" value={editData.employmentDate}
                onChange={(e) => setEditData({ ...editData, employmentDate: e.target.value })}
                className="w-full border p-2 mb-3" required />

            <select value={editData.departmentId}
                onChange={(e) => setEditData({ ...editData, departmentId: e.target.value })}
                className="w-full border p-2 mb-3" required>
                <option value="">Выберите отделение</option>
                {departments.map(dept => (
                <option key={dept.id} value={dept.id}>{dept.name}</option>
                ))}
            </select>

            <select value={editData.positionId}
                onChange={(e) => setEditData({ ...editData, positionId: e.target.value })}
                className="w-full border p-2 mb-3" required>
                <option value="">Выберите должность</option>
                {positions.map(pos => (
                <option key={pos.id} value={pos.id}>{pos.title}</option>
                ))}
            </select>

            <div className="flex justify-between">
                <button onClick={() => setEditId(null)} className="text-gray-600 hover:underline">Отмена</button>
                <button
                onClick={async () => {
                    try {
                    await axios.put(`/employees/${editId}`, editData);
                    const res = await axios.get("/employees");
                    setEmployees(res.data);
                    setEditId(null);
                    } catch (err) {
                    console.error("Ошибка при редактировании", err);
                    }
                }}
                className="bg-orange-600 text-white px-4 py-2 rounded"
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
