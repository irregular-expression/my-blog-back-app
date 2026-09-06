package ru.irrexp.practicum.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.irrexp.practicum.dao.ImageDao;
import ru.irrexp.practicum.exception.ServerException;
import ru.irrexp.practicum.service.ImageService;

import java.io.IOException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ImageServiceImpl implements ImageService {

    private final ImageDao dao;

    @Override
    public boolean load(Integer postId, MultipartFile file) {
        try {
            dao.save(postId, file.getBytes());
            return true;
        } catch (IOException e) {
            throw new ServerException("Не удалось прочитать файл " + file.getName());
        }
    }

    @Override
    public Optional<byte[]> getByPostId(Integer postId) {
        return dao.findByPostId(postId);
    }

}
