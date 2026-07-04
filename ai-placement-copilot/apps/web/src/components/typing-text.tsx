"use client";

import { useEffect, useState } from "react";

export function TypingText({ text, speed = 18 }: { text: string; speed?: number }) {
  const [visible, setVisible] = useState("");
  useEffect(() => {
    setVisible("");
    let index = 0;
    const timer = window.setInterval(() => {
      index += 1;
      setVisible(text.slice(0, index));
      if (index >= text.length) window.clearInterval(timer);
    }, speed);
    return () => window.clearInterval(timer);
  }, [text, speed]);
  return (
    <span>
      {visible}
      <span className="ml-1 inline-block h-4 w-2 animate-pulse bg-cyan-electric align-middle" />
    </span>
  );
}

