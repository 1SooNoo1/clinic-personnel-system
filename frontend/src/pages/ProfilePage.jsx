import { useEffect, useState } from "react";
import axios from "../api/axios";
import Header from "../components/Header";
import '../index.css'; 

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
      <main className="profile-container">
        <h1 className="profile-title">Профиль сотрудника</h1>

        <div className="profile-card">
          {!editMode ? (
            <>
              <div className="profile-info">
                <div className="profile-field">
                  <span className="profile-label">ФИО</span>
                  <span className="profile-value">{profile.fullName}</span>
                </div>
                <div className="profile-field">
                  <span className="profile-label">Email</span>
                  <span className="profile-value">{profile.email}</span>
                </div>
                <div className="profile-field">
                  <span className="profile-label">Телефон</span>
                  <span className="profile-value">{profile.phone}</span>
                </div>
                <div className="profile-field">
                  <span className="profile-label">Дата рождения</span>
                  <span className="profile-value">{profile.birthDate || "—"}</span>
                </div>
              </div>

              <button className="edit-button" onClick={() => setEditMode(true)}>
                Редактировать
              </button>

              <button className="download-button mt-4" onClick={async () => {
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
              }}>
                Скачать трудовую книжку (PDF)
              </button>
            </>
          ) : (
            <div className="edit-form">
              <input
                type="text"
                value={formData.fullName}
                onChange={(e) => setFormData({ ...formData, fullName: e.target.value })}
                placeholder="ФИО"
                className="edit-input"
              />
              <input
                type="email"
                value={formData.email}
                onChange={(e) => setFormData({ ...formData, email: e.target.value })}
                placeholder="Email"
                className="edit-input"
              />
              <input
                type="text"
                value={formData.phone}
                onChange={(e) => setFormData({ ...formData, phone: e.target.value })}
                placeholder="Телефон"
                className="edit-input"
              />
              <input
                type="date"
                value={formData.birthDate}
                onChange={(e) => setFormData({ ...formData, birthDate: e.target.value })}
                className="edit-input"
              />

              <div className="edit-actions">
                <button className="edit-cancel" onClick={() => setEditMode(false)}>
                  Отмена
                </button>
                <button className="edit-save" onClick={handleSave}>
                  Сохранить
                </button>
              </div>
            </div>
          )}
        </div>
      </main>
    </div>
  );
}