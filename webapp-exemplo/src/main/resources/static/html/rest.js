const imoveisList = document.getElementById('imoveisList');
const formAdicionarImovel = document.getElementById('formAdicionarImovel');
const logoutButton = document.getElementById('logout');
const formLogin = document.getElementById('formLogin');
const formRegistro = document.getElementById('formRegistro');
const mensagemLogin = document.getElementById('mensagemLogin');
const mensagemRegistro = document.getElementById('mensagemRegistro');
const searchButton = document.getElementById('searchButton');
const loginLogoutBtn = document.getElementById('loginLogoutBtn');
const searchInput = document.getElementById('searchImovel');
const formEditarImovel = document.getElementById('formEditarImovel');
const editModal = document.getElementById('editModal');
const token = localStorage.getItem('token');
let user = localStorage.getItem("userName");

let imoveisCarregados = []; // Variável para armazenar os imóveis carregados

async function carregarImoveis(query = '') {
    try {
        // Condicionalmente adicionar o token de autorização, caso o usuário esteja logado
        const headers = {
            'Content-Type': 'application/json',
        };

        if (token) {
            headers['Authorization'] = `Bearer ${token}`;
        }

        const response = await fetch(`/api/imoveis${query ? `?search=${encodeURIComponent(query)}` : ''}`, {
            method: 'GET',
            headers: headers,
        });

        if (!response.ok) {
            throw new Error(`Erro na requisição: ${response.statusText}`);
        }

        const imoveis = await response.json();
        imoveisCarregados = imoveis; // Armazena os imóveis carregados

        if (Array.isArray(imoveis)) {
            exibirImoveis(imoveis); // Exibe os imóveis carregados na tela
        } else {
            throw new Error('Erro: A resposta não é uma lista de imóveis.');
        }
    } catch (error) {
        console.error('Erro ao carregar imóveis:', error);
        alert(`Erro ao carregar imóveis: ${error.message}`);
    }
}

function verificarUsuarioLogado() {
    const token = localStorage.getItem('token');
    const user = localStorage.getItem('userName');
    if (token && user) {
        document.getElementById('userDisplayName').textContent = `Bem-vindo(a), ${user}!`;
        return true;
    }
    return false;
}

