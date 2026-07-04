import { Activity, BrainCircuit, FileText, Flame } from "lucide-react";
import Link from "next/link";
import { AppShell } from "@/components/app-shell";
import { StreamingPanel } from "@/components/ai/streaming-panel";
import { ReadinessChart } from "@/components/charts/readiness-chart";
import { GlassCard } from "@/components/glass-card";
import { StatCard } from "@/components/stat-card";
import { TypingText } from "@/components/typing-text";

const agents = ["Research Agent", "Resume Agent", "Interview Agent", "Coding Agent", "Career Roadmap Agent"];

export default function DashboardPage() {
  return (
    <AppShell active="/dashboard">
      <section className="grid gap-4 xl:grid-cols-[1.35fr_0.65fr]">
        <GlassCard className="relative min-h-[360px] overflow-hidden p-7">
          <p className="text-sm uppercase tracking-[0.28em] text-cyan-electric/80">Placement readiness command center</p>
          <h2 className="mt-5 text-4xl font-semibold leading-tight text-white lg:text-6xl"><span className="gradient-text animate-shimmer">AI agents preparing you</span> for the next offer loop.</h2>
          <p className="mt-5 max-w-2xl text-base leading-7 text-slate-300"><TypingText text="Upload evidence, practice interviews, review code, chat with notes, and let the roadmap agent prioritize your next move." /></p>
          <div className="mt-7 flex flex-wrap gap-3"><Link href="/resume" className="focus-ring rounded-[8px] bg-cyan-electric px-5 py-3 text-sm font-semibold text-slate-950">Analyze resume</Link><Link href="/interview" className="focus-ring rounded-[8px] border border-white/15 px-5 py-3 text-sm text-white hover:bg-white/10">Start mock interview</Link></div>
        </GlassCard>
        <GlassCard><p className="text-sm text-slate-400">Agent mesh</p><h2 className="mb-5 text-2xl font-semibold text-white">Live orchestration</h2><div className="space-y-3">{agents.map((agent, index) => <div key={agent} className="flex items-center justify-between rounded-[8px] border border-white/10 bg-white/5 px-3 py-3"><span className="text-sm text-slate-200">{agent}</span><span className="rounded-full border border-mint-signal/20 bg-mint-signal/10 px-2 py-1 text-xs text-mint-signal">{index === 0 ? "retrieving" : "ready"}</span></div>)}</div></GlassCard>
      </section>
      <section className="grid gap-4 md:grid-cols-2 xl:grid-cols-4">
        <StatCard label="Placement readiness" value="82%" delta="+14 this month" icon={Activity} />
        <StatCard label="ATS compatibility" value="91%" delta="+9 after rewrite" icon={FileText} />
        <StatCard label="Interview confidence" value="78%" delta="+11 in 6 mocks" icon={BrainCircuit} />
        <StatCard label="Daily streak" value="12" delta="3 focus blocks today" icon={Flame} />
      </section>
      <ReadinessChart />
      <section className="grid gap-4 xl:grid-cols-[0.85fr_1.15fr]"><GlassCard><p className="text-sm text-slate-400">Daily AI roadmap</p><h2 className="mb-5 text-xl font-semibold text-white">Prioritized actions</h2>{["Resume ATS pass", "DSA pattern drill", "RAG system design mock", "Behavioral STAR bank"].map((item) => <div key={item} className="mb-3 rounded-[8px] border border-white/10 bg-white/5 p-4 text-sm text-slate-300">{item}</div>)}</GlassCard><StreamingPanel /></section>
    </AppShell>
  );
}

