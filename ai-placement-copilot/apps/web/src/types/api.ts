export type ResumeAnalysis = {
  atsScore: number;
  matchedKeywords: string[];
  missingSkills: string[];
  suggestions: string[];
  optimizedBullets: string[];
};

export type InterviewEvaluation = {
  confidenceScore: number;
  communicationScore: number;
  technicalScore: number;
  feedback: string[];
  nextQuestion: string;
};

export type CodeReviewResult = {
  verdict: string;
  bugs: string[];
  optimizations: string[];
  timeComplexity: string;
  spaceComplexity: string;
  followUps: string[];
};

export type RagAnswer = {
  answer: string;
  citations: Array<{ documentName: string; chunkId: string; score: number }>;
};

