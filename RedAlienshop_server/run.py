# Flask              3.0.1
# Flask-Migrate      4.0.7 
# Flask-SQLAlchemy   3.1.1
# SQLAlchemy         2.0.25

from app import create_app

flask_app = create_app()

logo = """ 
 #####    ######   ####       ##     ##        ####    ######   ##  ##             ####    ##  ##    ####    #####
 ##  ##   ##       ## ##     ####    ##         ##     ##       ### ##            ##  ##   ##  ##   ##  ##   ##  ##
 ##  ##   ##       ##  ##   ##  ##   ##         ##     ##       ######            ##       ##  ##   ##  ##   ##  ##
 #####    ####     ##  ##   ######   ##         ##     ####     ######             ####    ######   ##  ##   #####
 ####     ##       ##  ##   ##  ##   ##         ##     ##       ## ###                ##   ##  ##   ##  ##   ##
 ## ##    ##       ## ##    ##  ##   ##         ##     ##       ##  ##            ##  ##   ##  ##   ##  ##   ##
 ##  ##   ######   ####     ##  ##   ######    ####    ######   ##  ##             ####    ##  ##    ####    ##
                                                                         """



if __name__ == "__main__":
  print(logo)
  flask_app.run(host="0.0.0.0",debug=True, threaded=True, port=8080 )
  
# db 오류 발생할 때, 먼저 DB를 생성 했는지 체크하자
# migration 디렉토리 + db파일이 이미 생성되어 있다면 삭제하고 다시 아래 DB생성 과정 수행해야 한다

# DB 생성 과정
# 1. Terminal에 flask db init 입력
# 2. Terminal에 flask db migrate 입력
# 3. Terminal에 flask db upgrade
# 다 입력 후, SQLite browser로 db파일을 살펴보면 테이블이 생성되어 있을 것이다
