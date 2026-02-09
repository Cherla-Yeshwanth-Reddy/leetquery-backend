# LeetQuery Backend

> **SQL Execution Engine for LeetQuery Mobile Learning App**

A production-ready Spring Boot REST API that executes SQL queries against a MySQL database, designed for the LeetQuery mobile application to provide real-time SQL learning experiences.

[![Java](https://img.shields.io/badge/Java-17-orange.svg)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.2-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![MySQL](https://img.shields.io/badge/MySQL-8.0-blue.svg)](https://www.mysql.com/)

---

## 🚀 Features

- ✅ **REST API** for SQL query execution
- ✅ **Multi-Query Support**: SELECT, INSERT, UPDATE, DELETE, CREATE, ALTER, DROP
- ✅ **Automatic Schema Initialization** with tutorial database
- ✅ **Comprehensive Error Handling** with SQL state codes
- ✅ **CORS Enabled** for mobile app integration
- ✅ **Cloud-Ready** for Railway deployment

---

## 📋 Prerequisites

- **Java 17** or higher
- **Maven 3.6+**
- **MySQL Database** (Railway, AWS RDS, or local)

---

## 🏃 Running Locally

### 1. Set Environment Variable

The application requires a single environment variable containing your MySQL JDBC connection string.

**Windows PowerShell:**
```powershell
$env:MYSQL_URL="jdbc:mysql://[host]:[port]/[database]?user=[username]&password=[password]"
```

**Linux/Mac:**
```bash
export MYSQL_URL="jdbc:mysql://[host]:[port]/[database]?user=[username]&password=[password]"
```

**Example:**
```powershell
$env:MYSQL_URL="jdbc:mysql://containers-us-west-123.railway.app:6543/railway?user=root&password=yourpassword"
```

### 2. Build the Project

```bash
mvn clean package
```

### 3. Run the Application

```bash
java -jar target/leetquery-backend-1.0.0.jar
```

The server will start on **http://localhost:8080**

---

## 🌐 API Endpoints

### Execute Query

**Endpoint:** `POST /executeQuery`

**Request:**
```json
{
  "query": "SELECT * FROM students LIMIT 5;"
}
```

**Response (Success):**
```json
{
  "success": true,
  "queryType": "SELECT",
  "headers": ["id", "name", "department_id", "gpa", "enrollment_year"],
  "rows": [
    ["1", "Alice Johnson", "1", "3.85", "2022"],
    ["2", "Bob Smith", "1", "3.42", "2021"]
  ],
  "rowCount": 2,
  "message": "2 row(s) returned"
}
```

**Response (Error):**
```json
{
  "success": false,
  "error": "Table 'railway.nonexistent' doesn't exist",
  "sqlState": "42S02",
  "errorCode": 1146,
  "queryType": "ERROR"
}
```

### Health Check

**Endpoint:** `GET /health`

**Response:** `LeetQuery Backend is running!`

---

## 🗄️ Database Schema

The application automatically creates a tutorial database with sample data:

| Table | Description |
|-------|-------------|
| **departments** | Academic departments (5 records) |
| **instructors** | Faculty members (7 records) |
| **students** | Student records (15 records) |
| **courses** | Course catalog (10 records) |
| **enrollments** | Student enrollments (20 records) |

Schema initialization happens automatically on startup via `schema.sql`.

---

## ☁️ Deploy to Railway

### Step 1: Create Railway Project

1. Go to [Railway.app](https://railway.app)
2. Create a new project
3. Add **MySQL** database service
4. Copy the **MySQL URL** from the database settings

### Step 2: Deploy from GitHub

1. Push this code to GitHub:
```bash
git init
git add .
git commit -m "Initial commit"
git branch -M main
git remote add origin https://github.com/yourusername/leetquery-backend.git
git push -u origin main
```

2. In Railway, click **New** → **GitHub Repo**
3. Select your `leetquery-backend` repository
4. Railway will automatically detect the Spring Boot app

### Step 3: Configure Environment Variables

In Railway dashboard, add environment variable:

- **Key:** `MYSQL_URL`
- **Value:** Your Railway MySQL connection string (from Step 1)

Format: `jdbc:mysql://[host]:[port]/railway?user=root&password=[password]`

### Step 4: Deploy

Railway will automatically:
- Build the project using Maven
- Run the application using the `Procfile`
- Assign a public URL

Your API will be available at: `https://your-app.railway.app`

---

## 🧪 Testing

### Test with cURL

**SELECT Query:**
```bash
curl -X POST https://your-app.railway.app/executeQuery \
  -H "Content-Type: application/json" \
  -d '{"query": "SELECT * FROM students LIMIT 5;"}'
```

**INSERT Query:**
```bash
curl -X POST https://your-app.railway.app/executeQuery \
  -H "Content-Type: application/json" \
  -d '{"query": "INSERT INTO students (name, department_id, gpa, enrollment_year) VALUES (\"New Student\", 1, 3.7, 2024);"}'
```

**Show Tables:**
```bash
curl -X POST https://your-app.railway.app/executeQuery \
  -H "Content-Type: application/json" \
  -d '{"query": "SHOW TABLES;"}'
```

---

## 🔧 Configuration

### Environment Variables

| Variable | Description | Required | Default |
|----------|-------------|----------|---------|
| `MYSQL_URL` | JDBC connection string | ✅ Yes | - |
| `PORT` | Server port | ❌ No | 8080 |

### Application Properties

Edit `src/main/resources/application.properties` to customize:

- Connection pool settings (HikariCP)
- SQL initialization behavior
- Logging levels

---

## 🛡️ Security Notes

⚠️ **Important:** This backend executes arbitrary SQL queries for educational purposes.

**For Production:**
- Implement query whitelisting or sandboxing
- Add rate limiting
- Use database user with restricted permissions
- Add authentication/authorization
- Implement input validation beyond basic checks

---

## 📁 Project Structure

```
leetquery-backend/
├── src/main/java/com/leetquery/backend/
│   ├── LeetQueryBackendApplication.java    # Main application
│   ├── controller/QueryController.java     # REST endpoints
│   ├── service/QueryExecutionService.java  # SQL execution logic
│   ├── model/                              # DTOs
│   └── config/CorsConfig.java              # CORS configuration
├── src/main/resources/
│   ├── application.properties              # Configuration
│   └── schema.sql                          # Database schema
├── pom.xml                                 # Maven dependencies
└── Procfile                                # Railway deployment config
```

---

## 🐛 Troubleshooting

**Error: "No suitable driver found"**
- Ensure `MYSQL_URL` starts with `jdbc:mysql://`

**Error: "Access denied"**
- Verify username and password in `MYSQL_URL`

**Error: "Unknown database"**
- Check database name in `MYSQL_URL`

**Schema not initializing:**
- Verify `spring.sql.init.mode=always` in `application.properties`
- Check application logs for SQL errors

**Railway deployment fails:**
- Ensure `Procfile` exists in root directory
- Verify `MYSQL_URL` environment variable is set in Railway
- Check Railway build logs for errors

---

## 📄 License

MIT License - Free to use for educational purposes

---

## 🤝 Contributing

This is an educational project. Contributions welcome!

---

## 📧 Support

For issues or questions, please open an issue on GitHub.

---

**Built with ❤️ for SQL learners everywhere**
