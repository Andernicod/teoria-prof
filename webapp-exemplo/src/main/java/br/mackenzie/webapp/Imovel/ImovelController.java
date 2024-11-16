package br.mackenzie.webapp.Imovel;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import br.mackenzie.webapp.security.dao.UserDao;
import br.mackenzie.webapp.security.model.DAOUser;
import br.mackenzie.webapp.security.model.UserDTO;

@RestController
@RequestMapping("/api/imoveis")
class ImovelController {

    @Autowired
    private ImovelRepo imovelRepo;

    public ImovelController() {
    }

    @GetMapping
    Iterable<Imovel> getImoveis(@RequestParam Optional<String> search) {
        if (search.isPresent()) {
            return imovelRepo.findByRuaContainingIgnoreCase(search.get());
        } else {
            return imovelRepo.findAll();
        }
    }

    @GetMapping("/{id}")
    Optional<Imovel> getImovel(@PathVariable long id) {
        return imovelRepo.findById(id);
    }

    @Autowired
    private UserDao userDao; // Repositório de usuários

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

@PutMapping("/{imovelId}")
public Imovel updateImovel(@RequestBody Imovel imovelRequest, @PathVariable long imovelId) {
    // Obtendo o usuário logado
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String username = authentication.getName();
    DAOUser usuarioLogado = userDao.findByUsername(username);

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

@DeleteMapping("/{id}")
public void deleteImovel(@PathVariable long id) {
    // Obtendo o usuário logado
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String username = authentication.getName();
    DAOUser usuarioLogado = userDao.findByUsername(username);

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