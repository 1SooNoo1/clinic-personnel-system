import { useState } from "react";
import axios from "../api/axios";
import { useNavigate, Link } from "react-router-dom";
import { FiPhone, FiLock } from "react-icons/fi";
import '../index.css'; 

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
    <div className="login-container">
      <form onSubmit={handleLogin} className="login-card">
        <h2 className="login-title">Вход в систему</h2>

        {error && <div className="error-message">{error}</div>}

        <div className="form-group">
          <label className="label">Телефон</label>
          <div className="input-wrapper">
            <FiPhone className="input-icon" />
            <input
              type="text"
              value={phone}
              onChange={(e) => setPhone(e.target.value)}
              placeholder="Введите телефон"
              className="input-field"
              required
            />
          </div>
        </div>

        <div className="form-group">
          <label className="label">Пароль</label>
          <div className="input-wrapper">
            <FiLock className="input-icon" />
            <input
              type="password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              placeholder="Введите пароль"
              className="input-field"
              required
            />
          </div>
        </div>

        <button type="submit" className="login-button">Войти</button>

        <p className="register-link">
          Нет аккаунта?{" "}
          <Link to="/register">Зарегистрироваться</Link>
        </p>
      </form>
    </div>
  );
}