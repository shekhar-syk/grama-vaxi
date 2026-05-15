import React, { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import { useAuth } from "../context/AuthContext";
import { auth } from "../firebase/config";
import { signOut } from "firebase/auth";
import { LogOut, Home, ShieldCheck, Languages } from "lucide-react";

const Navbar = () => {
  const { user } = useAuth();
  const navigate = useNavigate();
  const [lang, setLang] = useState(localStorage.getItem("lang") || "en");

  const toggleLanguage = () => {
    const newLang = lang === "en" ? "kn" : "en";
    setLang(newLang);
    localStorage.setItem("lang", newLang);
    window.location.reload(); // Refresh to apply translations globally
  };

  const handleLogout = async () => {
    try {
      await signOut(auth);
      navigate("/login");
    } catch (error) {
      console.error("Error logging out:", error);
    }
  };

  return (
    <nav className="bg-white border-b border-slate-200 sticky top-0 z-50">
      <div className="container mx-auto px-4 h-16 flex items-center justify-between">
        <Link to="/" className="flex items-center gap-2">
          <div className="bg-green-600 p-1.5 rounded-lg">
            <ShieldCheck className="text-white w-6 h-6" />
          </div>
          <span className="text-xl font-bold text-slate-900 tracking-tight">Grama-Vaxi Web</span>
        </Link>

        <div className="flex items-center gap-4">
          <button
            onClick={toggleLanguage}
            className="flex items-center gap-1.5 text-slate-600 hover:text-green-600 font-medium px-3 py-1.5 rounded-lg border border-slate-200 hover:border-green-200 transition-all"
          >
            <Languages size={18} />
            <span>{lang === "en" ? "ಕನ್ನಡ" : "English"}</span>
          </button>

          {user && (
            <>
              <Link to="/" className="hidden md:flex text-slate-600 hover:text-green-600 items-center gap-1.5 font-medium transition-colors">
                <Home size={18} />
                <span>Dashboard</span>
              </Link>
              <button
                onClick={handleLogout}
                className="bg-slate-100 hover:bg-red-50 text-slate-600 hover:text-red-600 p-2 rounded-full transition-all"
              >
                <LogOut size={20} />
              </button>
            </>
          )}
        </div>
      </div>
    </nav>
  );
};

export default Navbar;
