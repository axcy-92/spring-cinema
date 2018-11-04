package com.axcy.springcinema.web.controller;

import com.axcy.springcinema.entity.Auditorium;
import com.axcy.springcinema.entity.Event;
import com.axcy.springcinema.entity.json.EventEntity;
import com.axcy.springcinema.entity.json.NodeType;
import com.axcy.springcinema.entity.json.TypeField;
import com.axcy.springcinema.entity.user.User;
import com.axcy.springcinema.repository.AuditoriumRepository;
import com.axcy.springcinema.service.EventService;
import com.axcy.springcinema.service.UserService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Aleksei_Cherniavskii
 */

@Controller
public class FileImportController {

    private static final Logger LOG = LoggerFactory.getLogger(FileImportController.class);

    @Autowired
    private UserService userService;

    @Autowired
    protected EventService eventService;

    @Autowired
    protected AuditoriumRepository auditoriumRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @RequestMapping(value = "/import", method = RequestMethod.GET)
    public String importFileForm(Model model) {
        List<String> files = new ArrayList<>();
        model.addAttribute("files", files);
        return "file/import";
    }

    @RequestMapping(value = "/import", method = RequestMethod.POST)
    public String importFileProcess(@RequestParam("file") MultipartFile[] files,
                                   RedirectAttributes redirectAttributes) throws Exception {
        LOG.info("start import uploaded files");
        List<String> messages = new ArrayList<>();
        for (MultipartFile file : files) {
            if (!file.isEmpty()) {
                messages.add(processFile(file));
            } else {
                messages.add(file.getOriginalFilename() + " was not loaded: file is empty");
            }
        }
        LOG.info("import was successfully complete");
        redirectAttributes.addFlashAttribute("messages", messages);
        LOG.info("messages: " + messages.toString());
        return "redirect:/import";
    }

    @PreAuthorize("hasRole('BOOKING_MANAGER')")
    @RequestMapping(value = "/import-result", method = RequestMethod.GET)
    public String importFileResult() {
        return "/file/import-result";
    }

    private String processFile(MultipartFile file) throws Exception {
        String fileName = file.getOriginalFilename();
        LOG.info("process file : " + fileName);
        if (fileName.endsWith(".json")) {
            JsonNode jsonNode = objectMapper.readTree(file.getInputStream());
            NodeType type = objectMapper.treeToValue(jsonNode.get(NodeType.NODE_NAME), NodeType.class);
            switch (type.getTypeField()) {
                case USERS: {
                    User[] users = objectMapper.treeToValue(jsonNode.get(TypeField.USERS.getName()), User[].class);
                    for (User user : users) {
                        user = userService.register(user);
                        LOG.info(user.toString() + " was created");
                    }
                    return file.getOriginalFilename() + " was successfully loaded";
                }
                case EVENTS: {
                    EventEntity[] eventEntities = objectMapper.treeToValue(jsonNode.get(TypeField.EVENTS.getName()), EventEntity[].class);
                    for (EventEntity eventEntity : eventEntities) {
                        Event event = eventService.create(eventEntity.getName(),
                                            eventEntity.getDateTime(),
                                            eventEntity.getTicketPrice(),
                                            eventEntity.getRating());
                        Auditorium auditorium = auditoriumRepository.getById(eventEntity.getAuditorium());
                        if (auditorium != null) {
                            eventService.assignAuditorium(event, auditorium, event.getDateTime());
                        } else {
                            return "Not possible assign event" + event.getName() + " to auditorium " + eventEntity.getAuditorium()
                                    + " because auditorium not exists";
                        }
                        LOG.info(event.toString() + " was created");
                    }
                    return file.getOriginalFilename() + " was successfully loaded";
                }
                default: {
                    return "Unsupported type of imported file: " + type.toString();
                }
            }
        } else {
            return "Unsupported format of imported file: " + fileName;
        }
    }
}
