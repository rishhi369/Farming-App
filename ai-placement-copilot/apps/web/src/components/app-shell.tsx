import { Binary, BrainCircuit, FileText, Home, Map, Mic2, Network } from "lucide-react";
import Link from "next/link";
import type { ReactNode } from "react";
import { AuthStatus } from "@/components/auth-status";
import { cn } from "@/lib/utils";

const navigation = [
  { href: "/dashboard", label: "Command", icon: Home },
  { href: "/resume", label: "Resume", icon: FileText },
  { href: "/interview", label: "Interview", icon: Mic2 },
  { href: "/coding", label: "Coding", icon: Binary },
  { href: "/knowledge", label: "RAG", icon: BrainCircuit },
  { href: "/roadmap", label: "Roadmap", icon: Map }
];

export function AppShell({ children, active }: { children: ReactNode; active: string }) {
  return (
    <main className="relative min-h-screen overflow-hidden bg-mesh-grid bg-[length:42px_42px]">
      <div className="relative mx-auto flex min-h-screen w-full max-w-[1480px] gap-4 px-4 py-4 lg:px-6">
        <aside className="glass sticky top-4 hidden h-[calc(100vh-2rem)] w-72 flex-col rounded-[8px] p-4 lg:flex">
          <Link href="/" className="mb-8 flex items-center gap-3 px-2">
            <div className="flex h-10 w-10 items-center justify-center rounded-[8px] border border-cyan-electric/30 bg-cyan-electric/10">
              <Network className="h-5 w-5 text-cyan-electric" />
            </div>
            <div>
              <p className="text-sm font-semibold text-white">AI Placement</p>
              <p className="text-xs text-slate-400">Copilot OS</p>
            </div>
          </Link>
          <nav className="space-y-2">
            {navigation.map((item) => {
              const Icon = item.icon;
              const selected = item.href === active;
              return (
                <Link
                  key={item.href}
                  href={item.href}
                  className={cn(
                    "focus-ring flex items-center gap-3 rounded-[8px] px-3 py-3 text-sm transition",
                    selected ? "bg-cyan-electric/15 text-cyan-50 shadow-glow" : "text-slate-300 hover:bg-white/10 hover:text-white"
                  )}
                >
                  <Icon className="h-4 w-4" />
                  {item.label}
                </Link>
              );
            })}
          </nav>
          <div className="mt-auto rounded-[8px] border border-mint-signal/20 bg-mint-signal/10 p-4 text-xs leading-5 text-slate-300">
            Multi-agent evidence, RAG-backed answers, code traces, and interview analytics are ready for a portfolio demo.
          </div>
        </aside>
        <section className="flex min-w-0 flex-1 flex-col gap-4">
          <header className="glass flex items-center justify-between rounded-[8px] px-4 py-3 lg:px-5">
            <div>
              <p className="text-xs uppercase tracking-[0.24em] text-cyan-electric/80">Multi-agent placement intelligence</p>
              <h1 className="mt-1 text-xl font-semibold text-white lg:text-2xl">AI Placement Copilot</h1>
            </div>
            <AuthStatus />
          </header>
          {children}
        </section>
      </div>
    </main>
  );
}

