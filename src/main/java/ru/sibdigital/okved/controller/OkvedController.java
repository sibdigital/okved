package ru.sibdigital.okved.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import ru.sibdigital.okved.model.Okved;
import ru.sibdigital.okved.service.OkvedService;

import java.util.List;

@Controller
public class OkvedController {

    @Autowired
    private OkvedService okvedService;

    @GetMapping("/upload")
    public String upload() {
        return "upload";
    }

    @PostMapping("/process_file")
    public @ResponseBody String processFile(@RequestParam(name = "file") MultipartFile multipartFile) {
        return okvedService.processFile(multipartFile);
    }

    @GetMapping("/search")
    public @ResponseBody List<Okved> search(@RequestParam(name = "text") String text) {
        List<Okved> result = okvedService.findOkvedsBySearchText(text);
        return result;
    }
}
