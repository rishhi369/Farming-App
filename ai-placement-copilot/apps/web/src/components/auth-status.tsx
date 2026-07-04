"use client";

import { SignInButton, SignedIn, SignedOut, UserButton } from "@clerk/nextjs";
import { LogIn } from "lucide-react";

export function AuthStatus() {
  const key = process.env.NEXT_PUBLIC_CLERK_PUBLISHABLE_KEY;
  if (!key || key.includes("replace_me")) {
    return <div className="glass-subtle rounded-[8px] px-3 py-2 text-xs text-slate-300">Local demo auth</div>;
  }
  return (
    <>
      <SignedIn>
        <UserButton afterSignOutUrl="/" />
      </SignedIn>
      <SignedOut>
        <SignInButton mode="modal">
          <button className="focus-ring inline-flex items-center gap-2 rounded-[8px] border border-cyan-electric/30 px-3 py-2 text-sm text-cyan-100 hover:bg-cyan-electric/10">
            <LogIn className="h-4 w-4" />
            Sign in
          </button>
        </SignInButton>
      </SignedOut>
    </>
  );
}

