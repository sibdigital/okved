package ru.sibdigital.okved.service;

import org.springframework.web.multipart.MultipartFile;
import ru.sibdigital.okved.model.Okved;

import java.util.List;
import java.util.UUID;

public interface OkvedService {

    String processFile(MultipartFile multipartFile, String version);

    List<Okved> findOkvedsBySearchText(String text);

    String findLastSyntheticKindCode();

    Okved findOkvedById(UUID id);

    String createOkved(String okvedName, String description);

    String saveOkved(String id, String okvedName, String description, Short status);

    String deleteOkved(String id);
}
