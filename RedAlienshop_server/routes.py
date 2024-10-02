from flask import render_template, request, jsonify, Response
from models import User, Products, Comments, Brands
import simplejson as json
import base64


# flask의 jsonify()를 사용하여 client에게 응답할 경우, android에서 JSONObject 클래스를 사용하여 처리 할 수 없다
# simplejson.dumps를 사용할 것


def register_routes(app, db):
  json
  def usageGuide():
    print("Guid Test")

  @app.errorhandler(500)
  def internal_serverError(error):
    print(f"[!] {error}")

  @app.route("/", methods=["GET"])
  def index():
    people = User().query.all()  # models.py에 정의한 User클래스의 __repr__이 호출됨
    
    return str(people), 200
  

  @app.route("/login", methods=["POST", ])
  def login():
    data = None
    responseMsg = "Wrong Credentials !"
    points = 0

    json_obj = request.get_json()  # 전송받은 request에서 json 가져오기
    request_username = json_obj.get("username")
    request_password = json_obj.get("password")

    u = User.query.filter(User.username == request_username).first()
    p = User.query.filter(User.password == request_password).first()

    if(u and p):
      responseMsg = "Correct Credentials !"
      query_result = User.query.filter_by(username=request_username, password=request_password).first()
  
      if query_result :
        points = query_result.points

      data = {"message" : responseMsg, "request_username" : request_username, "points" : points  }
    else :
      data = {"message" : responseMsg, "request_username" : request_username}
        
    result = json.dumps(data)
    print(result)
    return result
    
  @app.route("/getAccounts", methods=["POST"])
  def getaccount():
    responseMsg = "Wrong Credentials !"
    
    json_obj = request.get_json()
    request_fullname = json_obj.get("fullname")
    request_phonum = json_obj.get("phonum")
    request_username = json_obj.get("username")

    f = User.query.filter(User.fullname == request_fullname).first()
    u = User.query.filter(User.phone_number == request_phonum).first()
    p = User.query.filter(User.username == request_username).first()

    if f and u and p :
      responseMsg = "Correct Credentials !"
      
    data = {"message" : responseMsg, "request_fullname" : request_fullname, "request_phone_number" : request_phonum, "request_username" : request_username}
    result = json.dumps(data)
    print(result)
    return result
  
  @app.route("/resetPassword", methods=["POST"])
  def changePassword():
    responseMsg = "Password reset Failed !"
    json_obj = request.get_json()
    username = json_obj.get("username")
    newPwd = json_obj.get("newPwd")

    user = User.query.filter(User.username == username).first()
    if(user):
      user.password = newPwd
      db.session.commit()
      responseMsg = "Password reset Successful !"

    data = {"message" : responseMsg, "username" : str(user.username), "newPwd" : str(user.password)}
    result = json.dumps(data)
    print(result)
    return result
  
  @app.route("/createAccount", methods=["POST"])
  def createAccount():
    responseMsg = "Create Account Failed !"
    subMsg = {"TAG" : "None"}
    
    json_obj = request.get_json()
    fullname = json_obj.get("fullname")
    phonum = json_obj.get("phonum")
    username = json_obj.get("username")
    password = json_obj.get("password")

    isFullname = User.query.filter(User.fullname == fullname ).first()
    isUser = User.query.filter(User.username == username).first()
    
    if(not isFullname and not isUser):
      user = User(fullname, phonum, username, password, 1000)
      db.session.add(user)
      db.session.commit()
      responseMsg = "Create Account Successful !" 
    else:
      if(isFullname):
        subMsg["TAG"] = "fullname"
        subMsg.update({"msg" : "already exists"})
      elif(isUser):
        subMsg["TAG"] = "username"
        subMsg.update({"msg" : "already exists"})

    if("msg" not in subMsg):
      result = f"{subMsg["TAG"]}"
    else:
      result = f"{subMsg["TAG"]} {subMsg["msg"]}"

    data = {"message" : responseMsg, "fullname" : fullname, "username" : username, "subMsg" : result}
    result = json.dumps(data)
    print(result)
    return result
  

  @app.route("/getProducts",methods=["GET"])
  def getProducts():
    products = Products().query.all()
    product_dict = [product.to_dict() for product in products]
    
    print("getProducts() : Successed !")
    return Response(json.dumps(product_dict), content_type="application/json",mimetype="application/json" )
   

  @app.route("/getBrands", methods=["GET"])
  def getBrands():
    brands = Brands().query.all()
    brands_dict = [brand.to_dict() for brand in brands] 
    print("getBrands() : Successed !")
    
    return Response(json.dumps(brands_dict), content_type="application/json",mimetype="application/json" )
  
  @app.route("/checkout", methods=["POST"])
  def checkout():
    data = {"message" : "Something Wrong"}

    result_dict = None

    json_obj = request.get_json()
    request_username = json_obj.get("username")
    request_password = json_obj.get("password")
    request_userUsePoints = json_obj.get("userUsePoints")

    user = User.query.filter(User.username == request_username, User.password == request_password).first()
    if(user):
      if(request_userUsePoints != None):
        previous_points = user.points
        current_points = previous_points - request_userUsePoints
        user.points = current_points
        db.session.commit()

        data["message"] = "Successful"
        data["username"] = request_username
        data["current_points"] = current_points

        print(f"\n[+] checkout() : username = {request_username}, Update points")
        print(f"\n[+] checkout() : previous_points = {previous_points}, current_points : {current_points}")
      else:
        print(f"\n[+] checkout() : request_userUsePoints is None")
        
    else:
      print("\n[+] checkout() : 쿼리결과가 없습니다")
  
    print(f"\n[+] checkout() : {data}")
    return Response(json.dumps(data), content_type="application/json",mimetype="application/json")
  
  @app.route("/getComments", methods=["POST"])
  def getComments():
    data_list = []
    responseMsg = {"message" : "Empty"}
    json_obj = request.get_json(silent=True)
    
    if json_obj:
      request_product_id =  json_obj.get("product_id")
      if request_product_id:
        queryResultObjList = Comments.query.filter(Comments.product_id == request_product_id).all()
        if queryResultObjList:
          responseMsg["message"] = "Exist"
          for obj in queryResultObjList:
            array = {"name" : obj.name, "comment" : obj.comment }
            data_list.append(array)

    data_list.insert(0, responseMsg)
    return Response(json.dumps(data_list), content_type="application/json",mimetype="application/json")  
    