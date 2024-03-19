FROM python:3.11.2

WORKDIR /diveni-ai

RUN pip install poetry

COPY . .

RUN poetry install
RUN poetry self add poetry-dotenv-plugin

EXPOSE 8000

CMD ["poetry","run", "uvicorn", "controller.gpt_controller:app", "--host", "0.0.0.0", "--port", "8000"]
