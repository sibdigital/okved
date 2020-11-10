package ru.sibdigital.okved.service;

import org.springframework.web.multipart.MultipartFile;
import ru.sibdigital.okved.model.Okved;

import java.util.List;
import java.util.UUID;

public interface OkvedService {

    String processFile(MultipartFile multipartFile, String version);

    List<Okved> findOkvedsBySearchText(String text);

    Okved findOkvedById(UUID id);

    String createOkved(String kind_name, String kind_code, String description, Short status);

    String saveOkved(String id, String okvedName, String description, Short status);

    String deleteOkved(String id);
}
