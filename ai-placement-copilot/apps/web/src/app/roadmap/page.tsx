import { CalendarDays, CheckCircle2, CircleDotDashed, Target, type LucideIcon } from "lucide-react";
import { AppShell } from "@/components/app-shell";
import { GlassCard } from "@/components/glass-card";

const weeks = [
  { title: "Week 1", theme: "Resume, ATS, proof of work", tasks: ["Rewrite top 5 bullets", "Index project docs", "Ship GitHub README polish"] },
  { title: "Week 2", theme: "DSA fluency", tasks: ["Arrays and hashing", "Trees and graphs", "Complexity explanation drills"] },
  { title: "Week 3", theme: "System and GenAI design", tasks: ["RAG architecture", "Agent orchestration", "Observability and evals"] },
  { title: "Week 4", theme: "Interview conversion", tasks: ["Behavioral STAR bank", "Company-specific mock loops", "Offer-readiness review"] }
];

export default function RoadmapPage() {
  return (
    <AppShell active="/roadmap">
      <section className="grid gap-4 xl:grid-cols-[0.8fr_1.2fr]">
        <GlassCard><p className="text-sm text-slate-400">Career Roadmap Agent</p><h2 className="mt-2 text-3xl font-semibold text-white">30-day placement sprint</h2><p className="mt-4 text-sm leading-7 text-slate-300">Generated from resume gaps, interview history, coding patterns, and uploaded knowledge.</p><div className="mt-6 grid gap-3 sm:grid-cols-3"><Signal icon={Target} label="Target" value="Product SDE" /><Signal icon={CalendarDays} label="Cadence" value="2 h/day" /><Signal icon={CheckCircle2} label="Outcome" value="Mock loop ready" /></div></GlassCard>
        <GlassCard><div className="space-y-4">{weeks.map((week, index) => <div key={week.title} className="rounded-[8px] border border-white/10 bg-white/5 p-4"><div className="mb-3 flex items-center gap-3"><div className="flex h-9 w-9 items-center justify-center rounded-[8px] border border-cyan-electric/20 bg-cyan-electric/10 text-cyan-electric"><CircleDotDashed className="h-4 w-4" /></div><div><p className="text-sm text-slate-400">{week.title}</p><h3 className="font-semibold text-white">{week.theme}</h3></div><span className="ml-auto text-xs text-slate-500">Phase {index + 1}</span></div><div className="grid gap-2 md:grid-cols-3">{week.tasks.map((task) => <div key={task} className="rounded-[8px] border border-white/10 bg-slate-950/40 p-3 text-sm text-slate-300">{task}</div>)}</div></div>)}</div></GlassCard>
      </section>
    </AppShell>
  );
}

function Signal({ icon: Icon, label, value }: { icon: LucideIcon; label: string; value: string }) {
  return <div className="rounded-[8px] border border-white/10 bg-white/5 p-3"><Icon className="mb-3 h-5 w-5 text-mint-signal" /><p className="text-xs text-slate-400">{label}</p><p className="mt-1 text-sm font-semibold text-white">{value}</p></div>;
}

