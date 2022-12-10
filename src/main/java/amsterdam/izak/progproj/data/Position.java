package amsterdam.izak.progproj.data;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@ToString
public class Position {
    private float x;
    private float y;
    private float z;
}
