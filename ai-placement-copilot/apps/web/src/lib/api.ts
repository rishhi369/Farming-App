import type { CodeReviewResult, InterviewEvaluation, RagAnswer, ResumeAnalysis } from "@/types/api";

const API_BASE_URL = process.env.NEXT_PUBLIC_API_BASE_URL ?? "http://localhost:8000/api/v1";
const WS_BASE_URL = process.env.NEXT_PUBLIC_WS_BASE_URL ?? "ws://localhost:8000/api/v1/ws";

async function request<T>(path: string, options: RequestInit = {}): Promise<T> {
  const headers = new Headers(options.headers);
  if (!headers.has("Content-Type") && !(options.body instanceof FormData)) {
    headers.set("Content-Type", "application/json");
  }
  const response = await fetch(`${API_BASE_URL}${path}`, { ...options, headers, cache: "no-store" });
  if (!response.ok) throw new Error((await response.text()) || `Request failed: ${response.status}`);
  return response.json() as Promise<T>;
}

export const api = {
  analyzeResume: (formData: FormData) => request<ResumeAnalysis>("/resume/analyze", { method: "POST", body: formData }),
  startInterview: (payload: { mode: string; targetCompany?: string }) =>
    request<{ sessionId: string; openingQuestion: string }>("/interviews/start", { method: "POST", body: JSON.stringify(payload) }),
  evaluateInterviewAnswer: (payload: { sessionId: string; answer: string; transcript: string[] }) =>
    request<InterviewEvaluation>("/interviews/evaluate", { method: "POST", body: JSON.stringify(payload) }),
  reviewCode: (payload: { language: string; problem: string; code: string }) =>
    request<CodeReviewResult>("/coding/review", { method: "POST", body: JSON.stringify(payload) }),
  ingestDocuments: (formData: FormData) => request<{ documentIds: string[]; chunks: number }>("/rag/documents", { method: "POST", body: formData }),
  askDocuments: (payload: { question: string; sessionId?: string }) =>
    request<RagAnswer>("/rag/query", { method: "POST", body: JSON.stringify(payload) })
};

export async function* streamAssistant(prompt: string, sessionId = "demo-session") {
  const response = await fetch(`${API_BASE_URL}/agents/stream`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ prompt, sessionId })
  });
  if (!response.ok || !response.body) throw new Error("Streaming request failed");
  const reader = response.body.getReader();
  const decoder = new TextDecoder();
  while (true) {
    const { value, done } = await reader.read();
    if (done) break;
    yield decoder.decode(value);
  }
}

export function createInterviewSocket(sessionId: string) {
  return new WebSocket(`${WS_BASE_URL}/interview/${sessionId}`);
}

