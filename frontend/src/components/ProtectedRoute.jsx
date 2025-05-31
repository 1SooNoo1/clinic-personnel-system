import { Navigate } from "react-router-dom";
import { useAuth } from "../hooks/useAuth";

export default function ProtectedRoute({ children, allowedRoles = [] }) {
  const { isAuthenticated, roles } = useAuth();

  if (!isAuthenticated) return <Navigate to="/login" />;
  if (allowedRoles.length && !roles.some(r => allowedRoles.includes(r))) {
    return <Navigate to="/unauthorized" />;
  }

  return children;
}
