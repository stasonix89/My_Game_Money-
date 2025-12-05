// src/api/axiosClient.ts
import axios from "axios";
import { useAuthStore } from "../store/authStore";

const axiosClient = axios.create({
    baseURL: "http://localhost:8081",
    withCredentials: true,
    headers: {
        "Content-Type": "application/json",
    },
});

// перехватчик ошибок
axiosClient.interceptors.response.use(
    (response) => response,
    (error) => {
        console.error("API Error:", error);

        const status = error?.response?.status;

        if (status === 401) {
            const { logout } = useAuthStore.getState();
            logout();

            if (window.location.pathname !== "/login") {
                window.location.href = "/login";
            }
        }

        return Promise.reject(error);
    }
);

export default axiosClient;
