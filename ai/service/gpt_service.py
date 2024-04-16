import os
import random
import re
from openai import OpenAI
from dto.user_story import UserStory
from dto.description_response import Description_Response
from dto.estimation_data import Estimation_data
import json

prompt_title = ("Task: Improve the title of this issue in its language: Create a homepage for information research"
                "\nDesigning an Information Research Homepage: Enhancing User Experience and Accessibility\n"
                "Task: Improve the title of this issue in its language: Create a homepage for [placeholder]\n"
                "Designing a Homepage for a Placeholder Website\n"
                "Task: Improve the title of this issue in its language: Erstellung einer Homepage\n"
                "Erstellung einer benutzerfreundlichen Homepage: Verbessernde User Experience\n"
                "Task: Improve the title of this issue in its language: ")

prompt_description_english = (
    "Task: Send JSON with \"description\" & \"acceptance_criteria\" for this User Story: create rest service for backend \n"
    "Solution: {\"description\" : \"As a developer, I want to create a REST service for the backend so that I can easily access and manipulate data from the database.\", "
    "\"acceptance_criteria\" : ["
    "\"The REST service should have endpoints for GET, POST, PUT & DELETE requests\","
    "\"Unit Tests should be written\"]}\n"
    "Task: Send JSON with \"description\" & \"acceptance_criteria\" for this User Story: create Homepage\n"
    "Solution: {\"description\" : \"As a user, I want to be able to access a homepage so that I can easily navigate to different sections of the website.\", "
    "\"acceptance_criteria\" : [\"The homepage should have a visually appealing design.\", \"The homepage should have a clear and concise navigation menu.\", \"It should have a search bar for easy navigation.\",\"The homepage should be accessible on different devices.\"]}\n"
    "Task: Send JSON with \"description\" & \"acceptance_criteria\" for this User Story: create a homepage for placerholder-company-1 with a java backend. It should not cost more than 100 thousand euros\n"
    "### Acceptance Criteria: \n"
    "* good looking design \n"
    "* logo of placerholder-company-1 should be on the navbar \n"
    "Solution: {\"description\" : \"As a developer, I want to create a visually appealing homepage for placerholder-company-1. The website should have a modern design and incorporate a Java backend.\", "
    "\"acceptance_criteria\" : ["
    "\"The homepage should have an aesthetically pleasing layout.\","
    "\"Use responsive design principles to ensure it looks great on various devices\","
    "\"The placerholder-company-1 logo must be prominently displayed on the navigation bar\"]}\n"
    "Task: Send JSON with \"description\" & \"acceptance_criteria\" for this User Story (at least 6 acceptance criteria): ")

prompt_description_german = ("Aufgabe: Sende ein JSON mit \"description\" & \"acceptance_criteria\" für diese User Story: Erstelle einen REST Service für das Backend \n"
                             "Antwort: {\"description\": \"Als Entwickler im Backend-Team möchte ich einen RESTful Service erstellen, um die Kommunikation zwischen dem Frontend und dem Backend zu ermöglichen.\","
                             "\"acceptance_criteria\": [\"Die Anfragen können GET, POST, PUT oder DELETE sein.\",\"Der Service muss sicherstellen, dass nur autorisierte Benutzer auf die Endpunkte zugreifen können.\",\"Der Service sollte die eingehenden Anfragen validieren, um sicherzustellen, dass sie den erwarteten Parametern und Formaten entsprechen.\"]}\n"
                             "Aufgabe: Sende ein JSON mit \"description\" & \"acceptance_criteria\" für diese User Story: Erstelle eine Homepage für placeholder-company-1 mit einem java Backend. Es sollte nicht mehr als 100 tausend Euro kosten\n"
                             "## Akzeptanz Kriterien \n"
                             "* gut aussehende Webseite \n"
                             "* Das Logo von placeholder-company-1 sollte auf der Navigationsleiste zu sehen sein \n"
                             "Antwort: {\"description\" : \"Als Entwickler möchte ich eine professionelle Homepage, mit einem Java-Backend für placeholder-company-1 erstellen, um eine benutzerfreundliche Plattform zu schaffen, die das Unternehmen online repräsentiert.\","
                             "\"acceptance_criteria\" : [\"Die Kosten für die Erstellung der Homepage dürfen 100.000 Euro nicht überschreiten\",\"Das Java-Backend muss robust und sicher sein, um die Funktionalität der Website zu unterstützen.\", \"Die Website sollte eine klare Navigation haben, um Benutzern das Auffinden von Informationen zu erleichtern\"]}\n"
                             "Aufgabe:  Sende ein JSON mit \"description\" & \"acceptance_criteria\" für diese User Story (mindestens 6 Akzeptanz Kriterien): ")

