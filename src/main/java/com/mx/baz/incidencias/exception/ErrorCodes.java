package com.mx.baz.incidencias.exception;

public final class ErrorCodes {
	private ErrorCodes() {
    }

    public static final String INCIDENCIA_NO_ENCONTRADA = "INC-404";
    public static final String INCIDENCIA_NO_TOMADA = "INC-001";
    public static final String INCIDENCIA_RESUELTA = "INC-002";
    public static final String INCIDENCIA_YA_TOMADA = "INC-003";

    public static final String EMPLEADO_FIN_SEMANA = "EMP-001";
    public static final String EMPLEADO_SEMANA = "EMP-002";
    public static final String EMPLEADO_NO_DISPONIBLE = "EMP-003";
}
