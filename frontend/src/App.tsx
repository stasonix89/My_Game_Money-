// src/App.tsx
import { useEffect } from "react";
import { BrowserRouter } from "react-router-dom";

import AppRouter from "./router/AppRouter";
import AppLayout from "./components/layout/AppLayout";
import axiosClient from "./api/axiosClient";
import { useAuthStore } from "./store/authStore";

function App() {
    const { setUser, setLoading } = useAuthStore();

    useEffect(() => {
        const initAuth = async () => {
            try {
                setLoading(true);
                const res = await axiosClient.get("/api/auth/me");
                setUser(res.data);
            } catch {
                setUser(null);
            } finally {
                setLoading(false);
            }
        };

        void initAuth();
    }, [setUser, setLoading]);

    return (
        <BrowserRouter>
            <AppLayout>
                <AppRouter />
            </AppLayout>
        </BrowserRouter>
    );
}

export default App;
