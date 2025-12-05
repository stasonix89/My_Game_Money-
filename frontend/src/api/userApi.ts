// src/api/userApi.ts
import axiosClient from "./axiosClient";
import type { AuthUser } from "../store/authStore";

export type LoginRequest = {
    username: string;
    password: string;
    rememberMe: boolean;
};

export type LoginResponse = {
    user: AuthUser;
};

export const userApi = {
    async login(data: LoginRequest): Promise<AuthUser> {
        const response = await axiosClient.post<LoginResponse>("/api/auth/login", data, {
            withCredentials: true, // важно для session + remember-me cookie
        });
        return response.data.user;
    },

    async logout(): Promise<void> {
        await axiosClient.post(
            "/api/auth/logout",
            {},
            {
                withCredentials: true,
            }
        );
    },

    async getCurrentUser(): Promise<AuthUser> {
        const response = await axiosClient.get<AuthUser>("/api/auth/me", {
            withCredentials: true,
        });
        return response.data;
    },
};
