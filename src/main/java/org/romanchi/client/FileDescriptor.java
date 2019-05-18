package org.romanchi.client;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.net.SocketAddress;
import java.util.List;

@Data
@AllArgsConstructor
@Builder
public class FileDescriptor {
    private List<SocketAddress> addresses;
    private String fileName;
    private Long fileSize;
}
