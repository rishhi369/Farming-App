from io import BytesIO
from pathlib import Path
from tempfile import NamedTemporaryFile
from docx2txt import process as docx_process
from pypdf import PdfReader

def extract_resume_text(file_name: str, content: bytes) -> str:
    suffix = Path(file_name).suffix.lower()
    if suffix == ".pdf":
        reader = PdfReader(BytesIO(content))
        return "\n".join(page.extract_text() or "" for page in reader.pages)
    if suffix == ".docx":
        with NamedTemporaryFile(suffix=".docx", delete=True) as temp:
            temp.write(content)
            temp.flush()
            return docx_process(temp.name) or ""
    return content.decode("utf-8", errors="ignore")

