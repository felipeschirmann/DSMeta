import { describe, it, expect } from "vitest";
import { render, screen } from "@testing-library/react";
import Header from "./index";

describe("Header Component", () => {
  it("renders logo and header title correctly", () => {
    render(<Header />);
    expect(screen.getByText("DSMeta")).toBeInTheDocument();
    expect(screen.getByAltText("DSMeta")).toBeInTheDocument();
  });

  it("renders author link correctly", () => {
    render(<Header />);
    const link = screen.getByRole("link", { name: "@felipeschirmann" });
    expect(link).toBeInTheDocument();
    expect(link).toHaveAttribute("href", "https://github.com/felipeschirmann");
  });
});
