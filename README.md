# payease-be
Repository for training fazztrack x BCA - ewallet project backend

## How to Run
Untuk menjalankannya, clone dan jalankan perintah berikut:   
1. Clone github: 
    - pastikan anda telah menginstall git di pc anda.
    - pilih folder tempat menyimpan clone repository ini.
    - buka terminal/command prompt (klik kanan).
    - jalankan perintah ````git clone https://github.com/othernima08/payease-be.git```.
    - tunggu proses clone selesai dan repository sudah ada di folder anda.

2. web-server dan database configuration
    - Jika anda ingin menjalankan spring boot di localhost, pastikan anda memiliki aplikasi seperti XAMPP sebagai web-server lokal
    - jalankan apache
    - masuk ke browser dan pergi ke halaman http://localhost/phpmyadmin
    - buatlah database baru bernama "payease"
    - karena ada beberapa data yang perlu di import pergi ke menu import, pilih select file.
    - upload file transaction_categories.sql dan providers.sql yang ada di folder resources/sql
    - selesai

2. Run spring boot:
    - Jika folder sudah tersimpan, silahkan buka folder tersebut menggunakan text editor (contoh: VS Code)
    - jalankan perintah berikut ```mvnw spring-boot:run```
    - program berhasil di jalankan.

## List of Service Methods

1. ## User :
    - **Register**: POST - localhost:9090/users/register
    - **Login**: POST - localhost:9090/users/login
    - **Get All Users**: Get - localhost:9090/users
    - **Get User By Id**: GET - localhost:9090/users/{user id}
    - **Create PIN**: PUT - localhost:9090/users/create-pin
    - **Reset Password**: PUT - localhost:9090/users/reset-password
    - **Change PIN**: PUT - localhost:9090/users/change-pin
    - **Change Password Request**: PUT - localhost:9090/users/change-password-after-login
    - **Request Reset Password Link**: PUT - localhost:9090/users/find-email-reset
    - **Delete Phone Number**: DELETE - localhost:9090/users/delete-phone-number/{user id}
    - **Add Phone Number**: PUT  localhost:9090/users/add-phone-number
    - **Update Image**: PUT  localhost:9090/users/update-image?{useParams, userId, value {user id}}
    - **Get Image**: GET    - **Update Image**: PUT  localhost:9090/users/update-image/{image id}

2. ## OTP :
    - **Register**: POST - localhost:9090/otp/generate
    - **Login**: PUT - localhost:9090/otp

3. ## Provider ##
    - **Create Provider**: POST - localhost:9090/providers

4. ## Transaction Categories ##
    - **Create Transaction Categories**: POST localhost:9090/transaction-categories

5. ## Virtual Accounts ##
    - **Create Virtual accounts**: POST localhost:9090/virtual-accounts?phoneNumber=081111111111
    - **Get By User**: GET localhost:9090/virtual-accounts/{user id}
    - **Delete virtual account**: GET localhost:9090/virtual-accounts/{user id}


6. ## Transactions ##
    - **Top Up**: POST localhost:9090/transactions/top-up
    - **Transfer**: POST localhost:9090/transactions/transfer
    - **Top Up History**: GET localhost:9090/transactions/top-up-history/{user id}
    - **Top Up History**: GET localhost:9090/transactions/transaction-history/0a9613db-78bf-42f0-b57b-9001d852dff6
    - **Top Up Pay**: PUT localhost:9090/transactions/top-up/{Payment Code}
    - **User Top Up History By Status**: GET localhost:9090/transactions/top-up-history?{
    userparams,
    userId={user id}
    isSuccess={true}
    }
 - **User Transaction History By Date Range**:
    localhost:9090/transactions/transaction-history-filter-date?userId=ae7b4891-e4f3-456e-bcca-785afe0b3b4d&startDate=Tuesday, 03 Oct 2023&endDate=Sunday, 15 Oct 2023


## Documentation API Online
Untuk dokumentasi lengkap REST API dan contoh request body, anda dapat mengakses link berikut ini: **https://documenter.getpostman.com/view/18356226/2s9YR85ZDm**

## Happy Coding~
