from pydantic_settings import BaseSettings
from pydantic_settings import SettingsConfigDict
from functools import lru_cache
import os


env_file = os.getenv("ENV_FILE", ".env")

class Settings(BaseSettings):
    model_config = SettingsConfigDict(env_file=env_file, env_file_encoding="utf-8")

    APP_NAME: str
    APP_ENV: str= "local"
    SERVICE_PORT: int
    DATABASE_URL: str
    LOG_LEVEL: str
    API_PREFIX: str
    DATABASE_ECHO: bool
    EXTERNAL_SERVICE_MEMBER_URL: str

@lru_cache()
def get_settings():
    settings = Settings()
    print(f"🔧 Loaded settings from: {env_file}")
    print(f"🌍 Environment: {settings.APP_ENV}")
    print(f"🔗 Member Service URL: {settings.EXTERNAL_SERVICE_MEMBER_URL}")
    return settings