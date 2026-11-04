# Subscription Management System

A responsive web application built with **Java (Servlets, JSP, and JDBC)** and **MySQL** for managing customers, plans, subscriptions, and payments.

## ✨ Features

*   **Admin Login**: Session-based login with salted PBKDF2 password verification.
*   **📊 Interactive Dashboard**: High-level metrics overview and recent activity tracking.
*   **Customer Management**: Persistent create, read, update, and delete operations.
*   **Subscription Plans**: Persistent tier and billing management.
*   **Subscriptions**: Persistent customer-to-plan assignments with renewal tracking.
*   **Payments & Reports**: Persisted payment tracking and reports based on database data.
*   **UI**: Dark mode, collapsible sidebar, and responsive pages.

## 🛠️ Tech Stack

*   **Backend**: Java 11+, Java Servlets, JSP (JavaServer Pages), JDBC
*   **Database**: MySQL 8+
*   **Frontend**: HTML5, CSS3 (Custom variables, Flexbox/Grid), Vanilla JavaScript
*   **Server**: Apache Tomcat 9.0+
*   **IDE**: IntelliJ IDEA (Configured with SmartTomcat)

## 🚀 Getting Started

### Prerequisites
*   [Java Development Kit (JDK) 11 or higher](https://www.oracle.com/java/technologies/javase-downloads.html)
*   [Apache Tomcat 9.0+](https://tomcat.apache.org/download-90.cgi)
*   IntelliJ IDEA (Ultimate or Community with SmartTomcat plugin)

### Installation

1.  **Clone the repository**:
    ```bash
    git clone https://github.com/yourusername/subscription-management-system.git
    cd subscription-management-system
    ```

2.  **Configure Tomcat in IntelliJ**:
    *   Go to `Run` > `Edit Configurations`.
    *   Add a new `Smart Tomcat` configuration.
    *   Set the **Tomcat server** path to your local Apache Tomcat installation.
    *   Set the **Deployment directory** to `src/main/webapp`.
    *   Set the **Context path** to `/` (or `/subscription-management-system`).

3.  **Create the database**:
    *   Install and start MySQL 8 or later.
    *   Run `src/main/resources/db/schema.sql` in MySQL Workbench or with the MySQL command line.
    *   Configure the following environment variables for the Tomcat run configuration:
        *   `SMS_DB_URL` (optional; defaults to `jdbc:mysql://localhost:3306/subscription_management?useSSL=false&serverTimezone=UTC`)
        *   `SMS_DB_USERNAME` (required)
        *   `SMS_DB_PASSWORD` (required)
    *   Never commit database credentials to GitHub.

4.  **Configure database credentials** in the Tomcat run configuration:
    *   Set `SMS_DB_USERNAME` to a MySQL user with access to `subscription_management`.
    *   Set `SMS_DB_PASSWORD` to that user's password.
    *   Optionally set `SMS_DB_URL` if MySQL is not at the default local URL.
    *   Never commit database credentials to GitHub.

5.  **Create the first admin user**:
    ```bash
    mvn compile
    java -cp target/classes com.subscription.security.PasswordUtil
    ```
    Enter a password of at least 12 characters in the terminal. The tool prints a salt and hash; use them in this SQL statement:
    ```sql
    INSERT INTO users (username, password_hash, password_salt, role, email)
    VALUES ('admin', '<hash printed by PasswordUtil>', '<salt printed by PasswordUtil>',
            'Admin', 'admin@example.com');
    ```
    Do not put a plain-text password in the database or source control.

6.  **Build and run the application**:
    ```bash
    mvn clean package
    ```
    *   Click the **Play/Run** button in IntelliJ.
    *   Navigate to `http://localhost:8080/login.jsp` in your web browser.

There are no default login credentials; the admin account must be created during setup.

## 📁 Project Structure

```text
Subscription-Management-System/
├── src/
│   └── main/
│       ├── java/com/subscription/servlet/  # Backend Java Servlets
│       │   ├── CustomerServlet.java
│       │   ├── DashboardServlet.java
│       │   ├── LoginServlet.java
│       │   └── ...
│       └── webapp/                         # Frontend Assets & Views
│           ├── css/                        # Stylesheets (variables, layout, forms, tables)
│           ├── js/                         # Frontend logic (app.js)
│           ├── images/                     # Logos and assets
│           ├── WEB-INF/views/              # Secure JSP pages and fragments (sidebar.jspf)
│           └── login.jsp                   # Public login page
└── pom.xml (if Maven is used) / lib/       # Dependencies
```

## 🤝 Contributing

Contributions, issues, and feature requests are welcome!
Feel free to check [issues page](https://github.com/yourusername/subscription-management-system/issues).

## 📝 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.
