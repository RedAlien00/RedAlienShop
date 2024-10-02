import sqlite3
import os

# directory의 파일들을 {path : [브랜드이름, 제품이름, 가격] } 으로 리턴
def productImg_to_dict(directory_path : str) -> dict:
  file_abspath_dict = {}
   
  for file in os.listdir(directory_path):  
    file_abspath = os.path.join(directory_path, file)
    basename = os.path.basename(file)
    name_without_ext = os.path.splitext(basename)[0]
    split_text_list =  name_without_ext.split("-")
    file_abspath_dict[file_abspath] = split_text_list

  return file_abspath_dict

# {path : [브랜드이름, 제품이름, 가격] }를 {이미지 binary : [브랜드이름, 제품이름, 가격] } 으로 리턴 
def read_image_data(productImg_dict : dict) -> dict:
  old_productImg_dict = productImg_dict
  new_productImg_dict = {}
  imgData_list = []
  keys = old_productImg_dict.keys()     # key들만 리스트로 생성

  for key in keys:
    with open(key, "rb") as file:    # 바이너리 모드로 읽기
      img_data = file.read()
      imgData_list.append(img_data)

  for key, imgData in zip(keys, imgData_list):
    new_productImg_dict[imgData] = old_productImg_dict[key]

  return new_productImg_dict

def insert_ProductsImage_ToDB(db_path, products_dir_path):
  file_abspath_dict = productImg_to_dict(products_dir_path)
  file_data_dict = read_image_data(file_abspath_dict)

  conn = sqlite3.connect(db_path)
  cursor = conn.cursor()
  
  for key, list in file_data_dict.items():
    # cursor.execute(f"INSERT INTO Products(image, title, price) VALUES({file_data}, 'test', 1000)") 이렇게하면 Syntax오류뜸 
    # 값을 직접 쿼리에 넣으면 에러가 발생함
    cursor.execute("INSERT INTO Products(image, brand, title, price) VALUES(?, ?, ?, ?);", (key, list[0], list[1], list[2]) )

  conn.commit()
  conn.close()
  print("[+] insert_ProductsImage_ToDB() : Done !")


# ---------------------------------------------------------------------------------------------------------

def brandlogo_path_to_dict(brandsLogo_dir_path):
  brandlogo_dict = {}

  for each_path in os.listdir(brandsLogo_dir_path):

    absPath = os.path.join(brandsLogo_dir_path, each_path)

    baseName = os.path.basename(each_path)
    name_without_ext = os.path.splitext(baseName)[0]
    if name_without_ext.startswith("ic_"):
      name_without_ext = name_without_ext[3:]
      brandlogo_dict[name_without_ext] = absPath  
    
  return brandlogo_dict

def read_Brandlogo(brandsLogo_dict):
  brandLogo_data_dict = {}

  for key in brandsLogo_dict:
    with open(brandsLogo_dict[key], "rb") as logo_file:
      logodata = logo_file.read()
      brandLogo_data_dict[key] = logodata
  return brandLogo_data_dict    

def insert_Brandlogo_ToDB(db_path, brandsLogo_dir_path):
  brandLogo_dict = brandlogo_path_to_dict(brandsLogo_dir_path)
  data_dict = read_Brandlogo(brandLogo_dict)

  conn = sqlite3.connect(db_path)
  cursor = conn.cursor()

  for key in data_dict:
    cursor.execute("INSERT INTO Brands(brand_logo, brand_name) VALUES(?, ?);", (data_dict[key], key))

  conn.commit()
  conn.close()
  print("[+] insert_Brandlogo_ToDB() : Done !")



def check_existing_tables(db_file):
  # SQLite 데이터베이스 파일에 연결
  conn = sqlite3.connect(db_file)
  cursor = conn.cursor()

  # 데이터베이스에서 테이블 목록 조회
  cursor.execute("SELECT name FROM sqlite_master WHERE type='table';")
  tables = cursor.fetchall()

  # 테이블 목록 출력
  if tables:
      print("----DB 테이블----")
      for table in tables:
          print(table[0])
  else:
      print("DB에 테이블이 존재하지 않습니다")

  # 연결 종료
  conn.close()