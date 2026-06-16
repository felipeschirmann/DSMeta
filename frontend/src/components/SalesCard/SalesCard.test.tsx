import { describe, it, expect, vi, beforeEach } from "vitest";
import { render, screen, waitFor, fireEvent } from "@testing-library/react";
import SalesCard from "./index";
import axios from "axios";

vi.mock("axios", () => {
  const mockAxios = Object.assign(
    vi.fn(() => Promise.resolve({ data: {} })),
    {
      get: vi.fn(() => Promise.resolve({
        data: {
          content: [
            {
              id: 1,
              sellerName: "Anakin",
              visited: 10,
              deals: 8,
              amount: 15000.0,
              date: "2026-06-15"
            }
          ]
        }
      })),
      isCancel: vi.fn(() => false)
    }
  );
  return {
    default: mockAxios
  };
});

describe("SalesCard Component", () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  it("renders card title correctly", async () => {
    render(<SalesCard />);
    expect(screen.getByRole("heading", { name: "Vendas", level: 2 })).toBeInTheDocument();
    await waitFor(() => {
      expect(axios.get).toHaveBeenCalled();
    });
  });

  it("loads and displays sales data on render", async () => {
    render(<SalesCard />);

    await waitFor(() => {
      expect(axios.get).toHaveBeenCalled();
      expect(screen.getByText("Anakin")).toBeInTheDocument();
      expect(screen.getByText("R$ 15000.00")).toBeInTheDocument();
    });
  });

  it("logs error when API request fails", async () => {
    const consoleSpy = vi.spyOn(console, "error").mockImplementation(() => { /* noop */ });
    vi.mocked(axios.get).mockRejectedValueOnce(new Error("Network Error"));
    
    render(<SalesCard />);
    
    await waitFor(() => {
      expect(consoleSpy).toHaveBeenCalledWith("Error fetching sales data:", expect.any(Error));
    });
    consoleSpy.mockRestore();
  });

  it("triggers API fetch when min or max date changes", async () => {
    render(<SalesCard />);
    const inputs = screen.getAllByRole("textbox");
    
    fireEvent.change(inputs[0], { target: { value: "10/06/2026" } });
    fireEvent.change(inputs[1], { target: { value: "20/06/2026" } });

    await waitFor(() => {
      expect(axios.get).toHaveBeenCalled();
    });
  });
});
