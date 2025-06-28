import { useState } from "react";
import axios from "../api/axios";
import { useNavigate, Link } from "react-router-dom";
import '../index.css'; 

export default function RegisterPage() {
  const navigate = useNavigate();
  const [form, setForm] = useState({
    fullName: "",
    phone: "",
    password: "",
    confirmPassword: "",
  });
  const [error, setError] = useState("");

  const handleSubmit = async () => {
    setError("");

    if (form.password !== form.confirmPassword) {
      setError("Пароли не совпадают");
      return;
    }

    try {
      await axios.post("/auth/register", {
        fullName: form.fullName,
        phone: form.phone,
        password: form.password,
        roles: "CANDIDATE"
      });

      // После регистрации — редирект на login
      navigate("/login");
    } catch (err) {
      console.error("Ошибка при регистрации", err);
      setError("Ошибка при регистрации: " + (err.response?.data || "неизвестная ошибка"));
    }
  };

  return (
    <div className="register-container">
      <div className="register-card">
        <h2 className="register-title">Регистрация</h2>

        <input
          type="text"
          placeholder="ФИО"
          value={form.fullName}
          onChange={(e) => setForm({ ...form, fullName: e.target.value })}
          className="register-input"
        />
        <input
          type="text"
          placeholder="Телефон"
          value={form.phone}
          onChange={(e) => setForm({ ...form, phone: e.target.value })}
          className="register-input"
        />
        <input
          type="password"
          placeholder="Пароль"
          value={form.password}
          onChange={(e) => setForm({ ...form, password: e.target.value })}
          className="register-input"
        />
        <input
          type="password"
          placeholder="Подтвердите пароль"
          value={form.confirmPassword}
          onChange={(e) => setForm({ ...form, confirmPassword: e.target.value })}
          className="register-input"
        />

        {error && <p className="register-error">{error}</p>}

        <button className="register-button" onClick={handleSubmit}>
          Зарегистрироваться
        </button>

        <Link to="/login" className="register-link">
          Уже есть аккаунт? Войдите
        </Link>
      </div>
    </div>
  );
}