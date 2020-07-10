package ru.sibdigital.okved.service;

import org.springframework.web.multipart.MultipartFile;
import ru.sibdigital.okved.model.Okved;

import java.util.List;

public interface OkvedService {

    String processFile(MultipartFile multipartFile);

    List<Okved> findOkvedsBySearchText(String text);
}
