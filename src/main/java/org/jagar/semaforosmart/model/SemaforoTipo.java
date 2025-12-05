package org.jagar.semaforosmart.model;

public enum SemaforoTipo {
    VEHICULAR("Vehicular"),
    PEATONAL("Peatonal");

    private final String etiqueta;

    SemaforoTipo(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    public String getEtiqueta() {
        return etiqueta;
    }
}
