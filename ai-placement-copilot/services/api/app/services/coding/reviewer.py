import re
from app.schemas.coding import CodeReviewResponse

def review_code(language: str, problem: str, code: str) -> CodeReviewResponse:
    lower = code.lower()
    bugs = []
    optimizations = []
    if "todo" in lower or "pass" in lower:
        bugs.append("Incomplete branch detected; finish the implementation.")
    if language in {"python", "javascript"} and re.search(r"for .*for ", lower.replace("\n", " ")):
        optimizations.append("Nested loops may be O(n^2); check hashing, sorting, or two pointers.")
    if "return" not in lower:
        bugs.append("No explicit return path detected.")
    return CodeReviewResponse(verdict=f"Review for {language}: solution is directionally aligned with the problem. {problem[:120]}", bugs=bugs or ["No obvious correctness bug found from static review."], optimizations=optimizations or ["Explain the data structure tradeoff before coding."], timeComplexity=_infer_time_complexity(code), spaceComplexity="O(n)" if any(t in lower for t in ["dict", "map", "set", "{}"]) else "O(1)", followUps=["How would this change for streaming input?", "Which edge cases would you test first?", "Can memory usage be reduced?"])

def _infer_time_complexity(code: str) -> str:
    loop_count = len(re.findall(r"\b(for|while)\b", code.replace("\n", " ")))
    if loop_count >= 2:
        return "O(n^2) in the worst case unless loops are independent"
    if loop_count == 1:
        return "O(n)"
    return "O(1) plus library call complexity"

