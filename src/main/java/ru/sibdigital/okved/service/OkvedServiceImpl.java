package ru.sibdigital.okved.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import ru.sibdigital.okved.dto.OkvedDto;
import ru.sibdigital.okved.model.Okved;
import ru.sibdigital.okved.model.Statuses;
import ru.sibdigital.okved.model.Types;
import ru.sibdigital.okved.repository.OkvedRepo;
import ru.sibdigital.okved.utils.ExcelParser;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class OkvedServiceImpl implements OkvedService {

    @Autowired
    private OkvedRepo okvedRepo;

    @Transactional
    public String processFile(MultipartFile multipartFile, String version) {
        try {
            // парсинг файла
            List<OkvedDto> dtos = ExcelParser.parseFile(multipartFile.getInputStream());

            // сброс статуса на 0
            okvedRepo.resetStatus();

            // импорт данных в базу
            List<Okved> models = new ArrayList<>();
            for (OkvedDto dto: dtos) {
                Okved newOkved = convertToOkved(dto);
                newOkved.setVersion(version);
                newOkved.setPath(version + "." + newOkved.getKindCode());
                Okved oldOkved = this.okvedRepo.findByPath(newOkved.getPath());
                if (oldOkved == null) {
                    models.add(newOkved);
                } else {
                    if (!oldOkved.equals(newOkved)) {
                        newOkved.setId(oldOkved.getId());
                        models.add(newOkved);
                    }
                }
            }
            okvedRepo.saveAll(models);

            // создание tsvectors
            okvedRepo.setTsVectors();
        } catch (Exception e) {
            e.printStackTrace();
            return "Ошибка! Не удалось обработать файл.";
        }
        return "Файл обработан.";
    }

    private Okved convertToOkved(OkvedDto okvedDto) {
        Okved okved = new Okved();

        String[] parts = okvedDto.getCode().split("\\.");
        switch (parts.length) {
            case 1:
                okved.setClassCode(parts[0]);
                okved.setTypeCode(Types.CLASS.getValue());
                break;
            case 2:
                okved.setClassCode(parts[0]);
                okved.setSubclassCode(String.valueOf(parts[1].charAt(0)));
                okved.setTypeCode(Types.SUBCLASS.getValue());
                if (parts[1].length() == 2) {
                    okved.setGroupCode(parts[1]);
                    okved.setTypeCode(Types.GROUP.getValue());
                }
                break;
            case 3:
                okved.setClassCode(parts[0]);
                okved.setSubclassCode(String.valueOf(parts[1].charAt(0)));
                okved.setGroupCode(parts[1]);
                okved.setSubgroupCode(String.valueOf(parts[2].charAt(0)));
                okved.setTypeCode(Types.SUBGROUP.getValue());
                if (parts[2].length() == 2) {
                    okved.setTypeCode(Types.KIND.getValue());
                }
                break;
            default:
                break;
        }

        okved.setKindCode(okvedDto.getCode());
        okved.setPath(okvedDto.getCode());
        okved.setStatus(Statuses.ACTIVE.getValue());
        okved.setKindName(okvedDto.getName());
        okved.setDescription(okvedDto.getDescription());
        return okved;
    }

    @Override
    public List<Okved> findOkvedsBySearchText(String text) {
        return okvedRepo.findBySearchText(text);
    }

    public Okved findOkvedById(UUID id) {
        return okvedRepo.findOkvedById(id);
    }

    public String createOkved(String kind_name, String kind_code, String description, Short status) {
        try {
            Okved newOkved = new Okved();
            newOkved.setKindCode(kind_code);
            newOkved.setKindName(kind_name);
            newOkved.setDescription(description);
            newOkved.setVersion("synt");
            newOkved.setPath("synt." + kind_code);
            newOkved.setStatus(status);
            this.okvedRepo.save(newOkved);
            this.okvedRepo.setTsVectorsById(newOkved.getId());
            return "ОКВЭД добавлен.";
        } catch (Exception e) {
            e.printStackTrace();
            return "Ошибка! Не удалось добавить ОКВЭД.";
        }
    }

    public String saveOkved(String id, String okvedName, String description, Short status) {
        try {
            Okved okved = okvedRepo.findOkvedById(UUID.fromString(id));
            okved.setKindName(okvedName);
            okved.setDescription(description);
            okved.setStatus(status);
            okvedRepo.save(okved);
            okvedRepo.setTsVectorsById(okved.getId());
            return "ОКВЭД изменен.";
        } catch (Exception e) {
            e.printStackTrace();
            return "Ошибка! Не удалось изменить ОКВЭД.";
        }
    }

    public String deleteOkved(String id) {
        try {
            Okved okved = this.okvedRepo.findOkvedById(UUID.fromString(id));
            this.okvedRepo.delete(okved);
            return "ОКВЭД удален.";
        } catch (Exception e) {
            e.printStackTrace();
            return "Ошибка! Не удалось удалить ОКВЭД.";
        }
    }
}
