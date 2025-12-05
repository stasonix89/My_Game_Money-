// src/pages/auth/LoginPage.tsx
import {type FormEvent, useState } from "react";
import { useNavigate } from "react-router-dom";
import { useAuthStore } from "../../store/authStore";
import { userApi } from "../../api/userApi";
import "../../styles/login.css";

export default function LoginPage() {
    const navigate = useNavigate();
    const { setUser, setLoading, setError, isLoading, error } = useAuthStore();

    const [username, setUsername] = useState("stasonix");
    const [password, setPassword] = useState("Dev1ds0ne135$");
    const [rememberMe, setRememberMe] = useState(true);

    const handleSubmit = async (e: FormEvent) => {
        e.preventDefault();
        setError(null);
        setLoading(true);

        try {
            const user = await userApi.login({ username, password, rememberMe });
            setUser(user);
            navigate("/budget/dashboard");
        } catch (err: any) {
            console.error(err);
            setError("Неверный логин или пароль");
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="login-page">
            <div className="login-card">
                <div className="login-logo">MMG</div>
                <h1 className="login-title">Вход в My Game Money</h1>
                <p className="login-subtitle">
                    Используйте root-аккаунт или свой логин и пароль.
                </p>

                <form onSubmit={handleSubmit} className="login-form">
                    <label className="login-field">
                        <span>Логин</span>
                        <input
                            type="text"
                            value={username}
                            autoComplete="username"
                            onChange={(e) => setUsername(e.target.value)}
                        />
                    </label>

                    <label className="login-field">
                        <span>Пароль</span>
                        <input
                            type="password"
                            value={password}
                            autoComplete="current-password"
                            onChange={(e) => setPassword(e.target.value)}
                        />
                    </label>

                    <label className="login-remember">
                        <input
                            type="checkbox"
                            checked={rememberMe}
                            onChange={(e) => setRememberMe(e.target.checked)}
                        />
                        <span>Запомнить меня на этом устройстве</span>
                    </label>

                    {error && <div className="login-error">{error}</div>}

                    <button
                        type="submit"
                        className="login-submit"
                        disabled={isLoading}
                    >
                        {isLoading ? "Входим..." : "Войти"}
                    </button>
                </form>
            </div>
        </div>
    );
}
