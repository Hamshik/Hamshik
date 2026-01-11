# ai_service.py
from fastapi import FastAPI
from pydantic import BaseModel

app = FastAPI()

class AnswerRequest(BaseModel):
    question: str
    answer: str

@app.post("/check")
def check_answer(req: AnswerRequest):
    question = req.question
    user_answer = req.answer.lower().strip()

    # Simple rule-based AI
    correct_answer_map = {
        "who is known as the father of the computer?": "charles babbage",
        "what is 2+2?": "4",
        # add more questions as needed
    }

    correct_answer = correct_answer_map.get(question.lower(), "")
    if correct_answer == "":
        # fallback AI logic (could use OpenAI GPT)
        return {"result": "Incorrect"}

    result = "Correct" if user_answer == correct_answer else "Incorrect"
    return {"result": result}