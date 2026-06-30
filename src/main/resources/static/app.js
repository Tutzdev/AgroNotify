const TOKEN_KEY = "agroNotifyToken";

function obterToken() {
    return localStorage.getItem(TOKEN_KEY);
}

function redirecionarParaLogin() {
    window.location.href = "login.html";
}

function verificarAutenticacao() {
    if (!obterToken()) {
        redirecionarParaLogin();
        return false;
    }

    return true;
}

function logout() {
    localStorage.removeItem(TOKEN_KEY);
    redirecionarParaLogin();
}

async function apiFetch(url, options = {}, exigeToken = true) {
    const headers = new Headers(options.headers || {});

    if (options.body && !headers.has("Content-Type")) {
        headers.set("Content-Type", "application/json");
    }

    if (exigeToken) {
        const token = obterToken();

        if (!token) {
            redirecionarParaLogin();
            throw new Error("Token nao encontrado.");
        }

        headers.set("Authorization", `Bearer ${token}`);
    }

    const response = await fetch(url, {
        ...options,
        headers
    });

    if (response.status === 401 && exigeToken) {
        localStorage.removeItem(TOKEN_KEY);
        redirecionarParaLogin();
        throw new Error("Sessao expirada.");
    }

    if (!response.ok) {
        throw new Error(await lerErro(response));
    }

    if (response.status === 204) {
        return null;
    }

    const contentType = response.headers.get("content-type") || "";

    if (contentType.includes("application/json")) {
        return response.json();
    }

    return response.text();
}

async function lerErro(response) {
    const contentType = response.headers.get("content-type") || "";

    if (contentType.includes("application/json")) {
        const dados = await response.json();
        return dados.message || dados.error || `Erro ${response.status}`;
    }

    const texto = await response.text();
    return texto || `Erro ${response.status}`;
}

function exibirMensagem(texto) {
    const mensagem = document.getElementById("mensagem");

    if (mensagem) {
        mensagem.textContent = texto;
    }
}

function obterDadosFormulario() {
    return {
        nome: document.getElementById("nome").value.trim(),
        telefone: document.getElementById("telefone").value.trim(),
        produto: document.getElementById("produto").value.trim(),
        dataEnvio: document.getElementById("dataEnvio").value,
        mensagem: document.getElementById("mensagemCliente").value.trim()
    };
}

function dataPadraoEnvio() {
    const data = new Date();
    data.setDate(data.getDate() + 7);
    return formatarDataInput(data);
}

function formatarDataInput(data) {
    const ano = data.getFullYear();
    const mes = String(data.getMonth() + 1).padStart(2, "0");
    const dia = String(data.getDate()).padStart(2, "0");
    return `${ano}-${mes}-${dia}`;
}

function mensagemPadrao(nome, produto) {
    const nomeCliente = String(nome || "").trim() || "cliente";
    const nomeProduto = String(produto || "").trim() || "o produto";

    return `Ol\u00e1, ${nomeCliente}! Tudo bem?\n\nAqui \u00e9 da Agropecu\u00e1ria Nossos Bichos. \uD83D\uDE0A\n\nNotamos que j\u00e1 faz algum tempo desde a sua \u00faltima compra de ${nomeProduto}.\n\nGostar\u00edamos de saber se seu pet j\u00e1 est\u00e1 precisando de uma nova reposi\u00e7\u00e3o. Caso queira, estamos \u00e0 disposi\u00e7\u00e3o para atend\u00ea-lo novamente!\n\nAgradecemos pela prefer\u00eancia e esperamos falar com voc\u00ea em breve.\n\nEquipe Agropecu\u00e1ria Nossos Bichos.`;
}

function configurarMensagemPadrao() {
    const nome = document.getElementById("nome");
    const produto = document.getElementById("produto");
    const mensagem = document.getElementById("mensagemCliente");

    if (!nome || !produto || !mensagem) {
        return;
    }

    let mensagemAutomatica = mensagemPadrao(nome.value, produto.value);

    if (!mensagem.value.trim()) {
        mensagem.value = mensagemAutomatica;
    }

    const atualizarMensagem = () => {
        const proximaMensagem = mensagemPadrao(nome.value, produto.value);

        if (!mensagem.value.trim() || mensagem.value === mensagemAutomatica) {
            mensagem.value = proximaMensagem;
        }

        mensagemAutomatica = proximaMensagem;
    };

    nome.addEventListener("input", atualizarMensagem);
    produto.addEventListener("input", atualizarMensagem);
}

function normalizarTelefone(telefone) {
    const digitos = String(telefone || "").replace(/\D/g, "");

    if (digitos.startsWith("55")) {
        return digitos;
    }

    return `55${digitos}`;
}

