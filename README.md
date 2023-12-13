# Dictionary App

Dictionary application (English to Vietnamese) for INT2204_2 OOP Course written in Java

## Table of contents

- [Authors](#authors)
- [Preview](#preview)
- [Key Features](#features)
- [Setup Instructions](#set-up)
    - [MYSQL Database Configuration](#mysql-database)
    - [Running the En-Vi Dictionary Application](#run)
- [Construction Tree](#tree)

## Authors
- Nhóm số 3 - INT2204_2 OOP:
    - [Nguyễn Bằng Anh](https://github.com/bangils37) - 21020007
    - [Nguyễn Quang Ninh](https://github.com/qninhdt) - 22021166 
    - [Lưu Huy Thành](https://github.com/luuhuythanh0111) - 22021124

## Preview



## Key Features

- A comprehensive English-to-Vietnamese dictionary equipped with an extensive words' database and seamlessly integrated with the Google Translate API.
- A feature-rich tool offering:
    - Search for Vietnamese word meanings.
    - Beautifully formatted word meanings in HTML.
    - Word addition functionality.
    - Word insertion from a file.
    - Word export to a file.
    - Word editing capabilities.
    - Word deletion functionality.
    - Google Translate-powered sentence translation (both English to Vietnamese and Vietnamese to English).
    - Google Translate Text-to-Speech functionality (both in English and Vietnamese).
    - MYSQL connectivity for a rich words' database.

## Setup Instructions

### MYSQL Database Configuration

- Install XAMPP: Download and install XAMPP, a popular web server distribution that includes MySQL.
- Start XAMPP Services: Launch XAMPP and ensure that both Apache and MySQL are running.
- Verify MySQL Port: Verify that the MySQL port is set to `3306`, which is the default port used by XAMPP.
- Create MySQL User: 
    - Name: `en-vi-dictionary`.
    - Password: `n1-02-dictionary`.
    - Enable option `[o] Create database with same name and grant all privileges.`
- Import `dictionary.sql` into the
  database `en-vi-dictionary` (`src/main/resources/sql/dictionary.sql`).

**Note**: You can configure your own username, port, password, ... by changing those accordingly
in `src/main/java/dictionary/server/DatabaseDictionary.java`.

### Running the En-Vi Dictionary Application

- Prerequisites: Ensure you have installed JDK 17 or higher and Maven (Apache Maven). For Windows users, ensure the JAVA_HOME environment variable is set correctly.
- Start XAMPP (Start Apache and MYSQL).
- Application Execution: Open a command prompt and navigate to the project directory (DictionaryApp). Execute the following command:
  ```
  cd DictionaryApp
  mvn clean javafx:run
  ```
  
## Construction Tree
![cay cau truc](https://github.com/bangils37/EnglishApp_JavaProject/assets/124591525/3c1a5edc-7add-4197-af54-f2a79c0b6d1b)
![cay cau truc 2](https://github.com/bangils37/EnglishApp_JavaProject/assets/124591525/cd638959-96fb-4985-846b-18186094bb14)
