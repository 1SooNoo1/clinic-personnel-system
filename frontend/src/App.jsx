import { BrowserRouter as Router, Routes, Route } from "react-router-dom";
import LoginPage from "./pages/LoginPage";
import Dashboard from "./pages/Dashboard";
import EmployeesPage from "./pages/EmployeesPage";
import ProfilePage from "./pages/ProfilePage"; 
import DepartmentStatsPage from "./pages/DepartmentStatsPage";
import ProtectedRoute from "./components/ProtectedRoute";
import VacanciesPage from "./pages/VacanciesPage";
import ApplicationsPage from "./pages/ApplicationsPage"
import MyApplicationsPage from "./pages/MyApplicationsPage";
import RegisterPage from "./pages/RegisterPage";
import DepartmentsPage from "./pages/DepartmentsPage";
import PositionsPage from "./pages/PositionsPage";
import VacancyAnalysisPage from "./pages/VacancyAnalysisPage";


function App() {
  return (
    <Router>
      <Routes>
        <Route path="/register" element={<RegisterPage />} />
        <Route path="/login" element={<LoginPage />} />
        <Route
          path="/dashboard"
          element={
            <ProtectedRoute allowedRoles={["ADMIN", "HR", "EMPLOYEE", "CANDIDATE"]}>
              <Dashboard />
            </ProtectedRoute>
          }
        />
        <Route
          path="/employees"
          element={
            <ProtectedRoute allowedRoles={["ADMIN", "HR"]}>
              <EmployeesPage />
            </ProtectedRoute>
          }
        />
        <Route path="/vacancies" element={
            <ProtectedRoute allowedRoles={["ADMIN", "HR", "EMPLOYEE", "CANDIDATE"]}>
              <VacanciesPage  />
            </ProtectedRoute>
          } 
        />
        
        <Route
          path="/vacancy/:id/analysis"
          element={
            <ProtectedRoute allowedRoles={["ADMIN", "HR"]}>
              <VacancyAnalysisPage />
            </ProtectedRoute>
          }
        />

        <Route path="/applications" element={
          <ProtectedRoute allowedRoles={["ADMIN", "HR"]}>
            <ApplicationsPage />
          </ProtectedRoute>
          }
        />
        <Route path="/my-applications" element={
            <ProtectedRoute allowedRoles={["ADMIN", "HR", "CANDIDATE"]}>
              <MyApplicationsPage />
            </ProtectedRoute>
          } 
        />
        <Route path="/stats" element={
            <ProtectedRoute allowedRoles={["ADMIN", "HR"]}>
              <DepartmentStatsPage />
            </ProtectedRoute>
          } 
        />
        <Route path="/departments" element={
          <ProtectedRoute allowedRoles={["ADMIN", "HR"]}>
            <DepartmentsPage />
          </ProtectedRoute>
        } />
        <Route path="/positions" element={
          <ProtectedRoute allowedRoles={["ADMIN", "HR"]}>
            <PositionsPage />
          </ProtectedRoute>
        } />

        <Route
          path="/profile"
          element={
            <ProtectedRoute allowedRoles={["ADMIN", "HR", "EMPLOYEE", "CANDIDATE"]}>
              <ProfilePage />
            </ProtectedRoute>
          }
        />
        <Route path="/unauthorized" element={<h1 className="p-4 text-red-500">Доступ запрещён</h1>} />
        <Route path="*" element={<LoginPage />} />
      </Routes>
    </Router>
  );
}

export default App;
