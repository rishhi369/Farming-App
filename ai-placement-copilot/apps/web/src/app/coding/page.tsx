import { AppShell } from "@/components/app-shell";
import { CodeWorkbench } from "@/components/coding/code-workbench";

export default function CodingPage() {
  return <AppShell active="/coding"><CodeWorkbench /></AppShell>;
}

