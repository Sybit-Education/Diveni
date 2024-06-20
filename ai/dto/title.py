from pydantic import BaseModel


class Title(BaseModel):
    name: str
    confidential_data: dict
