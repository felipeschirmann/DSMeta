import { describe, it, expect, vi, beforeEach } from "vitest";
import { render, screen, fireEvent, waitFor } from "@testing-library/react";
import NotificationButton from "./index";
import axios from "axios";
import { toast } from "react-toastify";

vi.mock("axios", () => {
  const mockAxios = Object.assign(
    vi.fn(() => Promise.resolve({ data: {} })),
    {
      get: vi.fn(() => Promise.resolve({ data: { content: [] } })),
      isCancel: vi.fn(() => false)
    }
  );
  return {
    default: mockAxios
  };
});

vi.mock("react-toastify", () => ({
  toast: {
    info: vi.fn(),
    error: vi.fn()
  }
}));

describe("NotificationButton Component", () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  it("renders notification button correctly", () => {
    render(<NotificationButton saleId={42} />);
    expect(screen.getByAltText("Notificar")).toBeInTheDocument();
  });

  it("triggers API call and toast message when clicked", async () => {
    render(<NotificationButton saleId={42} />);
    const btn = screen.getByAltText("Notificar").closest(".dsmeta-red-btn");
    expect(btn).toBeInTheDocument();

    if (btn) {
      fireEvent.click(btn);
    }

    await waitFor(() => {
      expect(axios).toHaveBeenCalled();
      expect(toast.info).toHaveBeenCalledWith("SMS Enviado com sucesso!!!");
    });
  });

  it("disables button during loading state to prevent double submits", async () => {
    let resolvePromise: (value: unknown) => void = vi.fn();
    const pendingPromise = new Promise((resolve) => {
      resolvePromise = resolve;
    });
    vi.mocked(axios).mockImplementationOnce(() => pendingPromise as unknown as Promise<import("axios").AxiosResponse>);

    render(<NotificationButton saleId={42} />);
    const btn = screen.getByAltText("Notificar").closest(".dsmeta-red-btn");

    if (btn) {
      fireEvent.click(btn);
      expect(btn).toHaveClass("dsmeta-disabled");

      fireEvent.click(btn);
      expect(axios).toHaveBeenCalledTimes(1);
    }

    resolvePromise({});
    await waitFor(() => {
      expect(btn).not.toHaveClass("dsmeta-disabled");
    });
  });

  it("displays error toast when API request fails", async () => {
    vi.mocked(axios).mockRejectedValueOnce(new Error("API Error"));
    render(<NotificationButton saleId={42} />);
    const btn = screen.getByAltText("Notificar").closest(".dsmeta-red-btn");
    
    if (btn) {
      fireEvent.click(btn);
    }
    
    await waitFor(() => {
      expect(toast.error).toHaveBeenCalledWith("Erro ao enviar a notificação SMS.");
    });
  });
});
