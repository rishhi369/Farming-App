"use client";

import { Area, AreaChart, Bar, BarChart, CartesianGrid, ResponsiveContainer, Tooltip, XAxis, YAxis } from "recharts";
import { GlassCard } from "@/components/glass-card";

const readiness = [
  { week: "W1", score: 48 },
  { week: "W2", score: 56 },
  { week: "W3", score: 61 },
  { week: "W4", score: 68 },
  { week: "W5", score: 76 },
  { week: "W6", score: 82 }
];
const gaps = [
  { skill: "DSA", current: 72, target: 90 },
  { skill: "System", current: 58, target: 84 },
  { skill: "SQL", current: 65, target: 82 },
  { skill: "GenAI", current: 76, target: 88 }
];

export function ReadinessChart() {
  return (
    <div className="grid gap-4 xl:grid-cols-[1.3fr_1fr]">
      <GlassCard>
        <p className="text-sm text-slate-400">Readiness trajectory</p>
        <h2 className="mb-5 text-lg font-semibold text-white">Adaptive weekly score</h2>
        <div className="h-72">
          <ResponsiveContainer width="100%" height="100%">
            <AreaChart data={readiness}>
              <CartesianGrid stroke="rgba(148,163,184,0.12)" vertical={false} />
              <XAxis dataKey="week" stroke="#94a3b8" tickLine={false} axisLine={false} />
              <YAxis stroke="#94a3b8" tickLine={false} axisLine={false} />
              <Tooltip contentStyle={{ background: "rgba(15,23,42,.92)", border: "1px solid rgba(91,231,255,.18)", borderRadius: 8 }} />
              <Area type="monotone" dataKey="score" stroke="#5be7ff" fill="rgba(91,231,255,.18)" strokeWidth={3} />
            </AreaChart>
          </ResponsiveContainer>
        </div>
      </GlassCard>
      <GlassCard>
        <p className="text-sm text-slate-400">Skill gap analysis</p>
        <h2 className="mb-5 text-lg font-semibold text-white">Current vs target</h2>
        <div className="h-72">
          <ResponsiveContainer width="100%" height="100%">
            <BarChart data={gaps} layout="vertical" margin={{ left: 16 }}>
              <CartesianGrid stroke="rgba(148,163,184,0.12)" horizontal={false} />
              <XAxis type="number" hide domain={[0, 100]} />
              <YAxis type="category" dataKey="skill" stroke="#cbd5e1" axisLine={false} tickLine={false} />
              <Tooltip contentStyle={{ background: "rgba(15,23,42,.92)", border: "1px solid rgba(132,248,199,.18)", borderRadius: 8 }} />
              <Bar dataKey="target" fill="rgba(148,163,184,.22)" radius={[4, 4, 4, 4]} />
              <Bar dataKey="current" fill="#84f8c7" radius={[4, 4, 4, 4]} />
            </BarChart>
          </ResponsiveContainer>
        </div>
      </GlassCard>
    </div>
  );
}

