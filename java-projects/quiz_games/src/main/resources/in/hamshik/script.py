import os
from openai import OpenAI
import json
PATH = os.path.join(os.path.expanduser("~"), "src", "main", "java", "resource", "in", "hamshik", "quiz.json")

try:
    os.remove(PATH)
except FileNotFoundError:
    pass


client = OpenAI(
  api_key="sk-proj-e2ERsE0AkLbHn0eqsfG7DRQNFzT3Ophp741HeBns" \
  "J1IWKHfJ8xT1tiiXt1p5IMPljv1rfnZ6dzT3BlbkFJpnoM-TXNGvMlieNJYOxMEV9oLO7qF_W7OU7hx9mAKLrm_a72GfclF5" \
  "N7Jsa0aa3V_qKywdA28A"
)

# Template JSON structure
json_pattern = """
[
  {
    "question": "questions?",
    "choices": ["choice1", "choice2", "choice3", "choice4"],
    "answer": "answer"
  }
]
"""

# Request 20 CS questions in JSON format
response = client.responses.create(
  model="gpt-5-nano",
  input=f"Create a JSON array of 20 multiple-choice quiz questions about computer science. "
        f"Use this structure as a template: {json_pattern}. "
        f"Ensure 'answer' matches the correct choice exactly.",
  store=True
)

# Extract text
quiz_text = response.output_text.strip()

try:
    # Convert to Python object
    quiz_data = json.loads(quiz_text)
except json.JSONDecodeError as e:
    print("Failed to parse JSON. Here's what the model returned:")
    print(quiz_text)
    raise e

# Write to file
with open(PATH, "w") as f:
    json.dump(quiz_data, f, indent=4)

print("Quiz JSON file created successfully!")
