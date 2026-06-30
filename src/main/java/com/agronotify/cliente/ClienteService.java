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
        cliente.setMensagem(mensagemDoRequest(request));

        cliente.setDataCadastro(LocalDate.now());
        cliente.setDataEnvio(request.getDataEnvio());
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
        cliente.setMensagem(mensagemDoRequest(request));
        cliente.setDataEnvio(request.getDataEnvio());

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

    private String mensagemDoRequest(ClienteRequest request) {
        if (request.getMensagem() != null && !request.getMensagem().isBlank()) {
            return request.getMensagem().trim();
        }

        return mensagemPadrao(request.getNome(), request.getProduto());
    }

    private String mensagemPadrao(String nome, String produto) {
        String nomeCliente = nome == null || nome.isBlank()
                ? "cliente"
                : nome.trim();
        String nomeProduto = produto == null || produto.isBlank()
                ? "o produto"
                : produto.trim();

        return "Ol\u00e1, " + nomeCliente + "! Tudo bem?\n\n"
                + "Aqui \u00e9 da Agropecu\u00e1ria Nossos Bichos. \uD83D\uDE0A\n\n"
                + "Notamos que j\u00e1 faz algum tempo desde a sua \u00faltima compra de "
                + nomeProduto
                + ".\n\n"
                + "Gostar\u00edamos de saber se seu pet j\u00e1 est\u00e1 precisando de uma nova reposi\u00e7\u00e3o. "
                + "Caso queira, estamos \u00e0 disposi\u00e7\u00e3o para atend\u00ea-lo novamente!\n\n"
                + "Agradecemos pela prefer\u00eancia e esperamos falar com voc\u00ea em breve.\n\n"
                + "Equipe Agropecu\u00e1ria Nossos Bichos.";
    }
}
