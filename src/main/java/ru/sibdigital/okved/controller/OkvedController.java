package ru.sibdigital.okved.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.sibdigital.okved.model.Okved;
import ru.sibdigital.okved.service.OkvedService;
import ru.sibdigital.okved.service.OkvedServiceImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

@Controller
public class OkvedController {

    @Autowired
    private OkvedServiceImpl okvedServiceImpl;

    @GetMapping("/upload")
    public String upload() {
        return "upload";
    }

    @PostMapping("/process_file")
    public @ResponseBody String processFile(@RequestParam(name = "file") MultipartFile multipartFile, @RequestParam(name = "version") String version) {
        return okvedServiceImpl.processFile(multipartFile, version);
    }

    @GetMapping("/search")
    public @ResponseBody List<Okved> search(@RequestParam(name = "text") String text) {
        List<Okved> result = okvedServiceImpl.findOkvedsBySearchText(text);
        return result;
    }

    @RequestMapping(value="/last_synt_okved_code", method=GET)
    public @ResponseBody String lastSyntOkvedCode() {
        String kind_code = "" + (Integer.parseInt(okvedServiceImpl.findLastSyntheticKindCode()) + 1);
        return kind_code;
    }

    @GetMapping("/new_okved")
    public String newOkved(@RequestParam(name = "okved_name") String okved_name, Model model) {
        model.addAttribute("okved_name", okved_name);
        return "new_okved";
    }

    @GetMapping("/okved")
    public String okvedForm(@RequestParam(name = "id") String id, Model model) {
        Okved okved = okvedServiceImpl.findOkvedById(UUID.fromString(id));
        model.addAttribute("kind_code", okved.getKindCode());
        model.addAttribute("kind_name", okved.getKindName());
        model.addAttribute("version", okved.getVersion());
        model.addAttribute("status", okved.getStatus());
        model.addAttribute("description", (okved.getDescription() != null) ? okved.getDescription() : "");
        model.addAttribute("okved_id", id);
        return "okvedform";
    }

    @GetMapping("/synt_okveds")
    public @ResponseBody List<Okved> getListSyntOkved() {
        List<Okved> list = okvedServiceImpl.getSyntOkveds().stream()
                            .collect(Collectors.toList());
        return list;
    }

    @PostMapping("/create_okved")
    public @ResponseBody String addOkved(@RequestParam(name = "okved-name") String okvedName, @RequestParam(name = "description") String description) {
        return okvedServiceImpl.createOkved(okvedName, description);
    }

    @PostMapping("/save_okved")
    public @ResponseBody String saveOkved(@RequestParam(name = "okved-id") String id, @RequestParam(name = "okved-name") String okvedName,
                                          @RequestParam(name = "description") String description, @RequestParam(name = "status") Short status) {
        return okvedServiceImpl.saveOkved(id, okvedName, description, status);
    }

    @PostMapping("/delete_okved")
    public @ResponseBody String deleteOkved(@RequestParam(name = "okved-id") String id) {
        return okvedServiceImpl.deleteOkved(id);
    }
}
