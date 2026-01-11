# Simple Inventory Spring
This repository is using [git-flow](https://github.com/gittower/git-flow-next/) with the following branches:
- main: Production releases
- develop: Development

## How To Run
1. Modify database config on ```application.properties```
2. Run ```mvn spring-boot:run```

## API Endpoint
- **POST /products**
  <br>Digunakan untuk menambahkan produk
    - Request Body:
        - name: String
        - stock: Integer
        - price: Double
        - description: String
- **GET /products**
  <br>Digunakan untuk menampilkan seluruh produk
- **GET /products/{id}**
  <br> Digunakan untuk menampilkan informasi produk secara detail
    - Path Variable:
        - id: Integer
- **UPDATE /products/{id}**
  <br> Digunakan untuk mengubah informasi produk
    - Path Variable:
        - id: Integer
    - Request Body:
        - name: String
        - stock: Integer
        - price: Double
        - description: String
- **PATCH /products/{id}/stock**
  <br> Digunakan untuk menambahkan stok penjualan produk
    - Path Variable:
        - id: Integer
    - Request Body:
        - quantity: Integer
- **DELETE /products/{id}**
  <br> Digunakan untuk menghapus produk
    - Path Variable:
        - id: Integer
- **PATCH /products/{id}/sales**
  <br> Digunakan untuk melakukan penjualan produk
    - Path Variable:
        - id: Integer
    - Request Param:
        - quantity: Integer
- **POST /users/register**
  <br>Digunakan mendaftarkan User
    - Request Param:
        - username: String
        - password: String
        - email: String
- **POST /users/login**
  <br>Digunakan melakukan login User
    - Request Param:
        - username: String
        - password: String