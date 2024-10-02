from util import *

# 아래 경로들을 수정하여, 자신이 원하는 제품 이미지, 브랜드 로고 이미지를 삽입할 수 있습니다
db_path = r"C:\Users\whdrj\Desktop\Insecure_App_Project\RedAlien_shop_server\instance\redAlienShop.db"
products_dir_path = r'C:\Users\whdrj\Desktop\product_img'
brandsLogo_dir_path = r"C:\Users\whdrj\Desktop\brand_logo"


# ---------- db에 Products 이미지들 삽입 ---------- 
# 사용시 조건
# 1. DB에 넣을 제품 이미지 파일의 확장자는 png이어야 합니다 ( svg는 텍스트 데이터라 불가합니다 )
# 2. DB에 넣을 제품 이미지 파일의 이름은 "브랜드-제품이름-가격" 구조이어야 합니다
# Ex) Nike-V2k Run Pure Platinum Wolf Grey-115,000.png, Discovery-남성 담보루 에센셜 트레이닝 하이넥 자켓-129,000.png

insert_ProductsImage_ToDB(db_path, products_dir_path)
# --------------------------------------------------


# ---------- db에 BrandLogo SVG 삽입 ---------- 
# 사용시 조건 
# 1. DB에 넣을 브랜드 로고 이미지 파일의 확장자는 png이어야 합니다 ( svg는 텍스트 데이터라 불가합니다 )
# 2. DB에 넣을 브랜드 로고 이미지 파일의 이름은 앞에 "ic_" 을 붙여야 합나다  
# Ex) ic_Discovery.png, ic_Mark gonzales.png 

# insert_Brandlogo_ToDB(db_path, brandsLogo_dir_path)
# ----------------------------------------------


# ---------- db의 테이블 목록 확인 ----------
# check_existing_tables(db_path)
# -------------------------------------------



