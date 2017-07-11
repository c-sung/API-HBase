### 新增四項功能於DataApi.java     
### 預設port為8095，可自行更改。      
### 資料儲存於server之arraylist，一旦關閉伺服器，資料將被清除。
### 附件users內含四名使用者之JSON假資料，可直接使用。     
## 1. POST : 新增資料。   
於postman選擇POST，路徑： http:localhost:8095/data   
於body打上欲新增資料之JSON格式，send。     
![image](http://i.imgur.com/Euo1R2g.png)      
伺服器回應：ok      
    
      
## 2. GET : 查詢資料。    
於postman選擇GET，路徑：http://localhost:8095/data/(手機號碼/家裡電話/姓名/email)      
![image2](http://i.imgur.com/Mm4dLLS.png)     
伺服器回應：所查詢之資料。     
若查詢無結果：     
![fail1](http://i.imgur.com/cmFfW4m.png)    
伺服器回應：查無此人。
      
          
## 3. PUT : 更新資料。
於postman選擇PUT，路徑：http://localhost:8095/data/(手機號碼/家裡電話/姓名/email)      
於BODY打上欲更改之資料(JSON)       
![patch](http://i.imgur.com/lyEP3Ou.png)      
伺服器回應：成功更新資料。 '新資料'       


## 4. DELETE : 刪除資料。
於postman選擇DELETE，路徑：http://localhost:8095/data/(手機號碼/家裡電話/姓名/email)       
![delete](http://i.imgur.com/qL27fIV.png)         
伺服器回應：已成功移除資料。
