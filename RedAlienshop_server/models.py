# DB객체는 models 파일이 필요하다 가정해보자
# modles 파일에서 app.py로 모델을 가져와야 한다
# Migrate가 modles를 DB의 테이블로 맵핑해준다

from app import db  
import base64

class User(db.Model):
  __tablename__ = "User"

  # Flask에서 지원하는 데이터 유형
  # Integer, String(size), Text, DataTime, Float, Boolean, PickleType, LargeBinary
  # String에 size를 주면 알아서 VARCHAR로 변환됨
    
  id = db.Column(db.Integer, primary_key = True)
  fullname = db.Column(db.String(20), nullable=False)
  phone_number = db.Column(db.String(15), nullable=False)
  username = db.Column(db.String(30), unique=True, nullable=False)
  password = db.Column(db.String(15), nullable=False)
  points = db.Column(db.Integer, nullable=False)
  # created_date = db.Column(db.DateTime, default=datetime.datetime.utcnow)
  def __init__(self, fullname=None, phone_number=None, username=None, password=None, points=None):
        self.fullname = fullname
        self.phone_number = phone_number
        self.username = username
        self.password = password
        self.points = points

  # 쿼리 결과 시 호출되는 메소드 
  # Ex) user = User.query.filter(User.username == request_username, User.password == request_password).first()
  # Ex) print(user) == __repr__() 호출
  def __repr__(self) :
   return f"User : {self.username}"
  
  @property
  def values(self):
     return {"username" : self.username,
                "fullname" : self.fullname,
                }


class Products(db.Model):
  __tablename__ = "Products"
  # default=100 방식은 오류는 발생하지 않으나, db파일을 직접 확인해보면 DEFAULT가 적용안됨
  # 참고 : https://docs.sqlalchemy.org/en/20/core/defaults.html#sqlalchemy.schema.DefaultClause
  product_id = db.Column(db.Integer, primary_key=True)
  image = db.Column(db.LargeBinary)
  brand = db.Column(db.String(20))
  title = db.Column(db.String(20))
  price = db.Column(db.Integer)
  comments_count = db.Column(db.Integer, nullable=False, server_default="0")

  def __init__(self, image=None, title=None, price=None, comments_count=None):
      self.image = image
      self.title = title
      self.price = price
      self.comments_count = comments_count

  def __repr__(self):
      return f'Products : {self.title}'  

  
  def to_dict(self):
      img_str = base64.b64encode(self.image).decode("utf-8") # byte 데이터는 jsonify에 사용 불가능해서 문자열로 변환
      return {"product_id" :  self.product_id,
              "image" : img_str,
              "brand" : self.brand,   
              "title" : self.title,
              "price" : self.price,
              "comments_count" :  self.comments_count
              }
    # Python에서 복잢한 Byte 데이터를 JSON형식으로 변환할 때는 Byte를 문자열로 디코딩 한 후, JSON으로 변환하는 것이 일반적이다
    # 방법 1 : <byte 객체>.decode("utf-8"), utf-8 에러 발생 할 경우, utf-16으로 하기
    # 방법 2 : str(<byte 객체>, "utf-8")
    # 하지만 이미지 데이터는 텍스트 데이터가 아니기 때문에, Base64로 인코딩 한 후, 문자열로 디코딩 하는게 좋다

class Comments(db.Model):
  __tablename__ = "Comments"
  # FOREIGN KEY 설정
  id = db.Column(db.Integer, primary_key=True)
  product_id = db.Column(db.Integer, db.ForeignKey('Products.product_id'), )  
  name = db.Column(db.String(20))
  comment = db.Column(db.Text)
  
  # db.relationship을 사용하여 Product와 Comment 모델 간의 관계를 정의
  # Products : 관계를 설정할 대상 모델, 
  # backref=db.backref("comment", lazy=True) : 대상 모델에서 역참조를 설정
  # comment라는 속성의 식별자로 정의, 
  # lazy=True : 관계된 데이터를 처음 접근할 때 쿼리를 실행하여 로드한다 기본값임
  products = db.relationship("Products", backref=db.backref("comment", lazy=True) ) 

  def __init__(self, product_id=None, comment=None):
      self.product_id = product_id
      self.comment = comment

  def __repr__(self):
      return f'Comments : {self.comment}'  

  @property
  def values(self):
      return {"product_id" :  self.product_id,
              "comment" : self.comment,
              }
  
class Brands(db.Model) :
    __tablename__ = "Brands"

    brand_id = db.Column(db.Integer, autoincrement=True, primary_key=True )
    brand_logo = db.Column(db.LargeBinary)
    brand_name = db.Column(db.String(20))

    def __init__(self, brand_id=None, brand_logo=None, brand_name=None):
        self.brand_id = brand_id
        self.brand_logo = brand_logo
        self.brand_name = brand_name

    def to_dict(self):
       brand_logo = base64.b64encode(self.brand_logo).decode("utf-8")
       return {"brand_id" : self.brand_id,
               "brand_logo" : brand_logo,
               "brand_name" : self.brand_name
               }


  
  