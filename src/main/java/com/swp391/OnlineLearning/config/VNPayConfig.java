package com.swp391.OnlineLearning.config;

import jakarta.servlet.http.HttpServletRequest;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class VNPayConfig {

    // ================== CONFIG ==================

    public static String vnp_PayUrl = "https://sandbox.vnpayment.vn/paymentv2/vpcpay.html";

    public static String vnp_ReturnUrl = "http://localhost:8080/payment/vnpay-return";

    public static String vnp_IpnUrl = "http://localhost:8080/payment/vnpay-ipn";

    public static String vnp_TmnCode = "0A88A4ZP";
    public static String secretKey = "BLETSZ08ITCSIV7TCG82KX83EYO1V3GN";

    public static String vnp_ApiUrl = "https://sandbox.vnpayment.vn/merchant_webapi/api/transaction";

    public static String vnp_Version = "2.1.0";
    public static String vnp_Command = "pay";
    public static String orderType = "other";

    // ================== HASH DATA (FIXED) ==================

    public static String buildHashData(Map<String, String> vnp_Params) {

        List<String> fieldNames = new ArrayList<>(vnp_Params.keySet());
        Collections.sort(fieldNames);

        StringBuilder hashData = new StringBuilder();

        for (int i = 0; i < fieldNames.size(); i++) {

            String fieldName = fieldNames.get(i);
            String fieldValue = vnp_Params.get(fieldName);

            if (fieldValue != null && !fieldValue.isEmpty()) {

                hashData.append(fieldName)
                        .append('=')
                        .append(URLEncoder.encode(fieldValue, StandardCharsets.UTF_8));

                if (i < fieldNames.size() - 1) {
                    hashData.append('&');
                }
            }
        }

        return hashData.toString();
    }

    // ================== SIGNATURE ==================

    public static String hmacSHA512(final String key, final String data) {
        try {
            Mac hmac512 = Mac.getInstance("HmacSHA512");

            SecretKeySpec secretKeySpec =
                    new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA512");

            hmac512.init(secretKeySpec);

            byte[] result = hmac512.doFinal(data.getBytes(StandardCharsets.UTF_8));

            StringBuilder sb = new StringBuilder(2 * result.length);
            for (byte b : result) {
                sb.append(String.format("%02x", b & 0xff));
            }

            return sb.toString();

        } catch (Exception e) {
            return "";
        }
    }

    // ================== IP ==================

    public static String getIpAddress(HttpServletRequest request) {
        String ip = request.getHeader("X-FORWARDED-FOR");
        if (ip == null) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }

    // ================== RANDOM ==================

    public static String getRandomNumber(int len) {
        Random rnd = new Random();
        String chars = "0123456789";

        StringBuilder sb = new StringBuilder(len);

        for (int i = 0; i < len; i++) {
            sb.append(chars.charAt(rnd.nextInt(chars.length())));
        }

        return sb.toString();
    }
}