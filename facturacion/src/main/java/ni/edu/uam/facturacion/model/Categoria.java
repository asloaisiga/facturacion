package ni.edu.uam.facturacion.model;

import lombok.*;

@Getter
@Setter
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Categoria {

    private Integer id;
    private String nombre;
    private boolean activa;

    @Override
    public String toString() {
        return nombre;
    }
}