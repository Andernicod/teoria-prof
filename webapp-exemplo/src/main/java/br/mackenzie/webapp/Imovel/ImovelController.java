package br.mackenzie.webapp.Imovel;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import org.springframework.security.core.Authentication;
import org.springframework.web.server.ResponseStatusException;

import br.mackenzie.webapp.security.dao.UserDao;
import br.mackenzie.webapp.security.model.DAOUser;

@RestController
@RequestMapping("/api/imoveis")
public class ImovelController {

    @Autowired
    private ImovelRepo imovelRepo;

    @Autowired
    private UserDao userDao; // Repositório de usuários

    // Método GET: Para carregar todos os imóveis ou filtrar por rua
    @GetMapping
    public Iterable<Imovel> getImoveis(@RequestParam Optional<String> search) {
        if (search.isPresent()) {
            return imovelRepo.findByRuaContainingIgnoreCase(search.get());
        } else {
            return imovelRepo.findAll();
        }
    }

    // Método GET: Para carregar um imóvel específico
    @GetMapping("/{id}")
    public Optional<Imovel> getImovel(@PathVariable long id) {
        return imovelRepo.findById(id);
    }

    // Método POST: Criar um novo imóvel (só para usuários autenticados)
    @PostMapping
    public Imovel createImovel(@RequestBody Imovel imovel) {
        // Obtendo o usuário logado a partir do contexto de segurança
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName(); // Nome de usuário do usuário logado

        // Verificando se o usuário existe no banco de dados
        DAOUser usuario = userDao.findByUsername(username);
        if (usuario == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado.");
        }

        // Associando o usuário ao imóvel
        imovel.setUsuario(usuario);

        // Salvando o imóvel no banco
        return imovelRepo.save(imovel);
    }

    // Método PUT: Atualizar um imóvel existente (só pode editar seus próprios imóveis)
    @PutMapping("/{imovelId}")
    public Imovel updateImovel(@RequestBody Imovel imovelRequest, @PathVariable long imovelId) {
        // Obtendo o usuário logado
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName(); // Nome de usuário do usuário logado

        // Buscando o imóvel pelo ID
        Optional<Imovel> existingImovel = imovelRepo.findById(imovelId);
        if (existingImovel.isPresent()) {
            Imovel imovel = existingImovel.get();

            // Verificando se o imóvel pertence ao usuário logado
            if (!imovel.getUsuario().getUsername().equals(username)) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Você não tem permissão para editar este imóvel.");
            }

            // Atualizando as informações do imóvel
            imovel.setTitulo(imovelRequest.getTitulo());
            imovel.setDescricao(imovelRequest.getDescricao());
            imovel.setPreco(imovelRequest.getPreco());
            imovel.setFoto(imovelRequest.getFoto());
            imovel.setTipo(imovelRequest.getTipo());
            imovel.setRua(imovelRequest.getRua());
            imovel.setNumero(imovelRequest.getNumero());
            imovel.setCidade(imovelRequest.getCidade());
            imovel.setEstado(imovelRequest.getEstado());
            imovel.setCep(imovelRequest.getCep());

            return imovelRepo.save(imovel);
        }

        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Imóvel não encontrado.");
    }

    // Método DELETE: Excluir um imóvel (só pode excluir seus próprios imóveis)
    @DeleteMapping("/{id}")
    public void deleteImovel(@PathVariable long id) {
        // Obtendo o usuário logado
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName(); // Nome de usuário do usuário logado

        // Buscando o imóvel pelo ID
        Optional<Imovel> imovelOptional = imovelRepo.findById(id);
        if (imovelOptional.isPresent()) {
            Imovel imovel = imovelOptional.get();

            // Verificando se o imóvel pertence ao usuário logado
            if (!imovel.getUsuario().getUsername().equals(username)) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Você não tem permissão para excluir este imóvel.");
            }

            imovelRepo.delete(imovel);
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Imóvel não encontrado.");
        }
    }
}