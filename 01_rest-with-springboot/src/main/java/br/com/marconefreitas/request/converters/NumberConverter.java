package br.com.marconefreitas.request.converters;

import br.com.marconefreitas.exception.ResourceNotFoundException;

public class NumberConverter {
    /*public static Double convertToDouble(String number) throws  IllegalArgumentException{
        if (number == null || number.isEmpty()) throw new ResourceNotFoundException("Please set a numeric value");
        String strNumber = number.replace(",", ".");
        return Double.parseDouble(strNumber);
    }*/

    public static boolean isNumeric(String number) {
        if (number == null || number.isEmpty()) return false;
        String strNumber = number.replace(",", ".");
        return (strNumber.matches("[-+]?[0-9]*\\.?[0-9]+"));
    }
}
