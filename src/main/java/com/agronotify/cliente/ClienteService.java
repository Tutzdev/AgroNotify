package com.agronotify.cliente;

import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class ClienteService {

    private final ClienteRepository clienteRepository;

    public ClienteService(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

    public ClienteResponse criar(ClienteRequest request) {

        Cliente cliente = new Cliente();

        cliente.setNome(request.getNome());
        cliente.setTelefone(request.getTelefone());
        cliente.setProduto(request.getProduto());

        cliente.setDataCadastro(LocalDate.now());
        cliente.setDataEnvio(LocalDate.now().plusDays(7));
        cliente.setStatusEnvio(StatusEnvio.PENDENTE);

        Cliente clienteSalvo = clienteRepository.save(cliente);

        return new ClienteResponse(clienteSalvo);
    }

    public List<ClienteResponse> listar() {
        return clienteRepository.findAll()
                .stream()
                .map(ClienteResponse::new)
                .toList();
    }

    public List<ClienteResponse> buscarPorTermo(String termo) {
        return clienteRepository
                .findByNomeContainingIgnoreCaseOrTelefoneContainingIgnoreCaseOrProdutoContainingIgnoreCase(
                        termo,
                        termo,
                        termo
                )
                .stream()
                .map(ClienteResponse::new)
                .toList();
    }

    public ClienteResponse buscar(Long id) {
        Cliente cliente = buscarEntidade(id);

        return new ClienteResponse(cliente);
    }

    public ClienteResponse atualizar(
            Long id,
            ClienteRequest request
    ) {
        Cliente cliente = buscarEntidade(id);

        cliente.setNome(request.getNome());
        cliente.setTelefone(request.getTelefone());
        cliente.setProduto(request.getProduto());

        Cliente clienteAtualizado = clienteRepository.save(cliente);

        return new ClienteResponse(clienteAtualizado);
    }

    public void deletar(Long id) {
        Cliente cliente = buscarEntidade(id);

        clienteRepository.delete(cliente);
    }

    private Cliente buscarEntidade(Long id) {
        return clienteRepository.findById(id)
                .orElseThrow(
                        () -> new ClienteNaoEncontradoException(id)
                );
    }
}