package g507.controldeconsumo.modelo;

public enum TipoConsumo {
    ELECTRICIDAD(1, "Electricidad"),
    AGUA(2, "Agua");

    private Integer id;
    private String tipo;

    TipoConsumo(Integer id, String tipo){
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
