FROM python:3.12-slim

WORKDIR /usr/local/app

# install dependencies
COPY requirements.txt ./
RUN pip install --no-cache-dir -r requirements.txt

#copy source code
COPY ./ /usr/local/app/

EXPOSE 8080

# specify application level and restricting the root user level command execution
RUN useradd app
USER app

CMD [ "uvicorn","app.main:app", "--host","0.0.0.0", "--port","8080" ]