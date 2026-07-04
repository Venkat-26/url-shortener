import os  
import sys
sys.path.insert(0,os.path.dirname(os.path.abspath(__file__)))
from dotenv import load_dotenv
from app import create_app


load_dotenv()

app = create_app()

print("\n >>> Registered routes:")
for rule in app.url_map.iter_rules():
    print(f" {rule.methods} {rule}")
    

if __name__ == '__main__':
    port = int(os.getenv('FLASK_PORT', 5000))
    app.run(debug=True, port = port)
    

 