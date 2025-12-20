package br.com.alurafood.pagamentos.model;

public enum Status {
    CRIADO("CRIADO"),
    CONFIRMADO("CONFIRMADO"),
    CONFIRMADO_NAO_INTEGRADO("CONFIRMADO NAO INTEGRADO"),
    CANCELADO("CANCELADO");

    private String status;

    Status(String status) {
        this.status = status;
    }

    public String getStatus() {
        return this.status;
    }
}
