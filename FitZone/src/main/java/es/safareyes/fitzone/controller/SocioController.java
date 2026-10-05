package es.safareyes.fitzone.controller;

import es.safareyes.fitzone.dto.CuotaResponseDto;
import es.safareyes.fitzone.dto.SocioEditarDto;
import es.safareyes.fitzone.dto.SocioFIltradoDto;
import es.safareyes.fitzone.dto.SocioResponseDto;
import es.safareyes.fitzone.model.Estado;
import es.safareyes.fitzone.service.SocioService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/socio")
@RequiredArgsConstructor
public class SocioController {

    private final SocioService socioService;


    @GetMapping("/buscar/dni/{dni}")
    public ResponseEntity<SocioResponseDto> buscarPorDni(@PathVariable String dni){
            return ResponseEntity.ok(SocioResponseDto.of(socioService.findByDni(dni)));
    }

    @GetMapping("/buscar/nombre/{nombre}")
    public ResponseEntity<List<SocioResponseDto>> buscarPorNombre(
            @PathVariable String nombre
    ) {
        List<SocioResponseDto> socios = socioService.buscarSocioPorNombre(nombre)
                .stream()
                .map(SocioResponseDto::of)
                .toList();

        return ResponseEntity.ok(socios);
    }


    @GetMapping("/filtrar")
    public ResponseEntity<Page<SocioResponseDto>> filtrarSocios(
            @PageableDefault(
                    size = 10,
                    page = 0,
                    direction = Sort.Direction.ASC,
                    sort = "fechaAlta"
            ) Pageable pageable,
            @RequestParam(required = false) Estado estado,
            @RequestParam(required = false) LocalDate fechaInicio,
            @RequestParam(required = false) LocalDate fechaFin,
            @RequestParam(required = false) UUID planId
    ) {
        SocioFIltradoDto filtro = new SocioFIltradoDto(
                estado,
                fechaInicio,
                fechaFin,
                planId
        );

        Page<SocioResponseDto> resultado = socioService
                .filtrar(pageable, filtro)
                .map(SocioResponseDto::of);

        return ResponseEntity.ok(resultado);
    }

    @GetMapping("/buscar/id/{id}")
    public ResponseEntity<SocioResponseDto> fichaSocioId(@PathVariable UUID id){
        return ResponseEntity.ok(SocioResponseDto.of(socioService.buscarFichaSocio(id)));
    }

    @PutMapping("/editar/{id}")
    public ResponseEntity<SocioResponseDto> editarSocio(@PathVariable UUID id, @RequestBody SocioEditarDto socioEditarDto){
        return ResponseEntity.ok(SocioResponseDto.of(socioService.editarSocio(id,socioEditarDto)));
    }

    @PatchMapping("/estado/{id}")
    public ResponseEntity<SocioResponseDto> cambiarEstado(@PathVariable UUID id){
        return ResponseEntity.ok(SocioResponseDto.of(socioService.activarDesactivarSocio(id)));
    }

    @GetMapping("/cuotas/{id}")
    public ResponseEntity<Page<CuotaResponseDto>> listarCuotasSocio(
            @PathVariable UUID id,
            Pageable pageable
    ) {
        Page<CuotaResponseDto> cuotas = socioService
                .obtenerHistorialPorSocio(id, pageable)
                .map(CuotaResponseDto::of);

        return ResponseEntity.ok(cuotas);
    }

    @GetMapping("/cuotas/pendientes")
    public ResponseEntity<List<SocioResponseDto>> listaMorosos(@RequestParam Integer mes,@RequestParam Integer anio){
        List<SocioResponseDto> listaSocio = socioService.obtenerSociosMorosos(mes,anio)
                .stream().map(SocioResponseDto::of).toList();
        return ResponseEntity.ok(listaSocio);
    }


}
