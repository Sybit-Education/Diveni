class Description_Response:
    description: str
    acceptance_criteria: list

    def __init__(self, description, acceptance_criteria):
        self.description = description
        self.acceptance_criteria = acceptance_criteria
