# Simple Inventory Spring
This repository is using [git-flow](https://github.com/gittower/git-flow-next/) with the following branches:
- main: Production releases
- develop: Development

## How To Run
Modify config on ```application.properties```

## Run Locally
Run ```mvn spring-boot:run```

## Run Docker
### Docker Compose
Run ```docker-compose up --build```

### Docker Image
you can use ```qiraht/simple-inventory-spring``` image and run ```docker run -p 8080:8080 qiraht/simple-inventory-spring```

## API Endpoint
### Swagger
 For Swagger API Documentation access ```/swagger-ui/index.html```
### POST /products
Used to add products. Authentication Required
    - Request Body:
        - name: String
        - stock: Integer
        - price: Double
        - description: String
### GET /products
Used to get product. Public Endpoint (No Authentication Required)
### GET /products/{id}
Used to get product detail. Public Endpoint (No Authentication Required)
- Path Variable:
    - id: Integer
### UPDATE /products/{id}
Used to update product. Authentication Required
- Path Variable:
    - id: Integer
- Request Body:
    - name: String
    - stock: Integer
    - price: Double
    - description: String
### PATCH /products/{id}/stock
Used to add stock. Authentication Required
- Path Variable:
    - id: Integer
- Request Body:
    - quantity: Integer
### DELETE /products/{id}
Used to delete product. Authentication Required
- Path Variable:
    - id: Integer
### PATCH /products/{id}/sales
Used to sell product. Authentication Required
- Path Variable:
    - id: Integer
- Request Body:
    - quantity: Integer
### POST /users/register
Used to register user. Public Endpoint (No Authentication Required)
- Request Param:
    - username: String
    - password: String
    - email: String
### POST /users/login
Used to login user. Public Endpoint (No Authentication Required)
- Request Param:
    - username: String
    - password: String