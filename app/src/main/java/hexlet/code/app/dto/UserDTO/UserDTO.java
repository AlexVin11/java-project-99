package hexlet.code.app.dto.UserDTO;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public final class UserDTO {
    private Long id;
    private String email;
    private String firstName;
    private String lastName;
    private String createdAt;
}
