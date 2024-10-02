from flask import Flask
from flask_sqlalchemy import SQLAlchemy
from flask_migrate import Migrate
from werkzeug.serving import WSGIRequestHandler

db = SQLAlchemy()
# 함수를 호출할 때만 실행되게끔
def create_app():
  app = Flask(__name__)
  app.config["SQLALCHEMY_DATABASE_URI"] = "sqlite:///./redAlienShop.db"
  # WSGIRequestHandler.protocol_version = "HTTP/1.1"
  db.init_app(app)
  
  from routes import register_routes
  register_routes(app, db)

  migrate = Migrate(app, db)




  return app

  