prompt_grammar = (
    "Task: Send a JSON with \"description\" & \"acceptance_criteria\" and fix grammar & syntax mistakes, but do not add new elements, for this markdown-text: "
    "As a user i want 2 be able to accessed a homepage so that I can easily navigate to different sections of the website.\n"
    "##### Acceptance Criteria:\n "
    "* The homepage should have a visualy appealing design.\n "
    "* The homepage should have an clear and concise navigation menü.\n"
    "Solution: {\"description\" : \"As a user, I want to be able to access a homepage so that I can easily navigate to different sections of the website.\n ##### Acceptance Criteria:\n \",\n"
    "\"acceptance_criteria\" : [\"* The homepage should have a visually appealing design.\",\"* The homepage should have a clear and concise navigation menu.\"]}\n"
    "Task: Send a JSON with \"description\" & \"acceptance_criteria\" and fix grammar & syntax mistakes, but do not add new elements, for this markdown-text: "
    "As a devloper, i want to crete a backend servize with a REST API so dhat i can easily manage and manipulate dayta from the database\n"
    "Solution: {\"description\" : \"As a developer, I want to create a backend service with a REST API so that I can easily manage and manipulate data from the database.\"\n"
    "\"acceptance_criteria\" : []}\n"
    "Task: Send a JSON with \"description\" & \"acceptance_criteria\" and fix grammar & syntax mistakes, but do not add new elements, for this markdown-text: ")

prompt_fibo = (
               "Task: Send a JSON with \"estimation\" and estimate the effort for this user story: Title: filter numbers out of the input field "
               "\nDescription: The existing Backend Service should filter numbers out of the input, since the system crashes with numbers.\n"
               "Valid Options are: ['1','2','3','5','8','13','21']\n"
               "Answer: {\"estimation\": \"2\"}\n"
               "Task: Send a JSON with \"estimation\" and estimate the effort for this user story: Title: Create a webapp like amazon "
               "\nDescription: Our Company should have a web application like amazon, but better\n"
               "## Acceptance Criteria: \n"
               "* robust and fast backend with java \n"
               "* good looking design \n"
               "* nosql database should be implemented \n"
               "Valid Options are: ['1','2','3','5','8','13','21']\n"
               "Answer: {\"estimation\": \"21\"}\n"
               "Task: Send a JSON with \"estimation\" and estimate the effort for this user story: Title:"
)

prompt_shirt = (
    "Task: Send a JSON with \"estimation\" and estimate the effort for this user story: Title: filter numbers out of the input field "
    "\nDescription: The existing Backend Service should filter numbers out of the input, since the system crashes with numbers.\n"
    "Valid Options are: ['XS','S','M','L','XL']\n"
    "Answer: {\"estimation\": \"S\"}\n"
    "Task: Send a JSON with \"estimation\" and estimate the effort for this user story: Title: Create a webapp like amazon "
    "\nDescription: Our Company should have a web application like amazon, but better\n"
    "## Acceptance Criteria: \n"
    "* robust and fast backend with java \n"
    "* good looking design \n"
    "* nosql database should be implemented \n"
    "Valid Options are: ['XS','S','M','L','XL']\n"
    "Answer: {\"estimation\": \"XL\"}\n"
    "Task: Send a JSON with \"estimation\" and estimate the effort for this user story: Title:"
)

