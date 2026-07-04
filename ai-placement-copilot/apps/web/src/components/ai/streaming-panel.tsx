"use client";

import { FormEvent, useState } from "react";
import { Bot, Send } from "lucide-react";
import { toast } from "sonner";
import { streamAssistant } from "@/lib/api";

export function StreamingPanel() {
  const [prompt, setPrompt] = useState("Create a 7-day plan to improve my SDE placement readiness.");
  const [response, setResponse] = useState("");
  const [loading, setLoading] = useState(false);

  async function onSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    setResponse("");
    setLoading(true);
    try {
      for await (const chunk of streamAssistant(prompt)) setResponse((current) => current + chunk);
    } catch (error) {
      toast.error(error instanceof Error ? error.message : "Streaming failed");
    } finally {
      setLoading(false);
    }
  }

  return (
    <section className="glass rounded-[8px] p-5">
      <div className="mb-4 flex items-center gap-3">
        <div className="rounded-[8px] border border-mint-signal/25 bg-mint-signal/10 p-2 text-mint-signal">
          <Bot className="h-5 w-5" />
        </div>
        <div>
          <p className="text-sm text-slate-400">Streaming agent console</p>
          <h2 className="text-lg font-semibold text-white">LangGraph career crew</h2>
        </div>
      </div>
      <form onSubmit={onSubmit} className="flex flex-col gap-3 md:flex-row">
        <input value={prompt} onChange={(event) => setPrompt(event.target.value)} className="focus-ring min-h-12 flex-1 rounded-[8px] border border-white/10 bg-white/10 px-4 text-sm text-white" />
        <button disabled={loading} className="focus-ring inline-flex min-h-12 items-center justify-center gap-2 rounded-[8px] bg-cyan-electric px-5 text-sm font-semibold text-slate-950 disabled:opacity-60">
          <Send className="h-4 w-4" />
          {loading ? "Streaming" : "Run"}
        </button>
      </form>
      <div className="scrollbar-thin mt-4 min-h-40 rounded-[8px] border border-white/10 bg-slate-950/50 p-4 text-sm leading-6 text-slate-200">
        {response || <span className="text-slate-500">Agent output appears here with token streaming from FastAPI.</span>}
      </div>
    </section>
  );
}

