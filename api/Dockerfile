FROM python:3.9-slim

# Instalamos mysql
RUN apt-get update && apt-get install -y \
    default-libmysqlclient-dev \
    pkg-config \
    build-essential \
    && rm -rf /var/lib/apt/lists/*

WORKDIR /api

COPY requirements.txt .

# Dependencias de python
RUN pip install --no-cache-dir -r requirements.txt
RUN pip install pymysql flask-cors

COPY . .

ENV FLASK_APP=src/app.py
ENV FLASK_ENV=development

EXPOSE 5000

CMD python -m flask run --host=0.0.0.0