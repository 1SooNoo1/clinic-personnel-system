import { useEffect, useState } from "react";
import axios from "../api/axios";
import Header from "../components/Header";

export default function ProfilePage() {
  const [profile, setProfile] = useState(null);
  const [editMode, setEditMode] = useState(false);
  const [formData, setFormData] = useState({
    fullName: "",
    email: "",
    phone: "",
    birthDate: ""
  });

  useEffect(() => {
    axios.get("/profile/me")
      .then(res => {
        setProfile(res.data);
        setFormData({
          fullName: res.data.fullName || "",
          email: res.data.email || "",
          phone: res.data.phone || "",
          birthDate: res.data.birthDate || ""
        });
      })
      .catch(err => console.error("Ошибка загрузки профиля", err));
  }, []);

  const handleSave = async () => {
    try {
      await axios.put(`/profile/me`, formData);
      setProfile({ ...profile, ...formData });
      setEditMode(false);
    } catch (err) {
      console.error("Ошибка сохранения профиля", err);
    }
  };

  if (!profile) return <p className="p-4">Загрузка...</p>;

  return (
    <div>
      <Header />
      <main className="p-6 max-w-2xl mx-auto">
        <h1 className="text-2xl font-bold mb-4">Профиль сотрудника</h1>

        {!editMode ? (
          <div className="space-y-2">
            <p><strong>ФИО:</strong> {profile.fullName}</p>
            <p><strong>Email:</strong> {profile.email}</p>
            <p><strong>Телефон:</strong> {profile.phone}</p>
            <p><strong>Дата рождения:</strong> {profile.birthDate || "—"}</p>

            <button
              onClick={() => setEditMode(true)}
              className="mt-4 bg-blue-600 text-white px-4 py-2 rounded"
            >
              Редактировать
            </button>
            <button
                onClick={async () => {
                    try {
                    const res = await axios.put(`/employees/my/workbook/pdf`, null, {
                      responseType: "blob"
                    });
                    const url = window.URL.createObjectURL(new Blob([res.data]));
                    const link = document.createElement("a");
                    link.href = url;
                    link.setAttribute("download", `workbook_${profile.id}.pdf`);
                    document.body.appendChild(link);
                    link.click();
                    link.remove();
                    } catch (err) {
                    console.error("Ошибка при экспорте трудовой книжки", err);
                    }
                }}
                className="mt-4 bg-gray-700 text-white px-4 py-2 rounded"
                >
                Скачать трудовую книжку (PDF)
            </button>
          </div>
        ) : (
          <div className="space-y-3">
            <input
              type="text"
              value={formData.fullName}
              onChange={(e) => setFormData({ ...formData, fullName: e.target.value })}
              placeholder="ФИО"
              className="w-full border p-2"
            />
            <input
              type="email"
              value={formData.email}
              onChange={(e) => setFormData({ ...formData, email: e.target.value })}
              placeholder="Email"
              className="w-full border p-2"
            />
            <input
              type="text"
              value={formData.phone}
              onChange={(e) => setFormData({ ...formData, phone: e.target.value })}
              placeholder="Телефон"
              className="w-full border p-2"
            />
            <input
              type="date"
              value={formData.birthDate}
              onChange={(e) => setFormData({ ...formData, birthDate: e.target.value })}
              className="w-full border p-2"
            />

            <div className="flex justify-between">
              <button
                onClick={() => setEditMode(false)}
                className="text-gray-600 hover:underline"
              >
                Отмена
              </button>
              <button
                onClick={handleSave}
                className="bg-green-600 text-white px-4 py-2 rounded"
              >
                Сохранить
              </button>
            </div>
          </div>
        )}
      </main>
    </div>
  );
}