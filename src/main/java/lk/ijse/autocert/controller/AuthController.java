package lk.ijse.autocert.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lk.ijse.autocert.dto.ApiResponse;
import lk.ijse.autocert.dto.AuthDTO;
import lk.ijse.autocert.dto.RegisterDTO;
import lk.ijse.autocert.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse> registerUser(@RequestBody RegisterDTO registerDTO) {
        return ResponseEntity.ok(
                new ApiResponse(200, "User registered successfully", authService.register(registerDTO))
        );
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse> loginUser(@RequestBody AuthDTO authDTO,
                                                 HttpServletResponse response) {

        // ✅ Step 1: Authenticate and generate JWT token
        String jwtToken = authService.authenticate(authDTO);

        // ✅ Step 2: Create secure HttpOnly cookie
        Cookie cookie = new Cookie("token", jwtToken);
        cookie.setHttpOnly(true);       // 🔒 Prevent access from JS
        cookie.setSecure(true);         // 🔐 Only sent via HTTPS (enable HTTPS in prod)
        cookie.setPath("/");            // 📍 Makes cookie available across all endpoints
        cookie.setMaxAge(24 * 60 * 60); // ⏳ Expires in 1 day

        // ✅ Step 3: Add cookie to response
        response.addCookie(cookie);

        return ResponseEntity.ok(
                new ApiResponse(200, "User logged in successfully", null)
        );
    }


    // Sample dashboard endpoint accessible only by ADMIN
    @GetMapping("/admin/dashboard")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse> adminDashboard() {
        return ResponseEntity.ok(new ApiResponse(200, "Welcome to Admin Dashboard", null));
    }

    // Sample dashboard endpoint accessible only by CUSTOMER
    @GetMapping("/customer/dashboard")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<ApiResponse> customerDashboard() {
        return ResponseEntity.ok(new ApiResponse(200, "Welcome to Customer Dashboard", null));
    }

    // Sample dashboard endpoint accessible only by INSPECTOR
    @GetMapping("/inspector/dashboard")
    @PreAuthorize("hasRole('INSPECTOR')")
    public ResponseEntity<ApiResponse> inspectorDashboard() {
        return ResponseEntity.ok(new ApiResponse(200, "Welcome to Inspector Dashboard", null));
    }
}
