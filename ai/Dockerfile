FROM python:3.11.2

WORKDIR /ai

RUN pip install poetry==2.1.3

COPY . .

RUN poetry install --no-root

EXPOSE 8000

CMD ["poetry", "run", "uvicorn", "controller.gpt_controller:app", "--host", "0.0.0.0", "--port", "8000"]
