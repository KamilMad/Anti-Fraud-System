package com.example.AntiFraudSystem.utilities;

public class LuhnAlgorithm {

    public static boolean isValidCardNumber(String cardNumber) {
        // Clean the card number by removing non-digit characters
        String cleanedCardNumber = cardNumber.replaceAll("[^0-9]", "");

        // Convert the cleaned card number to a character array
        char[] digits = cleanedCardNumber.toCharArray();

        // Start from the second-to-last digit and double every second digit
        for (int i = digits.length - 2; i >= 0; i -= 2) {
            int digit = Character.getNumericValue(digits[i]);
            digit *= 2;

            // If the doubled digit is greater than 9, subtract 9 from it
            if (digit > 9) {
                digit -= 9;
            }

            digits[i] = Character.forDigit(digit, 10);
        }

        // Sum all the digits in the modified card number
        int sum = 0;
        for (char digit : digits) {
            sum += Character.getNumericValue(digit);
        }

        // The card number is valid if the sum is divisible by 10
        return sum % 10 == 0;
    }

}