if (formAdicionarImovel) {
    formAdicionarImovel.onsubmit = async (event) => {
        event.preventDefault();

        // Verifica se o usuário está logado (token presente)
        if (!token) {
            const loginPopUp = document.createElement('div');
            loginPopUp.classList.add('pop-up-login');
            loginPopUp.innerHTML = `
                <div class="pop-up-content">
                    <p>Você precisa estar logado para adicionar um imóvel.</p>
                    <button id="loginAgoraBtn">Login</button>
                    <button id="fecharPopUpBtn">Fechar</button>
                </div>
            `;
            document.body.appendChild(loginPopUp);

            // Ações dos botões do pop-up
            document.getElementById('loginAgoraBtn').onclick = () => window.location.href = 'login.html';
            document.getElementById('fecharPopUpBtn').onclick = () => loginPopUp.remove();
            return;
        }

        const imovelData = obterDadosImovel();
        
        try {
            const response = await fetch('/api/imoveis', {
                method: 'POST',
                headers: { 
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${token}`
                },
                body: JSON.stringify(imovelData),
            });

            if (response.ok) {
                carregarImoveis();
                formAdicionarImovel.reset();
                formAdicionarImovel.style.display = 'none';
            } else {
                const errorText = await response.text();
                exibirMensagemDeErro('Erro ao adicionar imóvel: ' + errorText);
            }
        } catch (error) {
            console.error('Erro ao adicionar imóvel:', error);
            exibirMensagemDeErro('Erro ao adicionar imóvel: ' + error.message);
        }
    };
}

function exibirImoveis(imoveis) {
    imoveisList.innerHTML = ''; // Limpa a lista de imóveis na tela
    if (imoveis.length === 0) {
        imoveisList.innerHTML = '<li>Nenhum imóvel encontrado.</li>';
    } else {
        imoveis.forEach(imovel => {
            const li = document.createElement('li');
            li.classList.add('imovel');
            li.innerHTML = `
                <img src="${imovel.foto}" alt="${imovel.titulo}">
                <div class="imovel-details">
                    <strong>${imovel.titulo}</strong><br>
                    <span>${imovel.descricao}</span><br>
                    <span>R$ ${imovel.preco}</span><br>
                    <span><em>${imovel.tipo === 'venda' ? 'Para Venda' : 'Para Aluguel'}</em></span><br>
                    <span>${imovel.rua}, ${imovel.numero} - ${imovel.cidade}, ${imovel.estado} - CEP: ${imovel.cep}</span>
                </div>
                <div id="botoes-ED">
                    ${imovel.usuario.username === user ? `
                        <button class="edit" onclick="editarImovel(${imovel.id})">Editar</button>
                        <button class="delete" onclick="excluirImovel(${imovel.id})">Excluir</button>
                    ` : ''}
                </div>
            `;
            imoveisList.appendChild(li);
        });
    }
}

// Alterar a função do botão de pesquisa
if (searchButton) {
    searchButton.onclick = () => {
        const query = searchInput.value.trim().toLowerCase();
        
        // Filtra os imóveis carregados com base no texto da pesquisa
        const imoveisFiltrados = imoveisCarregados.filter(imovel => {
            return imovel.titulo.toLowerCase().includes(query) ||
                   imovel.descricao.toLowerCase().includes(query) ||
                   imovel.rua.toLowerCase().includes(query) ||
                   imovel.cidade.toLowerCase().includes(query) ||
                   imovel.estado.toLowerCase().includes(query);
        });

        // Exibe os imóveis filtrados
        exibirImoveis(imoveisFiltrados);
    };
}

async function editarImovel(id) {
    try {
        const response = await fetch(`/api/imoveis/${id}`, {
            method: 'GET',
            headers: { 
                'Authorization': `Bearer ${token}` // Adiciona o token no cabeçalho
            }
        });
        const imovel = await response.json();
        
        if (imovel) {
            document.getElementById('tituloImovelEdit').value = imovel.titulo;
            document.getElementById('descricaoImovelEdit').value = imovel.descricao;
            document.getElementById('precoImovelEdit').value = imovel.preco;
            document.getElementById('fotoImovelEdit').value = imovel.foto;
            document.getElementById('tipoImovelEdit').value = imovel.tipo;
            document.getElementById('ruaImovelEdit').value = imovel.rua;
            document.getElementById('numeroImovelEdit').value = imovel.numero;
            document.getElementById('cidadeImovelEdit').value = imovel.cidade;
            document.getElementById('estadoImovelEdit').value = imovel.estado;
            document.getElementById('cepImovelEdit').value = imovel.cep;
            
            editModal.style.display = 'block';

            formEditarImovel.onsubmit = async (event) => {
                event.preventDefault();
                const imovelData = obterDadosImovelEdit();
                const responseUpdate = await fetch(`/api/imoveis/${id}`, {
                    method: 'PUT',
                    headers: { 
                        'Content-Type': 'application/json',
                        'Authorization': `Bearer ${token}` // Adiciona o token no cabeçalho
                    },
                    body: JSON.stringify(imovelData),
                });

                if (responseUpdate.ok) {
                    carregarImoveis();
                    editModal.style.display = 'none';
                } else {
                    const errorText = await responseUpdate.text();
                    console.error('Erro ao editar imóvel:', errorText);
                    exibirMensagemDeErro(errorText); // Exibir erro na tela
                }
            };
        }
    } catch (error) {
        console.error('Erro ao buscar imóvel para edição:', error);
        exibirMensagemDeErro('Erro ao buscar imóvel para edição.');
    }
}

async function buscarImoveis() {
    const termo = document.getElementById('campoBusca').value;
    const response = await fetch(`/api/imoveis/buscar?termo=${encodeURIComponent(termo)}`);
    const imoveis = await response.json();

    if (imoveis) {
        exibirImoveis(imoveis);
    } else {
        alert("Nenhum imóvel encontrado.");
    }
}

async function excluirImovel(id) {
    if (confirm('Você tem certeza que deseja excluir este imóvel?')) {
        try {
            const response = await fetch(`/api/imoveis/${id}`, {
                method: 'DELETE',
                headers: { 
                    'Authorization': `Bearer ${token}` // Adiciona o token no cabeçalho
                }
            });
            if (response.ok) {
                carregarImoveis();
            } else {
                const errorText = await response.text();
                exibirMensagemDeErro(errorText); // Exibir erro na tela
            }
        } catch (error) {
            console.error('Erro ao excluir imóvel:', error);
            exibirMensagemDeErro('Erro ao excluir imóvel.');
        }
    }
}

let userLoggedIn = localStorage.getItem("token");
if (userLoggedIn) {
    loginLogoutBtn.textContent = "Sair";
    loginLogoutBtn.classList.add('logout');
    document.getElementById('userDisplayName').textContent = user ? `Bem-vindo(a), ${user}!` : "Usuário";
} else {
    loginLogoutBtn.textContent = "Login";
    document.getElementById('userDisplayName').textContent = "Bem-vindo(a)!";
}
loginLogoutBtn.addEventListener("click", function() {
    const userLoggedIn = localStorage.getItem("token");
    if (userLoggedIn) {
        localStorage.removeItem("token");
        localStorage.removeItem("userName");
        location.reload(); // Recarrega a página após logout
    } else {
        window.location.href = "login.html"; // Redireciona para a página de login
    }
});

if (formLogin) {
    formLogin.onsubmit = async (event) => {
        event.preventDefault();
        mensagemLogin.textContent = '';
        const username = document.getElementById('usernameLogin').value;
        const password = document.getElementById('passwordLogin').value;

        try {
            const response = await fetch('/api/usuarios/login', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ username, password }),
            });

            const message = await response.text();
            mensagemLogin.textContent = message;
            if (response.ok) {
                localStorage.setItem('usuarioLogado', username);
                window.location.href = '/imoveis.html';
            }
        } catch (error) {
            console.error('Erro no login:', error);
            mensagemLogin.textContent = 'Erro ao realizar login.';
        }
    };
}

if (formRegistro) {
    formRegistro.onsubmit = async (event) => {
        event.preventDefault();
        mensagemRegistro.textContent = '';

        const nome = document.getElementById('nomeRegistro').value;
        const sobrenome = document.getElementById('sobrenomeRegistro').value;
        const email = document.getElementById('emailRegistro').value;
        const username = document.getElementById('usernameRegistro').value;
        const password = document.getElementById('passwordRegistro').value;

        try {
            const response = await fetch('/api/usuarios', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ nome, sobrenome, email, username, password }),  // Incluindo os novos campos
            });

            const message = await response.text();
            mensagemRegistro.textContent = message;

            if (response.ok) {
                formRegistro.reset();
            }
        } catch (error) {
            console.error('Erro no registro:', error);
            mensagemRegistro.textContent = 'Erro ao registrar usuário.';
        }
    };
}

function obterDadosImovel() {
    return {
        titulo: document.getElementById('tituloImovel').value,
        descricao: document.getElementById('descricaoImovel').value,
        preco: document.getElementById('precoImovel').value,
        foto: document.getElementById('fotoImovel').value,
        tipo: document.getElementById('tipoImovel').value,
        rua: document.getElementById('ruaImovel').value,
        numero: document.getElementById('numeroImovel').value,
        cidade: document.getElementById('cidadeImovel').value,
        estado: document.getElementById('estadoImovel').value,
        cep: document.getElementById('cepImovel').value,
    };
}

function obterDadosImovelEdit() {
    return {
        titulo: document.getElementById('tituloImovelEdit').value,
        descricao: document.getElementById('descricaoImovelEdit').value,
        preco: document.getElementById('precoImovelEdit').value,
        foto: document.getElementById('fotoImovelEdit').value,
        tipo: document.getElementById('tipoImovelEdit').value,
        rua: document.getElementById('ruaImovelEdit').value,
        numero: document.getElementById('numeroImovelEdit').value,
        cidade: document.getElementById('cidadeImovelEdit').value,
        estado: document.getElementById('estadoImovelEdit').value,
        cep: document.getElementById('cepImovelEdit').value,
    };
}

function preencherFormulario(imovel) {
    document.getElementById('tituloImovel').value = imovel.titulo;
    document.getElementById('descricaoImovel').value = imovel.descricao;
    document.getElementById('precoImovel').value = imovel.preco;
    document.getElementById('fotoImovel').value = imovel.foto;
    document.getElementById('tipoImovel').value = imovel.tipo;
    document.getElementById('ruaImovel').value = imovel.rua;
    document.getElementById('numeroImovel').value = imovel.numero;
    document.getElementById('cidadeImovel').value = imovel.cidade;
    document.getElementById('estadoImovel').value = imovel.estado;
    document.getElementById('cepImovel').value = imovel.cep;
    document.getElementById('usuarioImovel').value = imovel.usuario;
}

if (editModal) {
    editModal.onclick = (event) => {
        if (event.target === editModal) {
            editModal.style.display = 'none';
        }
    };
}

function exibirMensagemDeErro(mensagem) {
    const errorMessageDiv = document.getElementById('error-message');
    const errorTextSpan = document.getElementById('error-text');
    const closeErrorBtn = document.getElementById('close-error');
    errorTextSpan.textContent = mensagem;
    errorMessageDiv.style.display = 'block';
    closeErrorBtn.onclick = () => {
        errorMessageDiv.style.display = 'none';
    };
    setTimeout(() => {
        errorMessageDiv.style.display = 'none';
    }, 5000);
}