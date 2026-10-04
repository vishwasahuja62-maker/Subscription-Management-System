# Subscription Management System 🚀

A modern, robust, and responsive web application built with **Java (Servlets & JSP)** for managing SaaS subscriptions, customer billing, and payment tracking. Designed with a premium UI/UX, featuring a fully functional dark mode and a dynamic collapsible sidebar.

## ✨ Features

*   **🔑 Admin Login**: Login interface with session-based authentication structure.
*   **📊 Interactive Dashboard**: High-level metrics overview and recent activity tracking.
*   **👥 Customer Management**: Full CRUD interface to add, edit, and manage subscriber details.
*   **📦 Subscription Plans**: Tiered billing management (e.g., Basic, Pro, Enterprise).
*   **💳 Payments & Billing**: Ledger for tracking transaction history and payment statuses.
*   **🌙 Premium UI/UX**:
    *   System-wide persistent Dark Mode (saves preference in `localStorage`).
    *   Collapsible navigation sidebar for maximizing screen real estate.
    *   Modern typography and micro-interactions for a snappy user experience.
    *   Custom logo masking to eliminate JPEG background artifacts.

> **Note:** The application currently utilizes an **in-memory data store** for demonstration purposes. Full JDBC/Database integration is pending.

## 🛠️ Tech Stack

*   **Backend**: Java 11+, Java Servlets, JSP (JavaServer Pages)
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

3.  **Run the Application**:
    *   Click the **Play/Run** button in IntelliJ.
    *   Navigate to `http://localhost:8080/login.jsp` in your web browser.

### Default Credentials
For demonstration purposes, you can use the following default credentials (if configured in your Servlet logic):
*   **Username**: `admin`
*   **Password**: `admin123`

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