function gerarWhatsappLink(cliente) {
    const telefone = normalizarTelefone(cliente.telefone);
    const mensagem = cliente.mensagem || mensagemPadrao(cliente.nome, cliente.produto);

    return `https://wa.me/${telefone}?text=${encodeURIComponent(mensagem)}`;
}

function obterWhatsappLink(cliente) {
    return cliente.whatsappLink || gerarWhatsappLink(cliente);
}

function configurarBotaoWhatsapp(botao, cliente) {
    botao.href = obterWhatsappLink(cliente);
    botao.target = "_blank";
    botao.rel = "noopener noreferrer";
}

async function login() {
    const email = document.getElementById("email").value.trim();
    const senha = document.getElementById("senha").value;

    try {
        exibirMensagem("");

        const resposta = await apiFetch("/auth/login", {
            method: "POST",
            body: JSON.stringify({ email, senha })
        }, false);

        localStorage.setItem(TOKEN_KEY, resposta.token);
        window.location.href = "dashboard.html";
    } catch (erro) {
        exibirMensagem(erro.message);
    }
}

async function carregarDashboard() {
    try {
        const dados = await apiFetch("/dashboard");

        document.getElementById("totalClientes").textContent =
            dados.totalClientes;
        document.getElementById("mensagensPendentes").textContent =
            dados.mensagensPendentes;
        document.getElementById("mensagensEnviadas").textContent =
            dados.mensagensEnviadas;
    } catch (erro) {
        exibirMensagem(erro.message);
    }
}

async function listarClientes() {
    try {
        const clientes = await apiFetch("/clientes");
        exibirClientes(clientes);
        exibirMensagem("");
    } catch (erro) {
        exibirMensagem(erro.message);
    }
}

async function buscarClientes() {
    const termo = document.getElementById("termoBusca").value.trim();

    if (!termo) {
        await listarClientes();
        return;
    }

    try {
        const clientes = await apiFetch(
            `/clientes/buscar?termo=${encodeURIComponent(termo)}`
        );

        exibirClientes(clientes);
        exibirMensagem("");
    } catch (erro) {
        exibirMensagem(erro.message);
    }
}

async function cadastrarCliente() {
    try {
        await apiFetch("/clientes", {
            method: "POST",
            body: JSON.stringify(obterDadosFormulario())
        });

        window.location.href = "clientes.html";
    } catch (erro) {
        exibirMensagem(erro.message);
    }
}

function obterIdCliente() {
    return new URLSearchParams(window.location.search).get("id");
}

async function carregarClienteParaEdicao() {
    const id = obterIdCliente();

    if (!id) {
        exibirMensagem("ID do cliente nao informado.");
        document.getElementById("editarClienteForm").hidden = true;
        return;
    }

    try {
        const cliente = await apiFetch(`/clientes/${encodeURIComponent(id)}`);

        document.getElementById("nome").value = cliente.nome;
        document.getElementById("telefone").value = cliente.telefone;
        document.getElementById("produto").value = cliente.produto;
        document.getElementById("dataEnvio").value = cliente.dataEnvio;
        document.getElementById("mensagemCliente").value =
            cliente.mensagem || mensagemPadrao(cliente.nome, cliente.produto);
        configurarMensagemPadrao();
    } catch (erro) {
        exibirMensagem(erro.message);
    }
}

async function editarCliente() {
    const id = obterIdCliente();

    if (!id) {
        exibirMensagem("ID do cliente nao informado.");
        return;
    }

    try {
        await apiFetch(`/clientes/${encodeURIComponent(id)}`, {
            method: "PUT",
            body: JSON.stringify(obterDadosFormulario())
        });

        window.location.href = "clientes.html";
    } catch (erro) {
        exibirMensagem(erro.message);
    }
}

async function carregarDetalhesCliente() {
    const id = obterIdCliente();

    if (!id) {
        exibirMensagem("ID do cliente nao informado.");
        return;
    }

    try {
        const cliente = await apiFetch(`/clientes/${encodeURIComponent(id)}`);

        preencherDetalhe("detalheId", cliente.id);
        preencherDetalhe("detalheNome", cliente.nome);
        preencherDetalhe("detalheTelefone", cliente.telefone);
        preencherDetalhe("detalheProduto", cliente.produto);
        preencherDetalhe("detalheDataCadastro", cliente.dataCadastro);
        preencherDetalhe("detalheDataEnvio", cliente.dataEnvio);
        preencherDetalhe("detalheStatus", cliente.statusEnvio);
        preencherDetalhe("detalheMensagem", cliente.mensagem || mensagemPadrao(cliente.nome, cliente.produto));

        const whatsapp = document.getElementById("whatsappBtn");
        const editar = document.getElementById("editarBtn");

        configurarBotaoWhatsapp(whatsapp, cliente);
        editar.href = `editar-cliente.html?id=${encodeURIComponent(cliente.id)}`;
        exibirMensagem("");
    } catch (erro) {
        exibirMensagem(erro.message);
    }
}

