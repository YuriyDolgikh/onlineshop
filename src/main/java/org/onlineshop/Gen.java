package org.onlineshop;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class Gen {
    public static void main(String[] args) {
        var enc = new BCryptPasswordEncoder();
        System.out.println(enc.encode("admin123"));
        System.out.println(enc.encode("manager123"));
    }
}
