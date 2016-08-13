package g507.controldeconsumo.modelo;

public enum Periodo {
    DIA("Día"),
    SEMANA("Semana"),
    MES("Mes"),
    BIMESTRE("Bimestre");

    private String descripcion;

    Periodo(String desc){
        this.descripcion = desc;
    }

    @Override
    public String toString() {
        return descripcion;
    }
}
