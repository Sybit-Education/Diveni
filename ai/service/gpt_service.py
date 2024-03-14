import os
from openai import OpenAI
from dto.user_story import UserStory
from dto.description_response import Description_Response
import json

API_KEY = os.environ.get('api_key')

model_id = 'gpt-3.5-turbo-instruct'

client = OpenAI(api_key=API_KEY)

def find_between( s, first, last ):
    try:
        start = s.index( first ) + len( first )
        end = s.index( last, start )
        return s[start:end]
    except ValueError:
        return ""

async def improve_title(original_title: str):
    print("gpt_service: --> improve_title()")
    prompt_input = "Improve the title of this issue: " + original_title
    completion = client.completions.create(
        model=model_id,
        prompt=prompt_input,
        max_tokens=30,
        temperature=0.2
    )
    response = find_between(completion.choices[0].text, '"', '"')
    print("gpt_service: <-- improve_title()")
    return response

async def improve_description(original_user_story: UserStory):
    print("gpt_service: --> improve_description()")
    prompt_input="Send JSON with 'description' & 'acceptance_criteria' (acceptance_criteria should be a list & description needs improvement) for this user story description: " + original_user_story.description
    print(prompt_input)
    completion = client.completions.create(
       model=model_id,
        prompt=prompt_input,
        max_tokens=250,
        temperature=0.1
    )
    print(completion.choices[0].text + "\n")
    data = json.loads(completion.choices[0].text, strict = False)
    print(data)
    description = data.get("description", "")
    acceptance_criteria = data.get("acceptance_criteria", [])
    description = description + "\n ##### Acceptance Criteria: \n"
    acceptance_criteria = ["* " + criteria + "\n" for criteria in acceptance_criteria]
    print(description)
    print("gpt_service: <-- improve_description()")
    return Description_Response(description, acceptance_criteria)
