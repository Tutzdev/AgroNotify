package com.agronotify.cliente;

import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.time.LocalDate;
import java.util.List;

@Service
public class ClienteService {

    private final ClienteRepository clienteRepository;
    private static final Comparator<Cliente> ORDEM_PRIORIDADE = Comparator
            .comparing((Cliente cliente) -> cliente.getStatusEnvio() == StatusEnvio.PENDENTE ? 0 : 1)
            .thenComparing(Cliente::getDataEnvio, Comparator.nullsLast(Comparator.naturalOrder()))
            .thenComparing(Cliente::getNome, Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER));

    public ClienteService(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

    public ClienteResponse criar(ClienteRequest request) {

        Cliente cliente = new Cliente();

        LocalDate dataCadastro = LocalDate.now();

        cliente.setNome(request.getNome());
        cliente.setTelefone(request.getTelefone());
        cliente.setProduto(request.getProduto());
        cliente.setDataCadastro(dataCadastro);
        cliente.setDataEnvio(request.getDataEnvio());
        cliente.setMensagem(mensagemDoRequest(request, dataCadastro, request.getDataEnvio()));
        cliente.setStatusEnvio(StatusEnvio.PENDENTE);

        Cliente clienteSalvo = clienteRepository.save(cliente);

        return new ClienteResponse(clienteSalvo);
    }

    public List<ClienteResponse> listar() {
        return clienteRepository.findAll()
                .stream()
                .sorted(ORDEM_PRIORIDADE)
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
                .sorted(ORDEM_PRIORIDADE)
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
        cliente.setDataEnvio(request.getDataEnvio());
        cliente.setMensagem(mensagemDoRequest(request, cliente.getDataCadastro(), request.getDataEnvio()));

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

    private String mensagemDoRequest(
            ClienteRequest request,
            LocalDate dataCadastro,
            LocalDate dataEnvio
    ) {
        if (request.getMensagem() != null
                && !request.getMensagem().isBlank()
                && !MensagemCliente.ehMensagemAutomatica(
                        request.getMensagem(),
                        request.getNome(),
                        request.getProduto(),
                        dataCadastro,
                        dataEnvio
                )) {
            return request.getMensagem().trim();
        }

        return MensagemCliente.mensagemPadrao(
                request.getNome(),
                request.getProduto(),
                dataCadastro,
                dataEnvio
        );
    }
}
