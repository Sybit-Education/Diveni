from pydantic import BaseModel
class UserStory(BaseModel):
    title: str
    description: str
    confidental_data: list
