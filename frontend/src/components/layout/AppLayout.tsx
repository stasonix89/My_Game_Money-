import { useState, type PropsWithChildren } from "react";
import Navbar from "./Navbar";
import Sidebar from "./Sidebar";
import "../../styles/theme.css";

export default function AppLayout({ children }: PropsWithChildren) {
    const [menuOpen, setMenuOpen] = useState(false);

    return (
        <div className="app-root">
            <Navbar onMenuClick={() => setMenuOpen(true)} />
            <Sidebar open={menuOpen} onClose={() => setMenuOpen(false)} />

            <main className="app-main">
                {children}
            </main>
        </div>
    );
}
