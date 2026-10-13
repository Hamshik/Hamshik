import os
from openai import OpenAI
import json
PATH = os.path.join(os.path.expanduser("~"), "src", "main", "java", "resource", "in", "hamshik", "userAns.json")


client = OpenAI(
  api_key="sk-proj-e2ERsE0AkLbHn0eqsfG7DRQNFzT3Ophp741HeBns" \
  "J1IWKHfJ8xT1tiiXt1p5IMPljv1rfnZ6dzT3BlbkFJpnoM-TXNGvMlieNJYOxMEV9oLO7qF_W7OU7hx9mAKLrm_a72GfclF5" \
  "N7Jsa0aa3V_qKywdA28A"
)

# Template JSON structure
with open(PATH, "r") as f:
    data = json.load(f)

# Request 20 CS questions in JSON format
response = client.responses.create(
  model="gpt-5-nano",
  input=f"See the answer from the json file{data} and give the score",
  store=True
)

# Extract text
quiz_text = response.output_text.strip()

