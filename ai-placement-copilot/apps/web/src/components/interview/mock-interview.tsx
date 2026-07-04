"use client";

import { FormEvent, useState } from "react";
import { Mic2, Send, Video } from "lucide-react";
import { toast } from "sonner";
import { GlassCard } from "@/components/glass-card";
import { TypingText } from "@/components/typing-text";
import { api } from "@/lib/api";
import type { InterviewEvaluation } from "@/types/api";

const modes = ["SDE", "Data Analyst", "DevOps", "GenAI Engineer"];

export function MockInterview() {
  const [mode, setMode] = useState("GenAI Engineer");
  const [sessionId, setSessionId] = useState("local-demo");
  const [answer, setAnswer] = useState("");
  const [turns, setTurns] = useState([{ role: "assistant", content: "Design a RAG pipeline with hallucination controls." }]);
  const [evaluation, setEvaluation] = useState<InterviewEvaluation>({
    confidenceScore: 84,
    communicationScore: 78,
    technicalScore: 81,
    feedback: ["Structure answers with problem, architecture, tradeoffs, and metrics.", "Mention retrieval evaluation and citation confidence thresholds."],
    nextQuestion: "How would you evaluate retrieval quality before shipping?"
  });

  async function start() {
    try {
      const response = await api.startInterview({ mode });
      setSessionId(response.sessionId);
      setTurns([{ role: "assistant", content: response.openingQuestion }]);
    } catch (error) {
      toast.error(error instanceof Error ? error.message : "Could not start interview");
    }
  }

  async function submit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    if (!answer.trim()) return;
    const nextTurns = [...turns, { role: "student", content: answer }];
    setTurns(nextTurns);
    setAnswer("");
    try {
      const result = await api.evaluateInterviewAnswer({ sessionId, answer, transcript: nextTurns.map((turn) => `${turn.role}: ${turn.content}`) });
      setEvaluation(result);
      setTurns([...nextTurns, { role: "assistant", content: result.nextQuestion }]);
    } catch (error) {
      toast.error(error instanceof Error ? error.message : "Evaluation failed");
    }
  }

  return (
    <div className="grid gap-4 xl:grid-cols-[1.15fr_0.85fr]">
      <GlassCard className="flex min-h-[680px] flex-col">
        <div className="mb-5 flex flex-wrap items-center justify-between gap-3">
          <div><p className="text-sm text-slate-400">AI Interview Agent</p><h2 className="text-2xl font-semibold text-white">Adaptive mock interview</h2></div>
          <div className="flex flex-wrap gap-2">{modes.map((item) => <button key={item} onClick={() => setMode(item)} className={`focus-ring rounded-[8px] border px-3 py-2 text-xs ${mode === item ? "border-cyan-electric bg-cyan-electric/15 text-cyan-50" : "border-white/10 text-slate-300 hover:bg-white/10"}`}>{item}</button>)}</div>
        </div>
        <div className="mb-4 flex gap-2">
          <button onClick={start} className="focus-ring inline-flex items-center gap-2 rounded-[8px] bg-cyan-electric px-4 py-2 text-sm font-semibold text-slate-950"><Mic2 className="h-4 w-4" />Start session</button>
          <button className="focus-ring inline-flex items-center gap-2 rounded-[8px] border border-white/10 px-4 py-2 text-sm text-slate-200"><Video className="h-4 w-4" />Webcam optional</button>
        </div>
        <div className="scrollbar-thin flex-1 space-y-3 overflow-y-auto rounded-[8px] border border-white/10 bg-slate-950/50 p-4">
          {turns.map((turn, index) => <div key={`${turn.role}-${index}`} className={`max-w-[86%] rounded-[8px] px-4 py-3 text-sm leading-6 ${turn.role === "assistant" ? "border border-cyan-electric/15 bg-cyan-electric/10 text-cyan-50" : "ml-auto border border-mint-signal/15 bg-mint-signal/10 text-emerald-50"}`}>{index === turns.length - 1 && turn.role === "assistant" ? <TypingText text={turn.content} speed={12} /> : turn.content}</div>)}
        </div>
        <form onSubmit={submit} className="mt-4 flex gap-3">
          <textarea value={answer} onChange={(event) => setAnswer(event.target.value)} className="focus-ring min-h-16 flex-1 rounded-[8px] border border-white/10 bg-white/10 p-3 text-sm text-white" />
          <button className="focus-ring inline-flex w-14 items-center justify-center rounded-[8px] bg-mint-signal text-slate-950"><Send className="h-5 w-5" /></button>
        </form>
      </GlassCard>
      <GlassCard>
        <p className="text-sm text-slate-400">Real-time evaluation</p><h2 className="mb-5 text-2xl font-semibold text-white">Signal board</h2>
        <Score label="Confidence" value={evaluation.confidenceScore} />
        <Score label="Communication" value={evaluation.communicationScore} />
        <Score label="Technical depth" value={evaluation.technicalScore} />
        <div className="mt-6 space-y-2">{evaluation.feedback.map((item) => <p key={item} className="rounded-[8px] border border-white/10 bg-slate-950/40 p-3 text-sm leading-6 text-slate-300">{item}</p>)}</div>
      </GlassCard>
    </div>
  );
}

function Score({ label, value }: { label: string; value: number }) {
  return <div className="mb-4"><div className="mb-2 flex justify-between text-sm"><span className="text-slate-300">{label}</span><span className="text-white">{value}%</span></div><div className="h-2 rounded-full bg-white/10"><div className="h-full rounded-full bg-gradient-to-r from-cyan-electric to-mint-signal" style={{ width: `${value}%` }} /></div></div>;
}

