// src/router/PrivateRoute.tsx
import React, {type JSX} from "react";
import { Navigate } from "react-router-dom";
import { useAuthStore } from "../store/authStore";

interface Props {
    children: JSX.Element;
}

const PrivateRoute: React.FC<Props> = ({ children }) => {
    const { isLoggedIn, loading } = useAuthStore();

    if (loading) return null; // можно добавить спиннер

    return isLoggedIn ? children : <Navigate to="/login" replace />;
};

export default PrivateRoute;
