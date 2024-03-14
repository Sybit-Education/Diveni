from fastapi import FastAPI
import service.gpt_service as service
from dto.user_story import UserStory
from dto.title import Title


app = FastAPI()


@app.post("/improve-title")
async def root(title: Title):
    print("gpt_controller: --> improve_title(), title={",title.name,"}")
    response = await service.improve_title(title.name)
    print("gpt_controller: <-- improve_title()")
    return {"improvedTitle": response}

@app.post("/improve-description")
async def improve_description(description: UserStory):
    print("gpt_controller: --> improve_description(), description={", description,"}")
    response = await service.improve_description(description)
    print("gpt_controller: <-- improve_description()")
    return {"improved_description" : response.description, "improved_acceptance_criteria": response.acceptance_criteria}


@app.get("/hello/{name}")
async def say_hello(name: str):
    return {"message": f"Hello {name}"}