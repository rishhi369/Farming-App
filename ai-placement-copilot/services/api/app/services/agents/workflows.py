from collections.abc import AsyncIterator
from typing import TypedDict
from app.core.config import settings
from app.services.ai.llm import llm_client

class CopilotState(TypedDict, total=False):
    prompt: str
    research: str
    resume: str
    interview: str
    coding: str
    roadmap: str
    final: str

def run_career_graph(prompt: str) -> CopilotState:
    try:
        from langgraph.graph import END, StateGraph
        graph = StateGraph(CopilotState)
        for name, node in [("research", _research_agent), ("resume", _resume_agent), ("interview", _interview_agent), ("coding", _coding_agent), ("roadmap", _roadmap_agent), ("final", _final_agent)]:
            graph.add_node(name, node)
        graph.set_entry_point("research")
        graph.add_edge("research", "resume")
        graph.add_edge("resume", "interview")
        graph.add_edge("interview", "coding")
        graph.add_edge("coding", "roadmap")
        graph.add_edge("roadmap", "final")
        graph.add_edge("final", END)
        return graph.compile().invoke({"prompt": prompt})
    except Exception:
        state: CopilotState = {"prompt": prompt}
        for node in [_research_agent, _resume_agent, _interview_agent, _coding_agent, _roadmap_agent, _final_agent]:
            state.update(node(state))
        return state

async def stream_agent_response(prompt: str) -> AsyncIterator[str]:
    state = run_career_graph(prompt)
    preface = f"Research: {state['research']}\nResume: {state['resume']}\nInterview: {state['interview']}\nCoding: {state['coding']}\nRoadmap: {state['roadmap']}\n\n"
    for token in preface.split(" "):
        yield token + " "
    async for chunk in llm_client.stream_text(state["final"]):
        yield chunk

def _research_agent(state: CopilotState) -> CopilotState:
    return {"research": "Map target role, company bar, required skills, and evidence gaps."}
def _resume_agent(state: CopilotState) -> CopilotState:
    return {"resume": "Align bullets to ATS terms, quantified impact, cloud deployment, and GenAI architecture."}
def _interview_agent(state: CopilotState) -> CopilotState:
    return {"interview": "Run adaptive loops and score confidence, communication, and depth."}
def _coding_agent(state: CopilotState) -> CopilotState:
    return {"coding": "Review correctness, complexity, edge cases, and follow-up questions."}
def _roadmap_agent(state: CopilotState) -> CopilotState:
    return {"roadmap": "Convert signals into a daily plan. " + ("CrewAI execution enabled." if settings.enable_crewai else "CrewAI adapter ready.")}
def _final_agent(state: CopilotState) -> CopilotState:
    return {"final": "Generate a concise recruiter-grade placement plan. Student request: " + state["prompt"]}

