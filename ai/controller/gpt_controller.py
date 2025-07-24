from fastapi import FastAPI
import service.gpt_service as service
from dto.user_story import UserStory
from dto.title import Title
from dto.estimation_data import Estimation_data

app = FastAPI()


@app.post("/improve-title")
async def improve_title(title: Title):
    print("gpt_controller: --> improve_title(), title={", title.name, "}")
    response = await service.improve_title(title.name, title.confidential_data)
    print("gpt_controller: <-- improve_title()")
    return {"improvedTitle": response}


@app.post("/improve-description")
async def improve_description(data: UserStory):
    print("gpt_controller: --> improve_description(), description={", data, "}")
    response = await service.improve_description(data)
    print("gpt_controller: <-- improve_description()")
    return {"improved_description": response.description, "improved_acceptance_criteria": response.acceptance_criteria}


@app.post("/grammar-check")
async def grammar_check(data: UserStory):
    print("gpt_controller: --> grammar_check(), description={", data, "}")
    response = await service.grammar_check(data)
    print("gpt_controller: <-- grammar_check()")
    return {"improved_description": response}


@app.post("/estimate-user-story")
async def estimate_user_story(data: Estimation_data):
    print("gpt_controller: --> estimate_user_story(), data={", data, "}")
    response = await service.estimate_user_story(data)
    print("gpt_controller: <-- estimate_user_story()")
    return {"estimation": response}


@app.post("/split-user-story")
async def split_user_story(data: UserStory):
    print("gpt_controller: --> split_user_story(), data={", data, "}")
    response = await service.split_user_story(data)
    print("gpt_controller: <-- split_user_story()")
    return {"new_user_stories": response}


@app.post("/mark-description")
async def mark_description(data: UserStory):
    print("gpt_controller: --> mark_description(), data={", data, "}")
    response = await service.mark_description(data)
    print("gpt_controller: <-- mark_description()")
    return {"description": response}


@app.get("/check-api-key")
def check_api_key():
    print("gpt_controller: --> check_api_key()")
    response = service.check_api_key()
    print("gpt_controller: <-- check_api_key()")
    return {"has_api_key": response}


@app.get("/health")
def health():
    return {"status": "UP"}
