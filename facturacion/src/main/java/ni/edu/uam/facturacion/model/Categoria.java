package ni.edu.uam.facturacion.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Categoria {

    private int id;
    private String nombre;
    private boolean activa;

    @Override
    public String toString() {
        return nombre;
    }
}