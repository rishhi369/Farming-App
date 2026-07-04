"use client";

import { useCallback, useState } from "react";
import { useDropzone } from "react-dropzone";
import { FileUp, Sparkles } from "lucide-react";
import { toast } from "sonner";
import { GlassCard } from "@/components/glass-card";
import { api } from "@/lib/api";
import type { ResumeAnalysis } from "@/types/api";

const sample: ResumeAnalysis = {
  atsScore: 82,
  matchedKeywords: ["React", "FastAPI", "PostgreSQL", "RAG", "Docker"],
  missingSkills: ["Kubernetes", "System design", "Observability"],
  suggestions: ["Quantify impact with latency, accuracy, cost, or adoption metrics.", "Move high-signal keywords into the top third.", "Add cloud deployment evidence."],
  optimizedBullets: [
    "Built a multi-agent placement copilot with Next.js, FastAPI, LangGraph, ChromaDB, Redis, and PostgreSQL.",
    "Designed citation-backed RAG workflows with streaming responses, semantic caching hooks, and hallucination guardrails."
  ]
};

export function ResumeAnalyzer() {
  const [jobDescription, setJobDescription] = useState("SDE role requiring React, TypeScript, Python, FastAPI, system design, cloud deployment, and AI/RAG experience.");
  const [file, setFile] = useState<File | null>(null);
  const [analysis, setAnalysis] = useState<ResumeAnalysis | null>(sample);
  const [loading, setLoading] = useState(false);
  const onDrop = useCallback((acceptedFiles: File[]) => setFile(acceptedFiles[0] ?? null), []);
  const { getRootProps, getInputProps, isDragActive } = useDropzone({
    onDrop,
    accept: { "application/pdf": [".pdf"], "application/vnd.openxmlformats-officedocument.wordprocessingml.document": [".docx"] },
    maxFiles: 1
  });

  async function analyze() {
    if (!file) {
      toast.info("Using sample signal until a resume is uploaded.");
      setAnalysis(sample);
      return;
    }
    const formData = new FormData();
    formData.append("file", file);
    formData.append("job_description", jobDescription);
    setLoading(true);
    try {
      setAnalysis(await api.analyzeResume(formData));
      toast.success("Resume analysis complete");
    } catch (error) {
      toast.error(error instanceof Error ? error.message : "Resume analysis failed");
      setAnalysis(sample);
    } finally {
      setLoading(false);
    }
  }

  return (
    <div className="grid gap-4 xl:grid-cols-[0.95fr_1.05fr]">
      <GlassCard className="space-y-4">
        <div>
          <p className="text-sm text-slate-400">Resume Analyzer Agent</p>
          <h2 className="text-2xl font-semibold text-white">ATS + recruiter fit</h2>
        </div>
        <div {...getRootProps()} className={`focus-ring flex min-h-44 cursor-pointer flex-col items-center justify-center rounded-[8px] border border-dashed p-5 text-center ${isDragActive ? "border-cyan-electric bg-cyan-electric/10" : "border-white/15 bg-white/5 hover:bg-white/10"}`}>
          <input {...getInputProps()} />
          <FileUp className="mb-3 h-8 w-8 text-cyan-electric" />
          <p className="font-medium text-white">{file ? file.name : "Drop PDF/DOCX resume"}</p>
          <p className="mt-2 max-w-sm text-sm text-slate-400">Extracts text, compares job keywords, scores ATS fit, and creates optimized bullets.</p>
        </div>
        <textarea value={jobDescription} onChange={(event) => setJobDescription(event.target.value)} className="focus-ring min-h-36 w-full rounded-[8px] border border-white/10 bg-slate-950/50 p-3 text-sm leading-6 text-white" />
        <button onClick={analyze} disabled={loading} className="focus-ring inline-flex w-full items-center justify-center gap-2 rounded-[8px] bg-cyan-electric px-5 py-3 text-sm font-semibold text-slate-950 disabled:opacity-60">
          <Sparkles className="h-4 w-4" />
          {loading ? "Analyzing" : "Analyze resume"}
        </button>
      </GlassCard>
      <GlassCard>{analysis && <AnalysisView analysis={analysis} />}</GlassCard>
    </div>
  );
}

function AnalysisView({ analysis }: { analysis: ResumeAnalysis }) {
  return (
    <div className="space-y-5">
      <div className="flex items-end justify-between">
        <div>
          <p className="text-sm text-slate-400">ATS score</p>
          <p className="text-5xl font-semibold text-white">{analysis.atsScore}%</p>
        </div>
      </div>
      <SignalList title="Matched keywords" items={analysis.matchedKeywords} />
      <SignalList title="Missing skills" items={analysis.missingSkills} />
      <SignalList title="Improvement suggestions" items={analysis.suggestions} />
      <SignalList title="Generated resume bullets" items={analysis.optimizedBullets} />
    </div>
  );
}

function SignalList({ title, items }: { title: string; items: string[] }) {
  return (
    <div className="rounded-[8px] border border-white/10 bg-white/5 p-4">
      <h3 className="mb-3 text-sm font-semibold text-white">{title}</h3>
      <div className="space-y-2">{items.map((item) => <div key={item} className="rounded-[8px] border border-white/10 px-3 py-2 text-sm text-slate-200">{item}</div>)}</div>
    </div>
  );
}

