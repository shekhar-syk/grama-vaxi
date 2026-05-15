import React, { useState, useEffect } from "react";
import { db } from "../firebase/config";
import { useAuth } from "../context/AuthContext";
import {
  collection,
  query,
  where,
  onSnapshot,
  addDoc,
  deleteDoc,
  doc,
  serverTimestamp
} from "firebase/firestore";
import {
  Plus,
  Trash2,
  Calendar,
  Activity,
  Info,
  Clock,
  CheckCircle2,
  AlertCircle
} from "lucide-react";
import { format, fromUnixTime, addMonths } from "date-fns";

const Dashboard = () => {
  const { user } = useAuth();
  const [animals, setAnimals] = useState([]);
  const [loading, setLoading] = useState(true);
  const [showAddModal, setShowAddModal] = useState(false);

  // Form State
  const [formData, setFormData] = useState({
    name: "",
    type: "Cow",
    breed: "",
    ageMonths: "",
    village: "",
  });

  useEffect(() => {
    if (!user) return;

    // Listen to animals collection for the current user
    const q = query(collection(db, "animals"), where("ownerUid", "==", user.uid));

    const unsubscribe = onSnapshot(q, (snapshot) => {
      const animalList = snapshot.docs.map(doc => ({
        id: doc.id,
        ...doc.data()
      }));
      setAnimals(animalList);
      setLoading(false);
    });

    return () => unsubscribe();
  }, [user]);

  const handleAddAnimal = async (e) => {
    e.preventDefault();
    try {
      const nextVaccineDate = new Date();
      nextVaccineDate.setMonth(nextVaccineDate.getMonth() + 6);

      await addDoc(collection(db, "animals"), {
        ...formData,
        ownerUid: user.uid,
        ageMonths: parseInt(formData.ageMonths),
        nextVaccineEpochDay: Math.floor(nextVaccineDate.getTime() / (24 * 60 * 60 * 1000)),
        updatedAt: serverTimestamp(),
        synced: true
      });

      setShowAddModal(false);
      setFormData({ name: "", type: "Cow", breed: "", ageMonths: "", village: "" });
    } catch (error) {
      console.error("Error adding animal:", error);
    }
  };

  const deleteAnimal = async (id) => {
    if (window.confirm("Are you sure you want to remove this animal record?")) {
      try {
        await deleteDoc(doc(db, "animals", id));
      } catch (error) {
        console.error("Error deleting animal:", error);
      }
    }
  };

  const getUpcomingVaccinesCount = () => {
    const todayEpoch = Math.floor(Date.now() / (24 * 60 * 60 * 1000));
    return animals.filter(a => a.nextVaccineEpochDay <= todayEpoch + 7).length;
  };

  if (loading) return (
    <div className="flex justify-center items-center h-64">
      <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-green-600"></div>
    </div>
  );

  return (
    <div className="space-y-8">
      {/* Header & Stats */}
      <div className="flex flex-col md:flex-row justify-between items-start md:items-center gap-4">
        <div>
          <h1 className="text-3xl font-bold text-slate-900">Farmer Dashboard</h1>
          <p className="text-slate-500">Welcome back! Here is your livestock overview.</p>
        </div>
        <button
          onClick={() => setShowAddModal(true)}
          className="flex items-center gap-2 bg-green-600 hover:bg-green-700 text-white px-6 py-3 rounded-xl font-bold transition-all shadow-lg shadow-green-100"
        >
          <Plus size={20} />
          Add New Animal
        </button>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
        <StatCard
          title="Total Livestock"
          value={animals.length}
          icon={<Activity className="text-blue-600" />}
          color="bg-blue-50"
        />
        <StatCard
          title="Upcoming Vaccines"
          value={getUpcomingVaccinesCount()}
          icon={<Clock className="text-orange-600" />}
          color="bg-orange-50"
          subtitle="Next 7 days"
        />
        <StatCard
          title="Health Status"
          value="Good"
          icon={<CheckCircle2 className="text-green-600" />}
          color="bg-green-50"
        />
      </div>

      {/* Animal List */}
      <div className="bg-white rounded-2xl border border-slate-200 overflow-hidden shadow-sm">
        <div className="px-6 py-4 border-b border-slate-100 flex justify-between items-center">
          <h2 className="font-bold text-lg text-slate-800">Your Animals</h2>
          <span className="text-sm text-slate-500">{animals.length} registered</span>
        </div>

        {animals.length === 0 ? (
          <div className="p-12 text-center">
            <div className="bg-slate-50 w-16 h-16 rounded-full flex items-center justify-center mx-auto mb-4">
              <Info className="text-slate-400" />
            </div>
            <p className="text-slate-500">No animals registered yet. Click "Add New Animal" to start.</p>
          </div>
        ) : (
          <div className="overflow-x-auto">
            <table className="w-full text-left">
              <thead>
                <tr className="bg-slate-50 text-slate-500 text-sm uppercase tracking-wider">
                  <th className="px-6 py-4 font-semibold">Animal Name</th>
                  <th className="px-6 py-4 font-semibold">Type / Breed</th>
                  <th className="px-6 py-4 font-semibold">Village</th>
                  <th className="px-6 py-4 font-semibold">Next Vaccination</th>
                  <th className="px-6 py-4 font-semibold text-right">Actions</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-slate-100">
                {animals.map((animal) => (
                  <tr key={animal.id} className="hover:bg-slate-50 transition-colors">
                    <td className="px-6 py-4">
                      <div className="font-bold text-slate-900">{animal.name}</div>
                      <div className="text-xs text-slate-500">ID: {animal.id.slice(-6).toUpperCase()}</div>
                    </td>
                    <td className="px-6 py-4">
                      <span className="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-green-100 text-green-800">
                        {animal.type}
                      </span>
                      <div className="text-sm text-slate-600 mt-1">{animal.breed}</div>
                    </td>
                    <td className="px-6 py-4 text-slate-600">{animal.village}</td>
                    <td className="px-6 py-4">
                      <div className="flex items-center gap-2 text-sm">
                        <Calendar size={14} className="text-slate-400" />
                        <span className="font-medium">
                          {format(fromUnixTime(animal.nextVaccineEpochDay * 24 * 60 * 60), "MMM dd, yyyy")}
                        </span>
                      </div>
                    </td>
                    <td className="px-6 py-4 text-right">
                      <button
                        onClick={() => deleteAnimal(animal.id)}
                        className="text-slate-400 hover:text-red-600 transition-colors p-2"
                      >
                        <Trash2 size={18} />
                      </button>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
      </div>

      {/* Add Animal Modal */}
      {showAddModal && (
        <div className="fixed inset-0 bg-slate-900/50 backdrop-blur-sm z-[60] flex items-center justify-center p-4">
          <div className="bg-white rounded-3xl w-full max-w-lg shadow-2xl overflow-hidden">
            <div className="px-8 py-6 border-b border-slate-100 flex justify-between items-center">
              <h2 className="text-xl font-bold text-slate-900">Add New Animal</h2>
              <button onClick={() => setShowAddModal(false)} className="text-slate-400 hover:text-slate-600">✕</button>
            </div>

            <form onSubmit={handleAddAnimal} className="p-8 space-y-4">
              <div className="grid grid-cols-2 gap-4">
                <div className="col-span-2">
                  <label className="block text-sm font-semibold text-slate-700 mb-1">Animal Name</label>
                  <input
                    type="text" required
                    className="w-full px-4 py-2.5 bg-slate-50 border border-slate-200 rounded-xl focus:ring-2 focus:ring-green-500 outline-none"
                    value={formData.name}
                    onChange={(e) => setFormData({...formData, name: e.target.value})}
                  />
                </div>
                <div>
                  <label className="block text-sm font-semibold text-slate-700 mb-1">Type</label>
                  <select
                    className="w-full px-4 py-2.5 bg-slate-50 border border-slate-200 rounded-xl focus:ring-2 focus:ring-green-500 outline-none"
                    value={formData.type}
                    onChange={(e) => setFormData({...formData, type: e.target.value})}
                  >
                    <option>Cow</option>
                    <option>Buffalo</option>
                    <option>Goat</option>
                    <option>Sheep</option>
                  </select>
                </div>
                <div>
                  <label className="block text-sm font-semibold text-slate-700 mb-1">Breed</label>
                  <input
                    type="text" required
                    className="w-full px-4 py-2.5 bg-slate-50 border border-slate-200 rounded-xl focus:ring-2 focus:ring-green-500 outline-none"
                    value={formData.breed}
                    onChange={(e) => setFormData({...formData, breed: e.target.value})}
                  />
                </div>
                <div>
                  <label className="block text-sm font-semibold text-slate-700 mb-1">Age (Months)</label>
                  <input
                    type="number" required
                    className="w-full px-4 py-2.5 bg-slate-50 border border-slate-200 rounded-xl focus:ring-2 focus:ring-green-500 outline-none"
                    value={formData.ageMonths}
                    onChange={(e) => setFormData({...formData, ageMonths: e.target.value})}
                  />
                </div>
                <div>
                  <label className="block text-sm font-semibold text-slate-700 mb-1">Village</label>
                  <input
                    type="text" required
                    className="w-full px-4 py-2.5 bg-slate-50 border border-slate-200 rounded-xl focus:ring-2 focus:ring-green-500 outline-none"
                    value={formData.village}
                    onChange={(e) => setFormData({...formData, village: e.target.value})}
                  />
                </div>
              </div>

              <button
                type="submit"
                className="w-full bg-green-600 hover:bg-green-700 text-white font-bold py-3 rounded-xl mt-4 transition-all"
              >
                Register Animal
              </button>
            </form>
          </div>
        </div>
      )}
    </div>
  );
};

const StatCard = ({ title, value, icon, color, subtitle }) => (
  <div className={`p-6 rounded-2xl ${color} border border-white shadow-sm`}>
    <div className="flex justify-between items-start">
      <div>
        <p className="text-slate-500 text-sm font-medium">{title}</p>
        <h3 className="text-3xl font-bold text-slate-900 mt-1">{value}</h3>
        {subtitle && <p className="text-xs text-slate-400 mt-1">{subtitle}</p>}
      </div>
      <div className="bg-white p-2 rounded-xl shadow-sm">
        {icon}
      </div>
    </div>
  </div>
);

export default Dashboard;
