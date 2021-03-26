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