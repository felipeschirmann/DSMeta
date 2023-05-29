import axios from "axios";
import icon from "../../assets/img/notification-icon.svg";
import { BASE_URL } from "../../utils/request";
import { toast } from "react-toastify";

import "./styles.css";

type Props = {
  saleId: number;
};

function handleClick(saleId: number){
  axios(`${BASE_URL}/sales/${saleId}/notification`)
    .then (response => {
      toast.info("SMS Envido com sucesso!!!");
    });
}

function NotificationButton({ saleId }: Props) {
  return (
    <>
      <div className="dsmeta-red-btn-container">
        <div className="dsmeta-red-btn" onClick={() => handleClick(saleId)}>
          <img src={icon} alt="Notificar" />
        </div>
      </div>
    </>
  );
}

export default NotificationButton;
