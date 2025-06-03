// src/pages/RegisterPage.jsx
import { useState } from "react";
import axios from "../api/axios";
import { useNavigate } from "react-router-dom";

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
    <div className="min-h-screen flex justify-center items-center bg-gray-100">
      <div className="bg-white shadow p-6 rounded w-full max-w-md">
        <h2 className="text-xl font-bold mb-4">Регистрация</h2>

        <input
          type="text"
          placeholder="ФИО"
          value={form.fullName}
          onChange={(e) => setForm({ ...form, fullName: e.target.value })}
          className="w-full border p-2 mb-3"
        />
        <input
          type="text"
          placeholder="Телефон"
          value={form.phone}
          onChange={(e) => setForm({ ...form, phone: e.target.value })}
          className="w-full border p-2 mb-3"
        />
        <input
          type="password"
          placeholder="Пароль"
          value={form.password}
          onChange={(e) => setForm({ ...form, password: e.target.value })}
          className="w-full border p-2 mb-3"
        />
        <input
          type="password"
          placeholder="Подтвердите пароль"
          value={form.confirmPassword}
          onChange={(e) => setForm({ ...form, confirmPassword: e.target.value })}
          className="w-full border p-2 mb-4"
        />

        {error && <p className="text-red-500 mb-3">{error}</p>}

        <button
          onClick={handleSubmit}
          className="w-full bg-blue-600 text-white py-2 rounded hover:bg-blue-700"
        >
          Зарегистрироваться
        </button>
      </div>
    </div>
  );
}
