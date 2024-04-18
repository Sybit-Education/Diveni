import os
import random
import re
from openai import OpenAI
from dto.user_story import UserStory
from dto.description_response import Description_Response
from dto.estimation_data import Estimation_data
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


def replace_confidential_data(data: str, confidential_map: dict[str, str]):
    is_docker_container = os.environ.get('STARTED_IN_DOCKER')
    fileDir = os.path.dirname(os.path.realpath('__file__'))
    if is_docker_container == 'true':
        filename = os.path.join(fileDir, '../diveni-ai/resource/names.txt')
    else:
        filename = os.path.join(fileDir, '../ai/resource/names.txt')
    filename = os.path.abspath(os.path.realpath(filename))
    names = readFile(filename)
    replaced_words = {}
    count = 0
    for private_data in confidential_map.keys():
        replaced_data = re.compile(private_data.strip(), re.IGNORECASE)
        if str(confidential_map.get(private_data)) == "company":
            data = replaced_data.sub("placeholder-" + str(confidential_map.get(private_data)) + "-" + str(count), data)
            replaced_words[private_data] = "placeholder-" + str(confidential_map.get(private_data)) + "-" + str(count)
        if str(confidential_map.get(private_data)) == "person":
            data = replaced_data.sub(names[count], data)
            replaced_words[private_data] = names[count]
        if str(confidential_map.get(private_data)) == "number":
            placerholder_number = random.randint(0, 1000000)
            data = replaced_data.sub(str(placerholder_number), data)
            replaced_words[private_data] = str(placerholder_number)
        count = count + 1
    return replaced_words, data


def replace_confidential_data_to_original(data, confidential_map: dict):
    if type(data) == str:
        for key, value in confidential_map.items():
            replaced_data = re.compile(value, re.IGNORECASE)
            data = replaced_data.sub(key, data)
        return data
    else:  # needed for list of strings
        for key, value in confidential_map.items():
            for x in range(len(data)):
                replaced_data = re.compile(value, re.IGNORECASE)
                data[x] = replaced_data.sub(key, data[x])
        return data

def get_prompt(information):
    is_docker_container = os.environ.get('STARTED_IN_DOCKER')
    fileDir = os.path.dirname(os.path.realpath('__file__'))
    if is_docker_container == 'true':
        path = "resource/prompts/"
    else:
        path = "../ai/resource/prompts/"
    filename = os.path.join(fileDir, path + "prompt_" + information + ".txt")
    prompt = open(filename, 'r', encoding='utf-8').read()
    return prompt



async def improve_title(original_title: str, confidential_data: dict):
    print("gpt_service: --> improve_title()")
    swapped_data, new_title = replace_confidential_data(original_title, confidential_data)
    client, model_id = setUp()
    # "Improve the title of this issue: "
    prompt_input = get_prompt("title") + new_title
    completion = client.completions.create(
        model=model_id,
        prompt=prompt_input,
        max_tokens=40,
        temperature=0.8
    )
    response = completion.choices[0].text.lstrip().rstrip()
    title = replace_confidential_data_to_original(response, swapped_data)
    print("gpt_service: <-- improve_title()")
    return title


async def improve_description(original_user_story: UserStory):
    print("gpt_service: --> improve_description()")
    swappedData, new_description = replace_confidential_data(original_user_story.description, original_user_story.confidential_data)
    client, model_id = setUp()
    # prompt_input = ("Send JSON with 'description' & 'acceptance_criteria' (acceptance_criteria should be a list & description needs "
    #                "improvement) for this user story description: ") + new_description

    if original_user_story.language == "english":
        final_prompt = get_prompt("improve_description") + new_description + "\n Solution: "
    else:
        final_prompt = get_prompt("improve_description_german") + new_description + "\n Antwort: "

    print(final_prompt)
    completion = client.completions.create(
        model=model_id,
        prompt=final_prompt,
        max_tokens=250,
        temperature=0.1
    )
    print(completion.choices[0].text + "\n")
    output = completion.choices[0].text
    start_brace = output.find('{')
    end_brace = output.rfind('}')
    json_ready_string = output[start_brace: end_brace + 1]
    print(json_ready_string)
    data = json.loads(json_ready_string)
    # data = json.loads(completion.choices[0].text.strip("'<>() ").replace('\'', '\"'), strict = False)
    description = data.get("description", "")
    acceptance_criteria = data.get("acceptance_criteria", [])
    if original_user_story.language == "german":
        description = description + "\n ##### Akzeptanz Kriterien: \n"
    else:
        description = description + "\n ##### Acceptance Criteria: \n"
    acceptance_criteria = ["* " + criteria + "\n" for criteria in acceptance_criteria]
    description = replace_confidential_data_to_original(description, swappedData)
    acceptance_criteria = replace_confidential_data_to_original(acceptance_criteria, swappedData)
    print("gpt_service: <-- improve_description()")
    return Description_Response(description, acceptance_criteria)


