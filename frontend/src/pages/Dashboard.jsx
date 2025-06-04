import Header from "../components/Header";
import { useAuth } from "../hooks/useAuth";

export default function Dashboard() {
  const { roles } = useAuth();

  return (
    <div>
      <Header />
      <main className="p-6">
        <h2 className="text-xl font-bold mb-2">Добро пожаловать!</h2>
        <h3 className="text-xl font-bold mb-2">Клиника имени доктора Крюкова</h3>
        <p className="text-gray-600">Ваша роль: {roles.join(", ")}</p>
      </main>
    </div>
  );
}
