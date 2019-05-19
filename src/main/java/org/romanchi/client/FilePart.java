package org.romanchi.client;

import lombok.*;

@Data
@AllArgsConstructor
@Builder
public class FilePart {
    private Integer id;
    private boolean isPresent = false;
}
