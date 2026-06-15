import DatePicker from "react-datepicker";
import "react-datepicker/dist/react-datepicker.css";
import NotificationButton from "../NotificationButton";
import { useEffect, useState } from "react";
import axios from "axios";
import { BASE_URL } from "../../utils/request";
import { Sale } from "../../models/sale";

import "./styles.css";

const formatLocalDate = (date: Date) => {
  const year = date.getFullYear();
  const month = String(date.getMonth() + 1).padStart(2, "0");
  const day = String(date.getDate()).padStart(2, "0");
  return `${year}-${month}-${day}`;
};

function SalesCard() {
  const [minDate, setMinDate] = useState(() => {
    const d = new Date();
    d.setDate(d.getDate() - 365 * 5);
    return d;
  });
  const [maxDate, setMaxDate] = useState(() => new Date());

  const [sales, setSales] = useState<Sale[]>([]);

  useEffect(() => {
    const controller = new AbortController();
    const dateMinFormatedToBackend = formatLocalDate(minDate);
    const dateMaxFormatedToBackend = formatLocalDate(maxDate);

    axios.get(`${BASE_URL}/sales?minDate=${dateMinFormatedToBackend}&maxDate=${dateMaxFormatedToBackend}`, {
      signal: controller.signal
    })
    .then((response) => {
      setSales(response.data.content);
    })
    .catch((error) => {
      if (!axios.isCancel(error)) {
        console.error("Error fetching sales data:", error);
      }
    });

    return () => {
      controller.abort();
    };
  }, [minDate, maxDate]);

  return (
    <>
      <div className="dsmeta-card">
        <h2 className="dsmeta-sales-title">Vendas</h2>
        <div className="dsmeta-date-inputs-container">
          <div className="dsmeta-form-control-container">
            <DatePicker
              selected={minDate}
              onChange={(date: Date) => setMinDate(date)}
              className="dsmeta-form-control"
              dateFormat="dd/MM/yyyy"
            />
          </div>
          <div className="dsmeta-form-control-container">
            <DatePicker
              selected={maxDate}
              onChange={(date: Date) => setMaxDate(date)}
              className="dsmeta-form-control"
              dateFormat="dd/MM/yyyy"
            />
          </div>
        </div>

        <div className="dsmeta-sales-table-container">
          <table className="dsmeta-sales-table">
            <thead>
              <tr>
                <th className="show992">ID</th>
                <th>Data</th>
                <th>Vendedor</th>
                <th className="show992">Visitas</th>
                <th className="show992">Vendas</th>
                <th>Total</th>
                <th>Notificar</th>
              </tr>
            </thead>
            <tbody>
              {sales.map((sale) => {
                return (
                  <tr key={sale.id} >
                    <td className="show992">#{sale.id}</td>
                    <td>{new Date(sale.date).toLocaleDateString() }</td>
                    <td>{sale.sellerName}</td>
                    <td className="show992">{sale.visited}</td>
                    <td className="show992">{sale.deals}</td>
                    <td>R$ {sale.amount.toFixed(2)}</td>
                    <td>
                      <div className="dsmeta-red-btn-container">
                        <NotificationButton saleId={sale.id}/>
                      </div>
                    </td>
                  </tr>
                );
              })}
            </tbody>
          </table>
        </div>
      </div>
    </>
  );
}

export default SalesCard;
