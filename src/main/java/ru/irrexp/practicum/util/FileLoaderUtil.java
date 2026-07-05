package ru.irrexp.practicum.util;

import lombok.experimental.UtilityClass;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.StreamUtils;
import ru.irrexp.practicum.exception.ServerException;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@UtilityClass
public class FileLoaderUtil {

    public String loadStringFromClasspath(String path) {
        try {
            return StreamUtils.copyToString(
                    new ClassPathResource(path).getInputStream(),
                    StandardCharsets.UTF_8
            );
        } catch (IOException e) {
            throw new ServerException(e.getMessage());
        }
    }

    public byte[] loadByteArrayFromClasspath(String path) {
        try {
            return new ClassPathResource(path).getInputStream().readAllBytes();
        } catch (IOException e) {
            throw new ServerException(e.getMessage());
        }
    }

}
