package g507.controldeconsumo.modelo;

public enum EmpresaElec {
    EDESUR(1, "Edesur"),
    EDENOR(2, "Edenor");

    private int id;
    private String descripcion;

    EmpresaElec(int id, String descripcion){

        this.id = id;
        this.descripcion = descripcion;
    }

    public static EmpresaElec getEmpresaById(int id){
        for (EmpresaElec empresa: EmpresaElec.values()) {
            if(empresa.getId() == id)
                return empresa;
        }
        return null;
    }

    public int getId() {
        return id;
    }

    @Override
    public String toString() {
        return descripcion;
    }
}