function preencherDetalhe(id, valor) {
    const elemento = document.getElementById(id);

    if (elemento) {
        elemento.textContent = valor ?? "";
    }
}

function limitarMensagemTabela(mensagem) {
    const texto = String(mensagem || "");

    if (texto.length <= 50) {
        return texto;
    }

    return `${texto.slice(0, 47).trimEnd()}...`;
}

async function deletarCliente(id) {
    if (!window.confirm("Deseja deletar este cliente?")) {
        return;
    }

    try {
        await apiFetch(`/clientes/${encodeURIComponent(id)}`, {
            method: "DELETE"
        });

        await listarClientes();
        exibirMensagem("Cliente deletado.");
    } catch (erro) {
        exibirMensagem(erro.message);
    }
}

function adicionarCelula(linha, valor, classe = "", titulo = "") {
    const celula = document.createElement("td");

    if (classe) {
        celula.className = classe;
    }

    if (titulo) {
        celula.title = titulo;
    }

    celula.textContent = valor ?? "";
    linha.appendChild(celula);
}

function exibirClientes(clientes) {
    const tabela = document.getElementById("listaClientes");
    tabela.replaceChildren();

    clientes.forEach((cliente) => {
        const linha = document.createElement("tr");

        adicionarCelula(linha, cliente.id);
        adicionarCelula(linha, cliente.nome);
        adicionarCelula(linha, cliente.telefone);
        adicionarCelula(linha, cliente.produto);
        const mensagemCompleta = cliente.mensagem || mensagemPadrao(cliente.nome, cliente.produto);

        adicionarCelula(linha, limitarMensagemTabela(mensagemCompleta), "celula-mensagem", mensagemCompleta);
        adicionarCelula(linha, cliente.dataCadastro);
        adicionarCelula(linha, cliente.dataEnvio);
        adicionarCelula(linha, cliente.statusEnvio);

        const acoes = document.createElement("td");
        const whatsapp = document.createElement("a");
        const detalhes = document.createElement("a");
        const editar = document.createElement("a");
        const deletar = document.createElement("button");

        configurarBotaoWhatsapp(whatsapp, cliente);
        whatsapp.className = "botao botao-principal botao-pequeno";
        whatsapp.textContent = "Enviar WhatsApp";

        detalhes.href = `cliente-detalhes.html?id=${encodeURIComponent(cliente.id)}`;
        detalhes.className = "botao botao-secundario botao-pequeno";
        detalhes.textContent = "Detalhes";

        editar.href = `editar-cliente.html?id=${encodeURIComponent(cliente.id)}`;
        editar.className = "botao botao-secundario botao-pequeno";
        editar.textContent = "Editar";

        deletar.type = "button";
        deletar.className = "botao botao-perigo botao-pequeno";
        deletar.textContent = "Deletar";
        deletar.addEventListener("click", () => deletarCliente(cliente.id));

        acoes.append(whatsapp, detalhes, editar, deletar);
        linha.appendChild(acoes);
        tabela.appendChild(linha);
    });
}

function configurarLogout() {
    const botao = document.getElementById("logoutBtn");

    if (botao) {
        botao.addEventListener("click", logout);
    }
}

document.addEventListener("DOMContentLoaded", () => {
    const pagina = document.body.dataset.pagina;

    if (pagina === "login") {
        document.getElementById("loginForm").addEventListener("submit", (evento) => {
            evento.preventDefault();
            login();
        });
        return;
    }

    if (!verificarAutenticacao()) {
        return;
    }

    configurarLogout();

    if (pagina === "dashboard") {
        carregarDashboard();
    }

    if (pagina === "clientes") {
        document.getElementById("buscarBtn").addEventListener("click", buscarClientes);
        document.getElementById("listarBtn").addEventListener("click", () => {
            document.getElementById("termoBusca").value = "";
            listarClientes();
        });
        document.getElementById("termoBusca").addEventListener("keydown", (evento) => {
            if (evento.key === "Enter") {
                buscarClientes();
            }
        });
        listarClientes();
    }

    if (pagina === "novo-cliente") {
        document.getElementById("dataEnvio").value = dataPadraoEnvio();
        configurarMensagemPadrao();
        document.getElementById("novoClienteForm").addEventListener("submit", (evento) => {
            evento.preventDefault();
            cadastrarCliente();
        });
    }

    if (pagina === "editar-cliente") {
        document.getElementById("editarClienteForm").addEventListener("submit", (evento) => {
            evento.preventDefault();
            editarCliente();
        });
        carregarClienteParaEdicao();
    }

    if (pagina === "cliente-detalhes") {
        carregarDetalhesCliente();
    }
});
