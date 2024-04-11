import os
import re
from openai import OpenAI
from dto.user_story import UserStory
from dto.description_response import Description_Response
import json


def setUp():
    API_KEY = os.environ.get('api_key')
    model_id = 'gpt-3.5-turbo-instruct'
    client = OpenAI(api_key=API_KEY)
    return client, model_id

def readFile(filename):
    filehandle = open(filename)
    names = filehandle.read().splitlines()
    filehandle.close()
    return names

def replaceConfidentalData(data: str, confidental_list):
    fileDir = os.path.dirname(os.path.realpath('__file__'))
    filename = os.path.join(fileDir, '../ai/resource/names.txt')
    filename = os.path.abspath(os.path.realpath(filename))
    names = readFile(filename)
    replacedWords = {}
    for x in range(len(confidental_list)):
        replacedData = re.compile(confidental_list[x], re.IGNORECASE)
        data = replacedData.sub(names[x], data)
        replacedWords[confidental_list[x]] = names[x]
    return replacedWords, data

def replaceConfidentalDataToOriginal(data, map: dict):
    if (type(data) == str):
        for key, value in map.items():
            replacedData = re.compile(value, re.IGNORECASE)
            data = replacedData.sub(key, data)
        return data
    else: # needed for list of strings
        for key, value in map.items():
            for x in range(len(data)):
                replacedData = re.compile(value, re.IGNORECASE)
                data[x] = replacedData.sub(key, data[x])
        return data



def find_between(s, first, last):
    try:
        start = s.index(first) + len(first)
        end = s.index(last, start)
        return s[start:end]
    except ValueError:
        return ""


async def improve_title(original_title: str):
    print("gpt_service: --> improve_title()")
    client, model_id = setUp()
    prompt_input = "Improve the title of this issue: " + original_title
    completion = client.completions.create(
        model=model_id,
        prompt=prompt_input,
        max_tokens=30,
        temperature=0.8
    )
    response = find_between(completion.choices[0].text, '"', '"')
    print("gpt_service: <-- improve_title()")
    return response


async def improve_description(original_user_story: UserStory):
    print("gpt_service: --> improve_description()")
    swappedData, new_description = replaceConfidentalData(original_user_story.description, original_user_story.confidental_data)
    client, model_id = setUp()
    prompt_input = ("Send JSON with 'description' & 'acceptance_criteria' (acceptance_criteria should be a list & description needs "
                    "improvement) for this user story description: ") + new_description
    print(prompt_input)
    completion = client.completions.create(
        model=model_id,
        prompt=prompt_input,
        max_tokens=250,
        temperature=0.1
    )
    data = json.loads(completion.choices[0].text, strict=False)
    description = data.get("description", "")
    acceptance_criteria = data.get("acceptance_criteria", [])
    description = description + "\n ##### Acceptance Criteria: \n"
    acceptance_criteria = ["* " + criteria + "\n" for criteria in acceptance_criteria]
    print(description)
    description = replaceConfidentalDataToOriginal(description, swappedData)
    acceptance_criteria = replaceConfidentalDataToOriginal(acceptance_criteria, swappedData)
    print("gpt_service: <-- improve_description()")
    return Description_Response(description, acceptance_criteria)


async def grammar_check(original_user_story: UserStory):
    print("gpt_service: --> grammar_check()")
    swappedData, new_description = replaceConfidentalData(original_user_story.description, original_user_story.confidental_data)
    print(original_user_story.description)
    client, model_id = setUp()
    prompt_input = ("Fix grammar & syntax mistakes, but do not add new elements. Send it back as a JSON with 'description' and "
                    "'acceptance_criteria' (list) field."
                    "If the text does not mention acceptance criteria, leave the field blank. This is the text: ") + new_description
    completion = client.completions.create(
        model=model_id,
        prompt=prompt_input,
        max_tokens=250,
        temperature=0.3
    )
    print(completion.choices[0].text + "\n")
    data = json.loads(completion.choices[0].text, strict=False)
    description = data.get("description", "")
    if "acceptance_criteria" in data:
        acceptance_criteria = data.get("acceptance_criteria")
        criterias = ""
        for criteria in acceptance_criteria:
            criterias = criterias + '* ' + criteria + "\n"
        if criterias != "":
            description = description + "\n Acceptance Criteria: \n" + criterias
    description = replaceConfidentalDataToOriginal(description, swappedData)
    print("gpt_service: <-- grammar_check()")
    return description
