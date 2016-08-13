package g507.controldeconsumo.modelo;

public enum TipoConsumo {
    LUZ("Luz"),
    AGUA("Agua");

    private String tipo;

    TipoConsumo(String tipo){
        this.tipo = tipo;
    }

    @Override
    public String toString() {
        return tipo;
    }
}
