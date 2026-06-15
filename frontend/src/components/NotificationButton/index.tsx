import axios from "axios";
import icon from "../../assets/img/notification-icon.svg";
import { BASE_URL } from "../../utils/request";
import { toast } from "react-toastify";
import { useState } from "react";

import "./styles.css";

type Props = {
  saleId: number;
};

function NotificationButton({ saleId }: Props) {
  const [loading, setLoading] = useState(false);

  const handleClick = () => {
    if (loading) return;

    setLoading(true);
    axios(`${BASE_URL}/sales/${saleId}/notification`)
      .then(() => {
        toast.info("SMS Enviado com sucesso!!!");
      })
      .catch((error) => {
        console.error("Erro ao enviar notificação:", error);
        toast.error("Erro ao enviar a notificação SMS.");
      })
      .finally(() => {
        setLoading(false);
      });
  };

  return (
    <div className="dsmeta-red-btn-container">
      <div
        className={`dsmeta-red-btn ${loading ? "dsmeta-disabled" : ""}`}
        onClick={handleClick}
        style={{
          opacity: loading ? 0.6 : 1,
          pointerEvents: loading ? "none" : "initial"
        }}
      >
        <img
          src={icon}
          alt="Notificar"
          style={{
            animation: loading ? "spin 1s linear infinite" : "none"
          }}
        />
      </div>
    </div>
  );
}

export default NotificationButton;
