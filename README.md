# payease-be
Repository for training fazztrack x BCA - ewallet project backend

## How to Run
To run it, clone and run the following commands:   
1. Clone github: 
    - make sure you have installed git on your pc.
    - select the folder where you want to save the clone repository.
    - open terminal/command prompt (right-click).
    - run the command ````git clone https://github.com/othernima08/payease-be.git```.
    - wait for the clone process to finish and the repository is in your folder.

2. web-server and database configuration
    - If you want to run spring boot on localhost, make sure you have an application like XAMPP as a local web-server
    - run apache
    - enter the browser and go to the page http://localhost/phpmyadmin
    - create a new database named "payease"
    - because there is some data that needs to be imported go to the import menu, select select file.
    - upload the transaction_categories.sql and providers.sql files in the resources/sql folder
    - done

2. Run spring boot:
    - If the folder has been saved, please open the folder using a text editor (example: VS Code)
    - run the following command ``mvnw spring-boot:run```
    - the program runs successfully.

## List of Service Methods

1. ## USER ##:
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

2. ## OTP ##:
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


## Online API Documentation
For complete REST API documentation and sample request bodies, you can access the following links: **https://documenter.getpostman.com/view/18356226/2s9YR85ZDm**

## Happy Coding~