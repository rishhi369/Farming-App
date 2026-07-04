from pydantic import BaseModel

class ReadinessMetric(BaseModel):
    label: str
    value: float
    delta: float

class RoadmapItem(BaseModel):
    day: str
    focus: str
    status: str

class SkillGap(BaseModel):
    skill: str
    current: float
    target: float

class DashboardSummary(BaseModel):
    readinessScore: float
    placementProbability: float
    streakDays: int
    activeRoadmapItems: int
    metrics: list[ReadinessMetric]
    roadmap: list[RoadmapItem]
    skills: list[SkillGap]

