import { AppShell } from "@/components/app-shell";
import { MockInterview } from "@/components/interview/mock-interview";

export default function InterviewPage() {
  return <AppShell active="/interview"><MockInterview /></AppShell>;
}