async def grammar_check(original_user_story: UserStory):
    print("gpt_service: --> grammar_check()")
    swappedData, new_description = replace_confidential_data(original_user_story.description, original_user_story.confidential_data)
    client, model_id = setUp()
    prompt_input = ("Fix grammar & syntax mistakes, but do not add new elements. Send it back as a JSON with 'description' and "
                    "'acceptance_criteria' (list) field."
                    "If the text does not mention acceptance criteria, leave the field blank. This is the text: ") + new_description
    final_prompt = get_prompt("grammar_check") + new_description + "\n Solution: "
    print(final_prompt)
    completion = client.completions.create(
        model=model_id,
        prompt=final_prompt,
        max_tokens=250,
        temperature=0
    )
    output = completion.choices[0].text
    start_brace = output.find('{')
    end_brace = output.rfind('}')
    json_ready_string = output[start_brace: end_brace + 1]
    data = json.loads(json_ready_string, strict=False)
    description = data.get("description", "")
    if "acceptance_criteria" in data:
        acceptance_criteria = data.get("acceptance_criteria")
        criterias = ""
        for criteria in acceptance_criteria:
            criterias = criterias + " \n" + criteria
        if criterias != "":
            if original_user_story.language == "english":
                text = "Acceptance Criteria"
            else:
                text = "Akzeptanz Kriterien"
            description = description + "\n ##### " + text + ": \n" + criterias
    description = replace_confidential_data_to_original(description, swappedData)
    print("gpt_service: <-- grammar_check()")
    return description

async def estimate_user_story(original_data: Estimation_data):
    print("gpt_service: --> estimate_user_story()")
    client, model_id = setUp()
    swappedDataTitle, new_title = replace_confidential_data(original_data.title, original_data.confidential_data)
    swappedDataDescription, new_description = replace_confidential_data(original_data.description, original_data.confidential_data)
    if original_data.voteSet == ['1', '2', '3', '5', '8', '13', '21']:
        prompt = get_prompt("estimation_fibo") + new_title + "\n Description: " + new_description + "\n Valid Options are: " + str(original_data.voteSet)
    elif original_data.voteSet == ['XS','S','M','L','XL']:
        prompt = get_prompt("estimation_shirt") + new_title + "\n Description: " + new_description + "\n Valid Options are: " + str(original_data.voteSet)
    elif original_data.voteSet == ['1','2','3','4','5','6','8','10','12','16']:
        prompt = get_prompt("estimation_hour") + new_title + "\n Description: " + new_description + "\n Valid Options are: " + str(original_data.voteSet)
    elif original_data.voteSet == ['1','2','3','4','5','6','7','8','9','10']:
        prompt = get_prompt("estimation_number") + new_title + "\n Description: " + new_description + "\n Valid Options are: " + str(original_data.voteSet)
    else:
        prompt = ("Task: Send a JSON with \"estimation\" and estimate the effort for this user story: Title: " + new_title + "\n "
                  "Description: " + new_description + "\n Valid Options are: " + str(original_data.voteSet))
    print(prompt)
    completion = client.completions.create(
        model=model_id,
        prompt=prompt,
        max_tokens=250,
        temperature=0
    )
    print(completion.choices[0].text)
    output = completion.choices[0].text
    start_brace = output.find('{')
    end_brace = output.rfind('}')
    json_ready_string = output[start_brace: end_brace + 1]
    data = json.loads(json_ready_string, strict=False)
    estimation = data.get("estimation", "")
    print("gpt_service: <-- estimate_user_story()")
    return estimation
