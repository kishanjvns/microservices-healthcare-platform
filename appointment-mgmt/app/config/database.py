from sqlalchemy.ext.asyncio import create_async_engine
from sqlalchemy import MetaData
from .settings import get_settings
from sqlalchemy import Table, Column, Integer, String

metadata = MetaData()

db_settings = get_settings()
engine = create_async_engine(db_settings.DATABASE_URL, echo=db_settings.DATABASE_ECHO)

async def get_engine():
    return engine

async def get_db_connection():
    async with engine.connect() as connection:
        yield connection
        await connection.commit()

appointment_table = Table(
    'appointments', metadata,
    Column('id', Integer, primary_key=True),
    Column('patient_name', String, nullable=False),
    Column('patient_id', String, nullable=False),
    Column('doctor_name', String, nullable=False),
    Column('doctor_id', String, nullable=False),
    Column('appointment_date_time', String, nullable=False),
    Column('reason', String, nullable=False),
    Column('status', String, nullable=False),
)

async def init_db():
    async with engine.begin() as conn:
        await conn.run_sync(metadata.create_all)
