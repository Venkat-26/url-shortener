import os
from sqlalchemy import create_engine
from sqlalchemy.engine import URL
from sqlalchemy.orm import sessionmaker
from dotenv import load_dotenv


load_dotenv()

# DATABASE_URL = (f"postgresql://{os.getenv('DB_USER')}:{os.getenv('DB_PASSWORD')}@{os.getenv('DB_HOST')}:{os.getenv('DB_PORT')}/{os.getenv('DB_NAME')}")
connection_url = URL.create(
    drivername="postgresql",
    username=os.getenv('DB_USER'),
    password=os.getenv('DB_PASSWORD'),
    host=os.getenv('DB_HOST'),
    port=int(os.getenv('DB_PORT')),
    database=os.getenv('DB_NAME')
)
engine = create_engine(connection_url)
sessionLocal = sessionmaker(bind=engine)

def get_db():
    db = sessionLocal()
    try:
        yield db
    finally:
        db.close()


