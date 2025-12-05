package org.jagar.semaforosmart.model;

public class SemaforoDetalle {
    private Integer id;
    private String nombre;
    private SemaforoTipo tipo;
    private String ubicacion;
    private String descripcion;
    private boolean modoAutomatico;
    private int tiempoRojo;
    private int tiempoAmarillo;
    private int tiempoVerde;
    private int tiempoPeatonal;

    public SemaforoDetalle() {
    }

    public SemaforoDetalle(Integer id, String nombre, SemaforoTipo tipo, String ubicacion, String descripcion,
                           boolean modoAutomatico, int tiempoRojo, int tiempoAmarillo, int tiempoVerde, int tiempoPeatonal) {
        this.id = id;
        this.nombre = nombre;
        this.tipo = tipo;
        this.ubicacion = ubicacion;
        this.descripcion = descripcion;
        this.modoAutomatico = modoAutomatico;
        this.tiempoRojo = tiempoRojo;
        this.tiempoAmarillo = tiempoAmarillo;
        this.tiempoVerde = tiempoVerde;
        this.tiempoPeatonal = tiempoPeatonal;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public SemaforoTipo getTipo() {
        return tipo;
    }

    public void setTipo(SemaforoTipo tipo) {
        this.tipo = tipo;
    }

    public String getUbicacion() {
        return ubicacion;
    }

    public void setUbicacion(String ubicacion) {
        this.ubicacion = ubicacion;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public boolean isModoAutomatico() {
        return modoAutomatico;
    }

    public void setModoAutomatico(boolean modoAutomatico) {
        this.modoAutomatico = modoAutomatico;
    }

    public int getTiempoRojo() {
        return tiempoRojo;
    }

    public void setTiempoRojo(int tiempoRojo) {
        this.tiempoRojo = tiempoRojo;
    }

    public int getTiempoAmarillo() {
        return tiempoAmarillo;
    }

    public void setTiempoAmarillo(int tiempoAmarillo) {
        this.tiempoAmarillo = tiempoAmarillo;
    }

    public int getTiempoVerde() {
        return tiempoVerde;
    }

    public void setTiempoVerde(int tiempoVerde) {
        this.tiempoVerde = tiempoVerde;
    }

    public int getTiempoPeatonal() {
        return tiempoPeatonal;
    }

    public void setTiempoPeatonal(int tiempoPeatonal) {
        this.tiempoPeatonal = tiempoPeatonal;
    }

    public int getDuracionTotal() {
        return tiempoRojo + tiempoAmarillo + tiempoVerde + tiempoPeatonal;
    }
}