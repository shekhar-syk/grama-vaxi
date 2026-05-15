import React, { useState, useEffect } from "react";
import { db } from "../firebase/config";
import { collection, onSnapshot, query, orderBy } from "firebase/firestore";
import { Users, Stethoscope, MapPin, AlertTriangle } from "lucide-react";

const AdminPanel = () => {
  const [reports, setReports] = useState([]);
  const [animals, setAnimals] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const qReports = query(collection(db, "reports"), orderBy("createdAt", "desc"));
    const unsubscribeReports = onSnapshot(qReports, (snapshot) => {
      setReports(snapshot.docs.map(doc => ({ id: doc.id, ...doc.data() })));
    });

    const qAnimals = query(collection(db, "animals"));
    const unsubscribeAnimals = onSnapshot(qAnimals, (snapshot) => {
      setAnimals(snapshot.docs.map(doc => ({ id: doc.id, ...doc.data() })));
      setLoading(false);
    });

    return () => {
      unsubscribeReports();
      unsubscribeAnimals();
    };
  }, []);

  return (
    <div className="space-y-8">
      <div className="flex justify-between items-center">
        <h1 className="text-3xl font-bold text-slate-900">Vet & Admin Panel</h1>
        <div className="flex gap-3">
          <div className="bg-white px-4 py-2 rounded-xl border border-slate-200 flex items-center gap-2 shadow-sm">
            <Users size={18} className="text-blue-500" />
            <span className="font-bold">{animals.length}</span>
            <span className="text-slate-500 text-sm">Animals</span>
          </div>
        </div>
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-2 gap-8">
        {/* Sick Animal Reports */}
        <div className="bg-white rounded-2xl border border-slate-200 shadow-sm overflow-hidden">
          <div className="px-6 py-4 border-b border-slate-100 bg-red-50/50 flex items-center gap-2">
            <AlertTriangle className="text-red-600" size={20} />
            <h2 className="font-bold text-slate-800">Disease Reports</h2>
          </div>
          <div className="divide-y divide-slate-100 max-h-[500px] overflow-y-auto">
            {reports.length === 0 ? (
              <p className="p-8 text-center text-slate-500">No active health reports.</p>
            ) : (
              reports.map(report => (
                <div key={report.id} className="p-6 hover:bg-slate-50 transition-colors">
                  <div className="flex justify-between items-start mb-2">
                    <span className="font-bold text-slate-900">{report.animalName}</span>
                    <span className="text-xs font-medium text-slate-400">
                      {report.createdAt?.toDate().toLocaleDateString()}
                    </span>
                  </div>
                  <p className="text-sm text-slate-600 mb-3 bg-white p-3 rounded-lg border border-slate-100">
                    "{report.symptoms}"
                  </p>
                  <div className="flex items-center gap-4 text-xs text-slate-500">
                    <span className="flex items-center gap-1"><MapPin size={12}/> {report.village}</span>
                    <span className="flex items-center gap-1"><Stethoscope size={12}/> Needs Attention</span>
                  </div>
                </div>
              ))
            )}
          </div>
        </div>

        {/* Global Vaccine List */}
        <div className="bg-white rounded-2xl border border-slate-200 shadow-sm overflow-hidden">
          <div className="px-6 py-4 border-b border-slate-100 bg-green-50/50 flex items-center gap-2">
            <Stethoscope className="text-green-600" size={20} />
            <h2 className="font-bold text-slate-800">Global Vaccine Schedule</h2>
          </div>
          <div className="p-0 overflow-x-auto">
            <table className="w-full text-left text-sm">
              <thead className="bg-slate-50 text-slate-500 uppercase">
                <tr>
                  <th className="px-6 py-3">Animal</th>
                  <th className="px-6 py-3">Owner Village</th>
                  <th className="px-6 py-3">Due Date</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-slate-100">
                {animals.slice(0, 10).map(animal => (
                  <tr key={animal.id}>
                    <td className="px-6 py-4 font-medium">{animal.name} ({animal.type})</td>
                    <td className="px-6 py-4">{animal.village}</td>
                    <td className="px-6 py-4 text-orange-600 font-bold">Due Soon</td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </div>
      </div>
    </div>
  );
};

export default AdminPanel;
