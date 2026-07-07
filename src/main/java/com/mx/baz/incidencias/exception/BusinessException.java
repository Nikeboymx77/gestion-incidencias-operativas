package com.mx.baz.incidencias.exception;

public class BusinessException extends RuntimeException {
	private final String codigo;

    public BusinessException(String codigo, String mensaje) {
        super(mensaje);
        this.codigo = codigo;
    }

    public String getCodigo() {
        return codigo;
    }
}
