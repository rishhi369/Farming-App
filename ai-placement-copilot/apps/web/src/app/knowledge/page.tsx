import { AppShell } from "@/components/app-shell";
import { DocumentChat } from "@/components/rag/document-chat";

export default function KnowledgePage() {
  return <AppShell active="/knowledge"><DocumentChat /></AppShell>;
}

