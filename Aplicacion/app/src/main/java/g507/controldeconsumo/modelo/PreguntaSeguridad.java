package g507.controldeconsumo.modelo;

public enum PreguntaSeguridad {
    //Los id tienen que coincidir en la bd
    PELICULA(1, "Nombre de película favorita?"),
    MASCOTA(2, "Nombre de tu primer mascota?"),
    LIBRO(3, "Nombre de tu libro favorito?"),
    PROFESOR(4, "Apellido de tu profesor/a favorito/a?");


    private int id;
    private String pregunta;

    PreguntaSeguridad(int id, String pregunta){
        this.id = id;
        this.pregunta = pregunta;
    }

    public static PreguntaSeguridad getPreguntaById(int id){
        for (PreguntaSeguridad preg: PreguntaSeguridad.values()) {
            if(preg.getId() == id)
                return preg;
        }
        return null;
    }

    public int getId() {
        return id;
    }

    @Override
    public String toString() {
        return pregunta;
    }
}
