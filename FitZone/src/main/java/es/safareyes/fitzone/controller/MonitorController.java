package es.safareyes.fitzone.controller;

import es.safareyes.fitzone.model.Monitor;
import es.safareyes.fitzone.service.MonitorService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/monitor")
@RequiredArgsConstructor
public class MonitorController {

    private final MonitorService monitorService;


    @GetMapping("/all")
    public List<Monitor> findAll(){
        return  monitorService.findAll();
    }
}