prompt_hour = (
    "Task: Send a JSON with \"estimation\" and estimate the effort for this user story: Title: filter numbers out of the input field "
    "\nDescription: The existing Backend Service should filter numbers out of the input, since the system crashes with numbers.\n"
    "Valid Options are: ['1','2','3','4','5','6','8','10','12','16']\n"
    "Answer: {\"estimation\": \"2\"}\n"
    "Task: Send a JSON with \"estimation\" and estimate the effort for this user story: Title: Create a webapp like amazon "
    "\nDescription: Our Company should have a web application like amazon, but better\n"
    "## Acceptance Criteria: \n"
    "* robust and fast backend with java \n"
    "* good looking design \n"
    "* nosql database should be implemented \n"
    "Valid Options are: ['1','2','3','4','5','6','8','10','12','16']\n"
    "Answer: {\"estimation\": \"16\"}\n"
    "Task: Send a JSON with \"estimation\" and estimate the effort for this user story: Title:"
)

prompt_number = (
    "Task: Send a JSON with \"estimation\" and estimate the effort for this user story: Title: filter numbers out of the input field "
    "\nDescription: The existing Backend Service should filter numbers out of the input, since the system crashes with numbers.\n"
    "Valid Options are: ['1','2','3','4','5','6','7','8','9','10']\n"
    "Answer: {\"estimation\": \"2\"}\n"
    "Task: Send a JSON with \"estimation\" and estimate the effort for this user story: Title: Create a webapp like amazon "
    "\nDescription: Our Company should have a web application like amazon, but better\n"
    "## Acceptance Criteria: \n"
    "* robust and fast backend with java \n"
    "* good looking design \n"
    "* nosql database should be implemented \n"
    "Valid Options are: ['1','2','3','4','5','6','7','8','9','10']\n"
    "Answer: {\"estimation\": \"10\"}\n"
    "Task: Send a JSON with \"estimation\" and estimate the effort for this user story: Title:"
)


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
    fileDir = os.path.dirname(os.path.realpath('__file__'))
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


async def improve_title(original_title: str, confidential_data: dict):
    print("gpt_service: --> improve_title()")
    swapped_data, new_title = replace_confidential_data(original_title, confidential_data)
    client, model_id = setUp()
    # "Improve the title of this issue: "
    prompt_input = prompt_title + new_title
    completion = client.completions.create(
        model=model_id,
        prompt=prompt_input,
        max_tokens=40,
        temperature=0.8
    )
    response = completion.choices[0].text.lstrip().rstrip()
    print(response + "<--\n")
    title = replace_confidential_data_to_original(response, swapped_data)
    print(title + "\n")
    print("gpt_service: <-- improve_title()")
    return title


async def improve_description(original_user_story: UserStory):
    print("gpt_service: --> improve_description()")
    swappedData, new_description = replace_confidential_data(original_user_story.description, original_user_story.confidential_data)
    client, model_id = setUp()
    # prompt_input = ("Send JSON with 'description' & 'acceptance_criteria' (acceptance_criteria should be a list & description needs "
    #                "improvement) for this user story description: ") + new_description

    if original_user_story.language == "english":
        final_prompt = prompt_description_english + new_description + "\n Solution: "
    else:
        final_prompt = prompt_description_german+ new_description + "\n Antwort: "
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
    final_prompt = prompt_grammar + new_description + "\n Solution: "
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
        prompt = prompt_fibo + new_title + "\n Description: " + new_description + "\n Valid Options are: " + str(original_data.voteSet)
    elif original_data.voteSet == ['XS','S','M','L','XL']:
        prompt = prompt_shirt + new_title + "\n Description: " + new_description + "\n Valid Options are: " + str(original_data.voteSet)
    elif original_data.voteSet == ['1','2','3','4','5','6','8','10','12','16']:
        prompt = prompt_hour + new_title + "\n Description: " + new_description + "\n Valid Options are: " + str(original_data.voteSet)
    elif original_data.voteSet == ['1','2','3','4','5','6','7','8','9','10']:
        prompt = prompt_number + new_title + "\n Description: " + new_description + "\n Valid Options are: " + str(original_data.voteSet)
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
