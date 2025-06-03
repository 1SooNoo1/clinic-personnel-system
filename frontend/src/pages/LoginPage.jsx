import { useState } from "react";
import axios from "../api/axios";
import { useNavigate, Link } from "react-router-dom";
import { FiPhone, FiLock } from "react-icons/fi";

export default function LoginPage() {
  const [phone, setPhone] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState("");
  const navigate = useNavigate();

  const handleLogin = async (e) => {
    e.preventDefault();
    try {
      const res = await axios.post("/auth/login", { phone, password });
      localStorage.setItem("token", res.data.token);
      localStorage.setItem("roles", res.data.roles);
      localStorage.setItem("fullName", res.data.fullName);
      navigate("/dashboard");
    } catch (err) {
      setError("Неверный логин или пароль");
    }
  };

  return (
    <div className="min-h-screen flex items-center justify-center bg-gradient-to-r from-blue-100 via-white to-blue-100">
      <form
        onSubmit={handleLogin}
        className="bg-white p-8 rounded-xl shadow-xl w-full max-w-md space-y-4"
      >
        <h2 className="text-2xl font-bold text-center text-blue-700">Вход в систему</h2>
        {error && <p className="text-red-500 text-center">{error}</p>}

        <div className="flex items-center border rounded px-3 py-2">
          <FiPhone className="text-gray-400 mr-2" />
          <input
            type="text"
            placeholder="Телефон"
            value={phone}
            onChange={(e) => setPhone(e.target.value)}
            className="w-full outline-none"
            required
          />
        </div>

        <div className="flex items-center border rounded px-3 py-2">
          <FiLock className="text-gray-400 mr-2" />
          <input
            type="password"
            placeholder="Пароль"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            className="w-full outline-none"
            required
          />
        </div>

        <button
          type="submit"
          className="w-full py-2 rounded bg-gradient-to-r from-blue-600 to-blue-500 text-white font-semibold hover:opacity-90 transition"
        >
          Войти
        </button>

        <p className="text-sm text-center text-gray-600">
          Нет аккаунта?{" "}
          <Link to="/register" className="text-blue-600 hover:underline">
            Зарегистрироваться
          </Link>
        </p>
      </form>
    </div>
  );
}
