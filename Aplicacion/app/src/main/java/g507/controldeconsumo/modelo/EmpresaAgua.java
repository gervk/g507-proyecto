package g507.controldeconsumo.modelo;

public enum EmpresaAgua {
    AYSA(1, "AySA");

    private int id;
    private String descripcion;

    EmpresaAgua(int id, String descripcion){
        this.id = id;
        this.descripcion = descripcion;
    }

    public static EmpresaAgua getEmpresaById(int id){
        for (EmpresaAgua empresa: EmpresaAgua.values()) {
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
