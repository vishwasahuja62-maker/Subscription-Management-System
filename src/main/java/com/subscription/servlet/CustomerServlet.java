package com.subscription.servlet;

import com.subscription.dao.CustomerDAO;
import com.subscription.model.Customer;
import java.io.IOException;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.regex.Pattern;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/CustomerServlet")
public class CustomerServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$");
    private static final Pattern PHONE_PATTERN = Pattern.compile("^[6-9]\\d{9}$");
    private final CustomerDAO customerDAO = new CustomerDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            List<Customer> customers = customerDAO.getAllCustomers();
            if (ServletSupport.wantsJson(request)) {
                ServletSupport.writeJson(response, HttpServletResponse.SC_OK, customers);
                return;
            }
            request.setAttribute("customers", customers);
            request.getRequestDispatcher("/WEB-INF/views/customers.jsp").forward(request, response);
        } catch (SQLException e) {
            log("Unable to load customers.", e);
            ServletSupport.writeError(
                    request, response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Could not load customers.");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        String action = request.getParameter("action");
        try {
            if ("add".equals(action)) {
                Customer customer = buildCustomer(request);
                customer.setId(customerDAO.addCustomer(customer));
                respondSuccess(request, response, "Customer added.", customer.getId());
            } else if ("update".equals(action)) {
                Customer customer = buildCustomer(request);
                customer.setId(ServletSupport.requiredId(request));
                if (!customerDAO.updateCustomer(customer)) {
                    ServletSupport.writeError(request, response, HttpServletResponse.SC_NOT_FOUND,
                            "Customer was not found.");
                    return;
                }
                respondSuccess(request, response, "Customer updated.", customer.getId());
            } else if ("delete".equals(action)) {
                if (!customerDAO.deleteCustomer(ServletSupport.requiredId(request))) {
                    ServletSupport.writeError(request, response, HttpServletResponse.SC_NOT_FOUND,
                            "Customer was not found.");
                    return;
                }
                respondSuccess(request, response, "Customer deleted.", null);
            } else {
                ServletSupport.writeError(request, response, HttpServletResponse.SC_BAD_REQUEST,
                        "Unsupported customer action.");
            }
        } catch (IllegalArgumentException e) {
            ServletSupport.writeError(
                    request, response, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        } catch (SQLIntegrityConstraintViolationException e) {
            ServletSupport.writeError(request, response, HttpServletResponse.SC_CONFLICT,
                    "A customer with this email already exists or is still referenced.");
        } catch (SQLException e) {
            log("Unable to save customer.", e);
            ServletSupport.writeError(
                    request, response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Could not save customer.");
        }
    }

    private Customer buildCustomer(HttpServletRequest request) {
        Customer customer = new Customer();
        customer.setName(ServletSupport.required(request, "name"));
        customer.setEmail(ServletSupport.required(request, "email"));
        customer.setPhone(ServletSupport.required(request, "phone"));
        String dateOfBirth = ServletSupport.required(request, "dob");
        try {
            if (LocalDate.parse(dateOfBirth).isAfter(LocalDate.now())) {
                throw new IllegalArgumentException("Date of birth cannot be in the future.");
            }
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Date of birth must be a valid ISO date.");
        }
        customer.setDateOfBirth(dateOfBirth);
        customer.setAddress(ServletSupport.required(request, "address"));
        customer.setStatus(ServletSupport.required(request, "status"));
        if (!EMAIL_PATTERN.matcher(customer.getEmail()).matches()) {
            throw new IllegalArgumentException("Enter a valid email address.");
        }
        if (!PHONE_PATTERN.matcher(customer.getPhone()).matches()) {
            throw new IllegalArgumentException("Enter a valid 10-digit mobile number.");
        }
        if (customer.getName().length() > 100 || customer.getEmail().length() > 150) {
            throw new IllegalArgumentException("Name or email exceeds the allowed length.");
        }
        if (!"Active".equals(customer.getStatus()) && !"Inactive".equals(customer.getStatus())) {
            throw new IllegalArgumentException("Invalid customer status.");
        }
        return customer;
    }

    private void respondSuccess(HttpServletRequest request, HttpServletResponse response,
                                String message, Integer id) throws IOException {
        if (ServletSupport.wantsJson(request)) {
            java.util.Map<String, Object> result = new java.util.HashMap<>();
            result.put("message", message);
            if (id != null) {
                result.put("id", id);
            }
            ServletSupport.writeJson(response, HttpServletResponse.SC_OK, result);
        } else {
            response.sendRedirect(request.getContextPath() + "/CustomerServlet?action=list");
        }
    }
}
