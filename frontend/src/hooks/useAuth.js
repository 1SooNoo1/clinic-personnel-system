export function useAuth() {
  const token = localStorage.getItem("token");
  const roles = localStorage.getItem("roles");
  const fullName = localStorage.getItem("fullName");

  return {
    isAuthenticated: !!token,
    roles: roles ? roles.split(",") : [],
    fullName,
  };
}
