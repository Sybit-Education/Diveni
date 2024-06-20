from pydantic import BaseModel


class Estimation_data(BaseModel):
    title: str
    description: str
    voteSet: list
    confidential_data: dict
