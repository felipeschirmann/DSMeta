import { describe, it, expect, vi } from "vitest";
import { render, screen, waitFor } from "@testing-library/react";
import App from "./App";
import axios from "axios";

vi.mock("axios", () => {
  const mockAxios = Object.assign(
    vi.fn(() => Promise.resolve({ data: {} })),
    {
      get: vi.fn(() => Promise.resolve({
        data: {
          content: []
        }
      })),
      isCancel: vi.fn(() => false)
    }
  );
  return {
    default: mockAxios
  };
});

describe("App Component", () => {
  it("renders main dashboard structure with header and sales card", async () => {
    render(<App />);
    expect(screen.getByText("DSMeta")).toBeInTheDocument();
    expect(screen.getByRole("heading", { name: "Vendas", level: 2 })).toBeInTheDocument();
    await waitFor(() => {
      expect(axios.get).toHaveBeenCalled();
    });
  });
});
