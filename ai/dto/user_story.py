from pydantic import BaseModel


class UserStory(BaseModel):
    title: str
    description: str
    language: str
    confidential_data: dict
