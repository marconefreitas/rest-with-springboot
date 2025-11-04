package br.com.marconefreitas.exception;

import java.util.Date;

public record ExceptionReponse(Date timestamp, String msg, String details) {
}
