import { ArrowUpRight, type LucideIcon } from "lucide-react";
import { GlassCard } from "@/components/glass-card";

export function StatCard({ label, value, delta, icon: Icon }: { label: string; value: string; delta: string; icon: LucideIcon }) {
  return (
    <GlassCard className="min-h-36">
      <div className="flex items-start justify-between gap-4">
        <div>
          <p className="text-sm text-slate-400">{label}</p>
          <p className="mt-3 text-3xl font-semibold text-white">{value}</p>
        </div>
        <div className="rounded-[8px] border border-cyan-electric/25 bg-cyan-electric/10 p-3 text-cyan-electric">
          <Icon className="h-5 w-5" />
        </div>
      </div>
      <div className="mt-5 inline-flex items-center gap-1 rounded-[8px] border border-mint-signal/20 bg-mint-signal/10 px-2 py-1 text-xs text-mint-signal">
        <ArrowUpRight className="h-3.5 w-3.5" />
        {delta}
      </div>
    </GlassCard>
  );
}

