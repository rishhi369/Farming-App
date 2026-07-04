"use client";

import { FormEvent, useCallback, useState } from "react";
import { useDropzone } from "react-dropzone";
import { FileSearch, Send } from "lucide-react";
import { toast } from "sonner";
import { GlassCard } from "@/components/glass-card";
import { api } from "@/lib/api";
import type { RagAnswer } from "@/types/api";

export function DocumentChat() {
  const [files, setFiles] = useState<File[]>([]);
  const [question, setQuestion] = useState("Summarize the most important GenAI interview concepts from my notes.");
  const [answer, setAnswer] = useState<RagAnswer | null>({
    answer: "Prioritize retrieval pipelines, chunking strategy, embeddings, reranking, prompt grounding, evaluation, and observability.",
    citations: [{ documentName: "sample-notes.pdf", chunkId: "chunk-04", score: 0.91 }]
  });
  const onDrop = useCallback((acceptedFiles: File[]) => setFiles((current) => [...current, ...acceptedFiles]), []);
  const { getRootProps, getInputProps, isDragActive } = useDropzone({ onDrop, accept: { "application/pdf": [".pdf"], "text/plain": [".txt"], "application/vnd.openxmlformats-officedocument.wordprocessingml.document": [".docx"] } });

  async function ingest() {
    if (!files.length) return toast.info("Add notes, PDFs, or DOCX files to ingest.");
    const formData = new FormData();
    files.forEach((file) => formData.append("files", file));
    try {
      const result = await api.ingestDocuments(formData);
      toast.success(`Indexed ${result.chunks} chunks`);
    } catch (error) {
      toast.error(error instanceof Error ? error.message : "Document indexing failed");
    }
  }

  async function ask(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    try {
      setAnswer(await api.askDocuments({ question }));
    } catch (error) {
      toast.error(error instanceof Error ? error.message : "RAG query failed");
    }
  }

  return (
    <div className="grid gap-4 xl:grid-cols-[0.9fr_1.1fr]">
      <GlassCard className="space-y-4">
        <div><p className="text-sm text-slate-400">RAG Knowledge Assistant</p><h2 className="text-2xl font-semibold text-white">Chat with documents</h2></div>
        <div {...getRootProps()} className={`focus-ring flex min-h-48 cursor-pointer flex-col items-center justify-center rounded-[8px] border border-dashed p-5 text-center ${isDragActive ? "border-mint-signal bg-mint-signal/10" : "border-white/15 bg-white/5"}`}>
          <input {...getInputProps()} /><FileSearch className="mb-3 h-8 w-8 text-mint-signal" /><p className="font-medium text-white">Upload notes, PDFs, research papers</p><p className="mt-2 text-sm text-slate-400">Indexed into ChromaDB for citation-backed retrieval.</p>
        </div>
        <div className="space-y-2">{files.map((file) => <div key={file.name} className="rounded-[8px] border border-white/10 bg-white/5 px-3 py-2 text-sm text-slate-300">{file.name}</div>)}</div>
        <button onClick={ingest} className="focus-ring w-full rounded-[8px] bg-mint-signal px-5 py-3 text-sm font-semibold text-slate-950">Index documents</button>
      </GlassCard>
      <GlassCard className="flex min-h-[640px] flex-col">
        <div className="mb-4"><p className="text-sm text-slate-400">Citation-based answer engine</p><h2 className="text-2xl font-semibold text-white">Semantic search chat</h2></div>
        <div className="flex-1 rounded-[8px] border border-white/10 bg-slate-950/40 p-4 text-sm leading-6 text-slate-200">{answer?.answer}<div className="mt-5 space-y-2">{answer?.citations.map((citation) => <div key={`${citation.documentName}-${citation.chunkId}`} className="rounded-[8px] border border-cyan-electric/15 bg-cyan-electric/10 px-3 py-2 text-xs text-cyan-100">{citation.documentName} · {citation.chunkId} · score {citation.score.toFixed(2)}</div>)}</div></div>
        <form onSubmit={ask} className="mt-4 flex gap-3"><input value={question} onChange={(event) => setQuestion(event.target.value)} className="focus-ring min-h-12 flex-1 rounded-[8px] border border-white/10 bg-white/10 px-4 text-sm text-white" /><button className="focus-ring inline-flex w-12 items-center justify-center rounded-[8px] bg-cyan-electric text-slate-950"><Send className="h-5 w-5" /></button></form>
      </GlassCard>
    </div>
  );
}

