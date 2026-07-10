package com.agronotify.cliente;

import com.agronotify.auth.Usuario;

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

    public ClienteResponse criar(ClienteRequest request, Usuario usuario) {

        Cliente cliente = new Cliente();

        LocalDate dataCadastro = LocalDate.now();

        cliente.setNome(request.getNome());
        cliente.setTelefone(request.getTelefone());
        cliente.setProduto(request.getProduto());
        cliente.setDataCadastro(dataCadastro);
        cliente.setDataEnvio(request.getDataEnvio());
        cliente.setMensagem(mensagemDoRequest(request, dataCadastro, request.getDataEnvio()));
        cliente.setStatusEnvio(StatusEnvio.PENDENTE);
        cliente.setUsuario(usuario);

        Cliente clienteSalvo = clienteRepository.save(cliente);

        return new ClienteResponse(clienteSalvo);
    }

    public List<ClienteResponse> listar(Usuario usuario) {
        return clienteRepository.findByUsuario(usuario)
                .stream()
                .sorted(ORDEM_PRIORIDADE)
                .map(ClienteResponse::new)
                .toList();
    }

    public List<ClienteResponse> buscarPorTermo(String termo, Usuario usuario) {
        return clienteRepository
                .findByUsuarioAndNomeContainingIgnoreCaseOrUsuarioAndTelefoneContainingIgnoreCaseOrUsuarioAndProdutoContainingIgnoreCase(
                        usuario, termo, usuario, termo, usuario, termo
                )
                .stream()
                .sorted(ORDEM_PRIORIDADE)
                .map(ClienteResponse::new)
                .toList();
    }

    public ClienteResponse buscar(Long id, Usuario usuario) {
        Cliente cliente = buscarEntidade(id, usuario);

        return new ClienteResponse(cliente);
    }

    public ClienteResponse atualizar(
            Long id,
            ClienteRequest request,
            Usuario usuario
    ) {
        Cliente cliente = buscarEntidade(id, usuario);

        cliente.setNome(request.getNome());
        cliente.setTelefone(request.getTelefone());
        cliente.setProduto(request.getProduto());
        cliente.setDataEnvio(request.getDataEnvio());
        cliente.setMensagem(mensagemDoRequest(request, cliente.getDataCadastro(), request.getDataEnvio()));

        Cliente clienteAtualizado = clienteRepository.save(cliente);

        return new ClienteResponse(clienteAtualizado);
    }

    public ClienteResponse marcarComoEnviado(Long id, Usuario usuario) {
        Cliente cliente = buscarEntidade(id, usuario);

        cliente.setStatusEnvio(StatusEnvio.ENVIADO);

        Cliente clienteAtualizado = clienteRepository.save(cliente);

        return new ClienteResponse(clienteAtualizado);
    }

    public void deletar(Long id, Usuario usuario) {
        Cliente cliente = buscarEntidade(id, usuario);

        clienteRepository.delete(cliente);
    }

    private Cliente buscarEntidade(Long id, Usuario usuario) {
        return clienteRepository.findById(id)
                .filter(cliente -> cliente.getUsuario() != null
                        && usuario.getId().equals(cliente.getUsuario().getId()))
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
