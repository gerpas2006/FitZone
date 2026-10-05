package es.safareyes.fitzone.controller;

import es.safareyes.fitzone.dto.PlanResponseDto;
import es.safareyes.fitzone.repository.PlanRepository;
import es.safareyes.fitzone.service.PlanService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/plan")
@RequiredArgsConstructor
public class PlanController {

    private final PlanService planService;

    @GetMapping("/lista/planes")
    public ResponseEntity<List<PlanResponseDto>> listarPlanes(){
        List<PlanResponseDto> listaPlanes = planService.listarPlanes()
                .stream().map(PlanResponseDto::of).toList();
        return ResponseEntity.ok(listaPlanes);
    }
}
