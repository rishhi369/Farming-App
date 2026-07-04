import hashlib
import math
import uuid
from dataclasses import dataclass
from pathlib import Path
from app.core.config import settings

@dataclass
class RetrievedChunk:
    text: str
    document_name: str
    chunk_id: str
    score: float

class VectorStore:
    def __init__(self, collection_name: str = "placement_knowledge") -> None:
        self._memory: list[tuple[str, str, str, list[float]]] = []
        self._collection = None
        try:
            import chromadb
            Path(settings.chroma_persist_dir).mkdir(parents=True, exist_ok=True)
            client = chromadb.PersistentClient(path=str(settings.chroma_persist_dir))
            self._collection = client.get_or_create_collection(collection_name)
        except Exception:
            self._collection = None

    def add_documents(self, document_name: str, chunks: list[str]) -> list[str]:
        ids = [str(uuid.uuid4()) for _ in chunks]
        embeddings = [_embed(chunk) for chunk in chunks]
        if self._collection is not None and chunks:
            self._collection.add(ids=ids, documents=chunks, embeddings=embeddings, metadatas=[{"document_name": document_name} for _ in chunks])
        else:
            self._memory.extend(zip(ids, chunks, [document_name] * len(chunks), embeddings, strict=True))
        return ids

    def query(self, question: str, limit: int = 5) -> list[RetrievedChunk]:
        query_embedding = _embed(question)
        if self._collection is not None:
            result = self._collection.query(query_embeddings=[query_embedding], n_results=limit)
            documents = result.get("documents", [[]])[0]
            ids = result.get("ids", [[]])[0]
            metadatas = result.get("metadatas", [[]])[0]
            distances = result.get("distances", [[]])[0] if result.get("distances") else [0] * len(ids)
            return [RetrievedChunk(text=document, document_name=metadata.get("document_name", "document"), chunk_id=chunk_id, score=max(0.0, 1.0 - float(distance))) for document, metadata, chunk_id, distance in zip(documents, metadatas, ids, distances, strict=False)]
        ranked = sorted(self._memory, key=lambda item: _cosine(query_embedding, item[3]), reverse=True)
        return [RetrievedChunk(text=text, document_name=name, chunk_id=chunk_id, score=_cosine(query_embedding, emb)) for chunk_id, text, name, emb in ranked[:limit]]

def _embed(text: str, dimensions: int = 96) -> list[float]:
    vector = [0.0] * dimensions
    for token in text.lower().split():
        digest = hashlib.sha256(token.encode("utf-8")).digest()
        vector[int.from_bytes(digest[:2], "big") % dimensions] += 1.0
    norm = math.sqrt(sum(value * value for value in vector)) or 1.0
    return [value / norm for value in vector]

def _cosine(left: list[float], right: list[float]) -> float:
    return sum(a * b for a, b in zip(left, right, strict=False))

