# Simple REST APIs
This project provides some simple REST APIs to do somethings:
* Create new user
* Update user role
* Get user info
* Export excel file
* Download exported file

Swagger for above APIs was built.
Please refer it for usage.
Default swagger path: [swagger][http://localhost:3000/demo-swagger].

# Authentication
### User Accounts
This project has simple DB. This stored 2 users:
* normal user with: username `defaultuser`, password `password@123` 
* admin user with:   username `admin`, password `admin@123`
### JWT Authentication
Bearer authorization method was applied in this project.
JWT Token (encoded user info) was use for this method.
Client application can decode this token to get user info.

To get `jwt-token`, please refer swagger for login API. With received JWT, you can read user information in this token.

Then use received JWT Token to fill `Bearer <jwt-token>` for header authorization for required authorized APIs.

### Getting Started
Start the server on `localhost:3000`
```bash
$ clj -A:server
2020-09-28 09:27:18.207:INFO:oejs.Server:main: Started @3729ms
Server running on port 3000
```
Open swagger, try to use api by fill required fields of api.
[swagger][http://localhost:3000/demo-swagger]

# Simple Web Application
This is a simple web-app to perform some rest apis. Some functions of this app:
* Login/logout
* Export users into excel file.
* Download exported file.

### Release mode
There is no http server for web-app in release mode.
To run it need to setup nginx to server static web-app in resources/public.

Release build:
```bash
# Install npm package
npm i
# build css
gulp b
# Build js
shadow-cljs -M:frontend release frontend
```
### Dev mode
Start each session below in separate console:
```bash 
# watch css
gulp w
```
```bash 
# watch js
shadow-cljs -M:frontend watch frontend
```
Open app by link [localhost][http://localhost:3001]

*** Notes: Normal user don't have much function on this app. Please login with admin user.
