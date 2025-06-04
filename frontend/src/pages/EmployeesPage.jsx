import { useEffect, useState } from "react";
import axios from "../api/axios";
import Header from "../components/Header";
import { useAuth } from "../hooks/useAuth";
import '../index.css'; 

export default function EmployeesPage() {
  const [employees, setEmployees] = useState([]);
  const [search, setSearch] = useState("");
  const [departments, setDepartments] = useState([]);
  const [positions, setPositions] = useState([]);
  const [selectedDepartment, setSelectedDepartment] = useState("");
  const [selectedPosition, setSelectedPosition] = useState("");

  const [showModal, setShowModal] = useState(false);
  const [formData, setFormData] = useState({
    fullName: "",
    email: "",
    phone: "",
    birthDate: "",
    employmentDate: "",
    departmentId: "",
    positionId: ""
  });

  const [dismissId, setDismissId] = useState(null);
  const [dismissDate, setDismissDate] = useState("");
  const [transferId, setTransferId] = useState(null);
  const [transferData, setTransferData] = useState({ departmentId: "", positionId: "" });
  const [editId, setEditId] = useState(null);

  const { roles } = useAuth();

  // Загрузка данных
  useEffect(() => {
    axios.get("/employees").then(res => setEmployees(res.data));
    axios.get("/departments").then(res => setDepartments(res.data));
    axios.get("/positions").then(res => setPositions(res.data));
  }, []);

  // Фильтр сотрудников
  const filtered = employees.filter(emp =>
    emp.fullName.toLowerCase().includes(search.toLowerCase()) ||
    emp.email.toLowerCase().includes(search.toLowerCase())
  ).filter(emp =>
    (!selectedDepartment || emp.department?.id === parseInt(selectedDepartment)) &&
    (!selectedPosition || emp.position?.id === parseInt(selectedPosition))
  );

  const handleAdd = async () => {
    try {
      await axios.post("/employees", formData);
      const res = await axios.get("/employees");
      setEmployees(res.data);
      setShowModal(false);
      setFormData({
        fullName: "",
        email: "",
        phone: "",
        birthDate: "",
        employmentDate: "",
        departmentId: "",
        positionId: ""
      });
    } catch (err) {
      console.error("Ошибка при добавлении сотрудника", err);
    }
  };

  const handleDismiss = async () => {
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
  };

  const handleTransfer = async () => {
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
      console.error("Ошибка при переводе", err);
    }
  };

  const handleEdit = async () => {
    try {
      await axios.put(`/employees/${editId}`, formData);
      const res = await axios.get("/employees");
      setEmployees(res.data);
      setEditId(null);
    } catch (err) {
      console.error("Ошибка при редактировании", err);
    }
  };

  return (
    <div>
      <Header />
      <main className="employees-container">
        <h1 className="employees-title">Сотрудники</h1>

        <div className="filters">
          <input
            type="text"
            placeholder="Поиск по имени или email"
            value={search}
            onChange={(e) => setSearch(e.target.value)}
            className="filter-input"
          />

          <select
            value={selectedDepartment}
            onChange={(e) => setSelectedDepartment(e.target.value)}
            className="filter-select"
          >
            <option value="">Все отделения</option>
            {departments.map(dept => (
              <option key={dept.id} value={dept.id}>{dept.name}</option>
            ))}
          </select>

          <select
            value={selectedPosition}
            onChange={(e) => setSelectedPosition(e.target.value)}
            className="filter-select"
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
              className="text-blue-600 hover:text-blue-800"
            >
              Сбросить фильтры
            </button>
          )}
        </div>

        {(roles.includes("HR") || roles.includes("ADMIN")) && (
          <button
            className="add-button"
            onClick={() => setShowModal(true)}
          >
            Добавить сотрудника
          </button>
        )}

        <table className="employee-table">
          <thead>
            <tr>
              <th>ФИО</th>
              <th>Email</th>
              <th>Телефон</th>
              <th>Дата рождения</th>
              <th>Дата приёма</th>
              <th>Отделение</th>
              <th>Должность</th>
              <th>Дата увольнения</th>
              <th>Активен</th>
              <th>Действия</th>
            </tr>
          </thead>
          <tbody>
            {filtered.map(emp => (
              <tr key={emp.id} className="hover:bg-gray-50">
                <td>{emp.fullName}</td>
                <td>{emp.email}</td>
                <td>{emp.phone}</td>
                <td>{emp.birthDate || "—"}</td>
                <td>{emp.employmentDate || "—"}</td>
                <td>{emp.department?.name || "—"}</td>
                <td>{emp.position?.title || "—"}</td>
                <td>{emp.dismissalDate || "—"}</td>
                <td>
                  {emp.active ? (
                    <span className="status-tag status-yes">Да</span>
                  ) : (
                    <span className="status-tag status-no">Нет</span>
                  )}
                </td>
                <td className="action-buttons">
                  {emp.active && (roles.includes("HR") || roles.includes("ADMIN")) && (
                    <>
                      <button
                        className="action-button dismiss-button"
                        onClick={() => setDismissId(emp.id)}
                      >
                        Уволить
                      </button>
                      <button
                        className="action-button transfer-button"
                        onClick={() => {
                          setTransferId(emp.id);
                          setTransferData({
                            departmentId: emp.department?.id || "",
                            positionId: emp.position?.id || ""
                          });
                        }}
                      >
                        Перевести
                      </button>
                      <button
                        className="action-button edit-button"
                        onClick={() => {
                          setEditId(emp.id);
                          setFormData({
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

        {filtered.length === 0 && (
          <p className="no-results">Сотрудники не найдены</p>
        )}
      </main>

      {/* Модальное окно: Добавить */}
      {showModal && (
        <div className="modal-overlay">
          <div className="modal-content">
            <h2 className="modal-title">Добавить нового сотрудника</h2>

            <input
              placeholder="ФИО"
              value={formData.fullName}
              onChange={(e) => setFormData({ ...formData, fullName: e.target.value })}
              className="modal-input"
            />
            <input
              type="email"
              placeholder="Email"
              value={formData.email}
              onChange={(e) => setFormData({ ...formData, email: e.target.value })}
              className="modal-input"
            />
            <input
              placeholder="Телефон"
              value={formData.phone}
              onChange={(e) => setFormData({ ...formData, phone: e.target.value })}
              className="modal-input"
            />
            <input
              type="date"
              value={formData.birthDate}
              onChange={(e) => setFormData({ ...formData, birthDate: e.target.value })}
              className="modal-input"
            />
            <input
              type="date"
              value={formData.employmentDate}
              onChange={(e) => setFormData({ ...formData, employmentDate: e.target.value })}
              className="modal-input"
            />
            <select
              value={formData.departmentId}
              onChange={(e) => setFormData({ ...formData, departmentId: e.target.value })}
              className="modal-select"
            >
              <option value="">Выберите отделение</option>
              {departments.map(dept => (
                <option key={dept.id} value={dept.id}>
                  {dept.name}
                </option>
              ))}
            </select>
            <select
              value={formData.positionId}
              onChange={(e) => setFormData({ ...formData, positionId: e.target.value })}
              className="modal-select"
            >
              <option value="">Выберите должность</option>
              {positions.map(pos => (
                <option key={pos.id} value={pos.id}>
                  {pos.title}
                </option>
              ))}
            </select>

            <div className="modal-actions">
              <button
                className="modal-cancel"
                onClick={() => setShowModal(false)}
              >
                Отмена
              </button>
              <button
                className="modal-save"
                onClick={handleAdd}
              >
                Сохранить
              </button>
            </div>
          </div>
        </div>
      )}

      {/* Модальное окно: Уволить */}
      {dismissId && (
        <div className="modal-overlay">
          <div className="modal-content">
            <h2 className="modal-title">Уволить сотрудника</h2>
            <label className="block mb-2">Дата увольнения</label>
            <input
              type="date"
              value={dismissDate}
              onChange={(e) => setDismissDate(e.target.value)}
              className="modal-input"
            />
            <div className="modal-actions">
              <button
                className="modal-cancel"
                onClick={() => {
                  setDismissId(null);
                  setDismissDate("");
                }}
              >
                Отмена
              </button>
              <button
                className="modal-dismiss"
                onClick={handleDismiss}
              >
                Подтвердить
              </button>
            </div>
          </div>
        </div>
      )}

      {/* Модальное окно: Перевести */}
      {transferId && (
        <div className="modal-overlay">
          <div className="modal-content">
            <h2 className="modal-title">Перевести сотрудника</h2>
            <select
              value={transferData.departmentId}
              onChange={(e) => setTransferData({ ...transferData, departmentId: e.target.value })}
              className="modal-select"
            >
              <option value="">Выберите новое отделение</option>
              {departments.map(dept => (
                <option key={dept.id} value={dept.id}>{dept.name}</option>
              ))}
            </select>
            <select
              value={transferData.positionId}
              onChange={(e) => setTransferData({ ...transferData, positionId: e.target.value })}
              className="modal-select"
            >
              <option value="">Выберите новую должность</option>
              {positions.map(pos => (
                <option key={pos.id} value={pos.id}>{pos.title}</option>
              ))}
            </select>
            <div className="modal-actions">
              <button
                className="modal-cancel"
                onClick={() => setTransferId(null)}
              >
                Отмена
              </button>
              <button
                className="modal-transfer"
                onClick={handleTransfer}
              >
                Перевести
              </button>
            </div>
          </div>
        </div>
      )}

      {/* Модальное окно: Редактировать */}
      {editId && (
        <div className="modal-overlay">
          <div className="modal-content">
            <h2 className="modal-title">Редактировать сотрудника</h2>

            <input
              placeholder="ФИО"
              value={formData.fullName}
              onChange={(e) => setFormData({ ...formData, fullName: e.target.value })}
              className="modal-input"
            />
            <input
              type="email"
              placeholder="Email"
              value={formData.email}
              onChange={(e) => setFormData({ ...formData, email: e.target.value })}
              className="modal-input"
            />
            <input
              placeholder="Телефон"
              value={formData.phone}
              onChange={(e) => setFormData({ ...formData, phone: e.target.value })}
              className="modal-input"
            />
            <input
              type="date"
              value={formData.birthDate}
              onChange={(e) => setFormData({ ...formData, birthDate: e.target.value })}
              className="modal-input"
            />
            <input
              type="date"
              value={formData.employmentDate}
              onChange={(e) => setFormData({ ...formData, employmentDate: e.target.value })}
              className="modal-input"
            />
            <select
              value={formData.departmentId}
              onChange={(e) => setFormData({ ...formData, departmentId: e.target.value })}
              className="modal-select"
            >
              <option value="">Выберите отделение</option>
              {departments.map((dept) => (
                <option key={dept.id} value={dept.id}>
                  {dept.name}
                </option>
              ))}
            </select>
            <select
              value={formData.positionId}
              onChange={(e) => setFormData({ ...formData, positionId: e.target.value })}
              className="modal-select"
            >
              <option value="">Выберите должность</option>
              {positions.map((pos) => (
                <option key={pos.id} value={pos.id}>
                  {pos.title}
                </option>
              ))}
            </select>

            <div className="modal-actions">
              <button
                className="modal-cancel"
                onClick={() => setEditId(null)}
              >
                Отмена
              </button>
              <button
                className="modal-save"
                onClick={handleEdit}
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