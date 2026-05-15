import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import { db } from "../firebase/config";
import { useAuth } from "../context/AuthContext";
import { collection, addDoc, serverTimestamp } from "firebase/firestore";
import { AlertTriangle, Send, ChevronLeft } from "lucide-react";

const ReportDisease = () => {
  const { user } = useAuth();
  const navigate = useNavigate();
  const [loading, setLoading] = useState(false);
  const [formData, setFormData] = useState({
    animalName: "",
    symptoms: "",
    village: "",
    urgency: "Medium"
  });

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    try {
      await addDoc(collection(db, "reports"), {
        ...formData,
        ownerUid: user.uid,
        createdAt: serverTimestamp(),
        status: "Pending"
      });
      alert("Report submitted successfully. A vet will review it shortly.");
      navigate("/");
    } catch (error) {
      console.error("Error submitting report:", error);
      alert("Failed to submit report. Please try again.");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="max-w-2xl mx-auto">
      <button
        onClick={() => navigate("/")}
        className="flex items-center gap-2 text-slate-500 hover:text-slate-800 mb-6 transition-colors"
      >
        <ChevronLeft size={20} />
        Back to Dashboard
      </button>

      <div className="bg-white rounded-3xl shadow-xl shadow-slate-200 border border-slate-100 overflow-hidden">
        <div className="bg-red-600 p-6 text-white flex items-center gap-3">
          <AlertTriangle size={24} />
          <div>
            <h1 className="text-xl font-bold">Report Sick Animal</h1>
            <p className="text-red-100 text-sm">Alert local veterinary services immediately.</p>
          </div>
        </div>

        <form onSubmit={handleSubmit} className="p-8 space-y-6">
          <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
            <div>
              <label className="block text-sm font-semibold text-slate-700 mb-2">Animal Name / Tag</label>
              <input
                type="text" required
                className="w-full px-4 py-3 bg-slate-50 border border-slate-200 rounded-xl focus:ring-2 focus:ring-red-500 outline-none transition-all"
                placeholder="e.g. Gauri / Tag #123"
                value={formData.animalName}
                onChange={(e) => setFormData({...formData, animalName: e.target.value})}
              />
            </div>
            <div>
              <label className="block text-sm font-semibold text-slate-700 mb-2">Village / Location</label>
              <input
                type="text" required
                className="w-full px-4 py-3 bg-slate-50 border border-slate-200 rounded-xl focus:ring-2 focus:ring-red-500 outline-none transition-all"
                placeholder="e.g. Palhalli"
                value={formData.village}
                onChange={(e) => setFormData({...formData, village: e.target.value})}
              />
            </div>
          </div>

          <div>
            <label className="block text-sm font-semibold text-slate-700 mb-2">Urgency Level</label>
            <select
              className="w-full px-4 py-3 bg-slate-50 border border-slate-200 rounded-xl focus:ring-2 focus:ring-red-500 outline-none transition-all"
              value={formData.urgency}
              onChange={(e) => setFormData({...formData, urgency: e.target.value})}
            >
              <option>Low (Routine Check)</option>
              <option>Medium (Active Symptoms)</option>
              <option>High (Emergency / Critical)</option>
            </select>
          </div>

          <div>
            <label className="block text-sm font-semibold text-slate-700 mb-2">Symptoms Observed</label>
            <textarea
              required
              rows="4"
              className="w-full px-4 py-3 bg-slate-50 border border-slate-200 rounded-xl focus:ring-2 focus:ring-red-500 outline-none transition-all resize-none"
              placeholder="Describe what is wrong with the animal clearly..."
              value={formData.symptoms}
              onChange={(e) => setFormData({...formData, symptoms: e.target.value})}
            ></textarea>
          </div>

          <button
            type="submit"
            disabled={loading}
            className="w-full bg-red-600 hover:bg-red-700 disabled:bg-slate-300 text-white font-bold py-4 rounded-xl transition-all shadow-lg shadow-red-100 flex items-center justify-center gap-2"
          >
            {loading ? "Submitting Report..." : "Submit Emergency Report"}
            {!loading && <Send size={20} />}
          </button>
        </form>
      </div>
    </div>
  );
};

export default ReportDisease;
