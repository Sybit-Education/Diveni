from pydantic import BaseModel
class UserStory(BaseModel):
    title: str
    description: str
    confidential_data: dict
