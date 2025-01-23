package com.departure.helper.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.departure.helper.exception.ExcelDataProcessingException;
import com.departure.helper.exception.FileFormatException;
import com.departure.helper.service.DepartureNotificationService;
import com.departure.helper.util.ExcelFileValidator;

@Controller
public class DepartureNotificationController {

    @Autowired
    private DepartureNotificationService departureNotificationService;

    @GetMapping("/")
    public String showUploadForm() {

        return "index";
    }

    @PostMapping("/")
    public String processExcelFile(@RequestParam("file") MultipartFile file,
            @RequestParam("customTemplateFile") @Nullable MultipartFile customTemplateFile,
            RedirectAttributes redirectAttributes) {

        validateInput(file, customTemplateFile);

        InputStreamResource resource = departureNotificationService.processExcelFile(file, customTemplateFile);
        redirectAttributes.addFlashAttribute("archive", resource);
        return "redirect:/archive";
    }

    private static void validateInput(MultipartFile file, MultipartFile customTemplateFile) {

        ExcelFileValidator.validateXlsx(file);
        if (customTemplateFile != null) {
            ExcelFileValidator.validateXls(customTemplateFile);
        }
    }

    @GetMapping("/archive")
    public ResponseEntity<InputStreamResource> downloadDepartureNotifications(Model model) {

        InputStreamResource archive = (InputStreamResource) model.getAttribute("archive");
        if (archive == null) {
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=Departure notifications.zip")
                .contentType(org.springframework.http.MediaType.APPLICATION_OCTET_STREAM)
                .body(archive);
    }

    @ExceptionHandler({FileFormatException.class, ExcelDataProcessingException.class})
    public String handleFileFormatAndProcessingException(RuntimeException ex, Model model) {

        model.addAttribute("errorMessage", ex.getMessage());
        model.addAttribute("checked", model.getAttribute("checked"));
        return "index";
    }
}
