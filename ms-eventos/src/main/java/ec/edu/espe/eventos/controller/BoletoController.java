package ec.edu.espe.eventos.controller;

import ec.edu.espe.eventos.dto.BoletoDto;
import ec.edu.espe.eventos.dto.CompraBoletoDto;
import ec.edu.espe.eventos.model.Boleto;
import ec.edu.espe.eventos.service.BoletoService;
import ec.edu.espe.eventos.service.CompraService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/boletos")
public class BoletoController {

    @Autowired
    private BoletoService boletoService;

    @Autowired
    private CompraService compraService;

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

    @PostMapping("/comprar")
    public ResponseEntity<?> comprarBoletos(@RequestBody CompraBoletoDto compraDto) {
        try {
            String resultado = compraService.procesarCompraConPago(compraDto);
            return ResponseEntity.ok(resultado);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Error al procesar compra: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error interno: " + e.getMessage());
        }
    }

    @PostMapping("/directo")
    public ResponseEntity<?> crearBoletosDirecto(@RequestBody BoletoDto boletosDto) {
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

