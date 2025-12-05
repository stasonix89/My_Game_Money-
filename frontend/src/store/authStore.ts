// src/store/authStore.ts
import { create } from "zustand";

export type AuthUser = {
    id: number;
    username: string;
    displayName: string;
    roles: string[];
};

type AuthState = {
    user: AuthUser | null;
    isLoggedIn: boolean;
    loading: boolean;
    error: string | null;

    setUser: (user: AuthUser | null) => void;
    setLoading: (loading: boolean) => void;
    setError: (error: string | null) => void;

    logout: () => void;
    reset: () => void;
};

export const useAuthStore = create<AuthState>((set) => ({
    user: null,
    isLoggedIn: false,
    loading: true, // при запуске приложения загружаем /api/auth/me
    error: null,

    setUser: (user) =>
        set({
            user,
            isLoggedIn: !!user,
        }),

    setLoading: (loading) => set({ loading }),

    setError: (error) => set({ error }),

    logout: () =>
        set({
            user: null,
            isLoggedIn: false,
            error: null,
        }),

    reset: () =>
        set({
            user: null,
            isLoggedIn: false,
            loading: false,
            error: null,
        }),
}));
