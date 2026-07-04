"use client";

import Editor from "@monaco-editor/react";
import { BrainCircuit, Play } from "lucide-react";
import { useState } from "react";
import { toast } from "sonner";
import { GlassCard } from "@/components/glass-card";
import { api } from "@/lib/api";
import type { CodeReviewResult } from "@/types/api";

const starterCode = `def two_sum(nums, target):
    seen = {}
    for i, num in enumerate(nums):
        need = target - num
        if need in seen:
            return [seen[need], i]
        seen[num] = i
    return []`;

export function CodeWorkbench() {
  const [language, setLanguage] = useState("python");
  const [problem, setProblem] = useState("Given an array of integers, return indices of two numbers that add up to target.");
  const [code, setCode] = useState(starterCode);
  const [review, setReview] = useState<CodeReviewResult | null>({
    verdict: "Strong baseline. The hash map approach is interview-ready.",
    bugs: ["No obvious correctness bug found from static review."],
    optimizations: ["Explain why one-pass hashing avoids O(n^2)."],
    timeComplexity: "O(n)",
    spaceComplexity: "O(n)",
    followUps: ["How would you handle duplicates?", "Can you solve it when the array is sorted?"]
  });

  async function reviewCode() {
    try {
      setReview(await api.reviewCode({ language, problem, code }));
      toast.success("AI code review complete");
    } catch (error) {
      toast.error(error instanceof Error ? error.message : "Code review failed");
    }
  }

  return (
    <div className="grid gap-4 xl:grid-cols-[1.2fr_0.8fr]">
      <GlassCard className="space-y-4">
        <div className="flex flex-wrap items-center justify-between gap-3">
          <div><p className="text-sm text-slate-400">Coding Interview Copilot</p><h2 className="text-2xl font-semibold text-white">Monaco review bench</h2></div>
          <select value={language} onChange={(event) => setLanguage(event.target.value)} className="focus-ring rounded-[8px] border border-white/10 bg-slate-950 px-3 py-2 text-sm text-white">
            <option value="python">Python</option><option value="javascript">JavaScript</option><option value="java">Java</option><option value="cpp">C++</option>
          </select>
        </div>
        <textarea value={problem} onChange={(event) => setProblem(event.target.value)} className="focus-ring min-h-20 w-full rounded-[8px] border border-white/10 bg-white/10 p-3 text-sm text-white" />
        <div className="overflow-hidden rounded-[8px] border border-white/10">
          <Editor height="460px" theme="vs-dark" language={language === "cpp" ? "cpp" : language} value={code} onChange={(value) => setCode(value ?? "")} options={{ minimap: { enabled: false }, fontSize: 14, padding: { top: 16 }, scrollBeyondLastLine: false }} />
        </div>
        <button onClick={reviewCode} className="focus-ring inline-flex items-center gap-2 rounded-[8px] bg-cyan-electric px-5 py-3 text-sm font-semibold text-slate-950"><Play className="h-4 w-4" />Review code</button>
      </GlassCard>
      <GlassCard>
        <div className="mb-5 flex items-center gap-3"><div className="rounded-[8px] border border-cyan-electric/20 bg-cyan-electric/10 p-2 text-cyan-electric"><BrainCircuit className="h-5 w-5" /></div><div><p className="text-sm text-slate-400">Complexity and follow-ups</p><h2 className="text-2xl font-semibold text-white">AI review</h2></div></div>
        {review && <div className="space-y-4"><Panel title="Verdict" items={[review.verdict]} /><Panel title="Bugs" items={review.bugs} /><Panel title="Optimizations" items={review.optimizations} /><Panel title="Complexity" items={[`Time: ${review.timeComplexity}`, `Space: ${review.spaceComplexity}`]} /><Panel title="Follow-up questions" items={review.followUps} /></div>}
      </GlassCard>
    </div>
  );
}

function Panel({ title, items }: { title: string; items: string[] }) {
  return <div className="rounded-[8px] border border-white/10 bg-white/5 p-4"><h3 className="mb-3 text-sm font-semibold text-white">{title}</h3><div className="space-y-2">{items.map((item) => <p key={item} className="text-sm leading-6 text-slate-300">{item}</p>)}</div></div>;
}

