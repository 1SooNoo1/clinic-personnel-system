import React, { useEffect, useState } from 'react';
import { useParams, Link } from 'react-router-dom';
import axios from '../api/axios';
import { useAuth } from '../hooks/useAuth';
import '../index.css'; 

export default function VacancyAnalysisPage() {
  const { id } = useParams();
  const { token } = useAuth();

  const [employees, setEmployees] = useState([]);
  const [vacancyTitle, setVacancyTitle] = useState('');
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchAnalysis = async () => {
      try {
        const res = await axios.get(`/vacancies/${id}/analysis`, {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        });

        const vacancyRes = await axios.get(`/vacancies`, {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        });
        const found = vacancyRes.data.find(v => v.id === parseInt(id));
        if (found) {
          setVacancyTitle(`${found.positionTitle} / ${found.departmentName}`);
        }

        setEmployees(res.data);
      } catch (error) {
        console.error('Ошибка загрузки анализа:', error);
      } finally {
        setLoading(false);
      }
    };

    fetchAnalysis();
  }, [id, token]);

  return (
    <div className="analysis-container">
      <Link to="/vacancies" className="analysis-back-link">
        ← Назад к вакансиям
      </Link>

      <h1 className="analysis-title">Анализ вакансии: {vacancyTitle}</h1>

      {loading ? (
        <p className="analysis-loading">Загрузка данных...</p>
      ) : employees.length === 0 ? (
        <p className="analysis-empty">Нет подходящих сотрудников.</p>
      ) : (
        <div className="employee-grid">
          {employees.map(emp => (
            <div key={emp.id} className="employee-card">
              <p className="employee-name">{emp.fullName}</p>
              <div className="employee-info">
                <p><span className="employee-label">Email:</span> {emp.email}</p>
                <p><span className="employee-label">Телефон:</span> {emp.phone}</p>
                <p>
                  <span className="employee-label">Отделение:</span>{' '}
                  {emp.department?.name || 'Не указано'}
                </p>
                <p>
                  <span className="employee-label">Должность:</span>{' '}
                  {emp.position?.title || 'Не указана'}
                </p>
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  );
}