package com.agronotify.cliente;

import com.agronotify.auth.Usuario;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/clientes")
public class ClienteController {
    private final ClienteService clienteService;

    public ClienteController(ClienteService clienteService) { this.clienteService = clienteService; }

    @PostMapping
    public ResponseEntity<ClienteResponse> criar(@Valid @RequestBody ClienteRequest request,
                                                  @AuthenticationPrincipal Usuario usuario) {
        return ResponseEntity.ok(clienteService.criar(request, usuario));
    }

    @GetMapping
    public ResponseEntity<List<ClienteResponse>> listar(@AuthenticationPrincipal Usuario usuario) {
        return ResponseEntity.ok(clienteService.listar(usuario));
    }

    @GetMapping("/buscar")
    public ResponseEntity<List<ClienteResponse>> buscarPorTermo(@RequestParam String termo,
                                                                 @AuthenticationPrincipal Usuario usuario) {
        return ResponseEntity.ok(clienteService.buscarPorTermo(termo, usuario));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClienteResponse> buscar(@PathVariable Long id,
                                                  @AuthenticationPrincipal Usuario usuario) {
        return ResponseEntity.ok(clienteService.buscar(id, usuario));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ClienteResponse> atualizar(@PathVariable Long id,
                                                     @Valid @RequestBody ClienteRequest request,
                                                     @AuthenticationPrincipal Usuario usuario) {
        return ResponseEntity.ok(clienteService.atualizar(id, request, usuario));
    }

    @PatchMapping("/{id}/enviar")
    public ResponseEntity<ClienteResponse> marcarComoEnviado(@PathVariable Long id,
                                                              @AuthenticationPrincipal Usuario usuario) {
        return ResponseEntity.ok(clienteService.marcarComoEnviado(id, usuario));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id, @AuthenticationPrincipal Usuario usuario) {
        clienteService.deletar(id, usuario);
        return ResponseEntity.noContent().build();
    }
}
