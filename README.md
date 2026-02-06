# Has MS - Restaurant Management System

**Has MS** is a comprehensive Restaurant Management System built with Java Spring Boot. It is designed to streamline restaurant operations, managing everything from order processing to inventory management.

## 🚀 Technologies Used

- **Java 17**: Core programming language.
- **Spring Boot 3.2.1**: Framework for building the application.
  - `spring-boot-starter-web`: Building web applications, including RESTful applications.
  - `spring-boot-starter-data-jpa`: Persisting data in SQL stores with Java Persistence API using Spring Data and Hibernate.
- **MySQL**: Relational database management system (`mysql-connector-j` driver).
- **Maven**: Dependency management and build tool.

## 🛠️ Prerequisites

Before you begin, ensure you have met the following requirements:

- **Java Development Kit (JDK) 17** or later installed.
- **Maven** installed (or use the provided `mvnw` wrapper if available).
- **MySQL Server** installed and running.

## ⚙️ Installation & Setup

1. **Clone the repository**
   ```bash
   git clone https://github.com/HasCafe/Has-MS.git
   cd Has-MS
   ```

2. **Configure the Database**
   - Create a MySQL database (e.g., `has_ms_db`).
   - Update `src/main/resources/application.properties` (or `application.yml`) with your database credentials:
     ```properties
     spring.datasource.url=jdbc:mysql://localhost:3306/has_ms_db
     spring.datasource.username=your_username
     spring.datasource.password=your_password
     spring.jpa.hibernate.ddl-auto=update
     ```

3. **Build the Application**
   ```bash
   mvn clean install
   ```

4. **Run the Application**
   ```bash
   mvn spring-boot:run
   ```

   Alternatively, you can run the generated JAR file:
   ```bash
   java -jar target/restaurant-1.0-SNAPSHOT.jar
   ```

## 📝 Usage

Once the application is running, you can access the API or Web Interface (depending on implementation) typically at:
`http://localhost:8080`

## 🤝 Contributing

Contributions are welcome! Please follow these steps:

1. Fork the repository.
2. Create a new branch (`git checkout -b feature/your-feature`).
3. Commit your changes (`git commit -m 'Add some feature'`).
4. Push to the branch (`git push origin feature/your-feature`).
5. Open a Pull Request.
