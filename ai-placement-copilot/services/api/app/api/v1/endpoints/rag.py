from fastapi import APIRouter, File, UploadFile
from app.schemas.rag import Citation, DocumentIngestResponse, RagAnswerResponse, RagQueryRequest
from app.services.rag.chunking import chunk_text
from app.services.rag.vector_store import VectorStore
from app.services.resume.parser import extract_resume_text
router = APIRouter()
vector_store = VectorStore()
@router.post("/documents", response_model=DocumentIngestResponse)
async def ingest_documents(files: list[UploadFile] = File(...)) -> DocumentIngestResponse:
    document_ids = []
    chunk_count = 0
    for file in files:
        chunks = chunk_text(extract_resume_text(file.filename, await file.read()))
        document_ids.extend(vector_store.add_documents(file.filename, chunks))
        chunk_count += len(chunks)
    return DocumentIngestResponse(documentIds=document_ids, chunks=chunk_count)
@router.post("/query", response_model=RagAnswerResponse)
async def query_documents(payload: RagQueryRequest) -> RagAnswerResponse:
    chunks = vector_store.query(payload.question)
    if not chunks:
        return RagAnswerResponse(answer="No indexed documents were found. Upload notes or PDFs first.", citations=[])
    answer = "Based on retrieved material, focus on " + ", ".join(_top_terms(" ".join(chunk.text for chunk in chunks))[:8]) + ". Cite the source chunks and state uncertainty where notes are thin."
    return RagAnswerResponse(answer=answer, citations=[Citation(documentName=chunk.document_name, chunkId=chunk.chunk_id, score=round(chunk.score, 3)) for chunk in chunks])
def _top_terms(text: str) -> list[str]:
    stopwords = {"the", "and", "for", "with", "that", "this", "from", "your", "into"}
    terms = [token.strip(".,:;()[]{}").lower() for token in text.split() if len(token.strip(".,:;()[]{}")) > 4]
    return sorted(set(terms) - stopwords, key=terms.count, reverse=True) or ["retrieval quality", "citations", "evaluation"]

