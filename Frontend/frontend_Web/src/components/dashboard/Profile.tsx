"use client";

import { useState, useEffect } from "react";
import ProfileModal from "@/components/dashboard/ProfileModal"; // Importa el modal
import { getUserProfile, getUserPhoto } from "@/api/services/userApi";

export default function ProfileCard() {
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [userName, setUserName] = useState("Cargando...");
  const [userRole, setUserRole] = useState("Cargando...");
  const [userId, setUserId] = useState<number | null>(null);
  const [userPhoto, setUserPhoto] = useState<string | null>(null);

  useEffect(() => {
    const loadProfile = async () => {
      try {
        const data = await getUserProfile();
        setUserName(data.name);
        setUserRole(data.role || "Usuario");
        setUserId(data.userId);

        // Cargar foto de perfil
        if (data.userId) {
          try {
            console.log("Cargando foto para usuario:", data.userId);
            const photoUrl = await getUserPhoto(data.userId);
            console.log("Foto cargada:", photoUrl);
            setUserPhoto(photoUrl);
          } catch (photoError) {
            console.log("No hay foto de perfil o error cargando:", photoError);
            setUserPhoto(null);
          }
        }
      } catch (error) {
        console.error("Error loading profile:", error);
        setUserName("Error");
        setUserRole("Error");
      }
    };
    loadProfile();
  }, []);

  return (
    <>
      <div
        className="relative w-48 h-50 bg-white mt-5 rounded-xl shadow p-3 text-center cursor-pointer hover:shadow-lg transition-shadow"
        onClick={() => setIsModalOpen(true)} // Abre el modal al hacer clic en toda la tarjeta
      >
        {/* Encabezado */}
        <div className="flex justify-between items-start">
          <h2 className="text-sm font-medium text-gray-700">Perfil</h2>
        </div>

        {/* Imagen de perfil con borde circular */}
        <div className="mt-2 flex flex-col items-center">
          <div className="relative w-20 h-20 rounded-full border-4 border-cyan-400 flex items-center justify-center overflow-hidden">
            <img
              src={userPhoto || "/byte.png"}
              alt="Perfil"
              className="w-full h-full object-cover rounded-full"
              onError={(e) => {
                console.error("Error cargando imagen en Profile:", e);
                const target = e.target as HTMLImageElement;
                target.src = "/byte.png";
              }}
            />
          </div>

          {/* Nombre y rol */}
          <h3 className="mt-3 font-semibold text-gray-800 flex items-center gap-1">
            {userName}
            <span className="w-3 h-3 bg-green-500 rounded-full"></span>
          </h3>
          <p className="text-xs text-gray-500">{userRole}</p>
        </div>
      </div>

      {/* Modal */}
      <ProfileModal
        isOpen={isModalOpen}
        onClose={() => setIsModalOpen(false)}
      />
    </>
  );
}