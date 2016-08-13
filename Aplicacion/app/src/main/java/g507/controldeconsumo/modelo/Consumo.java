package g507.controldeconsumo.modelo;

import java.util.Date;

public class Consumo {
    private int id;
    private String username;
    private int arduino;
    private TipoConsumo tipo;
    private double nivel;
    private Date fecha;

    public Consumo(){

    }

    public Consumo(int id, String user, int codArduino, TipoConsumo tipo, double nivel, Date fecha){
        this.id = id;
        this.username = user;
        this.arduino = codArduino;
        this.tipo = tipo;
        this.nivel = nivel;
        this.fecha = fecha;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getArduino() {
        return arduino;
    }

    public void setArduino(int arduino) {
        this.arduino = arduino;
    }

    public TipoConsumo getTipo() {
        return tipo;
    }

    public void setTipo(TipoConsumo tipo) {
        this.tipo = tipo;
    }

    public double getNivel() {
        return nivel;
    }

    public void setNivel(double nivel) {
        this.nivel = nivel;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }
}
