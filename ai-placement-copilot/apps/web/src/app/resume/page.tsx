import { AppShell } from "@/components/app-shell";
import { ResumeAnalyzer } from "@/components/resume/resume-analyzer";

export default function ResumePage() {
  return <AppShell active="/resume"><ResumeAnalyzer /></AppShell>;
}

