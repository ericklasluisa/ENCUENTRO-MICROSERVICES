package ec.edu.espe.eventos.controller;

import ec.edu.espe.eventos.dto.BoletoDto;
import ec.edu.espe.eventos.model.Boleto;
import ec.edu.espe.eventos.service.BoletoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/boletos")
public class BoletoController {

    @Autowired
    private BoletoService boletoService;

    @PostMapping
    public ResponseEntity<?> crearBoletos(@RequestBody BoletoDto boletosDto) {
        try {
            boletoService.crearBoletos(boletosDto);
            return ResponseEntity.ok("Boletos creados exitosamente");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Error al crear boletos: " + e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @GetMapping("/{idBoleto}")
    public ResponseEntity<?> obtenerBoletoPorId(@PathVariable Long idBoleto) {
        try {
            Boleto boleto = boletoService.obtenerBoletoPorId(idBoleto);
            return ResponseEntity.ok(boleto);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Error al obtener boleto: " + e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}

