package g507.controldeconsumo.modelo;


public enum TipoEstadistica {
    POR_MES(1, "Por mes"),
    POR_DIA(2, "Por dia"),
    POR_HORA(3, "Por hora");

    private Integer id;
    private String tipo;

    TipoEstadistica(Integer id, String tipo){
        this.id = id;
        this.tipo = tipo;
    }

    @Override
    public String toString() {
        return tipo;
    }

    public Integer getId(){
        return id;
    }
}
