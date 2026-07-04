import type { Config } from "tailwindcss";

const config: Config = {
  darkMode: ["class"],
  content: ["./src/**/*.{ts,tsx}"],
  theme: {
    extend: {
      colors: {
        ink: "#09111f",
        panel: "rgba(9, 17, 31, 0.72)",
        cyan: { electric: "#5be7ff" },
        mint: { signal: "#84f8c7" },
        amber: { pulse: "#ffd166" }
      },
      boxShadow: {
        glow: "0 0 60px rgba(91, 231, 255, 0.18)",
        panel: "0 24px 80px rgba(2, 8, 23, 0.42)"
      },
      backgroundImage: {
        "mesh-grid":
          "linear-gradient(rgba(132,248,199,0.07) 1px, transparent 1px), linear-gradient(90deg, rgba(91,231,255,0.08) 1px, transparent 1px)"
      },
      animation: {
        shimmer: "shimmer 2.4s linear infinite"
      },
      keyframes: {
        shimmer: {
          "0%": { backgroundPosition: "0% 50%" },
          "100%": { backgroundPosition: "200% 50%" }
        }
      }
    }
  },
  plugins: []
};

export default config;

